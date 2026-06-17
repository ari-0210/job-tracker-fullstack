package com.arii.JobTracker.Service;

import com.arii.JobTracker.DTO.JobDTO;
import com.arii.JobTracker.DTO.StatisticsDTO;
import com.arii.JobTracker.Repository.JobRepository;
import com.arii.JobTracker.Security.BeanCopyUtils;
import com.arii.JobTracker.Security.SecurityUtils;
import com.arii.JobTracker.pojo.Job;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 申请事项业务逻辑处理核心实现类.
 * <p>注：方法实现由于自动继承 Service 接口的标准大厂 JavaDoc 规约，在此处无需二次冗余堆砌。</p>
 */
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class) // 双写一致性防线：数据库写入与 Redis 缓存蒸发必须在一个事务内原子执行
    public Job createJob(JobDTO dto) {
        Job job = new Job();
        BeanUtils.copyProperties(dto, job);
        job.setUserId(securityUtils.getCurrentUserId());
        job.setStatus("DRAFT");
        Job savedJob = jobRepository.save(job);
        // 数据发生实体膨胀，精准驱逐对应的统计大屏缓存
        String cacheKey = "stats:summary:" + job.getUserId();
        redisTemplate.delete(cacheKey);
        return savedJob;
    }

    // learn;查
    @Override
    public Page<Job> findAllJobs(Integer userId, int pageNumber, int pageSize, String searchTerm) {
        // 固化核心规则：全系统数据默认按最新更新时间（updateDate）由近及远呈现
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updateDate").descending());
        // 智能安全提取：判定是否需要执行跨库联合模糊匹配检索
        if (StringUtils.hasText(searchTerm)) { // learn;用StringUtils 检查关键字是否为空
            return jobRepository.findByUserIdAndCompanyContainingIgnoreCaseOrUserIdAndTagsContainingIgnoreCase(
                    userId, searchTerm, userId, searchTerm, pageable
            );
        } else {
            return jobRepository.findByUserId(userId, pageable);
        }

    }

    // learn;改
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Job updateJob(Integer userId, Integer jobId, JobDTO dto) {
        Job existingJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("数据不存在"));
// 强越权熔断：阻断非数据所有者通过黑客行为在前端拼接篡改他人数据
        if (!existingJob.getUserId().equals(userId)) {
            throw new RuntimeException("非法操作：你无权修改他人的数据");
        }

// 精准部分属性拷贝机制：防止前端传入未修改的空值洗掉数据库原本已有字段
        BeanCopyUtils.copyNonNullProperties(dto, existingJob);
        existingJob.setId(jobId);
        existingJob.setUserId(userId);

        Job saveJob = jobRepository.save(existingJob);
// 双写同步：数据变了，立即清理掉已产生数据倾斜的统计报表缓存,强迫下次查询去查数据库
        String cacheKey = "stats:summary:" + userId;
        redisTemplate.delete(cacheKey);
        return saveJob;
    }

    // learn;删
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Integer userId, Integer jobId) {
        // learn;不仅仅通过 id 删，还要确认这个 id 确实属于这个 userId
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("找不到该记录"));

        if (!job.getUserId().equals(userId)) {
            throw new RuntimeException("你没有权限删除他人的记录！");
        }
        jobRepository.deleteById(jobId);
        String cacheKey = "stats:summary:" + job.getUserId();
        redisTemplate.delete(cacheKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobsByIds(Integer userId, List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            // 将安全提取人作为联立级联查询硬性因子，杜绝批量删除时产生 ID 穿透越权清洗漏洞
            jobRepository.deleteByIdsAndUserId(userId, ids);
            String cacheKey = "stats:summary:" + userId;
            redisTemplate.delete(cacheKey);
        }
    }

    @Override
    public StatisticsDTO getAppStatistics(Integer userId) {
        String cacheKey = "stats:summary:" + userId;
// 第一防线：高抗并发 Cache-Aside 架构机制。先尝试从 Redis 获取,命中高速缓存则直接零延迟返回

        StatisticsDTO cached = (StatisticsDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // learn;2. 缓存未命中，执行之前的数据库查询逻辑
        StatisticsDTO dbData = calculateFromDb(userId);

        //写入 Redis 内存基线，强制配置 30 分钟生命周期（TTL），
        redisTemplate.opsForValue().set(cacheKey, dbData, 30, TimeUnit.MINUTES);

        return dbData;
    }

    /**
     * 提取源头底层数据库报表统计大字段计算逻辑.
     * <p>注：属于内部私有底层萃取工具函数，不对外开放契约，使用私有单行注释。</p>
     */
    private StatisticsDTO calculateFromDb(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        // learn;本月截止：从现在起到本月最后一天 23:59:59
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59);
        long total = jobRepository.countByUserId(userId);
        List<Object[]> results = jobRepository.countJobsByStatus(userId);

        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] result : results) {
            // learn;result[0] 是 status (String/Enum), result[1] 是 count (Long)
            statusMap.put(result[0].toString(), (Long) result[1]);
        }
        long next7 = jobRepository.countByDeadlineRange(userId, now, sevenDaysLater);
        long thisMonth = jobRepository.countByDeadlineRange(userId, now, endOfMonth);
        return new StatisticsDTO(total, statusMap, next7, thisMonth);
    }

    //learn;获取紧急事项
    @Override
    public List<Job> getUrgentJobs(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        // 召回迫近最前方 5 条倒计时任务
        return jobRepository.findTop5ByUserIdAndDeadlineBetweenOrderByDeadlineAsc(
                userId, now, sevenDaysLater
        );
    }
}

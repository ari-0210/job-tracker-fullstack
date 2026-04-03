package com.arii.JobTracker.Service;

import com.arii.JobTracker.DTO.StatisticsDTO;
import com.arii.JobTracker.Repository.JobRepository;
import com.arii.JobTracker.pojo.Job;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    //learning:把所有增删改查的方法都加上 userId 参数
    public Job createJob(Integer userId, Job job) {
        job.setUserId(userId);
        return jobRepository.save(job);
    }

    // r
    public Page<Job> findAllJobs(Integer userId, int pageNumber, int pageSize, String searchTerm) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updateDate").descending());
        if (StringUtils.hasText(searchTerm)) { // learning:用StringUtils 检查关键字是否为空
            return jobRepository.findByUserIdAndCompanyContainingIgnoreCaseOrUserIdAndTagsContainingIgnoreCase(
                    userId, searchTerm, userId, searchTerm, pageable
             );
        } else {
            return jobRepository.findByUserId(userId, pageable);
        }

    }

    // u
    @Transactional
    public Job updateJob(Integer userId, Job job) {
        Job existingJob = jobRepository.findById(job.getId())
                .orElseThrow(() -> new RuntimeException("数据不存在"));

        if (!existingJob.getUserId().equals(userId)) {
            throw new RuntimeException("非法操作：你无权修改他人的数据");
        }
        return jobRepository.save(job);
    }

    // d
    public void deleteJob(Integer userId, Integer jobId) {
        // learning:不仅仅通过 id 删，还要确认这个 id 确实属于这个 userId
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("找不到该记录"));

        if (!job.getUserId().equals(userId)) {
            throw new RuntimeException("你没有权限删除他人的记录！");
        }
        jobRepository.deleteById(jobId);
    }

    @Transactional // learing:对于修改数据库的操作加上事务注解,全部成功或者全部失败
    public void deleteJobsByIds(Integer userId, List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            jobRepository.deleteByIdsAndUserId(userId, ids);
        }
    }

    public StatisticsDTO getAppStatistics(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        // 本月截止：从现在起到本月最后一天 23:59:59
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59);



       //learn;
        long total = jobRepository.countByUserId(userId);
        List<Object[]> results = jobRepository.countJobsByStatus(userId);

        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] result : results) {
            // learn;result[0] 是 status (String/Enum), result[1] 是 count (Long)
            statusMap.put(result[0].toString(), (Long) result[1]);
        }
        long next7 = jobRepository.countByDeadlineRange(userId, now, sevenDaysLater);
        long thisMonth = jobRepository.countByDeadlineRange(userId, now, endOfMonth);
        return new StatisticsDTO(total, statusMap,next7, thisMonth);
    }



}
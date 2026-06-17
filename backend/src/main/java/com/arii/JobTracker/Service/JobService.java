package com.arii.JobTracker.Service;

import com.arii.JobTracker.DTO.JobDTO;
import com.arii.JobTracker.DTO.StatisticsDTO;
import com.arii.JobTracker.pojo.Job;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 申请事项流转及统计核心业务契约接口.
 *
 * @author Ari
 */
public interface JobService {

    /**
     * 创建并保存属于当前会话新用户的事项.
     * <p>新注册数据初始默认状态锁定为 'DRAFT'草稿，同步执行缓存蒸发清理机制.</p>
     *
     * @param jobDTO 包含新创建数据的结构载荷
     * @return 成功生成全局唯一主键后的持久化 Job 实体
     */
    Job createJob(JobDTO jobDTO);

    /**
     * 多维条件组合分页检索当前用户所有的申请数据.
     *
     * @param userId     当期登录人安全上下文唯一ID
     * @param pageNumber 目标检索页索引
     * @param pageSize   每页期望承载记录数上限
     * @param searchTerm 支持跨字段(公司/标签等)模糊比对关键字，传入为空时退化为全量检索
     * @return 经包装后的 Spring Data 标准 Page 实体
     */
    Page<Job> findAllJobs(Integer userId, int pageNumber, int pageSize, String searchTerm);

    /**
     * 高一致性防御式数据全字段/部分字段修改.
     *
     * @param userId 触发该操作的用户安全主键ID
     * @param jobId 期望变动的数据库原记录主键ID
     * @param dto 变动后的全新增量或存量值对象
     * @return 持久化无误后的最新实体数据对象
     */
    Job updateJob(Integer userId, Integer jobId, JobDTO dto);

    /**
     * 根据主键执行归属权审计删除.
     *
     * @param userId 执行清除动作的行为发出者安全ID
     * @param jobId 目标销毁记录主键ID
     */
    void deleteJob(Integer userId, Integer jobId);

    /**
     * 强并发高防型批量物理级联销毁.
     *
     * @param userId 当前操作人主键
     * @param ids 待收割的 ID 链表集合
     */
    void deleteJobsByIds(Integer userId, List<Integer> ids);

    /**
     * 高速缓存穿透防御型看板宏观指标分析统计.
     * <p>底层集成双端数据一致性缓存拦截规范：首发命中 Redis 内存加速，失效后兜底合并计算数据库数据并回填.</p>
     *
     * @param userId 目标分析对象用户安全ID
     * @return 组装完成的各状态及周期阶段聚合统计数据传输对象 DTO
     */
    StatisticsDTO getAppStatistics(Integer userId);

    /**
     * 提取当前用户临近危急周期的前5个最紧急代办事项.
     *
     * @param userId 目标检索主体安全ID
     * @return 严格按照截止日期倒序排列的 urgent 前五大危急事件集
     */
    List<Job> getUrgentJobs(Integer userId);

}
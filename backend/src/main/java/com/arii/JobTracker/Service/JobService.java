package com.arii.JobTracker.Service;

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

import java.util.List;

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



}
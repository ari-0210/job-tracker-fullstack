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
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // c
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    // r
    public Page<Job> findAllJobs(int pageNumber, int pageSize , String searchTerm ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("updateDate").descending());
        if (StringUtils.hasText(searchTerm)) { // use StringUtils to check if searchTerm is null
            return jobRepository.findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrTagsContainingIgnoreCase(
                     searchTerm, searchTerm, searchTerm, pageable
             );
        } else {
            return jobRepository.findAll(pageable);
        }

    }

    // u
    @Transactional
    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    // d
    public void deleteJob(Integer id) {
        jobRepository.deleteById(id);
    }

    @Transactional // 对于修改数据库的操作加上事务注解
    public void deleteJobsByIds(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            jobRepository.deleteAllByIdInBatch(ids);
        }
    }



}
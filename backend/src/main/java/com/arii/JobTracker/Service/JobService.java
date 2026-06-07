package com.arii.JobTracker.Service;

import com.arii.JobTracker.DTO.JobDTO;
import com.arii.JobTracker.DTO.StatisticsDTO;
import com.arii.JobTracker.pojo.Job;
import org.springframework.data.domain.Page;

import java.util.List;


public interface JobService {


    Job createJob(JobDTO dto);

    Page<Job> findAllJobs(Integer userId, int pageNumber, int pageSize, String searchTerm);

    Job updateJob(Integer userId, Integer jobId, JobDTO dto);

    void deleteJob(Integer userId, Integer jobId);

    void deleteJobsByIds(Integer userId, List<Integer> ids);


    StatisticsDTO getAppStatistics(Integer userId);

    List<Job> getUrgentJobs(Integer userId);

}
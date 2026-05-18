package com.arii.JobTracker.Repository;
import com.arii.JobTracker.pojo.JobFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobFileRepository  extends JpaRepository<JobFile, Integer> {
    List<JobFile> findByJobId(Integer jobId);
}

package com.arii.JobTracker.Service;

import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.JobFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    JobFile storeFile(MultipartFile file, Job job) throws IOException;

    List<JobFile> getFilesByJobId(Integer jobId);

    void deleteFileById(Integer id);

}
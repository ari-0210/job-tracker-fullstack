package com.arii.JobTracker.Service;

import com.arii.JobTracker.Repository.JobFileRepository;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.JobFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    @Autowired
    private JobFileRepository fileRepository;

    @Value("${file.upload-path}") // 从配置文件读取存放路径
    private String uploadPath;

    public JobFile storeFile(MultipartFile file, Job job) throws IOException {
        //learn; 1. 确保目录存在
        File directory = new File(uploadPath);
        if (!directory.exists()) directory.mkdirs();

        // learn;2. 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + extension;

        // leaen;3. 物理保存到硬盘
        Path targetLocation = Paths.get(uploadPath).resolve(savedName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // learn;4. 保存记录到数据库
        JobFile jobFile = new JobFile();
        jobFile.setOriginalFileName(originalName);
        jobFile.setSavedFileName(savedName);
        jobFile.setContentType(file.getContentType());
        jobFile.setFileSize(file.getSize());
        jobFile.setJob(job);

        return fileRepository.save(jobFile);
    }
    public List<JobFile> getFilesByJobId(Integer jobId){
        return  fileRepository.findByJobId(jobId);
    }
}

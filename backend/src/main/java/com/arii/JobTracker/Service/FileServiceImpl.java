package com.arii.JobTracker.Service;


import com.arii.JobTracker.Repository.JobFileRepository;
import com.arii.JobTracker.Repository.JobRepository;
import com.arii.JobTracker.Security.SecurityUtils;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.JobFile;
import jakarta.transaction.Transactional;
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
public class FileServiceImpl implements FileService {
    @Autowired
    private JobFileRepository fileRepository;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private JobRepository jobRepository;
    @Value("${file.upload-path}") // learn;从配置文件读取存放路径
    private String uploadPath;

    public JobFile storeFile(MultipartFile file, Job job) throws IOException {
        //learn; 1. 确保目录存在
        File directory = new File(uploadPath);
        if (!directory.exists()) directory.mkdirs();

        // learn;2. 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + extension;

        // learn;3. 物理保存到硬盘
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

    public List<JobFile> getFilesByJobId(Integer jobId) {
        Integer currentUserId = securityUtils.getCurrentUserId();

        // learn;1. 先查出这个 Job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // learn;2. 校验这个 Job 是不是当前用户的
        if (!job.getUserId().equals(currentUserId)) {
            throw new RuntimeException("你没有权限查看该任务的附件");
        }
        return fileRepository.findByJobId(jobId);
    }

    @Override
    @Transactional
    public void deleteFileById(Integer id) {
        // learn;1. 从数据库中查询文件是否存在
        JobFile jobFile = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("该文件在数据库中不存在，无法删除"));

        Integer currentUserId = securityUtils.getCurrentUserId();
        if (!jobFile.getJob().getUserId().equals(currentUserId)) {
            throw new RuntimeException("对不起，你没有权限删除该文件！"); // 会触发事务回滚
        }

        // learn;2. 拼接该文件在磁盘上的绝对路径 (使用的是长UUID文件名)
        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadPath, jobFile.getSavedFileName());

        try {
            java.nio.file.Files.deleteIfExists(filePath);
            System.out.println("磁盘物理文件删除成功：" + filePath.toString());
        } catch (java.io.IOException e) {
            //learn; 物理删除失败时抛出异常，触发事务回滚，防止出现“磁盘没删掉，数据库却删了”的情况
            throw new RuntimeException("物理文件删除失败，停止清理数据库数据: " + e.getMessage());
        }

        fileRepository.delete(jobFile);
        System.out.println("数据库记录删除成功，文件ID: " + id);
    }
}

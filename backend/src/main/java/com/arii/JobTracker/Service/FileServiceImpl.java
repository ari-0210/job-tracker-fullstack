package com.arii.JobTracker.Service;


import com.arii.JobTracker.Repository.JobFileRepository;
import com.arii.JobTracker.Repository.JobRepository;
import com.arii.JobTracker.Security.SecurityUtils;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.JobFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".pdf", ".docx", ".zip");
    @Override
    public JobFile storeFile(MultipartFile file, Job job) throws IOException {

        // learn; 提取后缀并强行转为小写比对
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new RuntimeException("非法文件名，拒绝上传！");
        }
        String extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();

        // 强行阻断黑客投毒：不在白名单内的后缀（如 .sh, .exe, .jsp）直接熔断
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("不支持的文件格式！仅允许上传图片、PDF、Word及压缩包");
        }

        // 自动探测并防御性创建缺失的本地存储目录
        File directory = new File(uploadPath);
        if (!directory.exists()) directory.mkdirs();

        // 提取扩展名后，利用全局唯一 UUID 重命名，切断公网恶意重名覆盖的可能
        String savedName = UUID.randomUUID().toString() + extension;

        // 将文件从内存复制交换到服务器硬盘
        Path targetLocation = Paths.get(uploadPath).resolve(savedName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 组装并返回数据库实体
        JobFile jobFile = new JobFile();
        jobFile.setOriginalFileName(originalName);
        jobFile.setSavedFileName(savedName);
        jobFile.setContentType(file.getContentType());
        jobFile.setFileSize(file.getSize());
        jobFile.setJob(job);

        return fileRepository.save(jobFile);
    }

    @Override
    public List<JobFile> getFilesByJobId(Integer jobId) {
        Integer currentUserId = securityUtils.getCurrentUserId();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("事项不存在"));

        // 水平越权安全防御：禁止用户通过拼装 jobId 查看属于其他用户的附件
        if (!job.getUserId().equals(currentUserId)) {
            throw new RuntimeException("你没有权限查看该事项的附件");
        }
        return fileRepository.findByJobId(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 显式指明任何异常均触发事务回滚
    public void deleteFileById(Integer id) {

        JobFile jobFile = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("该文件在数据库中不存在，无法删除"));
// 水平越权防御：只能删除自己上传的文件
        Integer currentUserId = securityUtils.getCurrentUserId();
        if (!jobFile.getJob().getUserId().equals(currentUserId)) {
            throw new RuntimeException("对不起，你没有权限删除该文件！");
        }

        // 定位该文件在磁盘上的绝对路径 (使用的是长UUID文件名)
        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadPath, jobFile.getSavedFileName());

        try {
            // 先尝试删除物理文件
            java.nio.file.Files.deleteIfExists(filePath);
            System.out.println("磁盘物理文件删除成功：" + filePath.toString());
        } catch (java.io.IOException e) {
            //物理删除失败时抛出异常，触发事务回滚，防止产生死锁和数据不一致
            throw new RuntimeException("物理文件删除失败，停止清理数据库数据: " + e.getMessage());
        }

        fileRepository.delete(jobFile);
        System.out.println("数据库记录删除成功，文件ID: " + id);
    }
}

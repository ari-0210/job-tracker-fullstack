package com.arii.JobTracker.Controller;

import com.arii.JobTracker.Repository.JobRepository;
import com.arii.JobTracker.Service.FileService;
import com.arii.JobTracker.common.Result;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.JobFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 事项附件管理控制层.
 * <p>处理与申请事项相关的物理文件上传、检索及安全删除逻辑.</p>
 *
 * @author Ari
 */
@Slf4j
@Tag(name = "03.上传文件管理", description = "事项上传文件附件")
@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private JobRepository jobRepository;

    /**
     * 上传单文件附件并关联至指定申请事项.
     *
     * @param file  前端传输的 Multipart 文件流对象
     * @param jobId 归属的事项主键ID
     * @return 包含唯一UUID文件名及存储元数据的 Result 外壳
     * @throws Exception 当指定任务不存在，或物理磁盘写入故障时抛出，由全局异常处理器拦截
     */
    @PostMapping("/upload")
    public Result<JobFile> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("jobId") Integer jobId) throws Exception {


        Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("事项不存在"));
if (file.getSize() > 10 * 1024 * 1024) { throw new RuntimeException("文件太大"); }

        String originalName = file.getOriginalFilename();
 String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
if (!Arrays.asList(".jpg", ".jpeg", ".png", ".pdf", ".docx", ".txt").contains(ext)) {
     throw new RuntimeException("非法文件格式");
 }

        JobFile savedFile = fileService.storeFile(file, job);


        return Result.success(savedFile);
    }

    /**
     * 获取指定申请事项下的所有附件列表.
     *
     * @param jobId 事项主键ID
     * @return 当前事项拥有的所有文件元数据集合
     */
    @GetMapping("/job/{jobId}")
    public Result<List<JobFile>> getFilesByJob(@PathVariable Integer jobId) {
        List<JobFile> files = fileService.getFilesByJobId(jobId);
        return Result.success(files);
    }

    /**
     * 根据主键ID清理附件.
     * <p>本接口具备级联越权校验，只有文件所有者才能成功触发删除.</p>
     *
     * @param id 文件记录主键ID
     * @return 统一成功响应外壳
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Integer id) {

            fileService.deleteFileById(id);
        return Result.success();
    }
}

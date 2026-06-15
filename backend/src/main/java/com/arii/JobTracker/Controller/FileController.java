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

import java.util.List;

@Slf4j
@Tag(name = "03.上传文件", description = "事项上传文件附件")
@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @Autowired
    private JobRepository jobRepository; // learn;用来校验 jobId 是否存在

    @PostMapping("/upload")
    public Result<JobFile> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("jobId") Integer jobId) throws Exception {

            // learn;1. 校验任务是否存在
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("任务不存在"));

            // learn;2. 调用 Service 保存文件
            JobFile savedFile = fileService.storeFile(file, job);

            // learn;3. 返回保存成功的实体（包含 ID、文件名等，方便前端展示）
        return Result.success(savedFile);
    }
    @GetMapping("/job/{jobId}")
    public Result<List<JobFile>> getFilesByJob(@PathVariable Integer jobId) {
        List<JobFile> files = fileService.getFilesByJobId(jobId);
        return Result.success(files);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Integer id) {

            fileService.deleteFileById(id);
        return Result.success();
    }
}

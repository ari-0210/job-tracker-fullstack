package com.arii.JobTracker.Controller;

import com.arii.JobTracker.Service.JobService;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.Result;
import com.arii.JobTracker.pojo.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j //learning:自动生成log
@Tag(name = "01.申请事项管理", description = "处理申请事项的增删改查及统计")
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;
    @Autowired
    private UserService userService;

    @Operation(summary = "新增事项")
    @PostMapping
    public ResponseEntity<Job> createJob(@AuthenticationPrincipal User user, @RequestBody Job job) {

        Job createdJob = jobService.createJob(user.getId(), job);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdJob.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdJob);
    }

    //learn;userId 通常不会作为 @RequestParam 让前端传过来。
//learn;原因：如果让前端传 userId，用户可以随意把浏览器 URL 里的 userId=1 改成 userId=2，从而偷看别人的数据。
    @Operation(summary = "分页查询", description = "支持关键字模糊搜索，返回带分页信息的列表")
    @GetMapping
    public ResponseEntity<Page<Job>> getAllJobs(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {

        Page<Job> jobsPage = jobService.findAllJobs(user.getId(), page, size, keyword);
        return ResponseEntity.ok(jobsPage);
    }

    @Operation(summary = "删除事项")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        jobService.deleteJob(user.getId(), id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "批量删除事项")
    @PostMapping("/batch-delete")
    public ResponseEntity<Void> deleteMultipleJobs(@AuthenticationPrincipal User user, @RequestBody Map<String, List<Integer>> payload) {

        List<Integer> idsToDelete = payload.get("ids");
        if (idsToDelete == null || idsToDelete.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        jobService.deleteJobsByIds(user.getId(), idsToDelete);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "更新事项")
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Integer id, @AuthenticationPrincipal User user, @RequestBody Job job) {
        Job updatedJob = jobService.updateJob(user.getId(), job);
            return ResponseEntity.ok(updatedJob);
    }

    @Operation(summary = "获取紧急事项")
    @GetMapping("/urgent")
    public Result<List<Job>> getUrgentJobs(@AuthenticationPrincipal User user) {
        return Result.success(jobService.getUrgentJobs(user.getId()));
    }
}
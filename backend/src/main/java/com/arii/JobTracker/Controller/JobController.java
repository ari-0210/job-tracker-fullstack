package com.arii.JobTracker.Controller;

import com.arii.JobTracker.DTO.JobDTO;
import com.arii.JobTracker.Security.SecurityUtils;
import com.arii.JobTracker.Service.JobService;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.VO.JobVO;
import com.arii.JobTracker.VO.PageVO;
import com.arii.JobTracker.pojo.Job;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j //learn;自动生成log
@Tag(name = "01.申请事项管理", description = "处理申请事项的增删改查及统计")
@RestController
@RequestMapping("/api/jobs")
public class JobController {


    @Autowired
    private JobService jobService;

    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private UserService userService;

    @Operation(summary = "新增事项")
    @PostMapping
    public ResponseEntity<JobVO> createJob(@Valid @RequestBody JobDTO jobDTO) {

        Job createdJob = jobService.createJob(jobDTO);
        JobVO vo = new JobVO();
        BeanUtils.copyProperties(createdJob, vo);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdJob.getId())
                .toUri();
        return ResponseEntity.created(location).body(vo);
    }

    //learn;userId 通常不会作为 @RequestParam 让前端传过来。
//learn;原因：如果让前端传 userId，用户可以随意把浏览器 URL 里的 userId=1 改成 userId=2，从而偷看别人的数据。
    @Operation(summary = "分页查询", description = "支持关键字模糊搜索，返回带分页信息的列表")
    @GetMapping
    public ResponseEntity<PageVO<JobVO>> getAllJobs(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {
        Integer userId = securityUtils.getCurrentUserId();
        //learn;去数据库查出带有分页信息的 Entity 原始数据
        Page<Job> jobsPage = jobService.findAllJobs(userId, page, size, keyword);

        //learn; 将内部的 List<Job> 转换成 List<JobVO>
        List<JobVO> voList = jobsPage.getContent().stream().map(job -> {
            JobVO vo = new JobVO();
            BeanUtils.copyProperties(job, vo); // learn;这里是从数据库查出来的全量数据，可以直接覆盖拷贝
            return vo;
        }).toList();

        // 3. 组装成自定义的 PageVO 返回
        return ResponseEntity.ok(new PageVO<>(jobsPage, voList));
    }

    @Operation(summary = "删除事项")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer id) {
        Integer userId = securityUtils.getCurrentUserId(); // 动态获取
        jobService.deleteJob(userId, id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "批量删除事项")
    @PostMapping("/batch-delete")
    public ResponseEntity<Void> deleteMultipleJobs(@RequestBody Map<String, List<Integer>> payload) {

        List<Integer> idsToDelete = payload.get("ids");
        if (idsToDelete == null || idsToDelete.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Integer userId = securityUtils.getCurrentUserId();
        jobService.deleteJobsByIds(userId, idsToDelete);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "更新事项")
    @PutMapping("/{id}")
    public ResponseEntity<JobVO> updateJob(@PathVariable Integer id, @Valid @RequestBody JobDTO jobDTO) {
        Integer userId = securityUtils.getCurrentUserId();
        Job updatedJob = jobService.updateJob(userId, id, jobDTO);
        JobVO vo = new JobVO();
        BeanUtils.copyProperties(updatedJob, vo);
        return ResponseEntity.ok(vo);
    }


}
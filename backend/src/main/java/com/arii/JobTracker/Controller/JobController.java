package com.arii.JobTracker.Controller;

import com.arii.JobTracker.DTO.JobDTO;
import com.arii.JobTracker.Security.SecurityUtils;
import com.arii.JobTracker.Service.JobService;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.VO.JobVO;
import com.arii.JobTracker.VO.PageVO;
import com.arii.JobTracker.common.Result;
import com.arii.JobTracker.common.ResultCode;
import com.arii.JobTracker.pojo.Job;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 事项核心管理控制层.
 * <p>处理事项生命周期的全流程管理，内置基于多层过滤的多维度复杂检索，具备严格的隔离级越权安全防御.</p>
 *
 * @author Ari
 */
@Slf4j 
@Tag(name = "01.申请事项管理", description = "处理申请事项的增删改查及统计")
@RestController
@RequestMapping("/api/jobs")
@Validated 
public class JobController {


    @Autowired
    private JobService jobService;

    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private UserService userService;

    /**
     * 新增单条事项接口.
     *
     * @param jobDTO 包含新事项核心信息（如公司、标签、截止日期等）的合法数据载荷
     * @return 包含生成主键ID及初始状态的 JobVO 视图数据外壳
     */
    @Operation(summary = "新增事项")
    @PostMapping
    public Result<JobVO> createJob(@Valid @RequestBody JobDTO jobDTO) {

        Job createdJob = jobService.createJob(jobDTO);
        JobVO vo = new JobVO();
        BeanUtils.copyProperties(createdJob, vo);

        return Result.success(vo);
    }


    /**
     * 分页多条件安全检索申请事项接口.
     * <p>安全隔离原则：拒绝前端传入 userId 过滤条件，后端一律强行提取当前上下文登录 ID，彻底杜绝遍历 URL 水平越权窃取数据的可能.</p>
     *
     * @param keyword 模糊搜索关键字（支持公司名、标签等多维度模糊搜索），非必填
     * @param page    当前页码索引，从 0 开始，默认值为 0
     * @param size    每页期望拉取的数据条数，默认值为 10
     * @return 复合 PageVO 规范的分页高内聚响应实体外壳
     */
    @Operation(summary = "分页查询", description = "支持关键字模糊搜索，返回带分页信息的列表")
    @GetMapping
    public Result<PageVO<JobVO>> getAllJobs(
            @RequestParam(name = "keyword", required = false) String keyword,
            @Min(value = 0, message = "当前页码不能小于 0") 
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Min(value = 1, message = "每页拉取条数不能小于 1")
            @Max(value = 100, message = "每页拉取条数不能超过 100")//防御性封顶，防止内存爆掉
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {
        Integer userId = securityUtils.getCurrentUserId();

        Page<Job> jobsPage = jobService.findAllJobs(userId, page, size, keyword);


        List<JobVO> voList = jobsPage.getContent().stream().map(job -> {
            JobVO vo = new JobVO();
            BeanUtils.copyProperties(job, vo); 
            return vo;
        }).toList();

        // 3. 组装成自定义的 PageVO 返回
        return Result.success(new PageVO<>(jobsPage, voList));
    }

    /**
     * 根据主键ID安全删除事项.
     *
     * @param id 待移除的事项唯一主键ID
     * @return 统一无业务载荷成功响应外壳
     */
    @Operation(summary = "删除事项")
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Integer id) {
        Integer userId = securityUtils.getCurrentUserId(); // 动态获取
        jobService.deleteJob(userId, id);
        return Result.success();
    }

    /**
     * 批量安全清理事项接口.
     *
     * @param payload 键值对形式的包裹体，预期包含名为 "ids" 的整型主键数组
     * @return 统一成功状态码响应
     */
    @Operation(summary = "批量删除事项")
    @PostMapping("/batch-delete")
    public Result<Void> deleteMultipleJobs(@RequestBody Map<String, List<Integer>> payload) {

        List<Integer> idsToDelete = payload.get("ids");
        if (idsToDelete == null || idsToDelete.isEmpty()) {
            return Result.failed(ResultCode.BAD_REQUEST.getCode(), "删除传入的ID列表不能为空");
        }
        Integer userId = securityUtils.getCurrentUserId();
        jobService.deleteJobsByIds(userId, idsToDelete);
        return Result.success();
    }

    /**
     * 根据ID安全更新事项数据载荷接口.
     *
     * @param id 待修改的目标事项主键ID
     * @param jobDTO 包含更新后干货内容的数据传输对象
     * @return 修改成功并完成持久化后的最新 JobVO 视图外壳
     */
    @Operation(summary = "更新事项")
    @PutMapping("/{id}")
    public Result<JobVO> updateJob(@PathVariable Integer id, @Valid @RequestBody JobDTO jobDTO) {
        Integer userId = securityUtils.getCurrentUserId();
        Job updatedJob = jobService.updateJob(userId, id, jobDTO);
        JobVO vo = new JobVO();
        BeanUtils.copyProperties(updatedJob, vo);
        return Result.success(vo);
    }


}

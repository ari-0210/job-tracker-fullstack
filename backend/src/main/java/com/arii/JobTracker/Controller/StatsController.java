package com.arii.JobTracker.Controller;

import com.arii.JobTracker.DTO.StatisticsDTO;
import com.arii.JobTracker.Service.JobService;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.common.Result;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 仪表盘与宏观统计数据分析控制层.
 * <p>为前端提供看盘图表元数据，集成了 Spring Security 核心上下文用户抓取机制，隔离非法用户访问.</p>
 *
 * @author Ari
 */
@Slf4j
@RestController
@RequestMapping("/api/stats")
@Tag(name = "02.图表化", description = "统计数据和图表化")
public class StatsController {
    @Autowired
    private JobService jobService;
    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户的综合看板大盘统计指标.
     * <p>利用 @AuthenticationPrincipal 零漏点强行拦截上下文 Principal，防止参数篡改风险.</p>
     *
     * @param user 经 Spring Security 鉴权后注入的当前合法在线用户实体对象
     * @return 包含全量统计（如月度、7天紧迫度、状态分布等）的高内聚结构响应体
     */
    @Operation(summary = "事项统计")
    @GetMapping("/summary")
    public Result<StatisticsDTO> getSummary(@AuthenticationPrincipal User user) {
        return Result.success(jobService.getAppStatistics(user.getId()));
    }

    /**
     * 拉取当前用户最高紧急代办的前 5 条事项集锦.
     *
     * @param user 当前会话绑定的合法在线用户实体对象
     * @return 临近截止时间红线的前 5 条精简化急迫事项集合
     */
    @Operation(summary = "获取紧急事项")
    @GetMapping("/urgent")
    public Result<List<Job>> getUrgentJobs(@AuthenticationPrincipal User user) {
        return Result.success(jobService.getUrgentJobs(user.getId()));
    }
}

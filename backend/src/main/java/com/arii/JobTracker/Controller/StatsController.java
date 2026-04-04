package com.arii.JobTracker.Controller;

import com.arii.JobTracker.DTO.StatisticsDTO;
import com.arii.JobTracker.Service.JobService;
import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.pojo.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stats")
@Tag(name = "02.图表化", description = "统计数据和图表化")
public class StatsController {
    @Autowired
    private JobService jobService;
    @Autowired
    private UserService userService;


    @GetMapping("/summary")
    public ResponseEntity<StatisticsDTO> getSummary(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobService.getAppStatistics(user.getId()));
    }
}

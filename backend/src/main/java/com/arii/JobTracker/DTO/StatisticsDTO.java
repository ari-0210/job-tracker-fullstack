package com.arii.JobTracker.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO implements Serializable {
    @Schema(description = "事项合计条数")
    private long totalCount;
    @Schema(description = "状态统计")
    private Map<String, Long> statusCounts;
    @Schema(description = "7天内截止事项")
    private long next7DaysCount;
    @Schema(description = "本月内截止事项")
    private long thisMonthCount;
}

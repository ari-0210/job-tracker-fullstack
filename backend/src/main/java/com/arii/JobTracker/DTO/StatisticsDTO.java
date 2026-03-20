package com.arii.JobTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class StatisticsDTO {
    private long totalCount;
    private Map<String, Long> statusCounts;
}

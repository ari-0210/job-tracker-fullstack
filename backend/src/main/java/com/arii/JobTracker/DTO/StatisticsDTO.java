package com.arii.JobTracker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO implements Serializable {
    private long totalCount;
    private Map<String, Long> statusCounts;
    private long next7DaysCount;
    private long thisMonthCount;
}

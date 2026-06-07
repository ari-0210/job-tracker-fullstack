package com.arii.JobTracker.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobDTO {
    @Schema(description = "接收方", example = "投递公司名")
    private String company;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private String status;

    @Schema(description = "标签")
    private String tags;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "备注")
    private String notes;

    @Schema(description = "截止时间")
    @NotNull(message = "截止时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @Schema(description = "提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderDate;


}

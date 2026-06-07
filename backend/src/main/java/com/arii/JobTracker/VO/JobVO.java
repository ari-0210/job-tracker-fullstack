package com.arii.JobTracker.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "事项展示对象")
public class JobVO {

    @Schema(description = "事项ID")
    private Integer id;

    @Schema(description = "接收方/公司名")
    private String company;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @Schema(description = "提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderDate;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
}


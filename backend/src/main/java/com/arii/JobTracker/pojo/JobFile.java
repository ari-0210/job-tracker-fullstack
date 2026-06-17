package com.arii.JobTracker.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "job_files")
@AllArgsConstructor
@NoArgsConstructor
public class JobFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用户上传的文件名")
    @Column(nullable = false)
    private String originalFileName;

    @Schema(description = "存储硬盘上文件名")
    @Column(nullable = false)
    private String savedFileName;
    @Schema(description = "文件后缀或 MIME 类型", example = "application/pdf")
    private String contentType;

    @Schema(description = "文件大小（单位：字节）")
    private Long fileSize;

    @Schema(description = "多个文件对应的一个 Job 任务")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    @JsonIgnore // 防止在转 JSON 时发生循环引用导致崩溃
    private Job job;

    @Schema(description = "上传时间")
    @Column(name = "upload_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime = LocalDateTime.now();
}

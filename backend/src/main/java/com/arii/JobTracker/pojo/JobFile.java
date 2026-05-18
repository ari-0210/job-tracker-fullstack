package com.arii.JobTracker.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // learn;用户上传时文件名
    @Column(nullable = false)
    private String originalFileName;

    // learn;存储硬盘上文件名
    @Column(nullable = false)
    private String savedFileName;

    // learn;文件后缀或 MIME 类型（如 "application/pdf"），方便前端判断图标
    private String contentType;

    // learn;文件大小（单位：字节）
    private Long fileSize;

    // learn;多对一关联：多个文件对应一个 Job 任务
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    @JsonIgnore // learn;重要：防止在转 JSON 时发生循环引用导致崩溃
    private Job job;

    // learn;上传时间
    @Column(name = "upload_time") // 上传时间一旦生成，不许修改
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime = LocalDateTime.now();
}

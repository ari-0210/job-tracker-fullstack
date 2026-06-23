package com.arii.JobTracker.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "submissions", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"), // 1. 把原有的 user_id 索引写进来
        @Index(name = "idx_reminder_date", columnList = "reminder_date") // 2. 新增的提醒时间索引
})
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="user_id",nullable = false)
    private  Integer userId;

    //接收方
    @Column(name = "recipient")
    private String company;

    @Column(nullable = false)
    private String status = "DRAFT";


    private String tags;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String notes;


    @CreatedDate
    @Column(name = "submit_date", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyDate;


    @LastModifiedDate
    @Column(name = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;


    @Column(name = "deadline", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;


    @Column(name = "reminder_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderDate;



}

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
@EntityListeners(AuditingEntityListener.class) // Learn:监听审计事件
@Table(name = "submissions")
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

    // Learn;创建时间：只在插入时设置，之后不可更改
    @CreatedDate
    @Column(name = "submit_date", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyDate;

    // Learn;修改时间：每次更新都会自动刷新
    @LastModifiedDate
    @Column(name = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    //learn;ddl
    @Column(name = "deadline", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    //learn;ddl->提醒时间
    @Column(name = "reminder_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderDate;



}
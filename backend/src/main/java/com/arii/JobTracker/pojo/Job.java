package com.arii.JobTracker.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "submissions")
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //Learning:---新增---
    @Column(name ="user_id",nullable = false)
    private  Integer userId;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;
    //---
    private String company;
    private Integer status;
    private String position;
    private String applyDate;

    private String tags;
    @Column(name = "update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    @PrePersist
    protected void onCreate() {
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }




}
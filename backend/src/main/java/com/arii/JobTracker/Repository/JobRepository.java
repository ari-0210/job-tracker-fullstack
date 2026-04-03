package com.arii.JobTracker.Repository;

import com.arii.JobTracker.pojo.Job;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("jpa")
public interface JobRepository extends JpaRepository<Job, Integer> {
    // learn;searchTerm:company、tags (IgnoreCase),联合条件：(关键词 A OR 关键词 B) AND 用户ID
    Page<Job> findByUserIdAndCompanyContainingIgnoreCaseOrUserIdAndTagsContainingIgnoreCase(
            Integer userId1, String companySearch,
            Integer userId2, String tagsSearch,
            Pageable pageable
    );

    Page<Job> findByUserId(Integer userId, Pageable pageable);

    @Query("SELECT j.status, COUNT(j) FROM Job j WHERE j.userId = :userId GROUP BY j.status")
    List<Object[]> countJobsByStatus(@Param("userId") Integer userId);

    // learn;统计某个用户的所有记录总数
    long countByUserId(Integer userId);


    //learn;ddl
    @Query("SELECT COUNT(j) FROM Job j WHERE j.userId = :userId AND j.deadline BETWEEN :start AND :end")
    long countByDeadlineRange(
            @Param("userId") Integer userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    //截至事项
    List<Job> findTop5ByUserIdAndDeadlineBetweenOrderByDeadlineAsc(
            Integer userId, LocalDateTime start, LocalDateTime end
    );
    // learn;自定义删除 只有 id 在列表中 且 user_id 等于当前用户的才会被删
    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.id IN :ids AND j.userId = :userId")
    void deleteByIdsAndUserId(@Param("userId") Integer userId, @Param("ids") List<Integer> ids);
}
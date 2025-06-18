package com.arii.JobTracker.Repository;

import com.arii.JobTracker.pojo.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface JobRepository extends JpaRepository<Job, Integer> {
    // searchTerm:company、position、tags (IgnoreCase)
    Page<Job> findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrTagsContainingIgnoreCase(
            String companySearch,
            String positionSearch,
            String tagsSearch,
            Pageable pageable
    );
}
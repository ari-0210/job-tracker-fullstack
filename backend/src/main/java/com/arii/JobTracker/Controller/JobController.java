package com.arii.JobTracker.Controller;

import com.arii.JobTracker.Service.JobService;
import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;


    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        Job createdJob = jobService.createJob(job);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdJob.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdJob);
    }



    @GetMapping
    public ResponseEntity<Page<Job>> getAllJobs(
            @RequestParam(name = "search", required = false) String searchTerm,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {
        Page<Job> jobsPage = jobService.findAllJobs(page, size , searchTerm );
        return ResponseEntity.ok(jobsPage);
    }


    @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteJob(@PathVariable Integer id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/batch-delete")
    public ResponseEntity<Void> deleteMultipleJobs(@RequestBody Map<String, List<Integer>> payload) {
        List<Integer> idsToDelete = payload.get("ids");
        if (idsToDelete == null || idsToDelete.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        jobService.deleteJobsByIds(idsToDelete);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
        public ResponseEntity<Job> updateJob(@PathVariable Integer id, @RequestBody Job job) {
            Job updatedJob = jobService.updateJob(job);
            return ResponseEntity.ok(updatedJob);
    }
}
package com.arii.JobTracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@MapperScan("com.arii.JobTracker.Mapper")
@SpringBootApplication
public class JobTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobTrackerApplication.class, args);
	}

}

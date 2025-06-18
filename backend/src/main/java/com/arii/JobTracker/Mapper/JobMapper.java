package com.arii.JobTracker.Mapper;

import com.arii.JobTracker.pojo.Job;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Mapper
@Profile("mybatis")
public interface JobMapper {
    @Select("SELECT * FROM job")
    List<Job> list();

//    @Delete("DELETE FROM job WHERE id =#{id}")
//    public void delete(Integer id);
}
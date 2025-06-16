package com.arii.JobTracker.Repository;


import com.arii.JobTracker.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username); // 根据用户名查找用户
}
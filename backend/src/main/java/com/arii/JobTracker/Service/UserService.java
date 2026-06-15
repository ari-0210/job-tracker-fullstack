package com.arii.JobTracker.Service;

import com.arii.JobTracker.DTO.UserCreateRequestDto;
import com.arii.JobTracker.Repository.UserRepository;
import com.arii.JobTracker.pojo.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(UserCreateRequestDto userDto) {
        //  learn;检查用户名是否已存在
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        //  learn;创建新的 User 实体对象
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        //  learn;对密码进行哈希加密
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        //  learn;设置角色 (从 DTO 获取，或者给一个默认角色)
        newUser.setRoles(userDto.getRoles());

        //  learn;保存到数据库
        return userRepository.save(newUser);
    }

    //learn;Users实体
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
    }
}
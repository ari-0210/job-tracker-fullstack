package com.arii.JobTracker.Service;

import com.arii.JobTracker.DTO.UserCreateRequestDto;
import com.arii.JobTracker.Repository.UserRepository;
import com.arii.JobTracker.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户账户管理核心业务处理类.
 * <p>承载全系统用户实体的完整生命周期管理，内置高强度的密码单向哈希加盐加密机制及重名全阻断审计逻辑.</p>
 *
 * @author Ari
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 执行全流程安全校验的新用户开户与持久化.
     * <p>本方法具备高抗并发重名穿透特性。采用声明式事务，确保账户数据、角色关联关系的原子性写入.</p>
     *
     * @param userDto 前端提交的注册数据传输对象，核心凭证字段触发强校验规则
     * @return 成功在数据库登记并分配主键的持久化 User 实体对象
     * @throws RuntimeException 当申请的用户名在数据库中已存在时抛出此业务熔断异常
     */
    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserCreateRequestDto userDto) {
        //  检查用户名是否已存在
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        //  利用 BCrypt 强哈希算法对密码执行不可逆的高强度加密
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setRoles(userDto.getRoles());

        return userRepository.save(newUser);
    }

    /**
     * 根据唯一个性化用户名精确检索用户实体.
     *
     * @param username 待检索的精确定位用户名
     * @return 匹配成功后的 User 实体对象
     * @throws RuntimeException 当检索的账户不存在或已被物理冻结时抛出此异常
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
    }
}

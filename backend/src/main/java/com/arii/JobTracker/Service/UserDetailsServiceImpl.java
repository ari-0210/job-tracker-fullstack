package com.arii.JobTracker.Service;

import com.arii.JobTracker.Repository.UserRepository;
import com.arii.JobTracker.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 将 User 实体转换为 Spring Security 的 UserDetails 对象
        // AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()) 用于将逗号分隔的角色字符串转换为权限列表
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles())
        );
    }
}


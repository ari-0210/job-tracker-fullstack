package com.arii.JobTracker.Security;

import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    private UserService userService;

    public Integer getCurrentUserId() {
        // learn; 从上下文掏出认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // learn; 查出完整的用户对象，拿到真正的数据库 ID
            User user = userService.findByUsername(username);
            if (user != null) {
                return user.getId();
            }
        }
        throw new RuntimeException("当前未登录或身份已过期");
    }
}

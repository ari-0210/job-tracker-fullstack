package com.arii.JobTracker.Security;

import com.arii.JobTracker.Service.UserService;
import com.arii.JobTracker.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 基于 ThreadLocal 本地线程变量隔离的分布式会话安全上下文瞬时萃取工具类.
 * <p>用作在 Service 内部便捷安全地搜刮出当前执行该核心业务的登录人身份详情.</p>
 *
 * @author Ari
 */
@Component
public class SecurityUtils {

    @Autowired
    private UserService userService;

    /**
     * 从当前请求绑定的本地隔离线程上下文中，溯源并捕获当前登录人在数据库的真实唯一主键ID.
     * <p>本工具是全项目防水平越权的最核心防线。凡涉及根据用户区分隔离的业务，严禁依赖前端传参，一律调用此方法从内核提取.</p>
     *
     * @return 当前登录人的真实整型自增主键 ID
     * @throws RuntimeException 当会话断开、未携带 Token 强行闯入，或 ThreadLocal 上下文发生溢出失效时抛出此异常
     */
    public Integer getCurrentUserId() {
        //从当前请求被分配的独立工作线程(ThreadLocal)中捞取由拦截器预先打入的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
// 强校验认证凭证存在性以及其底层载荷是否派生自标准的 UserDetails
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
// 联动 UserRepository 锁定底层关系数据库的核心自增物理主键 ID
            User user = userService.findByUsername(username);
            if (user != null) {
                return user.getId();
            }
        }
        throw new RuntimeException("当前未登录或身份已过期");
    }
}

package camellia.service;

import camellia.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 24211
* @description 针对表【user】的数据库操作Service
* @createDate 2024-07-10 16:10:37
*/
public interface UserService extends IService<User> {
    /**
     * 注册逻辑校验
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param checkPassword 用户确定密码
     * @return
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登入
     * @param userAccount 账户
     * @param userPassword 密码
     * @return 返回脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
}

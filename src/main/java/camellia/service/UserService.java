package camellia.service;

import camellia.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

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
}

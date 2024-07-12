package camellia.service;

import camellia.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 24211
* @description 针对表【user】的数据库操作Service
* @createDate 2024-07-10 16:10:37
*/
public interface UserService extends IService<User> {



    /**
     * 注册逻辑校验
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 用户确定密码
     * @param planetCode
     * @return
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登入
     * @param userAccount 账户
     * @param userPassword 密码
     * @return 返回脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 查询所有用户
     * @return 用户集合
     */
    List<User> searchUsers(String username);


    /**
     * 通过id删除用户
     * @param id
     * @return 返回true/false
     */
    boolean deleteUser(long id);

    User getSafetyUser(User user);


    /**
     * 请求用户注销
     * @param request
     * @return
     */
    Integer userLogOut(HttpServletRequest request);
}

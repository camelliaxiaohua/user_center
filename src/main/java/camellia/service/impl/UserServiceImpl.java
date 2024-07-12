package camellia.service.impl;

import camellia.common.ErrorCode;
import camellia.constant.UserConstant;
import camellia.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import camellia.model.User;
import camellia.service.UserService;
import camellia.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户服务实现类
* @author 24211
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-10 16:10:37
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //密码加密——加盐
    private static final String SALT = "camellia";


    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册校验
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 用户确定密码
     * @param planetCode 用户编号
     * @return
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户编号过长");
        }
        //校验账户不能包含特殊字符,定义只能包含字母、数字和下划线的正则表达式。
        String validPattern = "^[a-zA-Z0-9_]*$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // 如果找到特殊字符，返回 -1L
        }
        //密码和确认密码不同
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //账户不能重复(放在校验最后，减少性能浪费。)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户名已被使用");
        }

        //用户编号账户不能重复(放在校验最后，减少性能浪费。)
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planet_code", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setIsDelete((short) 0); //设置逻辑值，防止SQL查询出问题。
        int saveResult = userMapper.insert(user);
        if (saveResult == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return user.getId();
    }

    /**
     * 用户登入逻辑实现
     *
     * @param userAccount  账户
     * @param userPassword 密码
     * @return 脱敏后的用户账户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        //校验账户不能包含特殊字符,定义只能包含字母、数字和下划线的正则表达式。
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号包含非法字符");// 如果找到特殊字符，返回 -1L
        }

        //2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在或者账号密码错误。
        if (user == null){
            log.info("user login fail, userAccount cannot match userPassword. account {} password {}", userAccount, userPassword);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或者账号密码错误");
        }

        //4.用户脱敏
        User safetyUser = getSafetyUser(user);
        //5.用户登入成功，设置登入成功的session。
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 通过用户昵称，模糊查询。
     * @param username
     * @return
     */
    @Override
    public List<User> searchUsers(String username) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        if (StringUtils.isAnyBlank(username)) {
            queryWrapper.like("username", username);
        }
        queryWrapper.select("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        return users;
    }


    /**
     * 通过id实现逻辑上的删除 <br>
     * 实际上就是查询要删除的id，将这个用户isDelete更新为1。
     * @param id
     * @return
     */
    @Override
    public boolean deleteUser(long id) {
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"ID为空");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        // 设置逻辑删除标记值，这里假设1表示已删除
        updateWrapper.set("is_delete", 1);
        int rows = userMapper.update(null, updateWrapper);
        return rows > 0;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){
        if (user == null) return null;
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPlanetCode(user.getPlanetCode());
        return safetyUser;
    }


    /**
     * 用户注销实现
     * @param request
     */
    @Override
    public Integer userLogOut(HttpServletRequest request) {
        //移除用户登入态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }
}




package camellia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import camellia.model.User;
import camellia.service.UserService;
import camellia.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户服务实现类
* @author 24211
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-10 16:10:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册校验
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param checkPassword 用户确定密码
     * @return
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1L;
        }
        if(userAccount.length()<4) return -1L;
        if (userPassword.length()<8) return -1L;
        //校验账户不能包含特殊字符,定义只能包含字母、数字和下划线的正则表达式。
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1L; // 如果找到特殊字符，返回 -1L
        }
        //密码和确认密码不同
        if (!checkPassword.equals(userPassword)) return -1L;
        //账户不能重复(放在校验最后，减少性能浪费。)
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count>0) {
            return -1L;
        }

        //2. 密码加密
        final String SALT = "camellia"; //加盐
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        int saveResult = userMapper.insert(user);
        if(saveResult == 0) return  -1L;
        return user.getId();
    }
}





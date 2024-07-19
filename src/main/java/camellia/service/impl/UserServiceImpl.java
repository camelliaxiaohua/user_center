package camellia.service.impl;

import camellia.common.ErrorCode;
import camellia.constant.UserConstant;
import camellia.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import camellia.model.User;
import camellia.service.UserService;
import camellia.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
* @author 24211
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-07-10 16:10:37
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 密码加密——加盐
     */
    private static final String SALT = "camellia";

    /**
     *
     */
    @Autowired
    private UserMapper userMapper;

    /**
     *
     */
    @Autowired
    private RedisTemplate redisTemplate;


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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名只能是大小写字母和下划线。"); // 如果找到特殊字符，返回 -1L
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

        //用户编号账户不能重复
        // todo 设置用户注册自动生成随机的5为编号码
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
        safetyUser.setTags(user.getTags());
        safetyUser.setProfile(user.getProfile());
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


    /**
     * 根据标签搜索用户
     * @param tagNameList 用户要拥有的标签
     * @return 返回安全对象
     */

    @Deprecated
    @Override
    public List<User> searchUserByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的标签为空。");
        }
        log.info("使用数据库查询：==============================");
        //记录开始时间
        long start = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的标签为空。");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        List<User> safetyUsers = users.stream().map(this::getSafetyUser).collect(Collectors.toList());
        //记录结束时间
        long end = System.currentTimeMillis();
        log.info("查询消耗时间为：  数据大小为："+(end-start)+"ms", tagNameList.size());
        return safetyUsers;
    }


    /**
     * 通过内存查询
     * @param tagNameList
     * @return
     */

    @Override
    public List<User> searchUserByTagsByRAM(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入的标签为空。");
        }
        //1.先查询所有的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2. 在内存中判断是否包含要求的标签
        return users.stream().filter(user->{
            if (StringUtils.isBlank(user.getTags())) return false;
            String tagsStr = user.getTags();
            // 使用 TypeToken<List<String>> 来反序列化 JSON 字符串为 List<String>
            //List<String> tempTagNameList = gson.fromJson(tagsStr, new TypeToken<List<String>>() {}.getType());
            //在新的gson中集合的类型传递进行了优化，不用再getType()拿类型了。
            List<String> tempTagNameList = gson.fromJson(tagsStr, new TypeToken<List<String>>(){});
            // 将 List 转换为 Set
            Set<String> tagNameSet = new HashSet<>(tempTagNameList);
            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) return false;
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }


    /**
     * <h6>用户更新操作</h6>
     * <p>1. 传来用户和session存储的用户信息相同，则是用户本人，可以进行修改信息，但仅限本人。</p>
     * <p>2. 传来的用户信息和session存储的用户信息不同，则需要判断是否为管理员，如果是可以修改其他用户。</p>
     * @param user 要修改的用户及信息。
     * @param userInfo session中存储的用户信息（当前登入用户信息）
     * @return 修改影响的数据
     */
    @Deprecated
    @Override
    public Integer updateUser(User user, User userInfo) {
        // 获取用户ID
        long userId = user.getId();
        long userInfoId = userInfo.getId();
        // 参数校验
        if (userId <= 0 || userInfoId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        //todo  补充校验，前端传空处理。

        // 用户权限判定
        if (isAdmin(userInfo) || userId == userInfoId) {
            User oldUser = userMapper.selectById(userId);
            if (oldUser == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
            }
            // 执行更新
            int updateCount = userMapper.updateById(user);
            return updateCount;
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对不起，你没有权限");
        }
    }


    /**
     * <h6>获取session中用户信息</h6>
     * @param request
     * @return 当前登入用户的信息。
     */
    @Override
    public User getUserLoginInfo(HttpServletRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未登入");
        }
        return (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    }


    /**
     * <h6>是否为管理员</h6>
     * @param request
     * @return true/false
     */
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user ==null || user.getUserRole() == UserConstant.DEFAULT_ROLE;
    }


    /**
     * <h6>(重载方法)是否为管理员</h6>
     * @param userLoginInfo
     * @return true/false
     */
    public boolean isAdmin(User userLoginInfo) {
        return userLoginInfo !=null && userLoginInfo.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     *
     * @param pageSize
     * @param pageNum
     * @param request
     * @return
     */
    @Override
    public Page<User> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        // todo 用户不用登入也可以查看？
        // 获取当前登录用户的信息
        User loginUser = getUserLoginInfo(request);
        String redisKey = String.format("camellia:user:recommend:%s", loginUser.getId());
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        //如果有缓存就加载缓存
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if(userPage != null){
            return userPage;
        }
        //无缓存，直接接查数据库。
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        //写缓存
        try{
            valueOperations.set(redisKey,userPage,10000, TimeUnit.MICROSECONDS);
        }catch (Exception e){
            log.error("redis set key error",e);
        }
        return userPage;
    }

}




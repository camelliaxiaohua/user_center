package camellia.controller;

import camellia.constant.UserConstant;
import camellia.model.User;
import camellia.model.request.UserLoginRequest;
import camellia.model.request.UserRegisterRequest;
import camellia.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Datetime: 2024/7/11上午10:03
 * @author: Camellia.xioahua
 * @RestController 适用于编写restful风格的api，返回值默认为json类型。
 */

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) return null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) return  null;
        Long id = userService.userRegister(userAccount, userPassword, checkPassword);
        return id;
    }

    @PostMapping("login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("前端发来的：userLoginRequest: {} request: {}", userLoginRequest,request);
        if (userLoginRequest == null) return null;
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)) return  null;
        User user = userService.userLogin(userAccount, userPassword, request);
        return user;
    }


    @GetMapping("/search")
    public List<User> searchUsers(String username,HttpServletRequest request) {
        //仅管理员可查询
        if (isAdmin(request)) return new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return list;
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id , HttpServletRequest request) {
        //仅管理员可删除。
        if(isAdmin(request))return false;
        //可以直接userService.removeById(id);
        boolean flag = userService.deleteUser(id);
        return flag;
    }

    /**
     * 是否为管理员
     * @param request
     * @return true/false
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user ==null || user.getUserRole() == UserConstant.DEFAULT_ROLE;
    }

}

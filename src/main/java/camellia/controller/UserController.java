package camellia.controller;

import camellia.common.BaseResponse;
import camellia.common.ErrorCode;
import camellia.common.ResultUtils;
import camellia.constant.UserConstant;
import camellia.exception.BusinessException;
import camellia.model.domain.User;
import camellia.model.request.UserLoginRequest;
import camellia.model.request.UserRegisterRequest;
import camellia.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Datetime: 2024/7/11上午10:03
 * @author: Camellia.xioahua
 * @RestController 适用于编写restful风格的api，返回值默认为json类型。
 */

@RestController
@RequestMapping("/user")
@Slf4j
//@CrossOrigin(origins = {"http://localhost:3000"})
@Tag(name = "hello")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * <h6>用户注册</h6>
     *
     * @param userRegisterRequest 封装的用户请求体
     * @return 注册id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        //将请求封装成UserRegisterRequest
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        //封装结果
        return ResultUtils.success(id);
    }



    /**
     * <h6>用户登录</h6>
     *
     * @param userLoginRequest
     * @param request
     * @return 用户登入信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("前端发来的：userLoginRequest: {} request: {}", userLoginRequest,request);
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户或者密码为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);

    }



    /**
     * <h6>用户注销</h6>
     *
     * @param request
     * @return 1-退出成功
     */

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogOut(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer i = userService.userLogOut(request);
        return ResultUtils.success(i);
    }



    /**
     * <h6>获取当前登录的用户信息</h6>
     *
     * @param request HttpServletRequest 对象，用于获取当前会话中的用户信息
     * @return 返回封装了用户信息的 BaseResponse 对象
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        //获取的登入用户信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = currentUser.getId();
        // 校验用户是否合法
        User user = userService.getById(userId);
        if(user.getIsDelete()==1)throw new BusinessException(ErrorCode.PARAMS_ERROR,"该账户状态异常");
        return ResultUtils.success(user);
    }



    /**
     * <h6>根据用户名搜索用户信息。</h6>
     *
     * @param username 要搜索的用户名，如果为空则返回所有用户
     * @param request HttpServletRequest 对象，用于获取请求上下文和检查用户权限
     * @return 封装了用户信息的 BaseResponse 对象，包含符合条件的用户列表
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request) {
        //仅管理员可查询
        if (userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户权限不足");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 如果用户名不为空，则添加模糊查询条件。
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        // 执行查询操作，获取用户列表。（没有数据则查所有）
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }



    /**
     * <h6>根据用户 ID 删除用户。</h6>
     *
     * @param id 用户的唯一标识符，即用户 ID
     * @param request HttpServletRequest 对象，用于获取请求上下文并检查用户权限
     * @return 封装了操作结果的 BaseResponse 对象，包含删除操作是否成功的布尔值
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id , HttpServletRequest request) {
        // 检查当前用户是否是管理员。
        // todo 添加用户自己可以删除账户（session用户信息是否和检索ID用户信息一致）
        if(userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //可以直接userService.removeById(id);
        Boolean flag = userService.deleteUser(id);
        return ResultUtils.success(flag);
    }



    /**
     * <h6>根据标签搜索用户。</h6>
     *
     * @param tagNameList 用户标签列表，可以为空。
     * @return 封装了用户列表的 BaseResponse 对象。
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        // 检查传入的标签列表是否为空
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入标签为空");
        }
        // 根据标签列表在内存中搜索用户
        List<User> users = userService.searchUserByTagsByRAM(tagNameList);
        return ResultUtils.success(users);
    }



    /**
     * <h6>推荐用户列表。</h6>
     *
     * @param pageSize 每页的用户数量
     * @param pageNum 当前页码
     * @param request HTTP 请求对象
     * @return 包含用户分页数据的 BaseResponse 对象
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request) {
        // 校验参数是否合法
        if(pageSize <= 0 || pageNum <= 0 || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用服务层方法获取推荐用户列表
        Page<User> userPage = userService.recommendUsers(pageSize, pageNum, request);
        return ResultUtils.success(userPage);
    }



    /**
     * 更新用户信息。
     *
     * @param user 包含要更新的用户信息的请求体
     * @param request HTTP 请求对象，用于获取当前登录用户的信息
     * @return 更新操作的结果，包含受影响的记录数
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        //1. 校验参数是否为空
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户为空，请输入要修改的用户。");
        }
        //2. 校验用户信息
        User userInfo = userService.getUserLoginInfo(request);
        Integer result = userService.updateUser(user,userInfo);
        return ResultUtils.success(result);
    }

}



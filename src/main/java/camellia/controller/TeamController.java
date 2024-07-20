package camellia.controller;

import camellia.common.BaseResponse;
import camellia.common.ErrorCode;
import camellia.common.ResultUtils;
import camellia.exception.BusinessException;
import camellia.model.domain.Team;
import camellia.model.domain.User;
import camellia.model.dto.TeamAddRequest;
import camellia.model.dto.TeamQuery;
import camellia.service.TeamService;
import camellia.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 队伍接口
 * @Datetime: 2024/7/20上午9:13
 * @author: Camellia.xioahua
 */
@RestController
@Slf4j
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;


    /**
     * <h6>创建新队伍</h6>
     *
     * @param teamAddRequest 包含团队创建信息的请求体对象，不能为空。
     * @param request        HTTP请求对象，用于获取当前用户的登录信息。
     * @return 包含新创建团队ID的响应对象。
     * @throws BusinessException 如果请求参数为空，则抛出参数错误异常。
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 检查请求参数是否为空
        if (teamAddRequest == null) {
            // 如果参数为空，抛出业务异常，表示参数错误
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前用户的登录信息
        User userLoginInfo = userService.getUserLoginInfo(request);

        // 创建一个新的Team对象，并将请求体中的属性复制到该对象中
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);

        // 调用teamService的addTeam方法，将新团队和用户信息传递进去，返回新团队的ID
        long teamId = teamService.addTeam(team, userLoginInfo);

        // 返回包含新团队ID的成功响应
        return ResultUtils.success(teamId);
    }



    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestParam Long id) {
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean saved = teamService.removeById(id);
        if (!saved){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }



    @PutMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateById(team);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }




    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }



    @GetMapping("/list")
    public BaseResponse<List<Team>> listTeams(TeamQuery teamQuery) {
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team, teamQuery);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtils.success(teamList);
    }


    /**
     * 分页查询团队列表
     *
     * @param teamQuery 查询参数，包含分页信息和查询条件
     * @return 包含分页结果的响应
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage( TeamQuery teamQuery) {
        // 检查查询参数是否为空
        if (teamQuery == null) {
            // 如果为空，抛出参数错误异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建一个 Team 对象
        Team team = new Team();
        // 将 teamQuery 对象的属性复制到 team 对象
        BeanUtils.copyProperties( teamQuery,team);

        // 创建分页对象，设置当前页码和每页大小
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());

        // 创建查询包装器，用于构造查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);

        // 使用分页对象和查询包装器执行分页查询
        Page<Team> resultPage = teamService.page(page, queryWrapper);

        // 返回包含分页结果的成功响应
        return ResultUtils.success(resultPage);
    }

}

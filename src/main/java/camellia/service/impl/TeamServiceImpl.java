package camellia.service.impl;

import camellia.common.ErrorCode;
import camellia.exception.BusinessException;
import camellia.model.domain.User;
import camellia.model.domain.UserTeam;
import camellia.model.enums.TeamStatusEnum;
import camellia.service.UserTeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import camellia.model.domain.Team;
import camellia.service.TeamService;
import camellia.mapper.TeamMapper;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author 24211
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-07-20 09:02:12
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * <h6>添加新团队</h6>
     * <p>1. 用户在应用中发起创建团队的请求，需要在数据库中保存团队的基本信息。
     * 这些信息需要存储在Team表中，以便后续能够对团队进行查询、更新和管理。</p>
     * <p>2. 当用户创建一个新团队后，需要记录用户与该团队的关系。
     * 这一关系存储在UserTeam表中。这样可以明确哪个用户创建了哪个团队，或者用户加入了哪些团队。</p>
     *
     * @param team      包含新团队信息的对象，不能为空。
     * @param loginUser 当前登录的用户信息，不能为空。
     * @return 新创建的团队ID。
     * @throws BusinessException 如果请求参数无效，或者团队创建失败，则抛出业务异常。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        final long userId = loginUser.getId();
        Long teamId = team.getId();

        // 获取分布式锁
        RLock lock = redissonClient.getLock("camellia:team:add:lock");
        try {
            // 尝试获取锁，等待时间为0，锁过期时间为30秒
            if (lock.tryLock(30000L, -1, TimeUnit.MILLISECONDS)) {

                // 检查请求参数是否为空
                if (team == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
                // 检查用户是否已登录，未登录则抛出异常
                if (loginUser == null) {
                    throw new BusinessException(ErrorCode.NOT_LOGIN);
                }

                // 校验团队信息
                // 检查队伍人数是否在合理范围内（>1且<=20）
                Integer maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
                if (maxNum < 1 || maxNum > 20) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
                }

                // 检查队伍标题是否为空或长度是否超过20个字符
                String name = team.getName();
                if (StringUtils.isBlank(name) || name.length() > 20) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
                }

                // 检查队伍描述长度是否超过512个字符
                String description = team.getDescription();
                if (StringUtils.isNotBlank(description) && description.length() > 512) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述为空或者过长");
                }

                // 检查队伍状态是否有效
                int status = Optional.ofNullable(team.getStatus()).orElse(0);
                TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
                if (statusEnum == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
                }

                // 如果队伍是加密状态，检查密码是否有效
                String password = team.getPassword();
                if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不正确");
                }

                // 检查过期时间是否有效（超时时间必须大于当前时间）
                Date expireTime = team.getExpireTime();
                if (new Date().after(expireTime)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不符");
                }

                // 检查用户创建的队伍数量是否超过限制（每个用户最多创建5个队伍）
                // todo 注意：该逻辑存在潜在bug，可能会导致并发创建多个队伍。建议通过加锁或Redis解决并发问题。
                QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                long hasTeamNum = this.count(queryWrapper);
                if (hasTeamNum >= 5) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
                }

                // 插入新的队伍信息
                // todo 设置为自动生成不重复的的id
                team.setId(null);
                team.setUserId(userId);
                boolean result = this.save(team);
                if (!result || teamId == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
                }

                // 插入用户与队伍的关系到关系表
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                result = userTeamService.save(userTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放锁，确保锁是当前线程持有的
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
        // 返回新创建的队伍ID
        return teamId;
    }

}

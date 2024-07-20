package camellia.service;

import camellia.model.domain.Team;
import camellia.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 24211
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-07-20 09:02:12
*/
public interface TeamService extends IService<Team> {

    /**
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser) ;

}

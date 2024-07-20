package camellia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import camellia.model.domain.UserTeam;
import camellia.service.UserTeamService;
import camellia.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 24211
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-07-20 09:04:09
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}





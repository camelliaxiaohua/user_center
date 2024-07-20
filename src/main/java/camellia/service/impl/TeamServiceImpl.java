package camellia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import camellia.model.domain.Team;
import camellia.service.TeamService;
import camellia.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 24211
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-07-20 09:02:12
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}





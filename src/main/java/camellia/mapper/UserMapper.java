package camellia.mapper;

import camellia.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24211
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-07-10 16:10:37
* @Entity camellia.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}





package camellia.job;

import camellia.mapper.UserMapper;
import camellia.model.User;
import camellia.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Datetime: 2024/7/18下午3:11
 * @author: Camellia.xioahua
 */
@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1813421726107029505L);

    @Scheduled(cron = "00 45 15 * * *")
    public void doCahceRecommendUser() {
        for (Long userId : mainUserList) {
            QueryWrapper queryWrapper = new QueryWrapper();
            Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
            String redisKey = String.format("camellia:user:recommend:%s", userId);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            //写缓存
            try{
                valueOperations.set(redisKey,userPage,10000, TimeUnit.MICROSECONDS);
                log.info("定时任务开始执行", userPage.getTotal());
            }catch (Exception e){
                log.error("redis set key error",e);
            }
        }
    }
}

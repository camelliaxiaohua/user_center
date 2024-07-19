package camellia.job;

import camellia.mapper.UserMapper;
import camellia.model.User;
import camellia.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <h6>定时任务类：</h6>
 *
 * 用于预缓存用户推荐数据。
 *
 * @Datetime: 2024/7/18 下午3:11
 * @Author: Camellia.xiaohua
 */
@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    // 重点用户ID列表
    // TODO 添加重点用户字段，查询符合用户并添加缓存
    private List<Long> mainUserList = Arrays.asList(1813421726107029505L);

    /**
     * 定时任务方法，每天15:45执行一次。
     * 方法内实现了对重点用户推荐数据的预缓存逻辑。
     */
    @Scheduled(cron = "00 55 14 * * *")
    public void doCacheRecommendUser() {
        // 获取分布式锁
        RLock lock = redissonClient.getLock("camellia:precachejob:docache:lock");
        try {
            // 尝试获取锁，等待时间为0，锁过期时间为30秒
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: "+Thread.currentThread().getName());
                // 遍历重点用户列表，获取每个用户的推荐数据并缓存到Redis
                for (Long userId : mainUserList) {
                    // 创建查询条件
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    // 分页查询用户数据，获取前20条记录
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    // 构造Redis缓存键
                    String redisKey = String.format("camellia:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写入缓存
                    try {
                        valueOperations.set(redisKey, userPage, 10000, TimeUnit.MILLISECONDS);
                        // 记录日志，表示任务执行成功
                        log.info("定时任务开始执行，缓存用户推荐数据成功，总数：{}", userPage.getTotal());
                    } catch (Exception e) {
                        // 记录日志，表示缓存写入发生错误
                        log.error("Redis缓存写入错误", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            // 处理获取锁过程中发生的异常
            log.error("获取分布式锁时发生异常", e);
        } finally {
            // 释放锁，确保锁是当前线程持有的
            if (lock.isHeldByCurrentThread()) {
                System.out.println("getLock: "+Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }
}

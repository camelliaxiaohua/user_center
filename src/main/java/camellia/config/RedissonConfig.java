package camellia.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * <h6>Redisson 配置类:</h6>
 * 用于配置 RedissonClient 实例。
 *
 * @Datetime: 2024/7/19 上午11:36
 * @Author: Camellia.xiaohua
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissonConfig {

    // Redis 主机地址
    private String host;

    // Redis 端口号
    private String port;

    // Redis 数据库索引
    private Integer redisson_database;

    /**
     * 创建 RedissonClient Bean。
     *
     * @return RedissonClient 实例
     * @throws IOException 如果配置文件加载失败
     */
    @Bean
    public RedissonClient redisson() throws IOException {
        // 1. 创建配置对象
        Config config = new Config();
        // 构造 Redis 地址
        String redisAddress = String.format("redis://%s:%s", host, port);
        // 配置单节点服务器地址和数据库
        config.useSingleServer().setAddress(redisAddress).setDatabase(redisson_database);
        // 2. 创建 RedissonClient 实例
        return Redisson.create(config);
    }
}

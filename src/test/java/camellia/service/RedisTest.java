package camellia.service;

import camellia.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * <h6>redis增删改查实现</h6>
 * @Datetime: 2024/7/18上午11:11
 * @author: Camellia.xioahua
 */
@SpringBootTest
public class RedisTest {

    /**
     * 引入Spring Data的Redis操作对象
     */
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testCURD(){
        //增
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("hello", "world");
        valueOperations.set("double",2.300);
        valueOperations.set("int",7);
        valueOperations.set("user",new User());
        //查
        Object object = valueOperations.get("hello");
        Assertions.assertTrue("world".equals(object));
        double d = (double) valueOperations.get("double");
        Assertions.assertTrue(d==2.300);
        int a = (int) valueOperations.get("int");
        Assertions.assertTrue(a==7);
        User user = (User) valueOperations.get("user");
        Assertions.assertTrue("user".equals(user));
        valueOperations.getAndDelete("user");
    }

}

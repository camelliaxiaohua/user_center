package camellia.service;
import java.util.Date;

import camellia.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 * @Datetime: 2024/7/10下午4:17
 * @author: Camellia.xioahua
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("Camellia");
        user.setUserAccount("123");
        user.setAvatarUrl("https://camelliaxiaohua-1313958787.cos.ap-shanghai.myqcloud.com/background%2FIMG_2131.JPG");
        user.setGender(0);
        user.setUserPassword("24211");
        user.setPhone("12345");
        user.setEmail("123@qq.com");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

}
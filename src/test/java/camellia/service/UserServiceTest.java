package camellia.service;

import camellia.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


/**
 * 用户服务测试
 * @Datetime: 2024/7/10下午4:17
 * @author: Camellia.xioahua
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    /**
     * Mybatis-Plus SQL语句测试
     */
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

    /**
     * 用户校验测试
     */
    @Test
    void userRegister() {
        //有空字段
        String username = "XIAOHUA";
        String password = "";
        String checkPassword = "242118888";
        String planetCode = "12345";
        Long result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertEquals(-1,result);
        //用户名<4位
        username="XI";
        result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertEquals(-1,result);
        //密码<8位
        username = "XIAOHUA";
        password = "24211";
        checkPassword = "24211";
        result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertEquals(-1,result);
        //用户名相同
        username = "Camellia";
        password = "242118888";
        checkPassword = "242118888";
        result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertEquals(-1,result);
        //包含特殊字符
        username = "Came**llia";
        password = "242118888";
        checkPassword = "242118888";
        result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertEquals(-1,result);
        //二次确认密码不一致
        username = "XiaoHua";
        password = "242118888";
        checkPassword = "242116666";
        result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertEquals(-1,result);
        //成功数据
        username = "Camellia_xiaohua";
        password = "242118888";
        checkPassword = "242118888";
        result = userService.userRegister(username, password, checkPassword, planetCode);
        Assertions.assertTrue(result>0);
    }

    @Test
    void searchUserByTags() {

        List<String> tagNameList = Arrays.asList("java","pathon","c++");
        List<User> users = userService.searchUserByTagsByRAM(tagNameList);
        Assertions.assertNotNull(users);
    }

}
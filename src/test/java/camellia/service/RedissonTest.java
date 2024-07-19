package camellia.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Datetime: 2024/7/19下午12:07
 * @author: Camellia.xioahua
 */
@SpringBootTest
public class RedissonTest {

    @Autowired
    RedissonClient redisson;

    @Test
    public void testRedisson() {
        //List
        RList<String> rList = redisson.getList("test-list");
        rList.add("camellia");
        rList.add("xiaohua");
        System.out.println(rList.get(0));
        System.out.println(rList.get(1));
        rList.remove(0);
        rList.remove(1);
    }

    @Test
    void testWatchDog() {
        RLock lock = redisson.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}

package camellia;

import camellia.once.ScheduledTasks;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Datetime: 2024/7/18下午9:09
 * @author: Camellia.xioahua
 */
@SpringBootTest
@Component
public class ScheduledTasksTest {

    @Autowired
    ScheduledTasks scheduledTasks;

    @Test
    public void testPerFormTask() throws InterruptedException {
        scheduledTasks.perFormTask();

        //因为SpringBoot上下文会在Test类执行完毕就关闭，为了ScheduledTasks执行效果直观，推迟Test类的结束。
        Thread.sleep(20000000);
    }
}

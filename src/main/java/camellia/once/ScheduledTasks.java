package camellia.once;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Datetime: 2024/7/18下午9:16
 * @author: Camellia.xioahua
 */

@Component
public class ScheduledTasks{

    @Scheduled(fixedRate = 5000)
    public void perFormTask() throws InterruptedException {

        System.out.println("testPerFormTask is execute："+System.currentTimeMillis());
    }

}

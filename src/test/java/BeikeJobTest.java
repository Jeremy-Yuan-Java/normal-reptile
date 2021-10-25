import com.jeremy.normal.EasyApplication;
import com.jeremy.normal.backend.BeikeJobTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = EasyApplication.class)
@RunWith(SpringRunner.class)
public class BeikeJobTest {


    @Resource
    private BeikeJobTestService beikeJobTestService;

    @Test
    public void startTest(){
        beikeJobTestService.run();
    }


}

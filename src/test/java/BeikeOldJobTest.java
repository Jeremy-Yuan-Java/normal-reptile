import com.jeremy.normal.EasyApplication;
import com.jeremy.normal.backend.BeikeJobTestService;
import com.jeremy.normal.backend.BeikeOldJobTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = EasyApplication.class)
@RunWith(SpringRunner.class)
public class BeikeOldJobTest {


    @Resource
    private BeikeOldJobTestService beikeOldJobTest;

    @Test
    public void startTest(){
        beikeOldJobTest.run();
    }


}

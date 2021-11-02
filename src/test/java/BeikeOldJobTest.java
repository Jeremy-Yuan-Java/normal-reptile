import com.jeremy.normal.EasyApplication;
import com.jeremy.normal.backend.SecondHandCommunityReptile;
import com.jeremy.normal.backend.SecondHandHousingReptile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = EasyApplication.class)
@RunWith(SpringRunner.class)
public class BeikeOldJobTest {


    @Resource
    private SecondHandHousingReptile secondHandHousingReptile;

    @Resource
    private SecondHandCommunityReptile secondHandCommunityReptile;


    @Test
    public void startSecondHandHousingReptileTest(){
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandCommunityReptileTest(){
        secondHandCommunityReptile.run();
    }

}

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

        secondHandHousingReptile.setMin(0);
        secondHandHousingReptile.setMax(500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest2(){
        secondHandHousingReptile.setMin(500);
        secondHandHousingReptile.setMax(1000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest3(){

        secondHandHousingReptile.setMin(1000);
        secondHandHousingReptile.setMax(1500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest4(){

        secondHandHousingReptile.setMin(1500);
        secondHandHousingReptile.setMax(2000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest5(){

        secondHandHousingReptile.setMin(2000);
        secondHandHousingReptile.setMax(2500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest6(){

        secondHandHousingReptile.setMin(2500);
        secondHandHousingReptile.setMax(3000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest7(){


        secondHandHousingReptile.setMin(3000);
        secondHandHousingReptile.setMax(3500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest8(){

        secondHandHousingReptile.setMin(3500);
        secondHandHousingReptile.setMax(4000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest9(){

        secondHandHousingReptile.setMin(4000);
        secondHandHousingReptile.setMax(4500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest10(){


        secondHandHousingReptile.setMin(4500);
        secondHandHousingReptile.setMax(5000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest11(){

        secondHandHousingReptile.setMin(5000);
        secondHandHousingReptile.setMax(5500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest12(){

        secondHandHousingReptile.setMin(5500);
        secondHandHousingReptile.setMax(6000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest13(){

        secondHandHousingReptile.setMin(6000);
        secondHandHousingReptile.setMax(6500);
        secondHandHousingReptile.run();
    }


    @Test
    public void startSecondHandHousingReptileTest14(){

        secondHandHousingReptile.setMin(6500);
        secondHandHousingReptile.setMax(7000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest15(){

        secondHandHousingReptile.setMin(7000);
        secondHandHousingReptile.setMax(7500);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest16(){

        secondHandHousingReptile.setMin(7500);
        secondHandHousingReptile.setMax(8000);
        secondHandHousingReptile.run();
    }

    @Test
    public void startSecondHandHousingReptileTest17(){

        secondHandHousingReptile.setMin(8000);
        secondHandHousingReptile.setMax(8500);
        secondHandHousingReptile.run();
    }
    @Test
    public void startSecondHandCommunityReptileTest(){
        secondHandCommunityReptile.run();
    }

}

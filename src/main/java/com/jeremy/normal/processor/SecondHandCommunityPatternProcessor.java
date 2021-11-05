package com.jeremy.normal.processor;

import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SecondHandCommunityPatternProcessor implements PageProcessor {

    private Site site = Site
            .me()
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")
            .setSleepTime(50000)
            .setRetryTimes(5)
            .setRetrySleepTime(100000)
            .setCycleRetryTimes(3)
            .setTimeOut(50000);

    @Override
    public void process(Page page) {
        System.out.println("page.getUrl().get():"+page.getUrl().get());
        //列表页
        if (page.getUrl().regex("https://wh.ke.com/xiaoqu/[a-z]+/pg\\w+").match() || page.getRequest().getUrl().equals("https://wh.ke.com/xiaoqu")) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                list.add("https://wh.ke.com/xiaoqu/qingshan/pg"+ i );
            }
            for (int i = 41; i > 0; i--) {
                list.add("https://wh.ke.com/xiaoqu/jiangan/pg" + i);
            }
            for (int i = 0; i < 27; i++) {
                list.add("https://wh.ke.com/xiaoqu/jianghan/pg" + i);
            }
            for (int i = 0; i < 24; i++) {
                list.add("https://wh.ke.com/xiaoqu/qiaokou/pg" + i);
            }
            for (int i = 0; i < 14; i++) {
                list.add("https://wh.ke.com/xiaoqu/dongxihu/pg"+ i);
            }
            for (int i = 49; i > 49; i--) {
                list.add("https://wh.ke.com/xiaoqu/wuchang/pg" + i);
            }
            for (int i = 0; i < 27; i++) {
                list.add("https://wh.ke.com/xiaoqu/hongshan/pg"+ i);
            }
            for (int i = 0; i < 27; i++) {
                list.add("https://wh.ke.com/xiaoqu/hanyang/pg"+ i);
            }
            for (int i = 0; i < 23; i++) {
                list.add("https://wh.ke.com/xiaoqu/donghugaoxin/pg"+ i);
            }
            for (int i = 0; i < 15; i++) {
                list.add("https://wh.ke.com/xiaoqu/jiangxia/pg" + i);
            }
            for (int i = 0; i < 12; i++) {
                list.add("https://wh.ke.com/xiaoqu/jiangxia/pg"+ i);
            }
            for (int i = 0; i < 21; i++) {
                list.add("https://wh.ke.com/xiaoqu/caidian/pg" + i);
            }
            for (int i = 0; i < 14; i++) {
                list.add("https://wh.ke.com/xiaoqu/huangbei/pg"+ i);
            }
            for (int i = 0; i < 8; i++) {
                list.add("https://wh.ke.com/xiaoqu/xinzhou/pg"+ i);
            }
            for (int i = 0; i < 5; i++) {
                list.add("https://wh.ke.com/xiaoqu/zhuankoukaifaqu/pg" + i);
            }
            for (int i = 0; i < 5; i++) {
                list.add("https://wh.ke.com/xiaoqu/hannan/pg" + i);
            }
            page.addTargetRequests(list);
            HashMap<String, String> fields = new HashMap<>(4);
            fields.put("page_url", "//ul[@class='listContent']//li[@class='xiaoquListItem']//a[@class='maidian-detail']/@href");
            fields.forEach((k, v) -> {
                List<String> pageList = page.getHtml().xpath(v).all().stream().filter(r -> r.contains("xiaoqu")).collect(Collectors.toList());
                page.putField(k, pageList);
                System.out.println("--------------page_url---------------:"+ JSONObject.toJSONString(pageList));
            });
        } else {
            //正文页
            HashMap<String, String> contentFields = new HashMap<>(32);


            contentFields.put("communityName", "/html/body/div[1]/div[2]/div[2]/div/div/div[1]/h1/text()");
            contentFields.put("communityUnitPrice", "/html/body/div[1]/div[3]/div[1]/div[2]/div[2]/div/span[1]/text()");
            contentFields.put("communityUnitPriceDesc", "/html/body/div[1]/div[3]/div[1]/div[2]/div[2]/div/span[2]/text()");

            contentFields.put("buildingType", "/html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[1]/span[2]/text()");
            contentFields.put("propertyExpenses", "/html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[2]/span[2]/text()");
            contentFields.put("propertyCompany", "/html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[3]/span[2]/text()");

            contentFields.put("developer", "/html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[4]/span[2]/text()");
            contentFields.put("totalBuilding", "/html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[5]/span[2]/text()");
            contentFields.put("totalHouse", "/html/body/div[1]/div[3]/div[1]/div[2]/div[3]/div[6]/span[2]/text()");

            contentFields.forEach((k, v) -> {
                List<String> all = page.getHtml().xpath(v).all();
                page.putField(k, all);
            });
        }
    }


    @Override
    public Site getSite() {
        return site;
    }


}
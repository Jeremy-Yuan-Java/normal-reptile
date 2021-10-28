package com.jeremy.normal.processor;

import com.jeremy.normal.mapper.SecondHandHousingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2019/8/27 22:58
 * yechangjun
 */
public class BeikeOldListPatternProcessor implements PageProcessor {

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
        //列表页
        if (page.getUrl().regex("https://wh.ke.com/ershoufang/pg\\w+").match() || page.getRequest().getUrl().equals("https://wh.ke.com/ershoufang/pg1co32y1a2a3p2p3p4/")) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list.add("https://wh.ke.com/ershoufang/pg" + i + "co32y1a2a3p2p3p4");
            }
            page.addTargetRequests(list);
            HashMap<String, String> fields = new HashMap<>(4);
            fields.put("page_url", "//ul[@class='sellListContent']//li[@class='clear']//a/@href");
            fields.forEach((k, v) -> {
                List<String> pageList = page.getHtml().xpath(v).all().stream().filter(r -> r.contains("ershoufang")).collect(Collectors.toList());
                page.putField(k, pageList);
            });
        } else {
            //正文页
            HashMap<String, String> contentFields = new HashMap<>(32);
            contentFields.put("title", "//div[@class='title-wrapper']//div[@class='content']//div[@class='title']//h1[@class='main']/text()");
            contentFields.put("price", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[2]/span[1]/text()");
            contentFields.put("unitPrice", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[2]/div[1]/div[1]/span/text()");
            contentFields.put("unit", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[2]/div[1]/div[1]/i/text()");
            contentFields.put("areaInfo", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[3]/div[3]/div[2]/text()");
            contentFields.put("communityName", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[4]/div[1]/a[1]/text()");
            contentFields.put("areaName", "/html/body/div[1]/div[4]/div[1]/div[2]/div[4]/div[2]/span[2]/a[1]/text()");
            contentFields.put("areaLocation", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[4]/div[2]/span[2]/a[2]/text()");


            contentFields.put("houseTypeListName", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li/span/text()");
            contentFields.put("houseTypeListValue", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li/text()");
//            contentFields.put("floor", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[2]/text()");
//            contentFields.put("area", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[3]/text()");
//            contentFields.put("house_structure", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[4]/text()");
//            contentFields.put("building_type", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[5]/text()");
//            contentFields.put("towards", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[6]/text()");
//            contentFields.put("building_structure", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[7]/text()");
//            contentFields.put("renovation_condition", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[8]/text()");
//            contentFields.put("echelon", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[9]/text()");
//            contentFields.put("is_elevator", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li[10]/text()");
//


            contentFields.put("listingTime", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[1]/text()");
            contentFields.put("trade", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[2]/text()");
            contentFields.put("lastTransaction", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[3]/text()");
            contentFields.put("housingPurpose", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[4]/text()");
            contentFields.put("years", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[5]/text()");
            contentFields.put("property", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[6]/text()");
            contentFields.put("mortgage", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[2]/div[2]/ul/li[7]/span[2]/text()");

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
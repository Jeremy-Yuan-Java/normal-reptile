package com.jeremy.normal.processor;

import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SecondHandHousingPatternProcessor implements PageProcessor {

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
        if (page.getUrl().regex("https://wh.ke.com/ershoufang/pg\\d+co41rs[\\w%]+").match()) {
            HashMap<String, String> fields = new HashMap<>(4);
            fields.put("page_url", "//ul[@class='sellListContent']//li[@class='clear']//a/@href");
            fields.forEach((k, v) -> {
                List<String> pageList = page.getHtml().xpath(v).all().stream().filter(r -> r.contains("ershoufang")).collect(Collectors.toList());
                page.putField(k, pageList);
                System.out.println("--------------page_url---------------:"+ JSONObject.toJSONString(pageList));
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
            contentFields.put("communityPageUrl", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[4]/div[1]/a[1]/@href");


            contentFields.put("areaName", "/html/body/div[1]/div[4]/div[1]/div[2]/div[4]/div[2]/span[2]/a[1]/text()");
            contentFields.put("areaLocation", "//*[@id=\"beike\"]/div[1]/div[4]/div[1]/div[2]/div[4]/div[2]/span[2]/a[2]/text()");


            contentFields.put("houseTypeListName", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li/span/text()");
            contentFields.put("houseTypeListValue", "/html/body/div[1]/div[5]/div[1]/div[1]/div/div/div[1]/div[2]/ul/li/text()");

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
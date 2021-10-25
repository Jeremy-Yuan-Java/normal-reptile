package com.jeremy.normal.processor;

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
public class BeikeListPatternProcessor implements PageProcessor {


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
        if (page.getUrl().regex("https://wh.fang.ke.com/loupan/pg\\d+").match() || page.getRequest().getUrl().equals("https://wh.fang.ke.com/loupan")) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list.add("https://wh.fang.ke.com/loupan/pg"+i);
            }

//            List<String> all = page.getHtml().links().regex("https://wh.fang.ke.com/loupan/pg\\d+").all();

            page.addTargetRequests(list);
            HashMap<String, String> fields = new HashMap<>(4);
            fields.put("page_url","//li[@class='resblock-list post_ulog_exposure_scroll has-results']//a/@href");
            fields.forEach((k, v) -> {
                List<String> pageList = page.getHtml().xpath(v).all();
                page.putField(k, pageList.stream().filter(r->!r.contains("#")).collect(Collectors.toList()));
            });
        } else {
            //正文页
            HashMap<String, String> contentFields = new HashMap<>(4);
            contentFields.put("name","//h2[@class='DATA-PROJECT-NAME']/text()");

            contentFields.put("price","//span[@class='price-number']/text()");

            contentFields.put("address","//div[@class='middle-info animation']//ul[@class='info-list']//li[1]//span[@class='content']/text()");

            contentFields.put("time","//div[@class='middle-info animation']//ul[@class='info-list']//li[2]//div[@class='open-date']//span[@class='content']/text()");

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
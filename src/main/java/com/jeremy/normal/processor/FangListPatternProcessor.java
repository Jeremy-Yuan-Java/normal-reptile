package com.jeremy.normal.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.List;

/**
 * yuanmeijie
 */
public class FangListPatternProcessor implements PageProcessor {


    private Site site = Site
            .me()
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
            .setSleepTime(5000)
            .setRetryTimes(5)
            .setRetrySleepTime(1000)
            .setCycleRetryTimes(5)
            .setTimeOut(5000);

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex("https://wuhan.newhouse.fang.com/house/s/b\\d+").match() || page.getRequest().getUrl().equals("https://wuhan.newhouse.fang.com/house/s/")) {
            List<String> all = page.getHtml().links().regex("https://wuhan.newhouse.fang.com/house/s/b\\d+").all();

            page.addTargetRequests(all);
            HashMap<String, String> fields = new HashMap<>(4);
            fields.put("page_url","//div[@class='nlc_details']//div[@class='nlcd_name']//a/@href");
            fields.forEach((k, v) -> {
                page.putField(k, page.getHtml().xpath(v).all());
            });
        } else {
            //正文页
            HashMap<String, String> contentFields = new HashMap<>(4);
            contentFields.put("name","//div[@class='tit clearfix']//h1//strong/text()");

            contentFields.put("price","//div[@class='inf_left fl mr10']//span/text()");

            contentFields.put("address","//div[@class='inf_left fl']//span/text()");

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
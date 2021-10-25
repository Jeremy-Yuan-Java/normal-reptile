package com.jeremy.normal.backend;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
import com.jeremy.normal.component.DefaultHttpClientDownloader;
import com.jeremy.normal.component.SpiderHolder;
import com.jeremy.normal.constans.CoreConstant;
import com.jeremy.normal.entity.FangEntity;
import com.jeremy.normal.processor.FangListPatternProcessor;
import com.jeremy.normal.service.FangService;
import com.jeremy.normal.util.LinkUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2019/9/11 16:19
 * yechangjun
 */
@Slf4j
@Component
@Scope("prototype")
public class FangJobTestService extends Thread {

    @Autowired
    private FangService fangService;

    @Override
    public void run() {


        FangListPatternProcessor processor = new FangListPatternProcessor();

        Map<String/*内容页链接*/, LinkedHashMap<String, String> /*列表页提取的数据*/> itemHolder = new ConcurrentHashMap<>();

        //构建正文页爬虫
        Spider contentWorker = buildContentWorker(itemHolder, processor);
        //构建列表页爬虫
        Spider spiderWorker = buildListSpiderWorker(contentWorker, itemHolder, processor);

        //将爬虫保存起来 用于控制其生命周期 比如停止爬虫
        SpiderHolder.putSpider(spiderWorker, contentWorker);

        //异步启动正文页和内容页的爬虫
        spiderWorker.runAsync();
        contentWorker.runAsync();

        //阻塞当前线程 直到列表页爬虫没有任务
        blockCurrentThread(spiderWorker);

        //列表页爬虫停止了，唤醒通知可能休眠的内容页爬虫
        wakeupContentSpider(contentWorker);

        //阻塞当前线程 直到内容页爬虫没有任务
        blockCurrentThread(contentWorker);

        //爬虫容器移除uuid对应的爬虫
        SpiderHolder.removeSpider(spiderWorker.getUUID());

    }


    private void wakeupContentSpider(Spider contentWorker) {
        //内容页爬虫设置没有新任务时退出
        contentWorker.setExitWhenComplete(true);
        //不添加链接 间接唤醒线程
        contentWorker.addUrl();
    }

    private void blockCurrentThread(Spider spiderWorker) {
        while (spiderWorker.getStatus() != Spider.Status.Stopped) {
            try {
                sleep(1000);
                log.info("sleep zzzzz+++++");
            } catch (InterruptedException e) {
                log.info("spiderWorker interruptedException", e);
            }
        }

        log.info("spiderWorker stoped|uuid={}", spiderWorker.getUUID());
    }


    private Spider buildListSpiderWorker(Spider contentWorker, Map<String, LinkedHashMap<String, String>> itemHolder, PageProcessor processor) {
        Spider spiderWorker = Spider.create(processor);
        spiderWorker.setDownloader(new DefaultHttpClientDownloader());
        spiderWorker.thread(1).setUUID(UUID.randomUUID().toString()).addUrl("https://wuhan.newhouse.fang.com/house/s/")
                .addPipeline((resultItems, task) -> {
                    String requestUrl = resultItems.getRequest().getUrl();

                    final List<LinkedHashMap<String, String>> resultList = new ArrayList<>();
                    Map<String, Object> resultMap = resultItems.getAll();
                    resultMap.forEach((k, v) -> {
                        List<String> vstr = (ArrayList<String>) v;
                        if (resultList.isEmpty()) {
                            for (int i = 0; i < vstr.size(); i++) {
                                resultList.add(new LinkedHashMap<>());
                            }
                        }
                        for (int i = 0; i < resultList.size(); i++) {
                            Map<String, String> obj = resultList.get(i);
                            obj.put(k, vstr.get(i));
                        }
                    });

                    //判断有没有正文页
                    if (Strings.isNullOrEmpty("//div[@class='nlc_details']//div[@class='nlcd_name']//a/@href")) {
                        //没有正文页面 直接保存数据
                        System.out.println(resultList);

                    } else {
                        //如果有正文页面 先把数据存放到内存里 等待后续处理完正文页后再一起保存
                        resultList.forEach((v) -> {
                            String url = v.get(CoreConstant.PAGE_URL);
                            if (!Strings.isNullOrEmpty(url)) {

                                //把正文页的链接添加到带爬取页面
                                url = LinkUtil.getAbsoluteURL(requestUrl, url);

                                itemHolder.put(url, v);
                                contentWorker.addUrl(url);
                            }
                        });
                    }
                });

        return spiderWorker;
    }

    private Spider buildContentWorker(Map<String, LinkedHashMap<String, String>> itemHolder, PageProcessor processor) {
        Spider contentWorker = Spider.create(processor);
        contentWorker.setDownloader(new DefaultHttpClientDownloader());
        contentWorker.thread(5).setUUID(UUID.randomUUID().toString()).
                addPipeline((resultItems, task) -> {
                    String requestUrl = resultItems.getRequest().getUrl();
                    log.info("get content page|url={}", requestUrl);
                    Map<String, Object> resultMap = resultItems.getAll();
                    //合并数据
                    LinkedHashMap<String, String> item = itemHolder.get(requestUrl);
                    //把暂存的数据删了 防止内存溢出
                    itemHolder.remove(requestUrl);

                    resultMap.forEach((k, v) -> {
                        String value = CollectionUtils.isEmpty(((List) v)) ? null : ((List) v).get(0).toString();
                        item.put(k, value);
                    });
                    //保存
                    final List<LinkedHashMap<String, String>> resultList = new ArrayList<>();
                    resultList.add(item);

                    List<FangEntity> fangEntities = new ArrayList<>();

                    resultList.forEach(r -> {
                        FangEntity fangEntity = new FangEntity();
                        fangEntity.setPageUrl(r.get("page_url"));
                        fangEntity.setName(r.get("name"));
                        fangEntity.setPrice(r.get("price"));
                        fangEntity.setAddress(r.get("address"));

                        fangEntities.add(fangEntity);
                    });

                    fangService.saveBatch(fangEntities);

                    System.out.println(resultList);
                });

//        }
        contentWorker.setExitWhenComplete(false);
        return contentWorker;
    }


}

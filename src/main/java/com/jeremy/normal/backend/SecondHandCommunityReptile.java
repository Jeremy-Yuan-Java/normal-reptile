package com.jeremy.normal.backend;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeremy.normal.component.DefaultHttpClientDownloader;
import com.jeremy.normal.constans.CoreConstant;
import com.jeremy.normal.entity.SecondHandCommunityEntity;
import com.jeremy.normal.mapper.SecondHandCommunityMapper;
import com.jeremy.normal.processor.SecondHandCommunityPatternProcessor;
import com.jeremy.normal.util.LinkUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Scope("prototype")
public class SecondHandCommunityReptile extends Thread {

    @Autowired
    private SecondHandCommunityMapper secondHandCommunityMapper;

    @Override
    public void run() {

        SecondHandCommunityPatternProcessor processor = new SecondHandCommunityPatternProcessor();

        Map<String/*内容页链接*/, LinkedHashMap<String, String> /*列表页提取的数据*/> itemHolder = new ConcurrentHashMap<>();

        //构建正文页爬虫
        Spider contentWorker = buildContentWorker(itemHolder, processor);
        //构建列表页爬虫
        Spider spiderWorker = buildListSpiderWorker(contentWorker, itemHolder, processor);

        //异步启动正文页和内容页的爬虫
        spiderWorker.runAsync();
        contentWorker.runAsync();

        //阻塞当前线程 直到列表页爬虫没有任务
        blockCurrentThread(spiderWorker);

        //列表页爬虫停止了，唤醒通知可能休眠的内容页爬虫
        wakeupContentSpider(contentWorker);

        //阻塞当前线程 直到内容页爬虫没有任务
        blockCurrentThread(contentWorker);
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
                sleep(10000);
                log.info("spiderWorker running sleep");
            } catch (InterruptedException e) {
                log.info("spiderWorker interruptedException", e);
            }
        }

        log.info("spiderWorker stoped|uuid={}", spiderWorker.getUUID());
    }


    private Spider buildListSpiderWorker(Spider contentWorker, Map<String, LinkedHashMap<String, String>> itemHolder, PageProcessor processor) {
        Spider spiderWorker = Spider.create(processor);
        spiderWorker.setDownloader(new DefaultHttpClientDownloader());
        spiderWorker.thread(4).setUUID(UUID.randomUUID().toString()).addUrl("https://wh.ke.com/xiaoqu")
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

                    resultList.forEach((v) -> {
                        String url = v.get(CoreConstant.PAGE_URL);
                        if (!Strings.isNullOrEmpty(url)&&judgeIsReptileByUrl(url)) {

                            //把正文页的链接添加到带爬取页面
                            url = LinkUtil.getAbsoluteURL(requestUrl, url);

                            itemHolder.put(url, v);
                            contentWorker.addUrl(url);
                        }
                    });
                });

        return spiderWorker;
    }

    private Spider buildContentWorker(Map<String, LinkedHashMap<String, String>> itemHolder, PageProcessor processor) {
        Spider contentWorker = Spider.create(processor);
        contentWorker.setDownloader(new DefaultHttpClientDownloader());
        contentWorker.thread(4).setUUID(UUID.randomUUID().toString()).
                addPipeline((resultItems, task) -> {
                    String requestUrl = resultItems.getRequest().getUrl();
                    log.info("get content page|url={}|uuid={}|spiderId={}", requestUrl);
                    Map<String, Object> resultMap = resultItems.getAll();
                    //合并数据
                    LinkedHashMap<String, String> item = itemHolder.get(requestUrl);
                    //把暂存的数据删了 防止内存溢出
                    itemHolder.remove(requestUrl);
                    System.out.println(resultMap);
                    resultMap.forEach((k, v) -> {
                        String value = CollectionUtils.isEmpty(((List) v)) ? null : ((List) v).get(0).toString();
                        item.put(k, value);

                    });
                    SecondHandCommunityEntity secondHandCommunityEntity = JSONObject.parseObject(JSONObject.toJSONString(item), SecondHandCommunityEntity.class);

                    if (StringUtils.isEmpty(item.get("communityName"))){
                        log.error("url:{}已开启屏蔽");
                    }else{
                        secondHandCommunityEntity.setCommunityName(item.get("communityName").trim());
                        secondHandCommunityMapper.insert(secondHandCommunityEntity);
                        System.out.println(secondHandCommunityEntity);
                    }
                });

//        }
        contentWorker.setExitWhenComplete(false);
        return contentWorker;
    }



    private boolean judgeIsReptileByUrl(String url) {
        LambdaQueryWrapper<SecondHandCommunityEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondHandCommunityEntity::getPageUrl, url);
        Integer integer = secondHandCommunityMapper.selectCount(queryWrapper);
        if (ObjectUtils.isEmpty(integer) || integer == 0) {
            return true;
        } else {
            return false;
        }

    }

}

package com.jeremy.normal.backend;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jeremy.normal.component.DefaultHttpClientDownloader;
import com.jeremy.normal.constans.CoreConstant;
import com.jeremy.normal.entity.SecondHandCommunityEntity;
import com.jeremy.normal.entity.SecondHandHousingEntity;
import com.jeremy.normal.mapper.SecondHandCommunityMapper;
import com.jeremy.normal.mapper.SecondHandHousingMapper;
import com.jeremy.normal.processor.SecondHandHousingPatternProcessor;
import com.jeremy.normal.util.LinkUtil;
import io.swagger.models.auth.In;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Scope("prototype")
public class SecondHandHousingReptile extends Thread {
    private Integer min;
    private Integer max;

    public void setMin(Integer min) {
        this.min = min;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @Autowired
    private SecondHandHousingMapper secondHandHousingMapper;

    @Autowired
    private SecondHandCommunityMapper secondHandCommunityMapper;

    @Override
    public void run() {

        SecondHandHousingPatternProcessor processor = new SecondHandHousingPatternProcessor();

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
        List<String> urls = new ArrayList<>();
        LambdaQueryWrapper<SecondHandCommunityEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.le(SecondHandCommunityEntity::getId, max);
        lambdaQueryWrapper.ge(SecondHandCommunityEntity::getId, min);

        List<SecondHandCommunityEntity> list = secondHandCommunityMapper.selectList(lambdaQueryWrapper);

            try {
                for (SecondHandCommunityEntity secondHandCommunityEntity : list) {
                    String str = "https://wh.ke.com/ershoufang/pg1" + "co41rs" + URLEncoder.encode(secondHandCommunityEntity.getCommunityName(), "utf-8") + "/";
                    urls.add(str);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        spiderWorker.startUrls(urls);
        spiderWorker.thread(4).setUUID(UUID.randomUUID().toString())
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
                        if (!Strings.isNullOrEmpty(url) && judgeIsReptileByUrl(url)) {

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
        contentWorker.thread(6).setUUID(UUID.randomUUID().toString()).
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

                        if (!"houseTypeListName".equals(k) && !"houseTypeListValue".equals(k)) {
                            String value = CollectionUtils.isEmpty(((List) v)) ? null : ((List) v).get(0).toString();
                            item.put(k, value);
                        }
                    });
                    List<String> nameList = (List<String>) resultMap.get("houseTypeListName");
                    List<String> valueList = (List<String>) resultMap.get("houseTypeListValue");

                    SecondHandHousingEntity secondHandHousingEntity = JSONObject.parseObject(JSONObject.toJSONString(item), SecondHandHousingEntity.class);

                    if (StringUtils.isEmpty(item.get("title"))) {
                        log.error("url:{}已开启屏蔽");
                    } else {
                        secondHandHousingEntity.setCommunityPageUrl(LinkUtil.getAbsoluteURL(requestUrl, secondHandHousingEntity.getCommunityPageUrl()));
                        if (nameList.indexOf("房屋户型") != -1) {
                            secondHandHousingEntity.setHouseType(valueList.get(nameList.indexOf("房屋户型")));
                        }
                        if (nameList.indexOf("所在楼层") != -1) {
                            secondHandHousingEntity.setFloor(valueList.get(nameList.indexOf("所在楼层")));
                        }
                        if (nameList.indexOf("建筑面积") != -1) {
                            secondHandHousingEntity.setArea(valueList.get(nameList.indexOf("建筑面积")));
                        }
                        if (nameList.indexOf("户型结构") != -1) {
                            secondHandHousingEntity.setHouseStructure(valueList.get(nameList.indexOf("户型结构")));
                        }
                        if (nameList.indexOf("套内面积") != -1) {
                            secondHandHousingEntity.setSetArea(valueList.get(nameList.indexOf("套内面积")));
                        }
                        if (nameList.indexOf("建筑类型") != -1) {
                            secondHandHousingEntity.setBuildingType(valueList.get(nameList.indexOf("建筑类型")));
                        }
                        if (nameList.indexOf("房屋朝向") != -1) {
                            secondHandHousingEntity.setTowards(valueList.get(nameList.indexOf("房屋朝向")));
                        }
                        if (nameList.indexOf("建筑结构") != -1) {
                            secondHandHousingEntity.setBuildingStructure(valueList.get(nameList.indexOf("建筑结构")));
                        }
                        if (nameList.indexOf("装修情况") != -1) {
                            secondHandHousingEntity.setRenovationCondition(valueList.get(nameList.indexOf("装修情况")));
                        }
                        if (nameList.indexOf("梯户比例") != -1) {
                            secondHandHousingEntity.setEchelon(valueList.get(nameList.indexOf("梯户比例")));
                        }
                        if (nameList.indexOf("配备电梯") != -1) {
                            secondHandHousingEntity.setIsElevator(valueList.get(nameList.indexOf("配备电梯")));
                        }

                        secondHandHousingMapper.insert(secondHandHousingEntity);

                        System.out.println(secondHandHousingEntity);
                    }

                });

//        }
        contentWorker.setExitWhenComplete(false);
        return contentWorker;
    }


    private boolean judgeIsReptileByUrl(String url) {
        LambdaQueryWrapper<SecondHandHousingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondHandHousingEntity::getPageUrl, url);
        Integer integer = secondHandHousingMapper.selectCount(queryWrapper);
        if (ObjectUtils.isEmpty(integer) || integer == 0) {
            return true;
        } else {
            return false;
        }

    }

}

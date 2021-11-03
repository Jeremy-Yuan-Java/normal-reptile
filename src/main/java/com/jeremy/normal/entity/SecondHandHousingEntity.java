package com.jeremy.normal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_second_hand_housing")
@Data
public class SecondHandHousingEntity {
    private int id;
    private String pageUrl;
    private String title;
    private String price;
    private String unitPrice;
    private String unit;
    private String areaInfo;
    private String communityName;
    private String areaName;
    private String areaLocation;
    private String houseType;
    private String floor;
    private String area;
    private String houseStructure;
    private String setArea;
    private String buildingType;
    private String towards;
    private String buildingStructure;
    private String renovationCondition;
    private String echelon;
    private String isElevator;
    private String listingTime;
    private String trade;
    private String lastTransaction;
    private String housingPurpose;
    private String years;
    private String property;
    private String mortgage;
    private String communityPageUrl;
}

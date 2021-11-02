package com.jeremy.normal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_second_hand_community")
@Data
public class SecondHandCommunityEntity {
    private int id;
    private String pageUrl;
    private String communityName;
    private String communityUnitPrice;
    private String communityUnitPriceDesc;
    private String buildingType;
    private String propertyExpenses;
    private String propertyCompany;
    private String developer;
    private String totalBuilding;
    private String totalHouse;
}

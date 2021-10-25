package com.jeremy.normal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_fang")
@Data
public class FangEntity{
    private int id;
    private String name;
    private String price;
    private String address;
    private String pageUrl;
    private String time;
}

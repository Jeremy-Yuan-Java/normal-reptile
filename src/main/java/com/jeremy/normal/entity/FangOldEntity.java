package com.jeremy.normal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_fang_old")
@Data
public class FangOldEntity {
    private int id;
    private String json;
}

-- auto-generated definition
create table t_second_hand_community
(
    id                        int auto_increment
        primary key,
    page_url                  varchar(255) null,
    community_name            varchar(255) null,
    community_unit_price      varchar(255) null,
    community_unit_price_desc varchar(255) null comment '小区参考价',
    building_type             varchar(255) null comment 'ji按住类型',
    property_expenses         varchar(255) null comment '物业费用',
    property_company          varchar(255) null comment '物业公司',
    developer                 varchar(255) null comment '开发商',
    total_building            varchar(255) null comment '楼栋总数',
    total_house               varchar(255) null comment '房屋总数'
);

-- auto-generated definition
create table t_second_hand_housing
(
    id                   int auto_increment
        primary key,
    page_url             varchar(255) null comment '页面list',
    title                varchar(255) null comment '标题',
    price                varchar(255) null comment '总价',
    unit_price           varchar(255) null comment '单价',
    unit                 varchar(255) null comment '单位',
    area_info            varchar(255) null comment '建筑历史',
    community_name       varchar(255) null comment '小区名称',
    area_name            varchar(255) null comment '大区域',
    area_location        varchar(255) null comment '小区域',
    house_type           varchar(255) null comment '房屋户型',
    floor                varchar(255) null comment '楼层',
    area                 varchar(255) null comment '面积',
    house_structure      varchar(255) null comment '户型结构',
    building_type        varchar(255) null comment '建筑类型',
    towards              varchar(255) null comment '朝向',
    building_structure   varchar(255) null comment '建筑类型',
    renovation_condition varchar(255) null comment '装修情况',
    echelon              varchar(255) null comment '梯户比例',
    is_elevator          varchar(255) null comment '是否有电梯',
    listing_time         varchar(255) null comment '挂牌日期',
    trade                varchar(255) null comment '交易权属',
    last_transaction     varchar(255) null comment '上次交易',
    housing_purpose      varchar(255) null comment '房屋用途',
    years                varchar(255) null comment '房屋年限',
    property             varchar(255) null comment '产权所属',
    mortgage             varchar(255) null comment '抵押信息',
    set_area             varchar(255) null comment '套内面积'
);


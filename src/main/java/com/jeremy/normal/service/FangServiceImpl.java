package com.jeremy.normal.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jeremy.normal.entity.FangEntity;
import com.jeremy.normal.mapper.FangMapper;
import org.springframework.stereotype.Service;

@Service
public class FangServiceImpl extends ServiceImpl<FangMapper, FangEntity> implements FangService {

}

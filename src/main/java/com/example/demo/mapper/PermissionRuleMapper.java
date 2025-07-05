package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.PermissionRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionRuleMapper extends BaseMapper<PermissionRule> {
    List<PermissionRule> findAllEnabledRules();
}

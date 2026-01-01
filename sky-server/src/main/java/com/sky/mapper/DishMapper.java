package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    // 所有基础 CRUD 操作已由 BaseMapper 提供
    // 可以使用 LambdaQueryWrapper 进行条件查询
}

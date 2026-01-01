package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // 所有基础 CRUD 操作已由 BaseMapper 提供
    // insert, deleteById, updateById, selectById 等方法可以直接使用
}

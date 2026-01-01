package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务层
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        //属性拷贝
        BeanUtils.copyProperties(categoryDTO, category);

        //分类状态默认为禁用状态0
        category.setStatus(StatusConstant.DISABLE);

        //设置创建时间、修改时间、创建人、修改人
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        // 使用 MyBatis-Plus 的 insert 方法
        categoryMapper.insert(category);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 使用 MyBatis-Plus 的分页
        Page<Category> page = new Page<>(
                categoryPageQueryDTO.getPage(),
                categoryPageQueryDTO.getPageSize()
        );

        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果分类名称不为空，添加模糊查询条件
        if (categoryPageQueryDTO.getName() != null && !categoryPageQueryDTO.getName().trim().isEmpty()) {
            queryWrapper.like(Category::getName, categoryPageQueryDTO.getName());
        }
        
        // 如果分类类型不为空，添加精确查询条件
        if (categoryPageQueryDTO.getType() != null) {
            queryWrapper.eq(Category::getType, categoryPageQueryDTO.getType());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Category::getCreateTime);

        // 执行分页查询
        IPage<Category> result = categoryMapper.selectPage(page, queryWrapper);
        
        return new PageResult(result.getTotal(), result.getRecords());
    }

    /**
     * 根据id删除分类
     * @param id
     */
    public void deleteById(Long id) {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 查询当前分类是否关联了菜品
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        Long dishCount = dishMapper.selectCount(dishQueryWrapper);
        
        if(dishCount > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        Long setmealCount = setmealMapper.selectCount(setmealQueryWrapper);
        
        if(setmealCount > 0){
            //当前分类下有套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        // 使用 MyBatis-Plus 的 deleteById 方法删除分类数据
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        //设置修改时间、修改人
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        // 使用 MyBatis-Plus 的 updateById 方法
        categoryMapper.updateById(category);
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        // 使用 MyBatis-Plus 的 updateById 方法
        categoryMapper.updateById(category);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    public List<Category> list(Integer type) {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 进行条件查询
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null, Category::getType, type);
        // 按排序字段升序排列
        queryWrapper.orderByAsc(Category::getSort);
        
        return categoryMapper.selectList(queryWrapper);
    }
}

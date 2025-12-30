package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据用户名查询员工
     * 这个方法可以保留，因为使用了自定义查询条件
     * 或者可以使用 MyBatis Plus 的 LambdaQueryWrapper 在 Service 层实现
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    // insert 方法已经不需要了，可以直接使用 BaseMapper 的 insert 方法
    // void insert(Employee employee);
}

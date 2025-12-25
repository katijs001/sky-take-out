package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /*插入员工数据
    **/
    @Insert("insert into emplogee(name,user,password,phone,sex,id_number,creat_time,update_time,creat_user,update_user)" +
            "values "+
            "#(name),#(user),")
    void insert(Employee employee);
}

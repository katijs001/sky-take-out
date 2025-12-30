package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /*
     * 新增员工*/
    @Override
    public void save(EmployeeDTO employeeDTO) {

        Employee employee=new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置账号状态，默认正常状态1正常，0锁定
        employee.setStatus(StatusConstant.ENABLE);
        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //TODO后期要改为当前登录用户的id
        employee.setCreateUser(10l);
        employee.setUpdateUser(10l);

        employeeMapper.insert(employee);
        return ;
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 使用 MyBatis-Plus 的分页

        Page<Employee> page = new Page<>(
                employeePageQueryDTO.getPage(),
                employeePageQueryDTO.getPageSize()
        );

        // 构建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        if (employeePageQueryDTO.getName() != null && !employeePageQueryDTO.getName().trim().isEmpty()) {
            queryWrapper.like(Employee::getName, employeePageQueryDTO.getName());
        }
        queryWrapper.orderByDesc(Employee::getCreateTime);

        // 执行分页查询
        IPage<Employee> result = employeeMapper.selectPage(page, queryWrapper);

        return new PageResult(result.getTotal(), result.getRecords());
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public void startOrstop(Integer status, Long id) {
        Employee employee=new  Employee();
        employee.setId(id);
        employee.setStatus(status);
        employeeMapper.updateById(employee);

        return ;
    }

    @Override
    public Employee finId(Integer id) {

        //使用Mybatis Plus的selectById方法
        Employee employee=employeeMapper.selectById(id);

        if(employee==null){
            log.info("员工不存在，id={}",id);
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        }
        employee.setPassword(null);
        return employee;



    }

}

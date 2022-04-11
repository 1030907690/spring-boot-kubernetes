package com.springboot.sample.service.impl;

import com.springboot.sample.bean.Users;
import com.springboot.sample.mapper.UsersMapper;
import com.springboot.sample.service.TestExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class TestExceptionServiceImpl implements TestExceptionService {

    @Resource
    private UsersMapper usersMapper;

    /***
     * Transaction rolled back because it has been marked as rollback-only
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void test() {
        Users entity = new Users();
        entity.setName("李四啊");
        usersMapper.insert(entity);
        System.out.println(entity.getId());
        if (true){
            throw new RuntimeException("测试事务回滚");
        }
    }
}

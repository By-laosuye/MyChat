package com.laosuye.mychat.common.user.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-09-09
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User>{

    public User getByOpenId(String openId) {
        return lambdaQuery().eq(User::getOpenId, openId).one();
    }
}
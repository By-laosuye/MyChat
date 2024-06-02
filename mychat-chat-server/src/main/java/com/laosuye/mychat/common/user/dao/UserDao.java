package com.laosuye.mychat.common.user.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.laosuye.mychat.common.commm.domain.enums.YesOrNoEnum;
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
public class UserDao extends ServiceImpl<UserMapper, User> {

    /**
     * 根据openId查询用户
     * @param openId openId
     * @return 用户信息
     */
    public User getByOpenId(String openId) {
        return lambdaQuery().eq(User::getOpenId, openId).one();
    }

    /**
     * 根据用户名查用户信息
     * @param name 用户名
     * @return  用户信息
     */
    public User getByName(String name) {
        return lambdaQuery()
                .eq(User::getName, name)
                .one();
    }

    /**
     * 修改用户名
     * @param uid 用户id
     * @param name 用户名
     */
    public void modifyName(Long uid, String name) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getName, name)
                .update();
    }

    /**
     * 佩戴徽章
     * @param uid 用户id
     * @param itemId 徽章id
     */
    public void wearingBadge(Long uid, Long itemId) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getItemId, itemId)
                .update();
    }

    /**
     * 将指定用户的狀態設置為无效。
     *
     * 此方法通过Lambda更新方式，修改用户表中指定UID用户的状态为无效（即，设置为YesOrNoEnum.YES的状态值）。
     * 主要用于处理需要标记用户为无效的情况，例如用户违规、账号注销等。
     *
     * @param uid 用户ID，用于指定需要更新状态的用户。
     */
    public void invalidUid(Long uid) {
        // 使用Lambda表达式方式更新用户状态
        lambdaUpdate()
                // 指定更新条件：用户ID等于uid
                .eq(User::getId, uid)
                // 设置更新内容：用户状态为无效
                .set(User::getStatus, YesOrNoEnum.YES.getStatus())
                // 执行更新操作
                .update();
    }
}

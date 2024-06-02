package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.user.domain.enums.RoleEnum;
import com.laosuye.mychat.common.user.service.IRoleService;
import com.laosuye.mychat.common.user.service.IUserRoleService;
import com.laosuye.mychat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private UserCache userCache;


    /**
     * 检查用户是否具有特定权限。
     *
     * 本方法通过查询用户的角色集合，来判断用户是否拥有指定的角色权限，或者是否是管理员。
     * 管理员拥有所有权限，因此如果用户是管理员，则无需进一步检查角色集合。
     *
     * @param uid 用户ID，用于查询用户的角色集合。
     * @param roleEnum 指定的角色枚举，表示需要检查的权限类型。
     * @return 如果用户具有指定权限或为管理员，则返回true；否则返回false。
     */
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        // 从用户缓存中获取指定用户的角色集合
        Set<Long> roleSet = userCache.getRoleSet(uid);

        // 检查用户是否为管理员，或者用户的角色集合中是否包含指定的角色ID
        return isAdmin(roleSet) || roleSet.contains((roleEnum.getId()));
    }

    /**
     * 检查用户是否为管理员。
     *
     * 本方法通过查询用户的角色集合，来判断用户是否是管理员。
     * 管理员拥有所有权限，因此如果用户是管理员，则无需进一步检查角色集合。
     *
     * @param roleSet 用户的角色集合。
     * @return 如果用户是管理员，则返回true；否则返回false。
     */
    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}

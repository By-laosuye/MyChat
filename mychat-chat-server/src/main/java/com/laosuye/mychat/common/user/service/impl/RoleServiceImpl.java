package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.user.domain.enums.RoleEnum;
import com.laosuye.mychat.common.user.service.IRoleService;
import com.laosuye.mychat.common.user.service.IUserRoleService;
import com.laosuye.mychat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private UserCache userCache;


    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains((roleEnum.getId()));
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}

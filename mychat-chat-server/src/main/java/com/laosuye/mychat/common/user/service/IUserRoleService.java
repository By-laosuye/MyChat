package com.laosuye.mychat.common.user.service;

import com.laosuye.mychat.common.user.domain.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laosuye.mychat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-10-02
 */
public interface IUserRoleService {


    /**
     * 是否有某个权限
     * @param uid
     * @param roleEnum
     * @return
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);

}

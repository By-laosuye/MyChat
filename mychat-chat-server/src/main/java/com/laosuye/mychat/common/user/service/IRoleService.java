package com.laosuye.mychat.common.user.service;

import com.laosuye.mychat.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laosuye.mychat.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-10-02
 */
public interface IRoleService {

    /**
     * 是否有某个权限
     * @param uid
     * @param roleEnum
     * @return
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);

}

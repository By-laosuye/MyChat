package com.laosuye.mychat.common.user.dao;

import com.laosuye.mychat.common.user.domain.entity.Role;
import com.laosuye.mychat.common.user.mapper.RoleMapper;
import com.laosuye.mychat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-10-02
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}

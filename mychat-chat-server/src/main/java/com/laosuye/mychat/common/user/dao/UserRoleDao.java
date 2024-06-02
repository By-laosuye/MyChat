package com.laosuye.mychat.common.user.dao;

import com.laosuye.mychat.common.user.domain.entity.UserRole;
import com.laosuye.mychat.common.user.mapper.UserRoleMapper;
import com.laosuye.mychat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-10-02
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {

    /**
     * 根据用户ID查询用户角色列表。
     *
     * 本方法通过使用Lambda查询方式，筛选出UID等于指定值的用户角色记录。
     * 它提供了根据用户ID快速检索用户角色信息的能力，这对于实现基于角色的访问控制等场景非常有用。
     *
     * @param uid 用户的唯一标识ID。这是查询的依据，用于精确匹配用户角色关系。
     * @return 返回一个用户角色列表，列表中的每个元素代表了一个用户的角色信息。
     */
    public List<UserRole> listByUid(Long uid) {
        // 使用LambdaQueryWrapper构造查询条件，查询UID等于uid的UserRole实体列表
        return lambdaQuery()
                .eq(UserRole::getUid, uid)
                .list();
    }
}

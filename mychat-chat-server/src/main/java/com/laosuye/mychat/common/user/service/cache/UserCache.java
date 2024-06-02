package com.laosuye.mychat.common.user.service.cache;

import com.laosuye.mychat.common.user.dao.BlackDao;
import com.laosuye.mychat.common.user.dao.ItemConfigDao;
import com.laosuye.mychat.common.user.dao.UserRoleDao;
import com.laosuye.mychat.common.user.domain.entity.Black;
import com.laosuye.mychat.common.user.domain.entity.ItemConfig;
import com.laosuye.mychat.common.user.domain.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户缓存
 */
@Component
public class UserCache {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private BlackDao blackDao;


    /**
     * 通过缓存获取指定用户的角色集合。
     * 使用缓存的原因是，用户的角色信息在一段时间内不会频繁变化，通过缓存可以减少数据库查询次数，提高性能。
     *
     * @param uid 用户ID，用于查询用户的角色信息。
     * @return 返回一个Long类型的Set集合，包含该用户的所有角色ID。
     * @Cacheable 缓存注解，指示该方法的结果应该被缓存。cacheNames指定缓存的名称为"user"，key指定缓存的键为"'roles:'+#uid"，即以"roles:"加上用户ID作为缓存的键。
     */
    @Cacheable(cacheNames = "user", key = "'roles:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        // 根据用户ID查询用户角色列表
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        // 使用流式编程获取所有角色的ID，并转换为Set集合返回
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }



    /**
     * 从缓存中获取黑名单映射表。
     * 此方法使用缓存注解，将查询结果缓存起来，以提高后续相同查询的效率。
     * 缓存名称为"user"，缓存的键为"blackList"。
     *
     * @return 返回一个Map，其中键为黑名单类型，值为该类型下的目标集合。
     */
    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        // 根据黑名单类型对黑名单实体进行分组
        Map<Integer, List<Black>> collect = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        // 初始化结果映射表，用于存放类型到目标集合的映射
        Map<Integer, Set<String>> result = new HashMap<>();
        // 遍历分组后的黑名单类型集合，将每个类型的黑名单目标转换为集合，并存入结果映射表
        collect.forEach((type, list) -> {
            result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        // 返回处理后的结果映射表
        return result;
    }


    /**
     * 从用户缓存中移除黑名单。
     *
     * 此方法通过注解@CacheEvict自动触发缓存的清除操作。特定的缓存名称和键值被指定用于准确地定位并清除目标缓存项。
     * 清除的缓存项是一个映射，其中包含整数键和字符串集合值，表示被移除的黑名单用户数据。
     *
     * @return 由于此方法主要用于清除缓存，因此它并不返回任何实质性的数据。返回值为null。
     */
    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }
}

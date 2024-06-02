package com.laosuye.mychat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1L, "超级管理员"),
    CHAT_MANAGER(2L, "群聊管理员"),
    ;

    private final Long id;
    private final String desc;
    /**
     * 缓存
     */
    private static final Map<Long, RoleEnum> cache;

    /**
     * 静态代码块
     */
    static {
        cache = Arrays.stream(RoleEnum.values()).collect(Collectors.toMap(RoleEnum::getId, Function.identity()));
    }

    /**
     * 根据ID获取角色枚举实例。
     *
     * 该方法通过ID从缓存中检索角色枚举实例。缓存的使用旨在提高查询效率，避免频繁的数据库访问。
     *
     * @param id 角色的唯一标识ID。此参数不应为null。
     * @return 对应于给定ID的角色枚举实例。如果缓存中不存在该ID，则返回null。
     */
    public static RoleEnum of(Long id) {
        return cache.get(id);
    }
}

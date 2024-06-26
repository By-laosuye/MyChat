package com.laosuye.mychat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@AllArgsConstructor
@Getter
public enum UserActiveStatusEnum {

    ONLINE(1, "在线"),
    OFFLINE(2, "离线");

    private final Integer status;
    private final String desc;
}

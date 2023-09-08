package com.laosuye.mychat.common.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author laosuye
 * @version 1.0
 * @data 2023/9/08/15:59
 */
@Getter
@AllArgsConstructor
public enum WSReqTypeEnum{
    LOGIN(1,"请求登录二维码"),
    HEARTBEAT(2,"心跳包"),
    AUTHORIZE(3,"登录认证");

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSReqTypeEnum> cache;

    static {
        cache = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    public static WSReqTypeEnum of(Integer type) {
        return cache.get(type);
    }
}

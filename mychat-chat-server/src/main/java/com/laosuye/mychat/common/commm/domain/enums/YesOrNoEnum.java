package com.laosuye.mychat.common.commm.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否枚举
 * @author laosuye
 */
@Getter
@AllArgsConstructor
public enum YesOrNoEnum {

    NO(0, "否"), YES(1, "是");


    private final Integer status;
    private final String desc;
}

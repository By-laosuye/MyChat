package com.laosuye.mychat.common.user.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 佩戴徽章请求参数
 */
@Data
public class WearingBadgeReq {

    @ApiModelProperty("徽章id")
    @NotNull
    private Long itemId;
}

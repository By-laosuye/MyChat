package com.laosuye.mychat.common.user.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BlackReq {

    @ApiModelProperty("拉黑用户的uid")
    @NotNull
    private Long uid;
}

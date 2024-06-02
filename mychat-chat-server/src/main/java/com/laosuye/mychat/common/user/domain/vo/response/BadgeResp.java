package com.laosuye.mychat.common.user.domain.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 徽章信息
 */
@Data
@ApiModel(value="BadgeResp对象", description="用户徽章详细信息")
public class BadgeResp {

    @ApiModelProperty(value = "徽章id")
    private Long id;

    @ApiModelProperty(value = "徽章图标")
    private String img;

    @ApiModelProperty(value = "徽章描述")
    private String describe;

    @ApiModelProperty(value = "是否用有 0否 1是")
    private Integer obtain;

    @ApiModelProperty(value = "是否佩戴 0否 1是")
    private Integer wearing;
}

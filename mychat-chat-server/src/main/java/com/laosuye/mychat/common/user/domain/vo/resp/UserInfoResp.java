package com.laosuye.mychat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户详细信息
 */
@Data
@ApiModel(value="UserInfoResp对象", description="用户详细信息")
public class UserInfoResp {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户名称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "剩余的改名次数")
    private Integer modifyNameChance;
}

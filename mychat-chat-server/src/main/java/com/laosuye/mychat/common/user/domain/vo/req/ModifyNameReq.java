package com.laosuye.mychat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class ModifyNameReq {

    @ApiModelProperty("用户姓名")
    @NotNull
    @Length(max = 6,message = "不可以太长，不然我记不住奥！")
    private String name;
}

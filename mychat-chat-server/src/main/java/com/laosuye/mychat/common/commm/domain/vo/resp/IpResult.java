package com.laosuye.mychat.common.commm.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 获取ip返回信息
 * @param <T>
 */
@Data
public class IpResult<T> implements Serializable {
    @ApiModelProperty("错误码")
    private Integer code;
    @ApiModelProperty("错误消息")
    private String msg;
    @ApiModelProperty("返回对象")
    private T data;

    /**
     * 检查操作是否成功。
     *
     * 该方法不接受参数，主要根据当前对象的 `code` 属性来判断操作是否成功。
     * `code` 属性为 null 或非0值时，均认为操作不成功。
     *
     * @return boolean - 如果操作成功，返回true；否则返回false。
     */
    public boolean isSuccess() {
        // 判断code是否非空且等于0，以确定操作是否成功
        return Objects.nonNull(this.code) && this.code == 0;
    }

}
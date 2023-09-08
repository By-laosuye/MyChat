package com.laosuye.mychat.common.websocket.domain.vo.resp;

import com.laosuye.mychat.common.websocket.domain.enums.WSRespTypeEnum;
import lombok.Data;

/**
 * @author laosuye
 * @version 1.0
 * @data 2023/9/08/16:03
 */
@Data
public class WSBaseResp<T> {
    /**
     * @see WSRespTypeEnum
     */
    private Integer type;
    private T data;
}

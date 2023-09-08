package com.laosuye.mychat.common.websocket.domain.vo.req;

import com.laosuye.mychat.common.websocket.domain.enums.WSReqTypeEnum;
import lombok.Data;

/**
 * @author laosuye
 * @version 1.0
 * @data 2023/9/08/15:57
 */
@Data
public class WSBaseReq {

    /**
     * @see WSReqTypeEnum
     */
    private Integer type;
    private String data;
}

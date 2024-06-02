package com.laosuye.mychat.common.user.service;

import com.laosuye.mychat.common.commm.domain.vo.request.CursorPageBaseReq;
import com.laosuye.mychat.common.commm.domain.vo.response.CursorPageBaseResp;
import com.laosuye.mychat.common.user.domain.entity.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laosuye.mychat.common.user.domain.vo.response.FriendResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2024-06-02
 */
public interface FriendService{


    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);
}

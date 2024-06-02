package com.laosuye.mychat.common.user.controller;


import com.laosuye.mychat.common.commm.domain.vo.request.CursorPageBaseReq;
import com.laosuye.mychat.common.commm.domain.vo.response.ApiResult;
import com.laosuye.mychat.common.commm.domain.vo.response.CursorPageBaseResp;
import com.laosuye.mychat.common.commm.util.RequestHolder;
import com.laosuye.mychat.common.user.domain.vo.response.FriendResp;
import com.laosuye.mychat.common.user.service.FriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 用户联系人表 前端控制器
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2024-06-02
 */
@Controller
@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "好友相关接口")
@Slf4j
public class FriendController {

    @Autowired
    private FriendService friendService;


    @GetMapping("page")
    @ApiOperation("联系人列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendList(uid, request));
    }

}


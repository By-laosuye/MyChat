package com.laosuye.mychat.common.user.controller;


import com.laosuye.mychat.common.commm.domain.vo.resp.ApiResult;
import com.laosuye.mychat.common.commm.util.AssertUtil;
import com.laosuye.mychat.common.commm.util.RequestHolder;
import com.laosuye.mychat.common.user.domain.enums.RoleEnum;
import com.laosuye.mychat.common.user.domain.vo.req.BlackReq;
import com.laosuye.mychat.common.user.domain.vo.req.ModifyNameReq;
import com.laosuye.mychat.common.user.domain.vo.req.WearingBadgeReq;
import com.laosuye.mychat.common.user.domain.vo.resp.BadgeResp;
import com.laosuye.mychat.common.user.domain.vo.resp.UserInfoResp;
import com.laosuye.mychat.common.user.service.IRoleService;
import com.laosuye.mychat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-09-09
 */
@RestController
@RequestMapping("/capi/user")
@Api(value = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private IRoleService roleService;


    @GetMapping("/userInfo")
    @ApiOperation("获取用户详细信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }


    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(),req.getName());
        return ApiResult.success();
    }


    @GetMapping("/badges")
    @ApiOperation("可选徽章列表预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(),req.getItemId());
        return ApiResult.success();
    }


    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<Void> black(@Valid @RequestBody BlackReq req) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower,"群聊管理员没有拉黑权限");
        userService.black(req);
        return ApiResult.success();
    }


}


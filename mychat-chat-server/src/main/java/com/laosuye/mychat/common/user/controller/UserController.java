package com.laosuye.mychat.common.user.controller;


import com.laosuye.mychat.common.user.domain.vo.resp.UserInfoResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/userInfo")
    @ApiOperation("获取用户详细信息")
    public UserInfoResp getUserInfo(@RequestParam Long uid) {
        return null;
    }

}


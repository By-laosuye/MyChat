package com.laosuye.mychat.common.user.service;

import com.laosuye.mychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laosuye.mychat.common.user.domain.vo.req.BlackReq;
import com.laosuye.mychat.common.user.domain.vo.resp.BadgeResp;
import com.laosuye.mychat.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-09-09
 */
public interface UserService {

    /**
     * 用户注册
     * @param insert 注册用户信息
     * @return 用户id
     */
    Long register(User insert);

    /**
     * 获取用户信息
     * @param uid 用户id
     * @return 用户信息
     */
    UserInfoResp getUserInfo(Long uid);

    /**
     * 修改名称
     * @param uid uid
     * @param name 名称
     */
    void modifyName(Long uid, String name);

    /**
     * 可选徽章列表
     * @param uid 用户id
     * @return 徽章列表
     */
    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     * @param uid 用户id
     * @param itemId 徽章id
     */
    void wearingBadge(Long uid, Long itemId);

    void black(BlackReq req);
}

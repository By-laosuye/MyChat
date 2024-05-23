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

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearingBadge(Long uid, Long itemId);

    void black(BlackReq req);
}

package com.laosuye.mychat.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.laosuye.mychat.common.commm.domain.enums.YesOrNoEnum;
import com.laosuye.mychat.common.user.domain.entity.ItemConfig;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.entity.UserBackpack;
import com.laosuye.mychat.common.user.domain.vo.resp.BadgeResp;
import com.laosuye.mychat.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * description  用户适配器
 * @author 老苏叶
 */
public class UserAdapter {
    /**
     * 构建保存用户信息
     * @param openId openId
     * @return 用户信息
     */
    public static User buildUserSave(String openId) {
        return User.builder().openId(openId).build();
    }

    /**
     * 构建用户授权信息
     * @param uid 用户id
     * @param userInfo 用户授权信息
     * @return 用户信息
     */
    public static User buildAuthorizeUser(Long uid, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(uid);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        return user;
    }

    /**
     * 构建用户信息
     * @param user 用户
     * @param modifyNameCount 修改昵称次数
     * @return 用户信息
     */
    public static UserInfoResp buildUserInfo(User user, Integer modifyNameCount) {
        UserInfoResp resp = new UserInfoResp();
        BeanUtil.copyProperties(user, resp);
        resp.setId(user.getId());
        resp.setModifyNameChance(modifyNameCount);
        return resp;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> itemConfigs, List<UserBackpack> backpacks, User user) {
        Set<Long> obtainItemSet = backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return itemConfigs.stream().map(a -> {
                    BadgeResp resp = new BadgeResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setObtain(obtainItemSet.contains(a.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    resp.setWearing(Objects.equals(a.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
                    return resp;
                }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                        .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}

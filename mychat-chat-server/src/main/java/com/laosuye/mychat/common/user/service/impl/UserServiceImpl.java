package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.commm.event.UserBlackEvent;
import com.laosuye.mychat.common.commm.event.UserRegisterEvent;
import com.laosuye.mychat.common.commm.util.AssertUtil;
import com.laosuye.mychat.common.user.dao.BlackDao;
import com.laosuye.mychat.common.user.dao.ItemConfigDao;
import com.laosuye.mychat.common.user.dao.UserBackpackDao;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.*;
import com.laosuye.mychat.common.user.domain.enums.BlackTypeEnum;
import com.laosuye.mychat.common.user.domain.enums.ItemEnum;
import com.laosuye.mychat.common.user.domain.enums.ItemTypeEnum;
import com.laosuye.mychat.common.user.domain.vo.req.BlackReq;
import com.laosuye.mychat.common.user.domain.vo.resp.BadgeResp;
import com.laosuye.mychat.common.user.domain.vo.resp.UserInfoResp;
import com.laosuye.mychat.common.user.service.UserService;
import com.laosuye.mychat.common.user.service.adapter.UserAdapter;
import com.laosuye.mychat.common.user.service.cache.ItemCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BlackDao blackDao;

    /**
     * 用户注册
     * @param insert 注册用户信息
     * @return 用户id
     */
    @Transactional
    @Override
    public Long register(User insert) {
        boolean save = userDao.save(insert);
        // 用户注册的事件
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, insert));
        return insert.getId();
    }

    /**
     * 获取用户信息
     * @param uid 用户id
     * @return 用户信息
     */
    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        //获取有效的徽章
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，换个名字吧");
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不够了，等后续获取改名卡活动吧！");
        //使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) {
            //改名
            userDao.modifyName(uid, name);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //查询用户佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, Long itemId) {
        //确保有徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem, "您还没有这个徽章，快去获取吧");
        //确保这东西是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "只有徽章才能佩戴");
        //佩戴徽章
        userDao.wearingBadge(uid, itemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black user = new Black();
        user.setType(BlackTypeEnum.UID.getType());
        user.setTarget(uid.toString());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    private void blackIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return;
        }
        try {
            Black inset = new Black();
            inset.setType(BlackTypeEnum.IP.getType());
            inset.setTarget(ip);
            blackDao.save(inset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

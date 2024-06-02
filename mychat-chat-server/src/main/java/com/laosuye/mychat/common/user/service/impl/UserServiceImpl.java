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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
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
     *
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
     *
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

    /**
     * 修改名称
     *
     * @param uid  uid
     * @param name 名称
     */
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

    /**
     * 可选徽章列表
     *
     * @param uid 用户id
     * @return 徽章列表
     */
    @Override
    public List<BadgeResp> badges(Long uid) {
        //从缓存中查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //查询用户佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    /**
     * 佩戴徽章
     *
     * @param uid    用户id
     * @param itemId 徽章id
     */
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

    /**
     * 将指定用户添加到黑名单中。
     * 此方法不仅将用户ID保存到黑名单表中，同时也会尝试屏蔽该用户相关的IP地址。
     * 并且，通过发布一个用户被屏蔽事件来通知其他感兴趣的组件或服务。
     *
     * @param req 包含需要被屏蔽的用户ID的信息。
     * @Transactional 注解确保此方法执行的过程中，所有的数据库操作都被视为一个单一的事务，
     * 并且在遇到异常时自动回滚。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void black(BlackReq req) {
        // 获取需要被屏蔽的用户ID
        Long uid = req.getUid();

        // 创建一个新的黑名单条目，标记类型为UID，并设置目标为用户ID
        Black user = new Black();
        user.setType(BlackTypeEnum.UID.getType());
        user.setTarget(uid.toString());
        // 将此黑名单条目保存到数据库
        blackDao.save(user);

        // 尝试获取用户的IP信息，以便后续可能的屏蔽操作
        User byId = userDao.getById(uid);

        // 尝试屏蔽用户的创建IP
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        // 尝试屏蔽用户的更新IP
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));

        // 发布一个用户被屏蔽事件，通知其他组件或服务
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }


    /**
     * 将指定的IP地址添加到黑名单中。
     * 如果IP地址为空或不合法，则不进行任何操作。
     * 添加IP地址到黑名单的过程是尝试性的，如果出现异常，则打印异常堆栈跟踪。
     *
     * @param ip 待添加到黑名单的IP地址。
     */
    private void blackIp(String ip) {
        // 检查IP地址是否为空或不合法，如果是，则直接返回。
        if (StringUtils.isBlank(ip)) {
            return;
        }
        try {
            // 创建一个新的黑名单记录对象。
            Black inset = new Black();
            // 设置黑名单记录的类型为IP。
            inset.setType(BlackTypeEnum.IP.getType());
            // 设置黑名单记录的目标为指定的IP地址。
            inset.setTarget(ip);
            // 尝试将新的黑名单记录保存到数据库中。
            blackDao.save(inset);
        } catch (Exception e) {
            // 如果在保存过程中发生异常，则打印异常信息。
            log.error("添加黑名单异常", e);
        }
    }

}

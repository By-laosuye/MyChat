package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.commm.domain.vo.request.CursorPageBaseReq;
import com.laosuye.mychat.common.commm.domain.vo.response.CursorPageBaseResp;
import com.laosuye.mychat.common.user.dao.FriendDao;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.Friend;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.vo.response.FriendResp;
import com.laosuye.mychat.common.user.service.FriendService;
import com.laosuye.mychat.common.user.service.adapter.FriendAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友相关服务
 */
@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private UserDao userDao;

    /**
     * 查询用户的友链分页信息。
     *
     * @param uid 用户ID，用于查询该用户的友链分页信息。
     * @param request 分页请求对象，包含分页参数。
     * @return 返回友链的分页响应对象，包含友链信息。
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        // 从数据库查询用户的友链分页信息
        CursorPageBaseResp<Friend> friendPage = friendDao.getFriendPage(uid,request);

        // 如果查询结果为空，则返回空的分页响应对象
        if (CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }

        // 提取友链中的用户ID列表
        List<Long> friendUids = friendPage.getList()
                .stream().map(Friend::getFriendUid)
                .collect(Collectors.toList());

        // 根据用户ID列表查询用户信息
        List<User> userList = userDao.getFriendList(friendUids);

        // 初始化分页响应对象，包含处理后的友链信息
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), userList));
    }
}

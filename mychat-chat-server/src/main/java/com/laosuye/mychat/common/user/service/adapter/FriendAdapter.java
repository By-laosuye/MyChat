package com.laosuye.mychat.common.user.service.adapter;

import com.laosuye.mychat.common.user.domain.entity.Friend;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.vo.response.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 好友适配器
 */
public class FriendAdapter {

    /**
     * 根据朋友列表和用户列表构建朋友响应对象列表。
     * 此方法通过映射用户列表到一个映射表中，然后根据朋友列表中的用户ID，提取对应的用户信息，
     * 最终构建并返回一个包含朋友信息的响应对象列表。
     *
     * @param list 朋友列表，每个朋友包含一个用户ID。
     * @param userList 用户列表，包含所有用户的详细信息。
     * @return 返回一个朋友响应对象列表，每个对象包含朋友的ID和活跃状态。
     */
    public static List<FriendResp> buildFriend(List<Friend> list, List<User> userList) {
        // 将用户列表转换为映射表，以用户ID为键，用户对象为值。
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));

        // 遍历朋友列表，为每个朋友构建响应对象，并填充相关信息。
        return list.stream().map(userFriend -> {
            FriendResp resp = new FriendResp();
            resp.setUid(userFriend.getFriendUid());
            // 从映射表中获取对应的朋友用户信息。
            User user = userMap.get(userFriend.getFriendUid());
            // 如果用户信息存在，则设置朋友的活跃状态。
            if (Objects.nonNull(user)) {
                resp.setActiveStatus(user.getActiveStatus());
            }
            return resp;
        }).collect(Collectors.toList());
    }
}

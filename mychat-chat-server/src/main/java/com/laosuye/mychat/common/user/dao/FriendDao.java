package com.laosuye.mychat.common.user.dao;

import com.laosuye.mychat.common.commm.domain.vo.request.CursorPageBaseReq;
import com.laosuye.mychat.common.commm.domain.vo.response.CursorPageBaseResp;
import com.laosuye.mychat.common.commm.util.CursorUtils;
import com.laosuye.mychat.common.user.domain.entity.Friend;
import com.laosuye.mychat.common.user.domain.vo.response.FriendResp;
import com.laosuye.mychat.common.user.mapper.FriendMapper;
import com.laosuye.mychat.common.user.service.FriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2024-06-02
 */
@Service
public class FriendDao extends ServiceImpl<FriendMapper, Friend> {

    /**
     * 获取好友分页信息。
     *
     * 通过游标方式从数据库查询指定用户的好友列表。此方法利用了CursorUtils工具类来简化分页查询的复杂性，
     * 并且支持高效的数据检索。传入的游标页面请求对象定义了查询的起始点和每页的大小。
     *
     * @param uid 用户ID，用于查询指定用户的好友列表。
     * @param cursorPageBaseReq 游标分页基础请求对象，包含分页参数如游标位置和每页项数。
     * @return 返回好友信息的游标页面响应对象，包含查询结果和新的游标位置。
     */
    public CursorPageBaseResp<Friend> getFriendPage(Long uid, CursorPageBaseReq cursorPageBaseReq) {
        // 使用CursorUtils的getCursorPageByMysql方法进行数据库查询。
        // 此方法封装了分页查询的逻辑，使得可以通过简单的调用获取分页数据。
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq,
                wrapper -> wrapper.eq(Friend::getUid, uid), Friend::getId);
    }

}

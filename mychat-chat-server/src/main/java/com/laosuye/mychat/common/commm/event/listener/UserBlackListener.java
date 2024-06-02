package com.laosuye.mychat.common.commm.event.listener;

import com.laosuye.mychat.common.commm.event.UserBlackEvent;
import com.laosuye.mychat.common.commm.event.UserRegisterEvent;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.enums.IdempotentEnum;
import com.laosuye.mychat.common.user.domain.enums.ItemEnum;
import com.laosuye.mychat.common.user.service.IUserBackpackService;
import com.laosuye.mychat.common.user.service.cache.UserCache;
import com.laosuye.mychat.common.websocket.service.WebSocketService;
import com.laosuye.mychat.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 拉黑用户监听器
 */
@Component
public class UserBlackListener {

    @Autowired
    private IUserBackpackService userBackpackService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserCache userCache;

    /**
     * 异步处理用户拉黑事件。
     * 此方法在用户拉黑操作的事务提交后触发，确保消息发送是在数据库操作完成后进行的。
     * 使用@Async注解使得方法异步执行，提高系统处理效率。
     * 使用@TransactionalEventListener注解将此方法注册为事务完成后的监听器。
     *
     * @param event 用户拉黑事件对象，包含执行拉黑操作的用户信息。
     *              通过此事件对象获取到被拉黑的用户信息，以便后续发送消息。
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendMsg(UserBlackEvent event) {
        // 从事件对象中获取被拉黑的用户信息
        User user = event.getUser();
        // 构建用户拉黑消息，并发送给所有在线用户
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }


    /**
     * 异步处理用户状态变更。
     * 此方法在用户相关事务提交后异步执行，用于处理用户黑名单操作。
     * 选择在事务提交后执行是为了确保用户状态的变更建立在所有相关事务操作成功完成的基础上。
     *
     * @param event 用户黑名单事件对象，包含需要操作的用户信息。
     * @Async 注解标识此方法为异步执行，允许方法在当前事务完成后独立执行。
     * @TransactionalEventListener 注解标识此方法为事务事件监听器，专门处理特定事务事件。
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStatus(UserBlackEvent event) {
        // 根据事件中用户ID，通过用户DAO层将该用户标记为无效。
        userDao.invalidUid(event.getUser().getId());
    }


    /**
     * 异步处理用户黑名单缓存刷新。
     * 此方法在用户黑名单操作事务提交后被异步调用，用于刷新用户黑名单的缓存。
     * 使用@TransactionalEventListener注解确保此方法在对应事件发生并提交事务后执行。
     * 使用@Async注解使方法异步执行，提高系统处理效率。
     *
     * @param event 用户黑名单事件对象，此处未直接使用，但注解指定了监听的事件类型。
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(UserBlackEvent event) {
        // 刷新用户黑名单缓存
        userCache.evictBlackMap();
    }


}

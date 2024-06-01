package com.laosuye.mychat.common.commm.event.listener;

import com.laosuye.mychat.common.commm.event.UserRegisterEvent;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.enums.IdempotentEnum;
import com.laosuye.mychat.common.user.domain.enums.ItemEnum;
import com.laosuye.mychat.common.user.service.IUserBackpackService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 用户注册事件监听器
 */
@Component
public class UserRegisterListener {

    @Autowired
    private IUserBackpackService userBackpackService;

    @Autowired
    private UserDao userDao;

    /**
     * 监听用户订阅，用户注册的时候进行发送改名卡
     * @Async 异步执行
     * 因为注册有事务，但是这个优先级没有用户注册的优先级高，所以这个执行时机为事务提交之后再执行
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    /**
     * 监听用户订阅，前100名注册用户发放的徽章
     * @Async 异步执行
     * 因为注册有事务，但是这个优先级没有用户注册的优先级高，所以这个执行时机为事务提交之后再执行
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        //前一百名注册徽章
        int registeredCount = userDao.count();
        if (registeredCount < 10){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }else if (registeredCount < 100){
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }

    }
}

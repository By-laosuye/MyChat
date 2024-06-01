package com.laosuye.mychat.common.commm.event.listener;

import com.laosuye.mychat.common.commm.event.UserOnlineEvent;
import com.laosuye.mychat.common.commm.event.UserRegisterEvent;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.enums.IdempotentEnum;
import com.laosuye.mychat.common.user.domain.enums.ItemEnum;
import com.laosuye.mychat.common.user.domain.enums.UserActiveStatusEnum;
import com.laosuye.mychat.common.user.service.IUserBackpackService;
import com.laosuye.mychat.common.user.service.IpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 用户上线事件监听器
 */
@Component
public class UserOnlineListener {

    @Autowired
    private IUserBackpackService userBackpackService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IpService ipService;

    /**
     * 异步处理用户上线事件，在事务提交后执行，如果执行失败则尝试重新执行。
     * 该方法用于更新用户在线状态和IP信息到数据库，并异步刷新IP详情。
     *
     * @param event 用户上线事件，包含需要更新的用户信息。
     */
    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void saveDB(UserOnlineEvent event) {
        User user = event.getUser(); // 从事件中获取用户信息
        User update = new User(); // 创建一个用户对象用于更新
        update.setId(user.getId()); // 设置用户ID
        update.setLastOptTime(user.getLastOptTime()); // 设置最后操作时间
        update.setIpInfo(user.getIpInfo()); // 设置IP信息
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getStatus()); // 设置用户活跃状态为在线
        userDao.updateById(update); // 更新用户信息到数据库
        ipService.refreshIpDetailAsync(user.getId()); // 异步刷新用户的IP详情
    }

}

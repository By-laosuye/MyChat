package com.laosuye.mychat.common.commm.event;

import com.laosuye.mychat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


/**
 * 用户上线事件
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {

    private User user;

    public UserOnlineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}

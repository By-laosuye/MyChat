package com.laosuye.mychat.common.commm.event;

import com.laosuye.mychat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 拉黑用户事件
 */
@Getter
public class UserBlackEvent extends ApplicationEvent {

    private User user;

    public UserBlackEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}

package com.laosuye.mychat.common.user.service;

import com.laosuye.mychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-09-09
 */
public interface UserService {

    Long register(User insert);
}

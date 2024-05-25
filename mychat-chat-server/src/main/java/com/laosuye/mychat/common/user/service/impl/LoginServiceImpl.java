package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.commm.constant.RedisKey;
import com.laosuye.mychat.common.commm.util.JwtUtils;
import com.laosuye.mychat.common.commm.util.RedisUtils;
import com.laosuye.mychat.common.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    /**
     * token过期时间
     */
    public static final int TOKEN_EXPIRE_DAYS = 3;
    public static final int TOKEN_RENEWAL_DAYS = 1;

    @Autowired
    private JwtUtils jwtUtils;


    /**
     * 刷新token 异步
     * @param token token
     */
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expire = RedisUtils.getExpire(userTokenKey, TimeUnit.DAYS);
        if (expire == -2) {
            return;
        }
        if (expire < TOKEN_RENEWAL_DAYS) {
            RedisUtils.expire(getUserTokenKey(uid), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    /**
     * 登录接口
     *
     * @param uid
     * @return
     */
    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 获取有效的uid
     * @param token
     * @return
     */
    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if (Objects.isNull(uid)) {
            return null;
        }
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        return Objects.equals(oldToken, token) ? uid : null;
    }

    /**
     * 从redis获取token
     * @param uid 用户id
     * @return token
     */
    private String getUserTokenKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
    }
}

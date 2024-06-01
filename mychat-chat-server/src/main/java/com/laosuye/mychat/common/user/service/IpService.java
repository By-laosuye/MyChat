package com.laosuye.mychat.common.user.service;

/**
 * IP服务
 */
public interface IpService {

    /**
     * 异步刷新用户IP信息
     * @param uid 用户ID
     */
    void refreshIpDetailAsync(Long uid);
}

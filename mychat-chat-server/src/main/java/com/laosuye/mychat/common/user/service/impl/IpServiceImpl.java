package com.laosuye.mychat.common.user.service.impl;


import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.laosuye.mychat.common.commm.domain.vo.resp.ApiResult;
import com.laosuye.mychat.common.commm.domain.vo.resp.IpResult;
import com.laosuye.mychat.common.commm.util.JsonUtils;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.IpDetail;
import com.laosuye.mychat.common.user.domain.entity.IpInfo;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.service.IpService;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IpServiceImpl implements IpService, DisposableBean {

    @Autowired
    private UserDao userDao;

    /**
     * 线程池
     */
    private static final ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS
            , new LinkedBlockingQueue<Runnable>(500), new NamedThreadFactory("refresh-ipDetail", false));


    /**
     * 异步刷新用户IP详情。
     * 此方法用于在后台线程中更新用户的IP详细信息，避免阻塞主线程，提高应用响应性。
     * 它首先尝试获取指定用户的IP信息，如果IP信息存在且需要刷新，则尝试三次获取IP的详细信息。
     * 如果能够成功获取到IP详细信息，则更新用户的IP信息并保存到数据库；如果获取失败，则记录错误日志。
     *
     * @param uid 用户ID，用于标识要刷新IP详情的用户。
     */
    @Override
    public void refreshIpDetailAsync(Long uid) {
        // 使用执行器在后台线程中执行任务
        executor.execute(() -> {
            // 根据用户ID获取用户对象
            User user = userDao.getById(uid);
            // 获取用户的IP信息
            IpInfo ipInfo = user.getIpInfo();
            // 如果IP信息为空，则直接返回，不进行后续操作
            if (Objects.isNull(ipInfo)) {
                return;
            }
            // 判断IP是否需要刷新
            String ip = ipInfo.needRefreshIp();
            if (StringUtil.isBlank(ip)) {
                return;
            }
            // 尝试三次获取IP的详细信息
            IpDetail ipDetail = tryGetIpDetailOrNullThreeTimes(ip);
            // 如果成功获取到IP详细信息
            if (Objects.nonNull(ipDetail)) {
                // 更新IP信息
                ipInfo.refreshIpDetail(ipDetail);
                // 创建一个新的用户对象用于更新
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                // 更新用户IP信息到数据库
                userDao.updateById(update);
                // TODO: 用户信息变更时，可能需要刷新缓存中的用户信息
                // userCache.userInfoChange(uid);
            } else {
                // 如果获取IP详细信息失败，记录错误日志
                log.error("get ip detail fail ip:{},uid:{}", ip, uid);
            }
        });
    }


    /**
     * 尝试三次获取IP详情。如果在三次尝试中任意一次成功获取到IP详情，则立即返回详情信息。
     * 如果三次尝试都未能成功获取IP详情，则返回null。
     * 在每次尝试失败之间，会暂停2秒钟。
     *
     * @param ip 需要查询详情的IP地址。
     * @return 成功获取到IP详情时返回详情对象，否则返回null。
     */
    private static IpDetail tryGetIpDetailOrNullThreeTimes(String ip) {
        // 尝试三次获取IP详情
        for (int i = 0; i < 3; i++) {
            // 尝试获取IP详情
            IpDetail ipDetail = getIpDetailOrNull(ip);
            // 如果成功获取到IP详情，则立即返回详情信息
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            // 如果未能获取到IP详情，尝试再次获取之前暂停2秒钟
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // 记录线程中断异常的日志信息
                log.error("tryGetIpDetailOrNullThreeTimes InterruptedException", e);
            }
        }
        // 如果三次尝试都未能获取到IP详情，则返回null
        return null;
    }


    /**
     * 根据IP地址获取详细的地理位置信息。
     * 通过调用淘宝IP接口，传入IP地址，返回该IP地址对应的具体地理位置信息。
     * 如果请求成功并返回有效数据，则解析数据并返回地理位置信息对象；否则返回null。
     *
     * @param ip 需要查询的IP地址
     * @return IP详情对象，包含地理位置信息；如果查询失败或数据解析异常，返回null
     */
    private static IpDetail getIpDetailOrNull(String ip) {
        // 构造请求URL，包含IP地址和访问密钥
        String url = "https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc";
        // 发起HTTP请求，获取IP地址信息的JSON数据
        String data = HttpUtil.get(url);
        try {
            // 将JSON数据解析为Java对象，此处使用了TypeReference来处理泛型
            IpResult<IpDetail> result = JSONUtil.toBean(data, new TypeReference<IpResult<IpDetail>>() {
            }, false);
            // 检查请求是否成功，如果成功则返回IP详情对象
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception e) {
            // 打印异常堆栈信息，用于问题排查
            e.printStackTrace();
        }
        // 如果请求失败或解析异常，返回null
        return null;
    }


    /**
     * 主函数：使用线程池执行任务，尝试获取指定IP的详细信息。
     * 该函数不接受参数且无返回值。
     * <p>
     * 主要步骤包括：
     * 1. 初始化开始时间。
     * 2. 循环执行100次任务。
     * 3. 每次任务会尝试三次获取IP详情，如果成功，则输出成功次数和当前耗时。
     */
    public static void main(String[] args) {
        Date beginTime = new Date(); // 记录程序开始执行的时间
        for (int i = 0; i < 100; i++) {
            int finalI = i; // 保存循环变量，以便在lambda表达式中使用
            executor.execute(() -> {
                // 尝试三次获取IP详情，如果成功，则进行下一步处理
                IpDetail ipDetail = tryGetIpDetailOrNullThreeTimes("117.85.113.4");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date(); // 获取当前时间
                    // 打印成功次数和从程序开始到当前任务执行成功的耗时
                    System.out.println(String.format("第%d次成功，目前耗时%dms", finalI, (date.getTime() - beginTime.getTime())));
                }
            });
        }
    }


    /**
     * 销毁执行器并等待其终止。
     * 该方法会尝试关闭执行器，并等待最多30秒以确保执行器成功关闭。
     * 如果在指定时间内执行器未能关闭，将会记录错误日志。
     *
     * @throws Exception 如果关闭执行器过程中发生错误，则抛出异常。
     */
    @Override
    public void destroy() throws Exception {
        executor.shutdown(); // 请求执行器关闭
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) { // 最多等待30秒以确保执行器关闭
            if (log.isErrorEnabled()) { // 如果日志记录器允许错误级别记录
                log.error("Timed out while waiting for executor [{}] to terminate", executor); // 记录未能按时关闭的错误
            }
        }
    }

}

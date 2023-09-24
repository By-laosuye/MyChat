package com.laosuye.mychat.common;


import com.laosuye.mychat.common.commm.util.JwtUtils;
import com.laosuye.mychat.common.user.dao.UserDao;

import com.laosuye.mychat.common.user.service.LoginService;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.service.WxService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private LoginService loginService;

    @Test
    public void testToken(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExMDAxLCJjcmVhdGVUaW1lIjoxNjk1NTYwODY3fQ.5QErCQpgi1z3GT8LBnGmj7m15hUwbFIdFt5sHAryAO4";
        Long validUid = loginService.getValidUid(token);
        System.out.println(validUid);
    }

    @Test
    public void testRedisson(){
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println("+++++++++++++++++++");
        lock.unlock();
    }


    @Test
    public void redis() {
        redisTemplate.opsForValue().set("name","卷心菜");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name); //卷心菜
    }



    @Test
    public void testJwt(){
        System.out.println(jwtUtils.createToken(1L)     );
        System.out.println(jwtUtils.createToken(1L));
        System.out.println(jwtUtils.createToken(1L));

    }

    @SneakyThrows
    @Test
    public void testUserDao() {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(11, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }

}

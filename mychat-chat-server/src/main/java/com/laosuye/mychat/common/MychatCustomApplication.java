package com.laosuye.mychat.common;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author zhongzb
 * @date 2021/05/27
 */
@SpringBootApplication(scanBasePackages = {"com.laosuye.mychat"})
//@MapperScan({"com.abin.mallchat.common.**.mapper"})
//@ServletComponentScan
public class MychatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(MychatCustomApplication.class,args);
    }

}
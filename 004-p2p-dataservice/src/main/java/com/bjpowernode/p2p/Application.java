package com.bjpowernode.p2p;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDubboConfiguration   //开启dubbo配置
@MapperScan(basePackages = "com.bjpowernode.p2p.mapper")
@EnableTransactionManagement    //开户事务(可选项)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

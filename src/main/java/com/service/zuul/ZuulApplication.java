package com.service.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author : pc
 * @date : 2020/7/17
 */
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableFeignClients(basePackages = "com.module.*.client")
@MapperScan(basePackages = {"com.service.auth.mapper"})
@ComponentScan(basePackages = {"com.scottxuan","com.module","com.service.zuul"})
public class ZuulApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class,args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ZuulApplication.class);
    }
}

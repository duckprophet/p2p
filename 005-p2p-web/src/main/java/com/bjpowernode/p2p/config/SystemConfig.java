package com.bjpowernode.p2p.config;

import com.bjpowernode.p2p.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName:SystemConfig
 * Package:com.bjpowernode.p2p.config
 * Description:
 *
 * @date:2020/4/13 14:40
 * @author:动力节点
 */
@Configuration
public class SystemConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //拦截的路径
        String[] addPathPatterns = {
                "/loan/**"
        };

        //排除的路径
        String[] excludePathPatterns = {
                "/loan/loan",
                "/loan/loanInfo",
                "/loan/page/register",
                "/loan/checkPhone",
                "/loan/register",
                "/loan/messageCode",
                "/loan/page/login",
                "/loan/login"
        };

        registry.addInterceptor(new UserInterceptor()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }
}

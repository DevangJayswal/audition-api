package com.audition.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ResponseHeaderInjector responseHeaderInjector;

    public WebConfig(ResponseHeaderInjector responseHeaderInjector) {
        this.responseHeaderInjector = responseHeaderInjector;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(responseHeaderInjector);
    }
}

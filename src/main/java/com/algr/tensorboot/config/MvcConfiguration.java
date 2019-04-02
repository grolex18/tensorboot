package com.algr.tensorboot.config;

import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.algr.tensorboot.filter.MDCFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    private static final int TOMCAT_RESOURCES_CACHE_SIZE = 40 * 1024;

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean mdc(MDCFilter mdc) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(mdc);
        registration.addUrlPatterns("/*");
        registration.setName("mdc");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public ConfigurableTomcatWebServerFactory servletContainer() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                StandardRoot standardRoot = new StandardRoot(context);
                standardRoot.setCacheMaxSize(TOMCAT_RESOURCES_CACHE_SIZE);
                context.setResources(standardRoot);
                log.info(String.format("New cache size (KB): %d", context.getResources().getCacheMaxSize()));
            }
        };
    }
}

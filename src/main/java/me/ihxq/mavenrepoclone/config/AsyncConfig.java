package me.ihxq.mavenrepoclone.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xq.h
 * 2019/12/3 15:28
 **/
@Configuration
@Slf4j
@ConfigurationProperties(prefix = "mrc.async")
public class AsyncConfig implements org.springframework.scheduling.annotation.AsyncConfigurer {
    private ThreadPoolProperties threadPoolProperties = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {
        private int corePoolSize = 10;
        private int maxPoolSize = 20;
        private int queueCapacity = 5;
        private String threadNamePrefix = "Async-";
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("error occurred in async thread: {}, params: {}", method, params, ex);
    }
}

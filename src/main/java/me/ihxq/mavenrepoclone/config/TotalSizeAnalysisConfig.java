package me.ihxq.mavenrepoclone.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import me.ihxq.mavenrepoclone.model.Item;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;

/**
 * @author xq.h
 * 2019/12/3 15:17
 **/
@Data
@ConfigurationProperties(prefix = "mrc.total-size")
public class TotalSizeAnalysisConfig implements InitializingBean {
    @Setter(AccessLevel.NONE)
    private Predicate<Item> docPredicate;
    @Setter(AccessLevel.NONE)
    private Predicate<Item> sourcePredicate;
    @Setter(AccessLevel.NONE)
    private ThreadPoolTaskExecutor executor;

    private boolean includeSource = true;
    private boolean includeDoc = true;

    private ThreadPoolProperties threadPoolProperties = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {
        private int corePoolSize = 60;
        private int maxPoolSize = 80;
        private int queueCapacity = 20;
        private String threadNamePrefix = "TSA-";
    }

    @Override
    public void afterPropertiesSet() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        executor.initialize();

        docPredicate = v -> {
            if (includeSource) {
                return !v.getName().contains("javadoc.jar");
            } else {
                return true;
            }
        };

        sourcePredicate = v -> {
            if (includeDoc) {
                return !v.getName().contains("sources.jar");
            } else {
                return true;
            }
        };
    }
}

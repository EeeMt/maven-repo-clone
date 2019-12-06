package me.ihxq.mavenrepoclone.Runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.constants.UrlConstant;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.InOutWrapper;
import me.ihxq.mavenrepoclone.processor.impl.TotalSizeProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static me.ihxq.mavenrepoclone.util.FileSizeUtil.humanReadableByteCount;

/**
 * @author xq.h
 * 2019/12/1 20:52
 **/
@Slf4j
@Service
public class TotalSizeRunner {
    private final TotalSizeProcessor totalSizeProcessor;

    public TotalSizeRunner(TotalSizeProcessor totalSizeProcessor) {
        this.totalSizeProcessor = totalSizeProcessor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterPropertiesSet() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        log.info("started {}", start);
        AtomicLong result = new AtomicLong();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            long bytes = result.get();
            log.info("current total: {}/{}", bytes, humanReadableByteCount(bytes, true));
        }, 2, 10, TimeUnit.SECONDS);
        Directory entrance = Directory.builder()
                .depth(0)
                .name("maven2/")
                .time(null)
                .url(UrlConstant.BASE_URL.compact())
                .build();
        InOutWrapper<Directory, AtomicLong> wrapper = InOutWrapper.of(entrance, result);
        totalSizeProcessor.process(wrapper).get();
        long total = result.get();
        Duration duration = Duration.between(LocalDateTime.now(), start);
        log.info("finished {}/{} {}", total, humanReadableByteCount(total, true), duration);
    }
}

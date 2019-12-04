package me.ihxq.mavenrepoclone.Runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.constants.UrlConstant;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.processor.impl.TotalSizeProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Future;

import static me.ihxq.mavenrepoclone.util.FileSizeUtil.humanReadableByteCount;

/**
 * @author xq.h
 * 2019/12/1 20:52
 **/
@Slf4j
@Service
public class TotalSizeRunner implements InitializingBean {
    private final TotalSizeProcessor totalSizeProcessor;

    public TotalSizeRunner(TotalSizeProcessor totalSizeProcessor) {
        this.totalSizeProcessor = totalSizeProcessor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("started {}", LocalDateTime.now());
        Directory entrance = Directory.builder()
                .depth(0)
                .name("maven2/")
                .time(null)
                .url(UrlConstant.BASE_URL.compact())
                .build();
        Future<Long> process = totalSizeProcessor.process(entrance);
        Long total = process.get();
        log.info("finished {} {}", humanReadableByteCount(total, true), LocalDateTime.now());
    }
}

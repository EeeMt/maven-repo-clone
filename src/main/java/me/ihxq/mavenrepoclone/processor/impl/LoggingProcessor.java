package me.ihxq.mavenrepoclone.processor.impl;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.processor.Processor;
import org.springframework.stereotype.Service;

/**
 * @author xq.h
 * 2019/12/3 15:52
 **/
@Slf4j
@Service
public class LoggingProcessor implements Processor<String, Void> {
    @Override
    public Void process(String in) throws Exception {
        return null;
    }
}

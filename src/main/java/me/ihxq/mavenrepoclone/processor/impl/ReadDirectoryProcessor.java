package me.ihxq.mavenrepoclone.processor.impl;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.Item;
import me.ihxq.mavenrepoclone.parser.SimpleParser;
import me.ihxq.mavenrepoclone.processor.Processor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author xq.h
 * 2019/12/1 21:00
 **/
@Slf4j
@Service
public class ReadDirectoryProcessor implements Processor<Directory, List<Item>> {
    private  final SimpleParser parser ;

    public ReadDirectoryProcessor(SimpleParser parser) {
        this.parser = parser;
    }

    @Override
    public List<Item> process(Directory in) {
        try {
            return parser.parse(in.getUrl());
        } catch (Exception e) {
            log.error("error: {}", in, e);
        }
        return Collections.emptyList();
    }
}

package me.ihxq.mavenrepoclone.processor;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.Item;
import me.ihxq.mavenrepoclone.parser.SimpleParser;

import java.util.Collections;
import java.util.List;

/**
 * @author xq.h
 * 2019/12/1 21:00
 **/
@Slf4j
public class ReadDirectoryProcessor implements Processor<Directory, List<Item>> {
    private SimpleParser parser = new SimpleParser();

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

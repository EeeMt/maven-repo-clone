package me.ihxq.mavenrepoclone.Runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.constants.UrlConstant;
import me.ihxq.mavenrepoclone.enums.ItemType;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.Item;
import me.ihxq.mavenrepoclone.parser.SimpleParser;
import me.ihxq.mavenrepoclone.processor.ReadDirectoryProcessor;
import me.ihxq.mavenrepoclone.processor.TotalSizeProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static me.ihxq.mavenrepoclone.util.FileSizeUtil.humanReadableByteCount;

/**
 * @author xq.h
 * 2019/12/1 20:52
 **/
@Slf4j
public class Starter {


    public static void main(String[] args) throws Exception {

        Starter starter = new Starter();
        starter.start();
    }


    public void start() throws Exception {
        log.info("started {}", LocalDateTime.now());
        SimpleParser simpleParser = new SimpleParser();
        TotalSizeProcessor totalSizeProcessor = new TotalSizeProcessor(new ReadDirectoryProcessor());
        List<Item> items = simpleParser.parse(UrlConstant.HOST_URL.compact());
        for (Item item : items) {
            ItemType itemType = item.getItemType();
            switch (itemType) {
                case FILE:
                    log.error("error");
                    break;
                case DIRECTORY:
                    item.setUrl(UrlConstant.BASE_URL.compact());
                    Long total = totalSizeProcessor.process((Directory) item);
                    System.out.println("total: " + total);
                    log.info("finished {} {}", humanReadableByteCount(total, true), LocalDateTime.now());
                    break;
                default:

            }
        }
    }
}

package me.ihxq.mavenrepoclone.processor;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.enums.ItemType;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.FileItem;
import me.ihxq.mavenrepoclone.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static me.ihxq.mavenrepoclone.enums.ItemType.DIRECTORY;
import static me.ihxq.mavenrepoclone.enums.ItemType.FILE;
import static me.ihxq.mavenrepoclone.util.FileSizeUtil.humanReadableByteCount;

/**
 * @author xq.h
 * 2019/12/1 21:15
 **/
@Slf4j
public class TotalSizeProcessor implements Processor<Directory, Long> {
    private Processor<Directory, List<Item>> readDirectoryProcessor;
    private static ThreadPoolExecutor executor;

    public TotalSizeProcessor(Processor<Directory, List<Item>> readDirectoryProcessor) {
        this.readDirectoryProcessor = readDirectoryProcessor;
        executor = new ThreadPoolExecutor(20, 20,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10));
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
    }

    private long logTotal = 0L;
    private long countTotal = 0L;


    @Override
    public Long process(Directory in) throws Exception {
        List<Item> itemList = readDirectoryProcessor.process(in);
        AtomicReference<Long> total = new AtomicReference<>(0L);
        for (Item item : itemList) {
            ItemType itemType = item.getItemType();
            if (itemType.equals(DIRECTORY)) {
                log.trace("processing directory: {}", item.getUrl());
                Directory directory = (Directory) item;
                executor.submit(() -> {
                    try {
                        Long subTotal = this.process(directory);
                        total.updateAndGet(v -> v + subTotal);
                    } catch (Exception e) {
                        log.error("error", e);
                    }
                });
            } else if (itemType.equals(FILE)) {
                FileItem fileItem = (FileItem) item;
                String currentFile = item.getUrl() + fileItem.getName();
                log.trace("processing file: {}", currentFile);
                long size = Optional.ofNullable(fileItem.getSize()).orElse(0L);
                total.updateAndGet(v -> v + size);
                String readableSize = humanReadableByteCount(fileItem.getSize(), true);
                log.trace("file size: {} / {} / {}; {}", fileItem.getName(), fileItem.getSize(), readableSize, countTotal);
                logTotal += size;
                countTotal++;
                String readableTotal = humanReadableByteCount(logTotal, true);
                log.trace("total: {} / {}", logTotal, readableTotal);
                System.out.print(String.format("\r total: %s, current: %s, current file size: %s",
                        readableTotal, currentFile, readableSize));
                //System.out.print(String.format("\r%d%%", i + 1));
            }
        }
        return total.get();
    }
}

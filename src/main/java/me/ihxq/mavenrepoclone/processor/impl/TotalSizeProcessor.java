package me.ihxq.mavenrepoclone.processor.impl;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.config.TotalSizeAnalysisConfig;
import me.ihxq.mavenrepoclone.enums.ItemType;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.FileItem;
import me.ihxq.mavenrepoclone.model.InOutWrapper;
import me.ihxq.mavenrepoclone.model.Item;
import me.ihxq.mavenrepoclone.processor.Processor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import static me.ihxq.mavenrepoclone.enums.ItemType.DIRECTORY;
import static me.ihxq.mavenrepoclone.enums.ItemType.FILE;
import static me.ihxq.mavenrepoclone.util.FileSizeUtil.humanReadableByteCount;

/**
 * @author xq.h
 * 2019/12/1 21:15
 **/
@Slf4j
@Service
public class TotalSizeProcessor implements Processor<InOutWrapper<Directory, AtomicLong>, CompletableFuture<Void>> {
    private Processor<Directory, List<Item>> readDirectoryProcessor;
    private final TotalSizeAnalysisConfig totalSizeAnalysisConfig;
    private ThreadPoolTaskExecutor executor;

    public TotalSizeProcessor(Processor<Directory, List<Item>> readDirectoryProcessor,
                              TotalSizeAnalysisConfig totalSizeAnalysisConfig) {
        this.readDirectoryProcessor = readDirectoryProcessor;
        this.totalSizeAnalysisConfig = totalSizeAnalysisConfig;
        this.executor = totalSizeAnalysisConfig.getExecutor();
    }

    private List<Item> readDirectory(Directory in) {
        try {
            return readDirectoryProcessor.process(in);
        } catch (Exception e) {
            log.error("read directory error: {}", in, e);
            return Collections.emptyList();
        }
    }

    private void processFile(InOutWrapper<FileItem, AtomicLong> wrapper) {
        FileItem in = wrapper.getIn();
        AtomicLong out = wrapper.getOut();
        try {
            String currentFile = in.getUrl() + in.getName();
            log.trace("processing file: {}", currentFile);
            long size = Optional.ofNullable(in.getSize()).orElse(0L);
            if (log.isTraceEnabled()) {
                String readableSize = humanReadableByteCount(in.getSize(), true);
                log.trace("file size: {} / {} / {};", in.getName(), in.getSize(), readableSize);
            }
            out.updateAndGet(v -> v + size);
        } catch (Exception e) {
            log.error("error", e);
        }
    }

    @Override
    public CompletableFuture<Void> process(InOutWrapper<Directory, AtomicLong> wrapper) {
        Directory directory = wrapper.getIn();
        if (directory.getDepth() <= totalSizeAnalysisConfig.getDirectoryLoggingMaxDepth()) {
            log.info("processing directory:  {}",  directory.getPath());
        }
        //System.out.printf("\rprocessing directory(%s): %s \r", Thread.currentThread().getName(), in.getPath());
        return CompletableFuture.supplyAsync(() -> this.readDirectory(directory), executor)
                .thenAcceptAsync(itemList -> itemList.stream()
                        .filter(totalSizeAnalysisConfig.getDocPredicate())
                        .filter(totalSizeAnalysisConfig.getSourcePredicate())
                        .forEach(item -> {
                            ItemType itemType = item.getItemType();
                            if (itemType.equals(DIRECTORY)) {
                                log.trace("processing directory: {}", item.getUrl());
                                InOutWrapper<Directory, AtomicLong> sub = InOutWrapper.of((Directory) item, wrapper.getOut());
                                CompletableFuture.runAsync(() -> process(sub), executor);
                            } else if (itemType.equals(FILE)) {
                                log.trace("processing file: {}", item.getUrl());
                                InOutWrapper<FileItem, AtomicLong> sub = InOutWrapper.of((FileItem) item, wrapper.getOut());
                                CompletableFuture.runAsync(() -> processFile(sub), executor);
                            }
                        }), executor);
    }

    private long unboxingFuture(CompletableFuture<Long> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("error unboxing: {}", future, e);
            return 0L;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.print("\033[H\033[2J");
        System.out.println(1000000L);
        Thread.sleep(2000L);
        Runtime.getRuntime().exec("clear");
        clearConsole();
        clearScreen();
        System.out.print("\033[H\033[2J");
        System.out.println(2000000L);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }
}

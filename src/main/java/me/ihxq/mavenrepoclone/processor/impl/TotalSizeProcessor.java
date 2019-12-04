package me.ihxq.mavenrepoclone.processor.impl;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.config.TotalSizeAnalysisConfig;
import me.ihxq.mavenrepoclone.enums.ItemType;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.FileItem;
import me.ihxq.mavenrepoclone.model.Item;
import me.ihxq.mavenrepoclone.processor.Processor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static me.ihxq.mavenrepoclone.enums.ItemType.DIRECTORY;
import static me.ihxq.mavenrepoclone.enums.ItemType.FILE;
import static me.ihxq.mavenrepoclone.util.FileSizeUtil.humanReadableByteCount;

/**
 * @author xq.h
 * 2019/12/1 21:15
 **/
@Slf4j
@Service
public class TotalSizeProcessor implements Processor<Directory, CompletableFuture<Long>> {
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

    private CompletableFuture<Long> processDirectory(Directory in) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.process(in).get();
            } catch (Exception e) {
                log.error("error processing directory", e);
                return 0L;
            }
        }, executor);
    }

    private CompletableFuture<Long> processFile(FileItem in) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String currentFile = in.getUrl() + in.getName();
                log.trace("processing file: {}", currentFile);
                long size = Optional.ofNullable(in.getSize()).orElse(0L);
                String readableSize = humanReadableByteCount(in.getSize(), true);
                log.trace("file size: {} / {} / {};", in.getName(), in.getSize(), readableSize);
                return size;
            } catch (Exception e) {
                log.error("error", e);
                return 0L;
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Long> process(Directory in) {
        List<Item> itemList = this.readDirectory(in);
        List<CompletableFuture<Long>> futures = itemList.stream()
                .filter(totalSizeAnalysisConfig.getDocPredicate())
                .filter(totalSizeAnalysisConfig.getSourcePredicate())
                .map(item -> {
                    ItemType itemType = item.getItemType();
                    if (itemType.equals(DIRECTORY)) {
                        log.trace("processing directory: {}", item.getUrl());
                        return process((Directory) item);
                    } else if (itemType.equals(FILE)) {
                        log.trace("processing file: {}", item.getUrl());
                        return processFile((FileItem) item);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("processed directory: {}", in.getPath());
            return futures.stream()
                    .mapToLong(this::unboxingFuture)
                    .sum();
        });
    }

    private long unboxingFuture(CompletableFuture<Long> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("error unboxing: {}", future, e);
            return 0L;
        }
    }
}

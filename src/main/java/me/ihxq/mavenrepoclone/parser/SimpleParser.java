package me.ihxq.mavenrepoclone.parser;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.model.FileItem;
import me.ihxq.mavenrepoclone.model.Item;
import me.ihxq.mavenrepoclone.processor.impl.HtmlFetchProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xq.h
 * 2019/12/1 21:27
 **/
@Slf4j
@Service
public class SimpleParser {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final HtmlFetchProcessor htmlFetchProcessor;

    public SimpleParser(HtmlFetchProcessor htmlFetchProcessor) {
        this.htmlFetchProcessor = htmlFetchProcessor;
    }

    public List<Item> parse(String url) throws IOException {
        String html = htmlFetchProcessor.process(url);
        Elements elements = Jsoup.parse(html).select("pre > a");
        List<Item> items = new ArrayList<>();
        for (Element element : elements) {
            try {
                this.doParse(url, element).ifPresent(items::add);
            } catch (Exception e) {
                log.error("parse error", e);
            }
        }
        return items;
    }

    private Optional<Item> doParse(String url, Element element) {
        String name = element.text();
        if (name.equals("../")) {
            return Optional.empty();
        }
        String href = element.attr("href");
        String itemUrl = url + href;
        Node node = element.nextSibling();
        String text = node.outerHtml().replaceAll("\n", "");
        List<String> collect = Arrays.stream(text.split(" "))
                .filter(v -> !Objects.equals("", v))
                .filter(v -> !Objects.equals(" ", v))
                .map(String::trim)
                .filter(v -> !Objects.equals("", v))
                .filter(v -> !Objects.equals(" ", v))
                .collect(Collectors.toList());
        LocalDateTime time = null;
        String timeStr = "-";
        String sizeStr = "-";
        if (collect.size() == 3) {
            timeStr = collect.get(0) + " " + collect.get(1);
            sizeStr = collect.get(2);
        } else if (collect.size() == 2
                && !Objects.equals(collect.get(0), "-")
                && !Objects.equals(collect.get(1), "-")) {
            timeStr = (collect.get(0) + " " + collect.get(1));
        }

        if (!timeStr.equals("-")) {
            try {
                time = LocalDateTime.parse(timeStr, DATE_TIME_FORMATTER);
            } catch (Exception e) {
                log.error("parse time failed: {}", timeStr, e);
            }
        }

        long size = 0;
        if (!sizeStr.equals("-")) {
            size = Long.parseLong(sizeStr);
        }
        if (name.endsWith("/")) {
            Directory build = Directory.builder()
                    .name(name)
                    .url(itemUrl)
                    .time(time)
                    //.depth()
                    .build();
            return Optional.of(build);
        } else {
            return Optional.of(FileItem.of(name, itemUrl, time, size));
        }
    }

}

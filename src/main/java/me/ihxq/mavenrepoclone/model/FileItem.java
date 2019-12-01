package me.ihxq.mavenrepoclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.ihxq.mavenrepoclone.enums.ItemType;

import java.time.LocalDateTime;

/**
 * @author xq.h
 * 2019/12/1 20:53
 **/
@Data
@AllArgsConstructor
public class FileItem implements Item {
    private String name;
    private String url;
    private LocalDateTime time;
    private Long size;

    public static FileItem of(String name, String url, LocalDateTime time, long size) {
        return new FileItem(name, url, time, size);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.FILE;
    }
}

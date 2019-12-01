package me.ihxq.mavenrepoclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.ihxq.mavenrepoclone.enums.ItemType;

import java.time.LocalDateTime;

/**
 * @author xq.h
 * 2019/12/1 21:01
 **/
@Data
@AllArgsConstructor
public class Directory implements Item {
    private String name;
    private String url;
    private LocalDateTime time;

    public static Directory of(String name, String url, LocalDateTime time) {
        return new Directory(name, url, time);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DIRECTORY;
    }
}

package me.ihxq.mavenrepoclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.ihxq.mavenrepoclone.constants.UrlConstant;
import me.ihxq.mavenrepoclone.enums.ItemType;

import java.time.LocalDateTime;

/**
 * @author xq.h
 * 2019/12/1 21:01
 **/
@Data
@Builder
@AllArgsConstructor
public class Directory implements Item {
    private String name;
    private String url;
    private LocalDateTime time;
    private int depth;

    public String getPath() {
        if (url == null) {
            return null;
        }
        return url.replace(UrlConstant.BASE_URL.getValue(), "/");
    }

    @Override
    public ItemType getItemType() {
        return ItemType.DIRECTORY;
    }
}

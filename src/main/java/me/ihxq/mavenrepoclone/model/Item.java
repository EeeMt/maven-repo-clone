package me.ihxq.mavenrepoclone.model;

import me.ihxq.mavenrepoclone.enums.ItemType;

/**
 * @author xq.h
 * 2019/12/1 20:53
 **/
public interface Item {
    ItemType getItemType();

    String getUrl();

    void setUrl(String url);
}

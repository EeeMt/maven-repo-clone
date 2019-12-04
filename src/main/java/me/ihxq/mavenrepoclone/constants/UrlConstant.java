package me.ihxq.mavenrepoclone.constants;

import lombok.Getter;

/**
 * @author xq.h
 * 2019/12/1 21:13
 **/
public enum UrlConstant {
    HOST_URL("https://repo1.maven.org/"),
    BASE_URL(HOST_URL.compact("maven2/"));

    @Getter
    private String value;

    UrlConstant(String value) {
        this.value = value;
    }

    public String compact(String uri) {
        return this.getValue() + uri;
    }

    public String compact() {
        return this.getValue();
    }
}

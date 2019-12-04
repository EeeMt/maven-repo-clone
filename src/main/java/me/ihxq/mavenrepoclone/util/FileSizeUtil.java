package me.ihxq.mavenrepoclone.util;

import lombok.SneakyThrows;

import java.io.IOException;

/**
 * @author xq.h
 * 2019/12/1 22:33
 **/
public class FileSizeUtil {
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        //noinspection SpellCheckingInspection
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @SneakyThrows
    public static void main(String[] args) {
        String size = humanReadableByteCount(Long.MAX_VALUE, true);
        System.out.println(size);
        throw new IOException("");
    }
}

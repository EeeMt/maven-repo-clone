package me.ihxq.mavenrepoclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xq.h
 * 2019/12/5 12:48
 **/
@Data
@AllArgsConstructor
public class InOutWrapper<IN, OUT> {
    private IN in;
    private OUT out;

    public static <IN, OUT> InOutWrapper<IN, OUT> of(IN in, OUT out) {
        return new InOutWrapper<>(in, out);
    }
}

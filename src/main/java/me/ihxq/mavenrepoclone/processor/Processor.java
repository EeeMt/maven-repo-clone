package me.ihxq.mavenrepoclone.processor;

/**
 * @author xq.h
 * on 2019/12/1 21:16
 **/
@FunctionalInterface
public interface Processor<T, R> {
    R process(T in) throws Exception;
}

package me.ihxq.mavenrepoclone.operator;

/**
 * @author xq.h
 * 2019/12/3 15:47
 **/
@FunctionalInterface
public interface Operator<T, R> {

    R calculate(T in);
}

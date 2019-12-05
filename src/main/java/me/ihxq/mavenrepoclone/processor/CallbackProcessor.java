package me.ihxq.mavenrepoclone.processor;

import java.util.function.Consumer;

/**
 * @author xq.h
 * on 2019/12/1 21:16
 **/
@FunctionalInterface
public interface CallbackProcessor<T, R> extends Processor<T, R> {
    R process(T in) throws Exception;

    default void callbackProcess(T in, Consumer<R> callback) throws Exception {
        callback.accept(this.process(in));
    }

    ;
}

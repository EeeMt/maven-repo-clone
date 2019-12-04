package me.ihxq.mavenrepoclone.operator.impl;

import me.ihxq.mavenrepoclone.model.Directory;
import me.ihxq.mavenrepoclone.operator.Operator;

import java.util.concurrent.Future;

/**
 * @author xq.h
 * 2019/12/3 16:15
 **/
public class AnalysisCurrentDirectoryFileTotalSize implements Operator<Directory, Future<Long>> {
    @Override
    public Future<Long> calculate(Directory in) {
        return null;
    }
}

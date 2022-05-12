package com.zj.processes;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 对应xml中serviceTask的class地址
 * 用于实现流程中的自定义逻辑，通常设计为无状态低耦合的通用rpc调用
 */
public class MyDiyDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println("MyDiyDelegate executing here");
    }
}

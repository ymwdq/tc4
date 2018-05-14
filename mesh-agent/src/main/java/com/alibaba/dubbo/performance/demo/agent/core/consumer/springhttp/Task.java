package com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp;

import org.springframework.web.context.request.async.DeferredResult;


public class Task {
    private DeferredResult<Integer> deferredResult;
    private int id;

    public Task(DeferredResult<Integer> deferredResult) {
        this.deferredResult = deferredResult;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setResult(Object obj) {
        this.deferredResult.setResult((Integer)obj);
    }

//    public void printSendTime() {
//        System.out.println("" + this.getId() + "send: " + System.currentTimeMillis());
//    }
//
//    public void printRevTime() {
//        System.out.println("" + this.getId() + "recv: " + System.currentTimeMillis());
//    }

}

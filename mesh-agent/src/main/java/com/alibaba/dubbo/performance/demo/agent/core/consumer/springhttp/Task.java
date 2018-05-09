package com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp;

import org.springframework.web.context.request.async.DeferredResult;


public class Task {
    private DeferredResult deferredResult;
    private int id;

    public Task(DeferredResult deferredResult) {
        this.deferredResult = deferredResult;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setResult(Object obj) {
        this.deferredResult.setResult(obj);
    }

}

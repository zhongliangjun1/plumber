/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.core.monitor;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.utils.TimeUtils;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: Monitor.java, v 0.1 5/23/15 11:33 PM liangjun.zhong Exp $$
 */
public abstract class Monitor {

    private static final LinkedBlockingQueue<MonitorEvent> eventsCacheQueue = new LinkedBlockingQueue<MonitorEvent>();

    public static void listen(MonitorEvent event) throws InterruptedException {
        eventsCacheQueue.offer(event,
            TimeUtils.getRemainingTime(event.getStartTime(), PlumberConfig.getResponseTimeout()),
            TimeUnit.MILLISECONDS);
    }

    public static void signal(String pipeName, String renderResult, MonitorEvent event) {
        List<MonitorNode> monitorNodes = event.getMonitorNodes();
        for (MonitorNode monitorNode : monitorNodes) {
            if (pipeName.equals(monitorNode.getPipeName())) {
                monitorNode.setNodeStatus(Status.SUBMIT);
                monitorNode.setRenderResult(renderResult);
                break;
            }
        }
    }

}

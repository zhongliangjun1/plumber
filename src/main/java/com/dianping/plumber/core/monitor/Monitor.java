/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.core.monitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.utils.CollectionUtils;
import com.dianping.plumber.utils.TimeUtils;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: Monitor.java, v 0.1 5/23/15 11:33 PM liangjun.zhong Exp $$
 */
public abstract class Monitor {

    private static final Logger                            logger            = Logger
                                                                                 .getLogger(Monitor.class);
    private static final LinkedBlockingQueue<MonitorEvent> eventsCacheQueue  = new LinkedBlockingQueue<MonitorEvent>();
    private static final List<MonitorEvent>                monitorEvents     = new LinkedList<MonitorEvent>();
    private static final List<MonitorEvent>                ineffectiveEvents = new ArrayList<MonitorEvent>();

    static {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                // event loop
                while (true) {
                    add();
                    monitor();
                    clear();
                }

            }
        });
    }

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

    private static void add() {
        MonitorEvent newMonitorEvent;

        if (CollectionUtils.isEmpty(monitorEvents)) {
            try {
                newMonitorEvent = eventsCacheQueue.take();
            } catch (InterruptedException e) {
                logger.error("take monitor event from eventsCacheQueue error", e);
                newMonitorEvent = null;
            }
        } else {
            newMonitorEvent = eventsCacheQueue.poll();
        }

        if (newMonitorEvent != null) {
            monitorEvents.add(newMonitorEvent);
            add();
        }
    }

    private static void monitor() {
        for (MonitorEvent monitorEvent : monitorEvents) {

            if (!isEffectiveEvents(monitorEvent)) {
                ineffectiveEvents.add(monitorEvent);
                continue;
            }

            List<MonitorNode> monitorNodes = monitorEvent.getMonitorNodes();
            for (MonitorNode monitorNode : monitorNodes) {
                Status nodeStatus = monitorNode.getNodeStatus();

                if (Status.INPROCESS == nodeStatus)
                    break;

                if (Status.SUBMIT == nodeStatus) {
                    String renderResult = monitorNode.getRenderResult();
                    LinkedBlockingQueue<String> pipeRenderResultQueue = monitorEvent
                        .getPipeRenderResultQueue();
                    boolean result = pipeRenderResultQueue.offer(renderResult);
                    if (result)
                        monitorNode.setNodeStatus(Status.FINISH);
                    continue;
                }

                if (Status.FINISH == nodeStatus)
                    continue;
            }

        }
    }

    private static boolean isEffectiveEvents(MonitorEvent monitorEvent) {
        if (monitorEvent.getResultReturnedFlag().isReturned())
            return false;

        if (TimeUtils.isTimeout(monitorEvent.getStartTime(), PlumberConfig.getResponseTimeout()))
            return false;

        return true;
    }

    private static void clear() {
        for (MonitorEvent ineffectiveEvent : ineffectiveEvents) {
            monitorEvents.remove(ineffectiveEvent);
        }
        ineffectiveEvents.clear();
    }

}

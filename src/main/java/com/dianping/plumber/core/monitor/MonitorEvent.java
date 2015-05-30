/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.core.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.dianping.plumber.core.ResultReturnedFlag;
import com.dianping.plumber.core.definitions.PlumberPipeDefinition;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: MonitorEvent.java, v 0.1 5/23/15 9:48 PM liangjun.zhong Exp $$
 */
public class MonitorEvent {

    private final List<PlumberPipeDefinition> pipeDefinitions;
    private final Date                        startTime;
    private final LinkedBlockingQueue<String> pipeRenderResultQueue;
    private final ResultReturnedFlag          resultReturnedFlag;

    private final List<MonitorNode>           monitorNodes;

    public MonitorEvent(List<PlumberPipeDefinition> pipeDefinitions,
                        LinkedBlockingQueue<String> pipeRenderResultQueue, Date startTime,
                        ResultReturnedFlag resultReturnedFlag) {
        this.pipeDefinitions = pipeDefinitions;
        this.pipeRenderResultQueue = pipeRenderResultQueue;
        this.startTime = startTime;
        this.resultReturnedFlag = resultReturnedFlag;
        this.monitorNodes = new ArrayList<MonitorNode>(pipeDefinitions.size());
        for (PlumberPipeDefinition definition : pipeDefinitions) {
            MonitorNode monitorNode = new MonitorNode(definition.getName(),
                definition.getPriority());
            monitorNodes.add(monitorNode);
        }
        Collections.sort(monitorNodes);
    }

    public List<PlumberPipeDefinition> getPipeDefinitions() {
        return pipeDefinitions;
    }

    public Date getStartTime() {
        return startTime;
    }

    public LinkedBlockingQueue<String> getPipeRenderResultQueue() {
        return pipeRenderResultQueue;
    }

    public ResultReturnedFlag getResultReturnedFlag() {
        return resultReturnedFlag;
    }

    public List<MonitorNode> getMonitorNodes() {
        return monitorNodes;
    }
}

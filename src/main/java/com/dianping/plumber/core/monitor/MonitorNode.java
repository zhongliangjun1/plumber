/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.core.monitor;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: MonitorNode.java, v 0.1 5/23/15 11:46 PM liangjun.zhong Exp $$
 */
public class MonitorNode implements Comparable<MonitorNode> {

    private final String    pipeName;
    private final int       priority;

    private volatile Status nodeStatus;
    private volatile String renderResult;

    public MonitorNode(String pipeName, int priority) {
        this.nodeStatus = Status.INPROCESS;
        this.pipeName = pipeName;
        this.priority = priority;
    }

    @Override
    public int compareTo(MonitorNode other) {
        return other.getPriority() - priority;
    }

    public String getPipeName() {
        return pipeName;
    }

    public int getPriority() {
        return priority;
    }

    public String getRenderResult() {
        return renderResult;
    }

    public void setRenderResult(String renderResult) {
        this.renderResult = renderResult;
    }

    public Status getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(Status nodeStatus) {
        this.nodeStatus = nodeStatus;
    }
}

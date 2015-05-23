/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.core.monitor;

import java.util.Date;
import java.util.List;

import com.dianping.plumber.core.definitions.PlumberPipeDefinition;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: MonitorEvent.java, v 0.1 5/23/15 9:48 PM liangjun.zhong Exp $$
 */
public class MonitorEvent {

    private final List<PlumberPipeDefinition> pipeDefinitions;
    private final Date                        startTimeStamp = new Date();

    public MonitorEvent(List<PlumberPipeDefinition> pipeDefinitions) {
        this.pipeDefinitions = pipeDefinitions;
    }
}

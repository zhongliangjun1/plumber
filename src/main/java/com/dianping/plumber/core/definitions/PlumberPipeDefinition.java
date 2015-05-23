package com.dianping.plumber.core.definitions;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-5
 * Time: PM3:40
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPipeDefinition extends PlumberPageletDefinition {

    private Integer priority;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}

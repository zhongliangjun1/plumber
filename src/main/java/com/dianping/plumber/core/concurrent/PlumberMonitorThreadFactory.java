package com.dianping.plumber.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: Monitor.java, v 0.1 5/23/15 11:33 PM liangjun.zhong Exp $$
 */
public class PlumberMonitorThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger        threadNumber;
    private final ThreadGroup          group;
    private final String               namePrefix;
    private final boolean              isDaemon;

    public PlumberMonitorThreadFactory() {
        this("Plumber-Monitor-Pool");
    }

    public PlumberMonitorThreadFactory(String name) {
        this(name, true);
    }

    public PlumberMonitorThreadFactory(String prefix, boolean daemon) {
        this.threadNumber = new AtomicInteger(1);
        this.group = new ThreadGroup(prefix + "-" + poolNumber.getAndIncrement() + "-threadGroup");
        this.namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
        this.isDaemon = daemon;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(),
            0);
        t.setDaemon(this.isDaemon);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    /**
     * @return the group
     */
    public ThreadGroup getGroup() {
        return group;
    }
}

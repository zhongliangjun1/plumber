package com.dianping.plumber.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: liming_liu
 * Date: 14/12/24
 * Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
public class PlumberThreadFactory implements ThreadFactory {

    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final AtomicInteger threadNumber;
    final ThreadGroup group;
    final String namePrefix;
    final boolean isDaemon;

    public PlumberThreadFactory(){
        this("Plumber-Pool");
    }

    public PlumberThreadFactory(String name) {
        this(name, true);
    }

    public PlumberThreadFactory(String preffix, boolean daemon){
        this.threadNumber = new AtomicInteger(1);

        this.group = new ThreadGroup(preffix + "-" + poolNumber.getAndIncrement() + "-threadGroup");

        this.namePrefix = preffix + "-" + poolNumber.getAndIncrement() + "-thread-";
        this.isDaemon = daemon;
    }

    public Thread newThread(Runnable r){
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);

        t.setDaemon(this.isDaemon);
        if (t.getPriority() != 5)
            t.setPriority(5);

        return t;
    }

    /**
     * @return the group
     */
    public ThreadGroup getGroup() {
        return group;
    }
}

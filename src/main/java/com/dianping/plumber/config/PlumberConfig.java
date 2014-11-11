package com.dianping.plumber.config;

import com.dianping.plumber.core.PlumberGlobals;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-2
 * Time: AM12:27
 * To change this template use File | Settings | File Templates.
 */
public class PlumberConfig {

    public static String getViewEncoding() {
        return Configuration.get("view.encoding", PlumberGlobals.DEFAULT_VIEW_ENCODING, String.class);
    }

    public static String getViewResourcesPath() {
        return Configuration.get("view.resourcesPath", String.class);
    }

    public static String getViewSuffix() {
        return Configuration.get("view.suffix", String.class);
    }

    public static String getViewSourceLoaderFactory() {
        return Configuration.get("view.viewSourceLoaderFactory", String.class);
    }

    public static String getViewRendererFactory() {
        return Configuration.get("view.viewRendererFactory", String.class);
    }

    public static String getPipeViewPlaceHolder() {
        return PlumberGlobals.PIPE_VIEW_PLACEHOLDER;
    }

    public static String getBarrierViewPlaceHolder() {
        return PlumberGlobals.BARRIER_VIEW_PLACEHOLDER;
    }

    public static int getConcurrentCorePoolSize() {
        return Configuration.get("concurrent.threadPool.corePoolSize", PlumberGlobals.DEFAULT_CONCURRENT_COREPOOLSIZE, Integer.class);
    }

    public static int getConcurrentMaximumPoolSize() {
        return Configuration.get("concurrent.threadPool.maximumPoolSize", PlumberGlobals.DEFAULT_CONCURRENT_MAXIMUMPOOLSIZE, Integer.class);
    }

    public static int getConcurrentKeepAliveTime() {
        return Configuration.get("concurrent.threadPool.keepAliveTime", PlumberGlobals.DEFAULT_CONCURRENT_KEEPALIVETIME, Integer.class);
    }

    public static int getConcurrentBlockingQueueCapacity() {
        return Configuration.get("concurrent.threadPool.blockingQueueCapacity", PlumberGlobals.DEFAULT_CONCURRENT_BLOCKINGQUEUECAPACITY, Integer.class);
    }



}

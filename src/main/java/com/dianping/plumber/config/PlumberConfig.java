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

    public static final String ConfigOverriderFactory = "configOverriderFactory";
    public static final String ViewEncoding = "view.encoding";
    public static final String ViewSourceLoaderFactory = "view.viewSourceLoaderFactory";
    public static final String ViewRendererFactory = "view.viewRendererFactory";
    public static final String ConcurrentCorePoolSize = "concurrent.threadPool.corePoolSize";
    public static final String ConcurrentMaximumPoolSize = "concurrent.threadPool.maximumPoolSize";
    public static final String ConcurrentKeepAliveTime = "concurrent.threadPool.keepAliveTime";
    public static final String ConcurrentBlockingQueueCapacity = "concurrent.threadPool.blockingQueueCapacity";

    public static String getConfigOverriderFactory() {
        return Configuration.get(ConfigOverriderFactory, String.class);
    }

    public static String getViewEncoding() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ViewEncoding)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ViewEncoding);
        }
        return Configuration.get(ViewEncoding, PlumberGlobals.DEFAULT_VIEW_ENCODING, String.class);
    }

    public static String getViewSourceLoaderFactory() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ViewSourceLoaderFactory)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ViewSourceLoaderFactory);
        }
        return Configuration.get(ViewSourceLoaderFactory, String.class);
    }

    public static String getViewRendererFactory() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ViewRendererFactory)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ViewRendererFactory);
        }
        return Configuration.get(ViewRendererFactory, String.class);
    }

    public static String getPipeViewPlaceHolder() {
        return PlumberGlobals.PIPE_VIEW_PLACEHOLDER;
    }

    public static String getBarrierViewPlaceHolder() {
        return PlumberGlobals.BARRIER_VIEW_PLACEHOLDER;
    }

    public static int getConcurrentCorePoolSize() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentCorePoolSize)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ConcurrentCorePoolSize);
        }
        return Configuration.get(ConcurrentCorePoolSize, PlumberGlobals.DEFAULT_CONCURRENT_COREPOOLSIZE, Integer.class);
    }

    public static int getConcurrentMaximumPoolSize() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentMaximumPoolSize)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ConcurrentMaximumPoolSize);
        }
        return Configuration.get(ConcurrentMaximumPoolSize, PlumberGlobals.DEFAULT_CONCURRENT_MAXIMUMPOOLSIZE, Integer.class);
    }

    public static int getConcurrentKeepAliveTime() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentKeepAliveTime)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ConcurrentKeepAliveTime);
        }
        return Configuration.get(ConcurrentKeepAliveTime, PlumberGlobals.DEFAULT_CONCURRENT_KEEPALIVETIME, Integer.class);
    }

    public static int getConcurrentBlockingQueueCapacity() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentBlockingQueueCapacity)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ConcurrentBlockingQueueCapacity);
        }
        return Configuration.get(ConcurrentBlockingQueueCapacity, PlumberGlobals.DEFAULT_CONCURRENT_BLOCKINGQUEUECAPACITY, Integer.class);
    }



}

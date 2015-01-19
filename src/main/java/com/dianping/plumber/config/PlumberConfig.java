package com.dianping.plumber.config;

import com.dianping.plumber.core.PlumberGlobals;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-2
 * Time: AM12:27
 * To change this template use File | Settings | File Templates.
 */
public class PlumberConfig {

    private static Logger logger = Logger.getLogger(PlumberConfig.class);

    public static final String ConfigOverriderFactory = "configOverriderFactory";
    public static final String Env = "env";
    public static final String ResponseContentType = "response.contentType";
    public static final String ResponseTimeout = "response.timeout";
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

    /**
     * get the env of plumber, default to be product
     * @return
     */
    public static String getEnv() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(Env)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(Env);
        }
        return Configuration.get(Env, PlumberGlobals.PRODUCT_ENV, String.class);
    }

    public static String getResponseContentType() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ResponseContentType)!=null ) {
            return PlumberConfigOverrider.getOverrideConfiguration(ResponseContentType);
        }
        return Configuration.get(ResponseContentType, PlumberGlobals.DEFAULT_RESPONSE_CONTENT_TYPE, String.class);
    }

    public static int getResponseTimeout() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ResponseTimeout)!=null ) {
            try {
                return (Integer) PlumberConfigOverrider.getOverrideConfiguration(ResponseTimeout);
            } catch (Exception e) {
                String msg = "load your override configuration of " + ResponseTimeout + " failure, plumber turn to use the default configuration";
                logger.error(msg, e);
            }
        }
        return Configuration.get(ResponseTimeout, PlumberGlobals.DEFAULT_RESPONSE_TIMEOUT, Integer.class);
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
            try {
                return (Integer) PlumberConfigOverrider.getOverrideConfiguration(ConcurrentCorePoolSize);
            } catch (Exception e) {
                String msg = "load your override configuration of " + ConcurrentCorePoolSize + " failure, plumber turn to use the default configuration";
                logger.error(msg, e);
            }
        }
        return Configuration.get(ConcurrentCorePoolSize, PlumberGlobals.DEFAULT_CONCURRENT_COREPOOLSIZE, Integer.class);
    }

    public static int getConcurrentMaximumPoolSize() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentMaximumPoolSize)!=null ) {
            try {
                return (Integer) PlumberConfigOverrider.getOverrideConfiguration(ConcurrentMaximumPoolSize);
            } catch (Exception e) {
                String msg = "load your override configuration of " + ConcurrentMaximumPoolSize + " failure, plumber turn to use the default configuration";
                logger.error(msg, e);
            }
        }
        return Configuration.get(ConcurrentMaximumPoolSize, PlumberGlobals.DEFAULT_CONCURRENT_MAXIMUMPOOLSIZE, Integer.class);
    }

    public static int getConcurrentKeepAliveTime() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentKeepAliveTime)!=null ) {
            try {
                return (Integer) PlumberConfigOverrider.getOverrideConfiguration(ConcurrentKeepAliveTime);
            } catch (Exception e) {
                String msg = "load your override configuration of " + ConcurrentKeepAliveTime + " failure, plumber turn to use the default configuration";
                logger.error(msg, e);
            }
        }
        return Configuration.get(ConcurrentKeepAliveTime, PlumberGlobals.DEFAULT_CONCURRENT_KEEPALIVETIME, Integer.class);
    }

    public static int getConcurrentBlockingQueueCapacity() {
        if ( PlumberConfigOverrider.getOverrideConfiguration(ConcurrentBlockingQueueCapacity)!=null ) {
            try {
                return (Integer) PlumberConfigOverrider.getOverrideConfiguration(ConcurrentBlockingQueueCapacity);
            } catch (Exception e) {
                String msg = "load your override configuration of " + ConcurrentBlockingQueueCapacity + " failure, plumber turn to use the default configuration";
                logger.error(msg, e);
            }
        }
        return Configuration.get(ConcurrentBlockingQueueCapacity, PlumberGlobals.DEFAULT_CONCURRENT_BLOCKINGQUEUECAPACITY, Integer.class);
    }



}

package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-28
 * Time: PM11:19
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberGlobals {

    public static final String CONFIG_PATH = "/plumber.yaml";

    public static final String DEV_ENV = "dev";

    public static final String PRODUCT_ENV = "product";

    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = "text/html;charset=UTF-8";

    public static final String DEFAULT_VIEW_ENCODING = "UTF-8";

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    public static final String PIPE_VIEW_PLACEHOLDER = "pb-pipe";

    public static final String BARRIER_VIEW_PLACEHOLDER = "pb-barrier";

    public static final int DEFAULT_CONCURRENT_COREPOOLSIZE = 50;

    public static final int DEFAULT_CONCURRENT_MAXIMUMPOOLSIZE = 50;

    public static final int DEFAULT_CONCURRENT_KEEPALIVETIME = 0;

    public static final int DEFAULT_CONCURRENT_BLOCKINGQUEUECAPACITY = 1000;

    public static final String EMPTY_RENDER_RESULT = "";

    public static final String CHUNKED_END = "</body></html>";

}

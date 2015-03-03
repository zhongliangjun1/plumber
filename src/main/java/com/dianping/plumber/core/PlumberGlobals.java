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

    public static final int DEFAULT_RESPONSE_TIMEOUT = 10000;

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

    public static final String PLUMBER_JS_PLACEHOLDER = "plumberJS";

    public static final String PLUMBER_JS =
                        "(function(global){\n"+
            "                var modules = {};\n"+
            "\n"+
            "                var ensureModule = function(name){\n"+
            "                    if(!modules[name]){\n"+
            "                        modules[name] = {\n"+
            "                            ready:false,\n"+
            "                            queue:[]\n"+
            "                        };\n"+
            "                    }\n"+
            "                };\n"+
            "                var executeModule = function(name){\n"+
            "                    var queue = modules[name].queue ;\n"+
            "                    var fn;\n"+
            "                    while(fn = queue.shift()){\n"+
            "                        fn.call(this);\n"+
            "                    }\n"+
            "                };\n"+
            "\n"+
            "                var plumber = {\n"+
            "                    ready:function(name){\n"+
            "                        ensureModule(name);\n"+
            "                        modules[name].ready = true;\n"+
            "                        executeModule(name);\n"+
            "                    },\n"+
            "                    execute:function(name,fn){\n"+
            "                        ensureModule(name);\n"+
            "                        modules[name].queue.push(fn);\n"+
            "                        if(modules[name].ready){\n"+
            "                            executeModule(name);\n"+
            "                        }\n"+
            "                    }\n"+
            "                };\n"+
            "\n"+
            "                global.plumber = plumber;\n"+
            "\n"+
            "            })(this);";

}

package com.dianping.plumber.utils;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberWorkerDefinitionsRepo;
import com.dianping.plumber.core.definitions.PlumberControllerDefinition;
import com.dianping.plumber.core.definitions.PlumberPipeDefinition;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-13
 * Time: PM6:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class ResponseUtils {

    private static Logger logger = Logger.getLogger(ResponseUtils.class);

    public static void setResponseContentType(HttpServletResponse response) {

        if ( StringUtils.isNotEmpty(response.getContentType()) )
            return;

        String contentType = PlumberConfig.getResponseContentType();
        boolean noCharsetInContentType = true;
        if ( contentType.toLowerCase().indexOf("charset=")!=-1 )
            noCharsetInContentType = false;

        if ( noCharsetInContentType ) {
            response.setContentType(
                    contentType + "; charset=" + PlumberConfig.getViewEncoding());
        } else {
            response.setContentType(contentType);
        }
    }

    /**
     * When buffering is disabled, the response is passed to a client synchronously,
     * immediately as it is received. Nginx will not try to read the whole response from the proxied server.
     * To learn more : http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_buffering
     */
    public static void disableNginxProxyBuffering(String plumberControllerName, HttpServletResponse response) {

        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo.getPlumberControllerDefinition(plumberControllerName);
        List<PlumberPipeDefinition> pipeDefinitions = controllerDefinition.getPipeDefinitions();

        if ( !CollectionUtils.isEmpty(pipeDefinitions) ) { // only when having pagelet of pipe type
            response.setHeader("X-Accel-Buffering", "no");
        }
    }

    public static void flushBuffer(HttpServletResponse response, String renderResult) {
        try {
            PrintWriter writer = response.getWriter();
            writer.print(renderResult);
            response.flushBuffer();
        } catch (IOException e) {
            /**
             * This occures due to client disconnection. There could be several reason for that like timeout,
             * internet connection broken, browser closed, server refused to accept request because of load etc.
             * In 99% case this exception should be ignored.
             */
            String msg = "response flush buffer with some IO problem";
            logger.warn(msg,e);
        }
    }

}

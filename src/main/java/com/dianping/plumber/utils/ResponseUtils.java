package com.dianping.plumber.utils;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-13
 * Time: PM6:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class ResponseUtils {

    private static Logger logger = Logger.getLogger(ResponseUtils.class);

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

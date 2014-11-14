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
            logger.error(e);
        }
    }

}

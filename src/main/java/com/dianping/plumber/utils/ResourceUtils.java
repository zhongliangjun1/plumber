package com.dianping.plumber.utils;

import com.dianping.plumber.exception.WebContextNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-29
 * Time: PM8:33
 * To change this template use File | Settings | File Templates.
 */
public class ResourceUtils {

    public static InputStream getResourceFromClassPath(String path) throws IOException {
        ClassPathResource loader = new ClassPathResource(path, ResourceUtils.class.getClassLoader());
        InputStream inputStream = loader.getInputStream();
        return inputStream;
    }

    public static String getResourceFromClassPath(String path, String encoding) throws IOException {
        InputStream inputStream = getResourceFromClassPath(path);
        String text = StringUtils.inputStreamToString(inputStream, encoding);
        return text;
    }

    public static InputStream getResourceFromWebContext(String path) {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        if ( webApplicationContext==null ) {
            throw new WebContextNotFoundException();
        }
        ServletContext servletContext = webApplicationContext.getServletContext();
        InputStream inputStream = servletContext.getResourceAsStream(path);
        return inputStream;
    }

    public static String getResourceFromWebContext(String path, String encoding) throws IOException {
        InputStream inputStream = getResourceFromWebContext(path);
        String text = StringUtils.inputStreamToString(inputStream, encoding);
        return text;
    }

}

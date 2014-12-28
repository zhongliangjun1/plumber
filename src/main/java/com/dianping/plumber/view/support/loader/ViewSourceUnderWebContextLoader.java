package com.dianping.plumber.view.support.loader;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberGlobals;
import com.dianping.plumber.exception.ViewSourceNotFoundException;
import com.dianping.plumber.exception.WebContextNotFoundException;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.ViewSourceLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-12-28
 * Time: AM2:07
 *
 * Implementation of ServletContextListener.
 * The configuration of this listener should be prior to the spring ContextLoaderListener.
 *
 */
public class ViewSourceUnderWebContextLoader implements ViewSourceLoader, ServletContextListener {

    private static ServletContext servletContext;

    @Override
    public String load(String viewPath) {

        if ( StringUtils.isEmpty(viewPath) )
            throw new IllegalArgumentException("viewPath can not be null !");

        if ( servletContext == null )
            throw new WebContextNotFoundException();

        String encoding = PlumberConfig.getViewEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = PlumberGlobals.DEFAULT_VIEW_ENCODING;
        }

        String viewSource;
        try {
            InputStream inputStream = servletContext.getResourceAsStream(viewPath);
            viewSource = StringUtils.inputStreamToString(inputStream, encoding);
        } catch (Exception e) {
            throw new ViewSourceNotFoundException("can not find viewSource under your webContext : "+viewPath, e);
        }

        return viewSource;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        servletContext = event.getServletContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        servletContext = null;
    }
}

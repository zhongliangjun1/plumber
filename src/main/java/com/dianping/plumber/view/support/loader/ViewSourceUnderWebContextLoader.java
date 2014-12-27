package com.dianping.plumber.view.support.loader;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberGlobals;
import com.dianping.plumber.exception.ViewSourceNotFoundException;
import com.dianping.plumber.utils.ResourceUtils;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.ViewSourceLoader;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-12-28
 * Time: AM2:07
 * To change this template use File | Settings | File Templates.
 */
public class ViewSourceUnderWebContextLoader implements ViewSourceLoader {

    @Override
    public String load(String viewPath) {

        if ( StringUtils.isEmpty(viewPath) )
            throw new IllegalArgumentException("viewPath can not be null !");

        String encoding = PlumberConfig.getViewEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = PlumberGlobals.DEFAULT_VIEW_ENCODING;
        }

        String viewSource;
        try {
            viewSource = ResourceUtils.getResourceFromWebContext(viewPath, encoding);
        } catch (IOException e) {
            throw new ViewSourceNotFoundException("can not find viewSource under your webContext : "+viewPath, e);
        }

        return viewSource;

    }

}

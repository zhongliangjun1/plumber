package com.dianping.plumber.view.support.loader;

import com.dianping.plumber.view.ViewSourceLoader;
import com.dianping.plumber.view.ViewSourceLoaderFactory;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-12-28
 * Time: AM2:10
 * To change this template use File | Settings | File Templates.
 */
public class ViewSourceUnderWebContextLoaderFactory implements ViewSourceLoaderFactory {

    @Override
    public ViewSourceLoader getSourceLoader() {
        return new ViewSourceUnderWebContextLoader();
    }

}

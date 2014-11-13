package com.dianping.plumber.view.support;

import com.dianping.plumber.view.ViewSourceLoader;
import com.dianping.plumber.view.ViewSourceLoaderFactory;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-13
 * Time: PM2:41
 * To change this template use File | Settings | File Templates.
 */
public class ViewSourceUnderClassPathLoaderFactory implements ViewSourceLoaderFactory {

    @Override
    public ViewSourceLoader getSourceLoader() {
        return new ViewSourceUnderClassPathLoader();
    }

}

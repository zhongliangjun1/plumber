package com.dianping.plumber.view.defalutImpl;

import com.dianping.plumber.view.ViewRenderer;
import com.dianping.plumber.view.ViewRendererFactory;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-31
 * Time: PM6:38
 * To change this template use File | Settings | File Templates.
 */
public class FreemarkerRendererFactory implements ViewRendererFactory {

    @Override
    public ViewRenderer getRenderer() {
        return new FreemarkerRenderer();
    }

}

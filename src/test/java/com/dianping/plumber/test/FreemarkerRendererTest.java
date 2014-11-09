package com.dianping.plumber.test;

import com.dianping.plumber.utils.EnvPrepareUtils;
import com.dianping.plumber.view.ViewSourceLoader;
import com.dianping.plumber.view.support.FreemarkerRenderer;
import com.dianping.plumber.view.support.ViewSourceUnderClassPathLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-30
 * Time: AM2:56
 * To change this template use File | Settings | File Templates.
 */
public class FreemarkerRendererTest {

    public static void main(String[] args) {
        EnvPrepareUtils.startLog4j();

        String viewName = "demo";
        //String viewSource = "<div>hello ${name}</div>";
        ViewSourceLoader loader = new ViewSourceUnderClassPathLoader();
        String viewSource = loader.load(viewName);
        Map<String, Object> modelForView = new HashMap<String, Object>();
        modelForView.put("name", "liangjun.zhong");
        modelForView.put("asideBarrier", "aside");
        FreemarkerRenderer renderer = new FreemarkerRenderer();
        System.out.println(renderer.render(viewName, viewSource, modelForView));
    }

}

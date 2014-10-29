package com.dianping.plumber.test;

import com.dianping.plumber.demo.FreemarkerRenderer;

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
        String viewName = "demo";
        String viewSource = "<div>hello ${name}</div>";
        Map<String, Object> modelForView = new HashMap<String, Object>();
        modelForView.put("name", "liangjun.zhong");
        FreemarkerRenderer renderer = new FreemarkerRenderer();
        System.out.println(renderer.render(viewName, viewSource, modelForView));
    }

}

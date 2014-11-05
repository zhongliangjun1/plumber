package com.dianping.plumber.test;

import com.dianping.plumber.view.ViewParser;
import com.dianping.plumber.view.ViewSourceLoader;
import com.dianping.plumber.view.support.ViewSourceUnderClassPathLoader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-6
 * Time: AM2:15
 * To change this template use File | Settings | File Templates.
 */
public class ViewParserTest {

    public static void main(String[] args) {
        String viewName = "demo";
        ViewSourceLoader loader = new ViewSourceUnderClassPathLoader();
        String viewSource = loader.load(viewName);
        List<String> pipeNames = ViewParser.recognizePipeNames(viewSource);
        if (pipeNames!=null) {
            for (String name : pipeNames) {
                System.out.println(name);
            }
        }
    }

}

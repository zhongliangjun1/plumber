package com.dianping.plumber.test;

import com.dianping.plumber.core.PlumberGlobals;
import com.dianping.plumber.config.Yaml;
import com.dianping.plumber.utils.ResourceUtils;


/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-30
 * Time: AM12:37
 * To change this template use File | Settings | File Templates.
 */
public class YamlTest {

    public static void main(String[] args) throws Exception {
        String text = ResourceUtils.getResourceFromClassPath(PlumberGlobals.CONFIG_PATH, "UTF-8");
        Yaml yaml = new Yaml(text);
        String configOverriderFactory = yaml.get("configOverriderFactory", String.class);
        String viewRendererFactory = yaml.get("view.viewRendererFactory", String.class);
        System.out.println(configOverriderFactory);
        System.out.println(viewRendererFactory);
    }

}

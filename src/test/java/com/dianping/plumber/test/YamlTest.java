package com.dianping.plumber.test;

import com.dianping.plumber.PlumberConstants;
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
        String text = ResourceUtils.getResourceFromClassPath(PlumberConstants.CONFIG_PATH, "UTF-8");
        Yaml yaml = new Yaml(text);
        String resourcesPath = yaml.get("view.resourcesPath", String.class);
        String suffix = yaml.get("view.suffix", String.class);
        String rendererFactory = yaml.get("view.rendererFactory", String.class);
        System.out.println(resourcesPath);  // resourcesPath : "/view"
        System.out.println(suffix);  // .ftl
        System.out.println(rendererFactory);  // com.dianping.plumber.view.ViewRendererFactory
    }

}

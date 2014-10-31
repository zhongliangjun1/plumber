package com.dianping.plumber.test;

import com.dianping.plumber.utils.ResourceUtils;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-31
 * Time: PM8:42
 * To change this template use File | Settings | File Templates.
 */
public class ResourceUtilsTest {

    public static void main(String[] args) throws IOException {

        String encoding = "UTF-8";

        String classpathFTL = ResourceUtils.getResourceFromClassPath("/view/demo.ftl", encoding);
        System.out.println(classpathFTL);

        System.out.println("-------------------------");

        String webcontextFTL = ResourceUtils.getResourceFromWebContext("/WEB-INF/pages/demo.ftl", encoding);
        System.out.println(webcontextFTL);
    }


}

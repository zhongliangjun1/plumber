package com.dianping.plumber.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-5
 * Time: PM4:29
 * To change this template use File | Settings | File Templates.
 */
public class PlumberWorkerDefinitionsRepo {

    private static List<String> controllerNames = new ArrayList<String>();
    private static List<String> pipeNames = new ArrayList<String>();

    public static void controllerRegister(String name) {
        if ( !controllerNames.contains(name) ) {
            controllerNames.add(name);
        }
    }

    public static void pipeRegister(String name) {
        if ( !pipeNames.contains(name) ) {
            pipeNames.add(name);
        }
    }

}

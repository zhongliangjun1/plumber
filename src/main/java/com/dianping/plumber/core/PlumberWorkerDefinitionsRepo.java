package com.dianping.plumber.core;

import com.dianping.plumber.exception.PlumberInitializeFailureException;
import org.springframework.context.ApplicationContext;

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

    private final static List<String> controllerNames = new ArrayList<String>();
    private final static List<String> pipeNames = new ArrayList<String>();

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

    public static void prepareWorkerDefinitions(ApplicationContext applicationContext) {
        if ( applicationContext==null )
            throw new PlumberInitializeFailureException("applicationContext is null !");
        preparePipeDefinitions(applicationContext);
        prepareControllerDefinitions(applicationContext);
    }

    private static void preparePipeDefinitions(ApplicationContext applicationContext) {
        if ( controllerNames.size()>0 ) {
            for (String pipeName : pipeNames) {
                PlumberPipe plumberPipe = (PlumberPipe) applicationContext.getBean(pipeName);
            }
        }
    }

    private static void prepareControllerDefinitions(ApplicationContext applicationContext) {

    }

}

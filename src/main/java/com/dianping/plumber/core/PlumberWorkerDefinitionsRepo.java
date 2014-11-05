package com.dianping.plumber.core;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.utils.CollectionUtils;
import com.dianping.plumber.view.ViewParser;
import com.dianping.plumber.view.ViewSourceLoader;
import com.dianping.plumber.view.ViewSourceLoaderFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final static Map<String, PlumberControllerDefinition> controllerDefinitionsRepo = new HashMap<String, PlumberControllerDefinition>();
    private final static Map<String, PlumberPipeDefinition> pipeDefinitionsRepo = new HashMap<String, PlumberPipeDefinition>();

    private static ViewSourceLoader viewSourceLoader;

    static {
        try {
            ViewSourceLoaderFactory viewSourceLoaderFactory = (ViewSourceLoaderFactory) Class.forName(PlumberConfig.getViewSourceLoaderFactory()).newInstance();
            viewSourceLoader = viewSourceLoaderFactory.getSourceLoader();
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare viewSourceLoader failure", e);
        }
    }

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
        try {
            if ( controllerNames.size()>0 ) {
                for (String pipeName : pipeNames) {
                    if ( pipeDefinitionsRepo.get(pipeName)==null ) {
                        PlumberPipeDefinition pipeDefinition = new PlumberPipeDefinition();
                        pipeDefinition.setPipeName(pipeName);

                        PlumberPipe plumberPipe = (PlumberPipe) applicationContext.getBean(pipeName);
                        boolean isRequired = plumberPipe.isRequired();
                        pipeDefinition.setRequired(isRequired);

                        String viewName = pipeName;
                        pipeDefinition.setViewName(viewName);
                        String viewSource = viewSourceLoader.load(viewName);
                        pipeDefinition.setViewSource(viewSource);

                        pipeDefinitionsRepo.put(pipeName, pipeDefinition);
                    }
                }
            }
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare pipeDefinitions failure", e);
        }
    }


    private static void prepareControllerDefinitions(ApplicationContext applicationContext) {
        try {
            if ( controllerNames.size()>0 ) {
                for (String controllerName : controllerNames) {
                    if ( controllerDefinitionsRepo.get(controllerName)==null ) {
                        PlumberControllerDefinition controllerDefinition = new PlumberControllerDefinition();
                        controllerDefinition.setControllerName(controllerName);

                        String viewName = controllerName;
                        controllerDefinition.setViewName(viewName);
                        String viewSource = viewSourceLoader.load(viewName);
                        controllerDefinition.setViewSource(viewSource);

                        List<String> pipeNames = ViewParser.recognizePipeNames(viewSource);
                        if ( !CollectionUtils.isEmpty(pipeNames) ) {
                            List<PlumberPipeDefinition> pipeDefinitions = new ArrayList<PlumberPipeDefinition>();
                            for (String pipeName : pipeNames) {
                                PlumberPipeDefinition pipeDefinition = pipeDefinitionsRepo.get(pipeName);
                                pipeDefinitions.add(pipeDefinition);
                            }
                            controllerDefinition.setPipeNames(pipeNames);
                            controllerDefinition.setPipeDefinitions(pipeDefinitions);
                        }

                        controllerDefinitionsRepo.put(controllerName, controllerDefinition);
                    }
                }
            }
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare controllerDefinitions failure", e);
        }
    }

}

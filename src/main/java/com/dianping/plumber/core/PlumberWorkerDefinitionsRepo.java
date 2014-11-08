package com.dianping.plumber.core;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.utils.CollectionUtils;
import com.dianping.plumber.view.ViewParser;
import com.dianping.plumber.view.ViewSourceLoader;
import com.dianping.plumber.view.ViewSourceLoaderFactory;

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
    private final static List<String> barrierNames = new ArrayList<String>();
    private final static Map<String, PlumberControllerDefinition> controllerDefinitionsRepo = new HashMap<String, PlumberControllerDefinition>();
    private final static Map<String, PlumberPipeDefinition> pipeDefinitionsRepo = new HashMap<String, PlumberPipeDefinition>();
    private final static Map<String, PlumberBarrierDefinition> barrierDefinitionsRepo = new HashMap<String, PlumberBarrierDefinition>();

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

    public static void barrierRegister(String name) {
        if ( !barrierNames.contains(name) ) {
            barrierNames.add(name);
        }
    }


    public static void prepareWorkerDefinitions() {
        prepareBarrierDefinitions();
        preparePipeDefinitions();
        prepareControllerDefinitions();
    }

    private static void prepareBarrierDefinitions() {
        try {
            if ( barrierNames.size()>0 ) {
                for (String barrierName : barrierNames) {
                    if ( barrierDefinitionsRepo.get(barrierName)==null ) {
                        PlumberBarrierDefinition barrierDefinition = new PlumberBarrierDefinition();
                        barrierDefinition.setName(barrierName);

                        String viewName = barrierName;
                        String viewSource = viewSourceLoader.load(viewName);
                        barrierDefinition.setViewSource(viewSource);

                        barrierDefinitionsRepo.put(barrierName, barrierDefinition);
                    }
                }
            }
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare barrierDefinition failure", e);
        }
    }

    private static void preparePipeDefinitions() {
        try {
            if ( pipeNames.size()>0 ) {
                for (String pipeName : pipeNames) {
                    if ( pipeDefinitionsRepo.get(pipeName)==null ) {
                        PlumberPipeDefinition pipeDefinition = new PlumberPipeDefinition();
                        pipeDefinition.setName(pipeName);

                        String viewName = pipeName;
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


    private static void prepareControllerDefinitions() {
        try {
            if ( controllerNames.size()>0 ) {
                for (String controllerName : controllerNames) {
                    if ( controllerDefinitionsRepo.get(controllerName)==null ) {
                        PlumberControllerDefinition controllerDefinition = new PlumberControllerDefinition();
                        controllerDefinition.setName(controllerName);

                        String viewName = controllerName;
                        String viewSource = viewSourceLoader.load(viewName);
                        controllerDefinition.setViewSource(viewSource);

                        List<String> barrierNames = ViewParser.recognizeBarrierNames(viewSource);
                        if ( !CollectionUtils.isEmpty(barrierNames) ) {
                            List<PlumberBarrierDefinition> barrierDefinitions = new ArrayList<PlumberBarrierDefinition>();
                            for (String barrierName : barrierNames) {
                                PlumberBarrierDefinition barrierDefinition = barrierDefinitionsRepo.get(barrierName);
                                barrierDefinitions.add(barrierDefinition);
                            }
                            controllerDefinition.setBarrierNames(barrierNames);
                            controllerDefinition.setBarrierDefinitions(barrierDefinitions);
                        }

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

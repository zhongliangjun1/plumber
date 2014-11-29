package com.dianping.plumber.core;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.exception.PlumberControllerNotFoundException;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.utils.CollectionUtils;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.*;

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

    private final static Map<String, String> controllerViewNamesRepo = new HashMap<String, String>();
    private final static Map<String, String> pipeViewNamesRepo = new HashMap<String, String>();
    private final static Map<String, String> barrierViewNamesRepo = new HashMap<String, String>();

    private final static Map<String, PlumberControllerDefinition> controllerDefinitionsRepo = new HashMap<String, PlumberControllerDefinition>();
    private final static Map<String, PlumberPipeDefinition> pipeDefinitionsRepo = new HashMap<String, PlumberPipeDefinition>();
    private final static Map<String, PlumberBarrierDefinition> barrierDefinitionsRepo = new HashMap<String, PlumberBarrierDefinition>();

    private final static ViewSourceLoader viewSourceLoader;
    private final static ViewRenderer viewRenderer;

    static {
        try {
            ViewSourceLoaderFactory viewSourceLoaderFactory = (ViewSourceLoaderFactory) Class.forName(PlumberConfig.getViewSourceLoaderFactory()).newInstance();
            viewSourceLoader = viewSourceLoaderFactory.getSourceLoader();
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare viewSourceLoader failure", e);
        }

        try {
            ViewRendererFactory viewRendererFactory = (ViewRendererFactory) Class.forName(PlumberConfig.getViewRendererFactory()).newInstance();
            viewRenderer = viewRendererFactory.getRenderer();
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare viewRenderer failure", e);
        }
    }

    public static void controllerRegister(String controllerName, String viewName) {
        if ( !controllerNames.contains(controllerName) ) {
            controllerNames.add(controllerName);
        }
        if ( StringUtils.isNotEmpty(viewName) ) {
            controllerViewNamesRepo.put(controllerName, viewName);
        } else {
            throw new PlumberInitializeFailureException("controller " + controllerName + " can not find valid viewName");
        }
    }


    public static void pipeRegister(String pipeName, String viewName) {
        if ( !pipeNames.contains(pipeName) ) {
            pipeNames.add(pipeName);
        }
        if ( StringUtils.isNotEmpty(viewName) ) {
            pipeViewNamesRepo.put(pipeName, viewName);
        } else {
            throw new PlumberInitializeFailureException("pipe " + pipeName + " can not find valid viewName");
        }
    }

    public static void barrierRegister(String barrierName, String viewName) {
        if ( !barrierNames.contains(barrierName) ) {
            barrierNames.add(barrierName);
        }
        if ( StringUtils.isNotEmpty(viewName) ) {
            barrierViewNamesRepo.put(barrierName, viewName);
        } else {
            throw new PlumberInitializeFailureException("barrier " + barrierName + " can not find valid viewName");
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

                        String viewName = barrierViewNamesRepo.get(barrierName);
                        barrierDefinition.setViewName(viewName);
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

                        String viewName = pipeViewNamesRepo.get(pipeName);
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


    private static void prepareControllerDefinitions() {
        try {
            if ( controllerNames.size()>0 ) {
                for (String controllerName : controllerNames) {
                    if ( controllerDefinitionsRepo.get(controllerName)==null ) {
                        PlumberControllerDefinition controllerDefinition = new PlumberControllerDefinition();
                        controllerDefinition.setName(controllerName);

                        String viewName = controllerViewNamesRepo.get(controllerName);
                        controllerDefinition.setViewName(viewName);
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

    public static PlumberControllerDefinition getPlumberControllerDefinition(String controllerName) {
        if ( StringUtils.isEmpty(controllerName) )
            throw new PlumberControllerNotFoundException("illegal controllerName : "+controllerName);
        PlumberControllerDefinition definition = controllerDefinitionsRepo.get(controllerName);
        if ( definition==null )
            throw new PlumberControllerNotFoundException("can not find ControllerDefinition of "+controllerName+" in controllerDefinitionsRepo");
        return definition;
    }

    public static ViewSourceLoader getViewSourceLoader() {
        return viewSourceLoader;
    }

    public static ViewRenderer getViewRenderer() {
        return viewRenderer;
    }

}

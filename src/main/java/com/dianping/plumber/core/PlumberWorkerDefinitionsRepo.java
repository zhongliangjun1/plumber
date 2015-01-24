package com.dianping.plumber.core;

import com.dianping.plumber.annotation.ParamFromController;
import com.dianping.plumber.annotation.ParamFromRequest;
import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.exception.PlumberControllerNotFoundException;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.utils.CollectionUtils;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.*;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
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
    private final static List<String> pageletNames = new ArrayList<String>();

    private final static Map<String, String> controllerViewPathsRepo = new HashMap<String, String>();
    private final static Map<String, String> pageletViewPathsRepo = new HashMap<String, String>();

    private final static Map<String, Class<PlumberController>> controllerClassesRepo = new HashMap<String, Class<PlumberController>>();
    private final static Map<String, Class<PlumberPagelet>> pageletClassesRepo = new HashMap<String, Class<PlumberPagelet>>();

    private final static Map<String, PlumberControllerDefinition> controllerDefinitionsRepo = new HashMap<String, PlumberControllerDefinition>();
    private final static Map<String, PlumberPageletDefinition> pageletDefinitionsRepo = new HashMap<String, PlumberPageletDefinition>();

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

    public static void controllerRegister(String controllerName, String viewPath, Class<PlumberController> controllerClass) {
        if ( !controllerNames.contains(controllerName) ) {
            controllerNames.add(controllerName);
        }

        if ( StringUtils.isNotEmpty(viewPath) ) {
            controllerViewPathsRepo.put(controllerName, viewPath);
        } else {
            throw new PlumberInitializeFailureException("controller " + controllerName + " can not find valid viewPath");
        }

        if ( controllerClass!=null ) {
            controllerClassesRepo.put(controllerName, controllerClass);
        } else {
            throw new PlumberInitializeFailureException("controller " + controllerName + " can not find valid controllerClass");
        }
    }

    public static void pageletRegister(String pageletName, String viewPath, Class<PlumberPagelet> pageletClass) {
        if ( !pageletNames.contains(pageletName) ) {
            pageletNames.add(pageletName);
        }

        if ( StringUtils.isNotEmpty(viewPath) ) {
            pageletViewPathsRepo.put(pageletName, viewPath);
        } else {
            throw new PlumberInitializeFailureException("pagelet " + pageletName + " can not find valid viewPath");
        }

        if ( pageletClass!=null ) {
            pageletClassesRepo.put(pageletName, pageletClass);
        } else {
            throw new PlumberInitializeFailureException("pagelet " + pageletName + " can not find valid pageletClass");
        }
    }


    public static void prepareWorkerDefinitions() {
        preparePageletDefinitions();
        prepareControllerDefinitions();
    }

    private static void preparePageletDefinitions() {
        try {
            if ( pageletNames.size()>0 ) {
                for (String pageletName : pageletNames) {
                    if ( pageletDefinitionsRepo.get(pageletName)==null ) {
                        PlumberPageletDefinition pageletDefinition = new PlumberPageletDefinition();
                        pageletDefinition.setName(pageletName);

                        String viewPath = pageletViewPathsRepo.get(pageletName);
                        pageletDefinition.setViewPath(viewPath);

                        String viewSource = viewSourceLoader.load(viewPath);
                        pageletDefinition.setViewSource(viewSource);

                        Class<PlumberPagelet> pageletClass = pageletClassesRepo.get(pageletName);
                        pageletDefinition.setPageletClass(pageletClass);

                        Map<Class,List<Field>> fieldsMap = getParamAnnotationFields(pageletClass);
                        pageletDefinition.setParamFromRequestFields(fieldsMap.get(ParamFromRequest.class));
                        pageletDefinition.setParamFromControllerFields(fieldsMap.get(ParamFromController.class));

                        pageletDefinitionsRepo.put(pageletName, pageletDefinition);
                    }
                }
            }
        } catch (Exception e) {
            throw new PlumberInitializeFailureException("prepare pageletDefinition failure", e);
        }
    }


    private static void prepareControllerDefinitions() {
        try {
            if ( controllerNames.size()>0 ) {
                for (String controllerName : controllerNames) {
                    if ( controllerDefinitionsRepo.get(controllerName)==null ) {
                        PlumberControllerDefinition controllerDefinition = new PlumberControllerDefinition();
                        controllerDefinition.setName(controllerName);

                        String viewPath = controllerViewPathsRepo.get(controllerName);
                        controllerDefinition.setViewPath(viewPath);
                        String viewSource = viewSourceLoader.load(viewPath);
                        controllerDefinition.setViewSource(viewSource);

                        Class<PlumberController> controllerClass = controllerClassesRepo.get(controllerName);
                        controllerDefinition.setControllerClass(controllerClass);

                        Map<Class,List<Field>> fieldsMap = getParamAnnotationFields(controllerClass);
                        controllerDefinition.setParamFromRequestFields(fieldsMap.get(ParamFromRequest.class));

                        List<String> barrierNames = ViewParser.recognizeBarrierNames(viewSource);
                        if ( !CollectionUtils.isEmpty(barrierNames) ) {
                            List<PlumberBarrierDefinition> barrierDefinitions = new ArrayList<PlumberBarrierDefinition>();
                            for (String barrierName : barrierNames) {
                                PlumberPageletDefinition pageletDefinition = pageletDefinitionsRepo.get(barrierName);
                                PlumberBarrierDefinition barrierDefinition = new PlumberBarrierDefinition();
                                BeanUtils.copyProperties(pageletDefinition, barrierDefinition);
                                barrierDefinitions.add(barrierDefinition);
                            }
                            controllerDefinition.setBarrierNames(barrierNames);
                            controllerDefinition.setBarrierDefinitions(barrierDefinitions);
                        }

                        List<String> pipeNames = ViewParser.recognizePipeNames(viewSource);
                        if ( !CollectionUtils.isEmpty(pipeNames) ) {
                            List<PlumberPipeDefinition> pipeDefinitions = new ArrayList<PlumberPipeDefinition>();
                            for (String pipeName : pipeNames) {
                                PlumberPageletDefinition pageletDefinition = pageletDefinitionsRepo.get(pipeName);
                                PlumberPipeDefinition pipeDefinition = new PlumberPipeDefinition();
                                BeanUtils.copyProperties(pageletDefinition, pipeDefinition);
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

    private static Map<Class,List<Field>> getParamAnnotationFields(Class clazz) {

        Map<Class,List<Field>> result = new HashMap<Class, List<Field>>();
        List<Field> paramFromRequestFields = new ArrayList<Field>();
        List<Field> paramFromControllerFields = new ArrayList<Field>();

        Field[] fields = clazz.getDeclaredFields();

        if ( fields!=null ) {
            for ( Field field : fields ) {

                ParamFromRequest paramFromRequest = field.getAnnotation(ParamFromRequest.class);
                if(paramFromRequest!=null){
                    field.setAccessible(true);
                    paramFromRequestFields.add(field);
                    continue;
                }

                ParamFromController paramFromController = field.getAnnotation(ParamFromController.class);
                if(paramFromController!=null){
                    field.setAccessible(true);
                    paramFromControllerFields.add(field);
                    continue;
                }
            }
        }

        result.put(ParamFromRequest.class, paramFromRequestFields);
        result.put(ParamFromController.class, paramFromControllerFields);
        return result;
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

/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.utils;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.dianping.plumber.core.PlumberGlobals;
import com.dianping.plumber.core.PlumberWorkerDefinitionsRepo;
import com.dianping.plumber.core.definitions.PlumberControllerDefinition;
import com.dianping.plumber.core.definitions.PlumberPageletDefinition;
import com.dianping.plumber.view.ViewRenderer;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: ViewRenderUtils.java, v 0.1 5/30/15 4:10 PM liangjun.zhong Exp $$
 */
public abstract class ViewRenderUtils {

    private static final Logger logger = Logger.getLogger(ViewRenderUtils.class);

    public static String getViewRenderResult(PlumberPageletDefinition definition,
                                             Map<String, Object> modelForView) {
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        String viewSource = getViewSource(definition);
        ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
        String result = viewRenderer.render(definition.getName(), viewSource, modelForView);
        if (StringUtils.isNotEmpty(result))
            renderResult = result;
        return renderResult;
    }

    public static String getViewRenderResult(PlumberControllerDefinition definition,
                                             Map<String, Object> modelForView) {
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        String viewSource = getViewSource(definition);
        ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
        String result = viewRenderer.render(definition.getName(), viewSource, modelForView);
        if (StringUtils.isNotEmpty(result))
            renderResult = result;
        return renderResult;
    }

    public static String getViewRenderResultWhenExceptionHappen(Exception e) {
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        if (EnvUtils.isDev()) {
            try {
                String result = ExceptionUtils.getFullStackTrace(e);
                if (StringUtils.isNotEmpty(result))
                    renderResult = result;
            } catch (Exception e1) {
                logger.error("can not get the exception full stack trace", e1);
            }
        }
        return renderResult;
    }

    public static String getViewSource(PlumberPageletDefinition definition) {
        String viewSource = definition.getViewSource();
        // for refresh
        if (EnvUtils.isDev()) {
            String viewPath = definition.getViewPath();
            viewSource = PlumberWorkerDefinitionsRepo.getViewSourceLoader().load(viewPath);
        }
        return viewSource;
    }

    public static String getViewSource(PlumberControllerDefinition definition) {
        String viewSource = definition.getViewSource();
        // for refresh
        if (EnvUtils.isDev()) {
            String viewPath = definition.getViewPath();
            viewSource = PlumberWorkerDefinitionsRepo.getViewSourceLoader().load(viewPath);
        }
        return viewSource;
    }

}

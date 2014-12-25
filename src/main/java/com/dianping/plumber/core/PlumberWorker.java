package com.dianping.plumber.core;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: AM12:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberWorker implements Runnable {

    protected Logger logger = Logger.getLogger(PlumberWorker.class);

    protected final PlumberPageletDefinition definition;
    protected final Map<String, Object> paramsFromRequest;
    protected final Map<String, Object> paramsFromController;
    protected Map<String, Object> modelForView = new HashMap<String, Object>();

    public PlumberWorker(PlumberPageletDefinition definition,
                         Map<String, Object> paramsFromRequest,
                         Map<String, Object> paramsFromController) {
        this.definition = definition;
        this.paramsFromRequest = paramsFromRequest;
        this.paramsFromController = paramsFromController;
    }

    public PlumberPageletDefinition getDefinition() {
        return definition;
    }

    public Map<String, Object> getParamsFromController() {
        return paramsFromController;
    }

    public Map<String, Object> getModelForView() {
        return modelForView;
    }


}

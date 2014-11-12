package com.dianping.plumber.core;

import com.dianping.plumber.view.ViewRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-11
 * Time: PM10:29
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPipeWorker extends PlumberWorker {

    private final PlumberPipe pipe;
    private final HttpServletResponse response;

    public PlumberPipeWorker(PlumberPipeDefinition definition, Map<String, Object> paramsFromController,
                             PlumberPipe pipe, HttpServletResponse response) {
        super(definition, paramsFromController);
        this.pipe = pipe;
        this.response = response;
    }

    @Override
    public void run() {
        try {
            ResultType resultType = pipe.execute(paramsFromController, modelForView);
            if ( resultType==ResultType.SUCCESS ) {
                String name = definition.getName();
                String viewSource = definition.getViewSource();
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String renderResult = viewRenderer.render(name, viewSource, modelForView);

                PrintWriter writer = response.getWriter();
                writer.print(renderResult);
                response.flushBuffer();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}

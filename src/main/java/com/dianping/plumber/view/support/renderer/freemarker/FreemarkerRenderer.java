package com.dianping.plumber.view.support.renderer.freemarker;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.utils.EnvUtils;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.ViewRenderer;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-30
 * Time: AM1:55
 * To change this template use File | Settings | File Templates.
 */
public class FreemarkerRenderer implements ViewRenderer {

    private final Logger logger = Logger.getLogger(this.getClass());
    private final Map<String, Template> templateCache = new ConcurrentHashMap<String, Template>();

    @Override
    public String render(String viewName, String viewSource, Map<String, Object> modelForView) {

        if ( StringUtils.isEmpty(viewName) || StringUtils.isEmpty(viewSource))
            return null;

        Template template = getTemplate(viewName, viewSource);
        if (template==null)
            return null;

        return getRenderResult(template, modelForView);
    }

    private Template getTemplate(String viewName, String viewSource) {
        Template template = templateCache.get(viewName);
        if (template == null || EnvUtils.isDev() ) {
            Configuration config = new Configuration();
            config.setTemplateUpdateDelay(600000);
            config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            config.setNumberFormat("#");
            config.setClassicCompatible(true);
            config.setDefaultEncoding("UTF-8");
            BufferedReader reader = new BufferedReader(new StringReader(viewSource));
            try {
                template = new Template(viewName, reader, config, PlumberConfig.getViewEncoding());
                templateCache.put(viewName, template);
            } catch(Exception e){
                logger.error(viewName+" template init failure", e);
            }
        }
        return template;
    }

    private String getRenderResult(Template template, Map<String, Object> modelForView) {
        String result = null;
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);
        try {
            template.process(modelForView, writer);
            writer.flush();
            result = stringWriter.toString();
        } catch (Exception e) {
            logger.error(template.getName() + " render failure", e);
        }
        return result;
    }


}

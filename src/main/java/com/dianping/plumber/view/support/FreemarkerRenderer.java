package com.dianping.plumber.view.support;

import com.dianping.plumber.core.PlumberConstants;
import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.ViewRenderer;
import freemarker.template.Configuration;
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
        if (template == null) {
            Configuration config = new Configuration();
            BufferedReader reader = new BufferedReader(new StringReader(viewSource));
            try {
                template = new Template(null, reader, config, getViewEncoding());
                templateCache.put(viewName, template);
            } catch(Exception e){
                logger.error(e);
            }
        }
        return template;
    }

    private String getViewEncoding() {
        String encoding = PlumberConfig.get("view.encoding", String.class);
        if (StringUtils.isEmpty(encoding)) {
            encoding = PlumberConstants.DEFAULT_VIEW_ENCODING;
        }
        return encoding;
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
            logger.error(e);
        }
        return result;
    }


}

package com.dianping.plumber.view.support.renderer.freemarker;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.DefaultObjectWrapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ltebean
 */
public class JSPSupport {

    public static Map<String,Object> getSupport(HttpServletRequest request,ServletContext servletContext){

        Map<String,Object> context = new HashMap<String, Object>();

        TaglibFactory taglibs = (TaglibFactory) servletContext.getAttribute(".freemarker.JspTaglibs");
        if( taglibs==null ){
            taglibs= new TaglibFactory(servletContext);
        }
        context.put("JspTaglibs", taglibs);

        ServletContextHashModel servletContextHashModel = (ServletContextHashModel)servletContext.getAttribute(".freemarker.Application");
        context.put("Application", servletContextHashModel);

        DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
        context.put("Request", new HttpRequestHashModel(request, wrapper));

        return context;
    }

}

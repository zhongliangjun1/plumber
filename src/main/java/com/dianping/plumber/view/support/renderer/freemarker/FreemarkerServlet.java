package com.dianping.plumber.view.support.renderer.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core.Configurable;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.*;
import freemarker.log.Logger;
import freemarker.template.*;
import freemarker.template.utility.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author ltebean
 */
public class FreemarkerServlet extends HttpServlet
{
    private static final Logger logger = Logger.getLogger("freemarker.servlet");

    public static final long serialVersionUID = -2440216393145762479L;

    private static final String INITPARAM_TEMPLATE_PATH = "TemplatePath";
    private static final String INITPARAM_NOCACHE = "NoCache";
    private static final String INITPARAM_CONTENT_TYPE = "ContentType";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String INITPARAM_DEBUG = "Debug";

    private static final String DEPR_INITPARAM_TEMPLATE_DELAY = "TemplateDelay";
    private static final String DEPR_INITPARAM_ENCODING = "DefaultEncoding";
    private static final String DEPR_INITPARAM_OBJECT_WRAPPER = "ObjectWrapper";
    private static final String DEPR_INITPARAM_WRAPPER_SIMPLE = "simple";
    private static final String DEPR_INITPARAM_WRAPPER_BEANS = "beans";
    private static final String DEPR_INITPARAM_WRAPPER_JYTHON = "jython";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER = "TemplateExceptionHandler";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW = "rethrow";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_DEBUG = "debug";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG = "htmlDebug";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE = "ignore";
    private static final String DEPR_INITPARAM_DEBUG = "debug";

    public static final String KEY_REQUEST = "Request";
    public static final String KEY_INCLUDE = "include_page";
    public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
    public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
    public static final String KEY_SESSION = "Session";
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";

    // Note these names start with dot, so they're essentially invisible from
    // a freemarker script.
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
    private static final String ATTR_REQUEST_PARAMETERS_MODEL =
            ".freemarker.RequestParameters";
    private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
    private static final String ATTR_APPLICATION_MODEL =
            ".freemarker.Application";
    private static final String ATTR_JSP_TAGLIBS_MODEL =
            ".freemarker.JspTaglibs";

    private static final String EXPIRATION_DATE;

    static {
        // Generate expiration date that is one year from now in the past
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(Calendar.YEAR, -1);
        SimpleDateFormat httpDate =
                new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z",
                        Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
    }

    private String templatePath;
    private boolean nocache;
    protected boolean debug;
    private Configuration config;
    private ObjectWrapper wrapper;
    private String contentType;
    private boolean noCharsetInContentType;

    public void init() throws ServletException {
        try {
            config = createConfiguration();

            // Set defaults:
            config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            contentType = DEFAULT_CONTENT_TYPE;

            // Process object_wrapper init-param out of order:
            wrapper = createObjectWrapper();
            if (logger.isDebugEnabled()) {
                logger.debug("Using object wrapper of class " + wrapper.getClass().getName());
            }
            config.setObjectWrapper(wrapper);

            // Process TemplatePath init-param out of order:
            templatePath = getInitParameter(INITPARAM_TEMPLATE_PATH);
            if (templatePath == null)
                templatePath = "class://";
            config.setTemplateLoader(createTemplateLoader(templatePath));

            // Process all other init-params:
            Enumeration initpnames = getServletConfig().getInitParameterNames();
            while (initpnames.hasMoreElements()) {
                String name = (String) initpnames.nextElement();
                String value = getInitParameter(name);

                if (name == null) {
                    throw new ServletException(
                            "init-param without param-name. "
                                    + "Maybe the web.xml is not well-formed?");
                }
                if (value == null) {
                    throw new ServletException(
                            "init-param without param-value. "
                                    + "Maybe the web.xml is not well-formed?");
                }

                if (name.equals(DEPR_INITPARAM_OBJECT_WRAPPER)
                        || name.equals(Configurable.OBJECT_WRAPPER_KEY)
                        || name.equals(INITPARAM_TEMPLATE_PATH)) {
                    // ignore: we have already processed these
                } else if (name.equals(DEPR_INITPARAM_ENCODING)) { // BC
                    if (getInitParameter(Configuration.DEFAULT_ENCODING_KEY) != null) {
                        throw new ServletException(
                                "Conflicting init-params: "
                                        + Configuration.DEFAULT_ENCODING_KEY + " and "
                                        + DEPR_INITPARAM_ENCODING);
                    }
                    config.setDefaultEncoding(value);
                } else if (name.equals(DEPR_INITPARAM_TEMPLATE_DELAY)) { // BC
                    if (getInitParameter(Configuration.TEMPLATE_UPDATE_DELAY_KEY) != null) {
                        throw new ServletException(
                                "Conflicting init-params: "
                                        + Configuration.TEMPLATE_UPDATE_DELAY_KEY + " and "
                                        + DEPR_INITPARAM_TEMPLATE_DELAY);
                    }
                    try {
                        config.setTemplateUpdateDelay(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        // Intentionally ignored
                    }
                } else if (name.equals(DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER)) { // BC
                    if (getInitParameter(Configurable.TEMPLATE_EXCEPTION_HANDLER_KEY) != null) {
                        throw new ServletException(
                                "Conflicting init-params: "
                                        + Configurable.TEMPLATE_EXCEPTION_HANDLER_KEY + " and "
                                        + DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER);
                    }

                    if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW.equals(value)) {
                        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                    } else if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_DEBUG.equals(value)) {
                        config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                    } else if  (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG.equals(value)) {
                        config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                    } else if  (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE.equals(value)) {
                        config.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                    } else {
                        throw new ServletException(
                                "Invalid value for servlet init-param "
                                        + DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER + ": " + value);
                    }
                } else if (name.equals(INITPARAM_NOCACHE)) {
                    nocache = StringUtil.getYesNo(value);
                } else if (name.equals(DEPR_INITPARAM_DEBUG)) { // BC
                    if (getInitParameter(INITPARAM_DEBUG) != null) {
                        throw new ServletException(
                                "Conflicting init-params: "
                                        + INITPARAM_DEBUG + " and "
                                        + DEPR_INITPARAM_DEBUG);
                    }
                    debug = StringUtil.getYesNo(value);
                } else if (name.equals(INITPARAM_DEBUG)) {
                    debug = StringUtil.getYesNo(value);
                } else if (name.equals(INITPARAM_CONTENT_TYPE)) {
                    contentType = value;
                } else {
                    config.setSetting(name, value);
                }
            } // while initpnames

            noCharsetInContentType = true;
            int i = contentType.toLowerCase().indexOf("charset=");
            if (i != -1) {
                char c = ' ';
                i--;
                while (i >= 0) {
                    c = contentType.charAt(i);
                    if (!Character.isWhitespace(c)) break;
                    i--;
                }
                if (i == -1 || c == ';') {
                    noCharsetInContentType = false;
                }
            }
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }

        ServletContext servletContext = getServletContext();
        ServletContextHashModel servletContextModel =
                (ServletContextHashModel) servletContext.getAttribute(
                        ATTR_APPLICATION_MODEL);
        if (servletContextModel == null)
        {
            servletContextModel = new ServletContextHashModel(this, wrapper);
            servletContext.setAttribute(
                    ATTR_APPLICATION_MODEL,
                    servletContextModel);
            TaglibFactory taglibs = new TaglibFactory(servletContext);
            servletContext.setAttribute(
                    ATTR_JSP_TAGLIBS_MODEL,
                    taglibs);
        }
    }

    /**
     * Create the template loader. The default implementation will create a
     * {@link freemarker.cache.ClassTemplateLoader} if the template path starts with "class://",
     * a {@link freemarker.cache.FileTemplateLoader} if the template path starts with "file://",
     * and a {@link freemarker.cache.WebappTemplateLoader} otherwise.
     * @param templatePath the template path to create a loader for
     * @return a newly created template loader
     * @throws java.io.IOException
     */
    protected TemplateLoader createTemplateLoader(String templatePath) throws IOException
    {
        if (templatePath.startsWith("class://")) {
            // substring(7) is intentional as we "reuse" the last slash
            return new ClassTemplateLoader(getClass(), templatePath.substring(7));
        } else {
            if (templatePath.startsWith("file://")) {
                templatePath = templatePath.substring(7);
                return new FileTemplateLoader(new File(templatePath));
            } else {
                return new WebappTemplateLoader(this.getServletContext(), templatePath);
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        process(request, response);
    }

    public void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        process(request, response);
    }

    private void process(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        // Give chance to subclasses to perform preprocessing
        if (preprocessRequest(request, response)) {
            return;
        }

        String path = requestUrlToTemplatePath(request);

        if (debug) {
            log("Requested template: " + path);
        }

        Template template = null;
        try {
            template = config.getTemplate(
                    path,
                    deduceLocale(path, request, response));
        } catch (FileNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Object attrContentType = template.getCustomAttribute("content_type");
        if(attrContentType != null) {
            response.setContentType(attrContentType.toString());
        }
        else {
            if (noCharsetInContentType) {
                response.setContentType(
                        contentType + "; charset=" + template.getEncoding());
            } else {
                response.setContentType(contentType);
            }
        }

        // Set cache policy
        setBrowserCachingPolicy(response);

        ServletContext servletContext = getServletContext();
        try {
            TemplateModel model = createModel(wrapper, servletContext, request, response);

            // Give subclasses a chance to hook into preprocessing
            if (preTemplateProcess(request, response, template, model)) {
                try {
                    // Process the template
                    template.process(model, response.getWriter());
                } finally {
                    // Give subclasses a chance to hook into postprocessing
                    postTemplateProcess(request, response, template, model);
                }
            }
        } catch (TemplateException te) {
            if (config.getTemplateExceptionHandler()
                    .getClass().getName().indexOf("Debug") != -1) {
                this.log("Error executing FreeMarker template", te);
            } else {
                ServletException e = new ServletException(
                        "Error executing FreeMarker template", te);
                // Attempt to set init cause, but don't freak out if the method
                // is not available (i.e. pre-1.4 JRE). This is required as the
                // constructor-passed throwable won't show up automatically in
                // stack traces.
                try {
                    e.getClass().getMethod("initCause",
                            new Class[] { Throwable.class }).invoke(e,
                            new Object[] { te });
                } catch (Exception ex) {
                    // Can't set init cause, we're probably running on a pre-1.4
                    // JDK, oh well...
                }
                throw e;
            }
        }
    }

    /**
     * Returns the locale used for the
     * {@link freemarker.template.Configuration#getTemplate(String, java.util.Locale)} call.
     * The base implementation simply returns the locale setting of the
     * configuration. Override this method to provide different behaviour, i.e.
     * to use the locale indicated in the request.
     */
    protected Locale deduceLocale(
            String templatePath, HttpServletRequest request, HttpServletResponse response) {
        return config.getLocale();
    }

    protected TemplateModel createModel(ObjectWrapper wrapper,
                                        ServletContext servletContext,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response) throws TemplateModelException {
        try {
            AllHttpScopesHashModel params = new AllHttpScopesHashModel(wrapper, servletContext, request);

            // Create hash model wrapper for servlet context (the application)
            ServletContextHashModel servletContextModel =
                    (ServletContextHashModel) servletContext.getAttribute(
                            ATTR_APPLICATION_MODEL);
            if (servletContextModel == null)
            {
                servletContextModel = new ServletContextHashModel(this, wrapper);
                servletContext.setAttribute(
                        ATTR_APPLICATION_MODEL,
                        servletContextModel);
                TaglibFactory taglibs = new TaglibFactory(servletContext);
                servletContext.setAttribute(
                        ATTR_JSP_TAGLIBS_MODEL,
                        taglibs);
                initializeServletContext(request, response);
            }
            params.putUnlistedModel(KEY_APPLICATION, servletContextModel);
            params.putUnlistedModel(KEY_APPLICATION_PRIVATE, servletContextModel);
            params.putUnlistedModel(KEY_JSP_TAGLIBS, (TemplateModel)servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));
//            // Create hash model wrapper for session
//            HttpSessionHashModel sessionModel;
//            HttpSession session = request.getSession(false);
//            if(session != null) {
//                sessionModel = (HttpSessionHashModel) session.getAttribute(ATTR_SESSION_MODEL);
//                if (sessionModel == null || sessionModel.isZombie()) {
//                    sessionModel = new HttpSessionHashModel(session, wrapper);
//                    session.setAttribute(ATTR_SESSION_MODEL, sessionModel);
//                    if(!sessionModel.isZombie()) {
//                        initializeSession(request, response);
//                    }
//                }
//            }
//            else {
//                //sessionModel = new HttpSessionHashModel(this, request, response, wrapper);
//            }
//            params.putUnlistedModel(KEY_SESSION, sessionModel);

            // Create hash model wrapper for request
            HttpRequestHashModel requestModel =
                    (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);
            if (requestModel == null || requestModel.getRequest() != request)
            {
                requestModel = new HttpRequestHashModel(request, response, wrapper);
                request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
                request.setAttribute(
                        ATTR_REQUEST_PARAMETERS_MODEL,
                        createRequestParametersHashModel(request));
            }
            params.putUnlistedModel(KEY_REQUEST, requestModel);
            params.putUnlistedModel(KEY_INCLUDE, new IncludePage(request, response));
            params.putUnlistedModel(KEY_REQUEST_PRIVATE, requestModel);

            // Create hash model wrapper for request parameters
            HttpRequestParametersHashModel requestParametersModel =
                    (HttpRequestParametersHashModel) request.getAttribute(
                            ATTR_REQUEST_PARAMETERS_MODEL);
            params.putUnlistedModel(KEY_REQUEST_PARAMETERS, requestParametersModel);
            return params;
        } catch (ServletException e) {
            throw new TemplateModelException(e);
        } catch (IOException e) {
            throw new TemplateModelException(e);
        }
    }

    /**
     * Maps the request URL to a template path that is passed to
     * {@link freemarker.template.Configuration#getTemplate(String, java.util.Locale)}. You can override it
     * (i.e. to provide advanced rewriting capabilities), but you are strongly
     * encouraged to call the overridden method first, then only modify its
     * return value.
     * @param request the currently processed request
     * @return a String representing the template path
     */
    protected String requestUrlToTemplatePath(HttpServletRequest request)
    {
        // First, see if it is an included request
        String includeServletPath  = (String) request.getAttribute("javax.servlet.include.servlet_path");
        if(includeServletPath != null)
        {
            // Try path info; only if that's null (servlet is mapped to an
            // URL extension instead of to prefix) use servlet path.
            String includePathInfo = (String) request.getAttribute("javax.servlet.include.path_info");
            return includePathInfo == null ? includeServletPath : includePathInfo;
        }
        // Seems that the servlet was not called as the result of a
        // RequestDispatcher.include(...). Try pathInfo then servletPath again,
        // only now directly on the request object:
        String path = request.getPathInfo();
        if (path != null) return path;
        path = request.getServletPath();
        if (path != null) return path;
        // Seems that it is a servlet mapped with prefix, and there was no extra path info.
        return "";
    }

    /**
     * Called as the first step in request processing, before the templating mechanism
     * is put to work. By default does nothing and returns false. This method is
     * typically overridden to manage serving of non-template resources (i.e. images)
     * that reside in the template directory.
     * @param request the HTTP request
     * @param response the HTTP response
     * @return true to indicate this method has processed the request entirely,
     * and that the further request processing should not take place.
     */
    protected boolean preprocessRequest(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        return false;
    }

    /**
     * This method is called from {@link #init()} to create the
     * FreeMarker configuration object that this servlet will use
     * for template loading. This is a hook that allows you
     * to custom-configure the configuration object in a subclass.
     * The default implementation returns a new {@link freemarker.template.Configuration}
     * instance.
     */
    protected Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * This method is called from {@link #init()} to create the
     * FreeMarker object wrapper object that this servlet will use
     * for adapting request, session, and servlet context attributes into
     * template models.. This is a hook that allows you
     * to custom-configure the wrapper object in a subclass.
     * The default implementation returns a wrapper that depends on the value
     * of <code>ObjectWrapper</code> init parameter. If <code>simple</code> is
     * specified, {@link freemarker.template.ObjectWrapper#SIMPLE_WRAPPER} is used; if <code>jython</code>
     * is specified, {@link freemarker.ext.jython.JythonWrapper} is used. In
     * every other case {@link freemarker.template.ObjectWrapper#DEFAULT_WRAPPER} is used.
     */
    protected ObjectWrapper createObjectWrapper() {
        String wrapper = getServletConfig().getInitParameter(DEPR_INITPARAM_OBJECT_WRAPPER);
        if (wrapper != null) { // BC
            if (getInitParameter(Configurable.OBJECT_WRAPPER_KEY) != null) {
                throw new RuntimeException("Conflicting init-params: "
                        + Configurable.OBJECT_WRAPPER_KEY + " and "
                        + DEPR_INITPARAM_OBJECT_WRAPPER);
            }
            if (DEPR_INITPARAM_WRAPPER_BEANS.equals(wrapper)) {
                return ObjectWrapper.BEANS_WRAPPER;
            }
            if(DEPR_INITPARAM_WRAPPER_SIMPLE.equals(wrapper)) {
                return ObjectWrapper.SIMPLE_WRAPPER;
            }
            if(DEPR_INITPARAM_WRAPPER_JYTHON.equals(wrapper)) {
                // Avoiding compile-time dependency on Jython package
                try {
                    return (ObjectWrapper) Class.forName("freemarker.ext.jython.JythonWrapper")
                            .newInstance();
                } catch (InstantiationException e) {
                    throw new InstantiationError(e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
//            return BeansWrapper.getDefaultInstance();
            return ObjectWrapper.DEFAULT_WRAPPER;
        } else {
            wrapper = getInitParameter(Configurable.OBJECT_WRAPPER_KEY);
            if (wrapper == null) {
//                return BeansWrapper.getDefaultInstance();
                return ObjectWrapper.DEFAULT_WRAPPER;
            } else {
                try {
                    config.setSetting(Configurable.OBJECT_WRAPPER_KEY, wrapper);
                } catch (TemplateException e) {
                    throw new RuntimeException(e.toString());
                }
                return config.getObjectWrapper();
            }
        }
    }

    protected ObjectWrapper getObjectWrapper() {
        return wrapper;
    }

    protected final String getTemplatePath() {
        return templatePath;
    }

    protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
        return new HttpRequestParametersHashModel(request);
    }

    /**
     * Called when servlet detects in a request processing that
     * application-global (that is, ServletContext-specific) attributes are not yet
     * set.
     * This is a generic hook you might use in subclasses to perform a specific
     * action on first request in the context. By default it does nothing.
     * @param request the actual HTTP request
     * @param response the actual HTTP response
     */
    protected void initializeServletContext(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
    }

    /**
     * Called when servlet detects in a request processing that session-global
     * (that is, HttpSession-specific) attributes are not yet set.
     * This is a generic hook you might use in subclasses to perform a specific
     * action on first request in the session. By default it does nothing. It
     * is only invoked on newly created sessions; it is not invoked when a
     * replicated session is reinstantiated in another servlet container.
     *
     * @param request the actual HTTP request
     * @param response the actual HTTP response
     */
    protected void initializeSession(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    /**
     * Called before the execution is passed to template.process().
     * This is a generic hook you might use in subclasses to perform a specific
     * action before the template is processed. By default does nothing.
     * A typical action to perform here is to inject application-specific
     * objects into the model root
     *
     * <p>Example: Expose the Serlvet context path as "baseDir" for all templates:
     *
     *<pre>
     *    ((SimpleHash) data).put("baseDir", request.getContextPath() + "/");
     *    return true;
     *</pre>
     *
     * @param request the actual HTTP request
     * @param response the actual HTTP response
     * @param template the template that will get executed
     * @param data the data that will be passed to the template. By default this will be
     *        an {@link freemarker.ext.servlet.AllHttpScopesHashModel} (which is a {@link freemarker.template.SimpleHash} subclass).
     *        Thus, you can add new variables to the data-model with the
     *        {@link freemarker.template.SimpleHash#put(String, Object)} subclass) method.
     * @return true to process the template, false to suppress template processing.
     */
    protected boolean preTemplateProcess(
            HttpServletRequest request,
            HttpServletResponse response,
            Template template,
            TemplateModel data)
            throws ServletException, IOException
    {
        return true;
    }

    /**
     * Called after the execution returns from template.process().
     * This is a generic hook you might use in subclasses to perform a specific
     * action after the template is processed. It will be invoked even if the
     * template processing throws an exception. By default does nothing.
     * @param request the actual HTTP request
     * @param response the actual HTTP response
     * @param template the template that was executed
     * @param data the data that was passed to the template
     */
    protected void postTemplateProcess(
            HttpServletRequest request,
            HttpServletResponse response,
            Template template,
            TemplateModel data)
            throws ServletException, IOException
    {
    }

    /**
     * Returns the {@link freemarker.template.Configuration} object used by this servlet.
     * Please don't forget that {@link freemarker.template.Configuration} is not thread-safe
     * when you modify it.
     */
    protected Configuration getConfiguration() {
        return config;
    }

    /**
     * If the parameter "nocache" was set to true, generate a set of headers
     * that will advise the HTTP client not to cache the returned page.
     */
    private void setBrowserCachingPolicy(HttpServletResponse res)
    {
        if (nocache)
        {
            // HTTP/1.1 + IE extensions
            res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, "
                    + "post-check=0, pre-check=0");
            // HTTP/1.0
            res.setHeader("Pragma", "no-cache");
            // Last resort for those that ignore all of the above
            res.setHeader("Expires", EXPIRATION_DATE);
        }
    }
}

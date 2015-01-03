#plumber

**plumber** 是一款实现页面 pagelet 并发执行的 java web 框架，并支持 BigPipe 的返回方式，以一切可以并发的方式提高你的页面响应速度。

##Core Features

* 支持将页面划分成多个可以并发执行的 pagelet , 每个 pagelet 有着自身独立的 mvc , 同时不同页面也可以复用这些 pagelet 。

* pagelet 提供同步 ( **barrier** ) 和异步 ( **pipe** ) 两种返回方式， **barrier** 方式实现的所有 pagelet 将作为页面的首次内容返回， **pipe** 方式实现的 pagelet 将在自身执行完成后以 [**BigPipe**](http://www.cubrid.org/blog/dev-platform/faster-web-page-loading-with-facebook-bigpipe/) 的方式继续返回。

##WorkFlow
![image](http://img.hb.aicdn.com/43b62f21e78717f8fb35bf4c47dfbb91a998661ad188-A01agD_fw658)

为设置、解析和控制 **barrier** **pipe** 等不同类型 pagelet， 保证它们的执行和返回顺序， **plumber** 有着自己的 **controller** ，在与 struts/spring mvc 等 web 框架一同运用的时候，strtus 等 MVC 框架的C将只承担请求参数转发的作用，，真正的 control 逻辑需要用户在 **plumber** 的 **controller** 层来完成。

当然用户并不需要关心具体 pagelet 具体的执行和返回顺序，这些都将由 **plumber** 来控制和保证，用户只需要在 **controller** 层通过框架提供的 **pb-barrier** 和 **pb-pipe** 这两个页面标签，来设置哪些 pagelet 为 **barrier** 类型，它们作为第一次 response 返回，哪些 pagelet 为 **pipe** 类型以 BigPipe 的方式后续继续返回。

一个 request 经由 struts/spring mvc 等 web 框架转发进入 **plumber** 的运行环境后，执行代码即为需要用户实现的一个 **controller** 加多个 **barrier** 或 **pipe** 。


##Getting Started

###1) 添加 pom 依赖

	<dependency>
	  <groupId>com.dianping</groupId>
	  <artifactId>plumber</artifactId>
	  <version>1.0.0-SNAPSHOT</version>
	</dependency>
	
###2) plumber.yaml
在 **resources** 下添加 plumber.yaml 配置文件 ：

	view:
	    encoding: UTF-8
	    viewSourceLoaderFactory: com.dianping.plumber.view.support.loader.ViewSourceUnderClassPathLoaderFactory
	    viewRendererFactory: com.dianping.plumber.view.support.renderer.freemarker.FreemarkerRendererFactory
	concurrent:
	    timeout: 1000
	    threadPool:
	        corePoolSize: 50
	        maximumPoolSize: 50
	        keepAliveTime: 0
	        blockingQueueCapacity: 1000
	        


###3）spring配置

	<bean id="plumber" class="com.dianping.plumber.core.Plumber">
    </bean>

###4）让我们来完成一个demo
demo 页面基于 struts+spring+plumber 来开发，模板引擎使用 **plumber** 默认提供的 freemarker，模板资源放在 resources 下。

demo 页面包含 headBarrier rightBarrier 和 mainPipe 3个 pagelet, 其中 headBarrier 和 rightBarrier 将作为首次内容输出，mainPipe 以 BigPipe 的方式后续输出。

![image](http://img.hb.aicdn.com/edd20546044223879ad06a1fb18a017aa1d9266a16f17-lFxzti_fw658)

mainPipe 类似于微博中 feed 这样比较耗时的模块，为了缓解用户等待页面加载的焦虑感，我们可以先返回一个执行起来不太耗时的框子页面（包含页头和右边栏），让用户看到部分输出，而不是对着一个空白页，且返回该框子页面后，客户端浏览器已经可以开始加载相关静态资源了。mainPipe 在处理完成后以 BigPipe 的方式作为第二次 response 返回。

#####struts
	public class StrutsDemo extends ActionSupport {

    	private Plumber plumber;

        @Override
        public String execute() throws Exception {


            String plumberControllerName = "demoController";

            Map<String, Object> paramsForController = new HashMap<String, Object>();
            paramsForController.put("demoDesc", "StrutsDemo");

            HttpServletResponse response = ServletActionContext.getResponse();
            plumber.execute(plumberControllerName, paramsForController, null, response);

            return null;
        }

        public void setPlumber(Plumber plumber) {
            this.plumber = plumber;
        }
    }
    

    <package name="struts" namespace="/struts" extends="struts-default">

        <action name="demo" class="com.dianping.struts.StrutsDemo">
        </action>

    </package>

#####PlumberController

**业务逻辑 PlumberController** :

	public class DemoController extends PlumberController {

	    @Override
	    public ResultType execute(Map<String, Object> paramsFromRequest, Map<String, Object> paramsForPagelets, Map<String, Object> modelForView) {

	        paramsForPagelets.put("param", "test paramFromController");

	        modelForView.put("title", "plumber-tutorial of " + paramsFromRequest.get("demoDesc"));

	        return ResultType.SUCCESS;
	    }

	}

 使用 **plumber** 时，struts 只是作为用户请求的转发器，controller 都是由实现了 PlumberController 的实现类真正接管。
 
 *paramsFromRequest* 来自于用户请求的参数，由struts、spring mvc 等 web 框架转发
 
 *paramsForPagelets* 提供给子级 pagelet 共用的参数
 
 *modelForView* 填充 demo.ftl 的 model
 
 
 
**Controller Spring 配置** :
 
 	<bean id="demoController" class="com.dianping.plumber.DemoController">
        <property name="viewPath" value="/view/demo.ftl" />
    </bean>
    
无论是 **controller** 还是 **barrier** 或 **pipe** , 在 **plumber** 中都需要被配置成一个 spring 的 bean ，因为 **plumber** 是直接基于 spring 来做对象管理的，并且 **plumber** 会自行将所有的 **controller** **barrier** 以及 **pipe** 的 scope 设置成 prototype。
 
**容器页面 demo.ftl** :

	<html>
        <head>
            <title>${title}</title>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <link rel="stylesheet" type="text/css" href="/static/css/demo.css">
        </head>

    <body>

        <div id="head" pb-barrier="headBarrier">
            ${headBarrier}
        </div>

        <div id="content">
            <div id="main" pb-pipe="mainPipe">
                main
            </div>

            <div id="right" pb-barrier="rightBarrier">
                ${rightBarrier}
            </div>

            <div class="clear-both"></div>
        </div>

        <script src="/static/js/lib/jquery-min.js"></script>
        <script src="/static/js/demo.js"></script>

    <#--</body>-->    
    <#--</html>-->
    
demo.ftl将作为所有 **barrier** 类型 pagelet 的容器页面，待所有的 **barrier** 执行完成并将其渲染结果填充到该页面后，作为第一次 response 返回。
   
**plumber** 提供了 **pb-barrier** 和 **pb-pipe** 两种页面属性，分别对应 barrier 和 pipe 两种返回方式， **plumber** 会自动解析该模板页面，识别出其中这两种类型的 pagelet 。

例如这里的 pb-barrier="headBarrier" ，**plumber** 将识别出这里有一个 **barrier** 类型的 pagelet，然后从 **spring** 容器中需找一个 id 为 headBarrier 的 pagelet ( **plumber** 基于 ) ，它的执行渲染结果





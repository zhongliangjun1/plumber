#plumber

**plumber** 是一款实现页面 pagelet 并发执行的 java web 框架，并支持以 [**BigPipe**](http://www.cubrid.org/blog/dev-platform/faster-web-page-loading-with-facebook-bigpipe/) 的方式返回你的 pagelet 执行结果，以一切可能并发的方式提高你的页面响应速度。



##Core Features

* 支持将页面划分成多个可以并发执行的 pagelet , 每个 pagelet 有着自身独立的 mvc , 同时不同页面也可以复用这些 pagelet 。

* pagelet 提供同步 ( **barrier** ) 和异步 ( **pipe** ) 两种返回方式， **barrier** 方式实现的所有 pagelet 将作为页面的首次内容返回， **pipe** 方式实现的 pagelet 将在自身执行完成后以 [**BigPipe**](http://www.cubrid.org/blog/dev-platform/faster-web-page-loading-with-facebook-bigpipe/) 的方式继续返回给客户端。




##WorkFlow
![image](http://img.hb.aicdn.com/43b62f21e78717f8fb35bf4c47dfbb91a998661ad188-A01agD_fw658)

为设置、解析和控制 **barrier** **pipe** 等不同类型 pagelet， 保证它们的执行和返回顺序， **plumber** 有着自己的 **controller** ，在与 struts/spring mvc 等 web 框架一同运用的时候，strtus 等 MVC 框架的C将只承担请求参数转发的作用，真正的 control 逻辑需要用户在 **plumber** 的 **controller** 层来完成。

当然用户并不需要关心具体 pagelet 具体的执行和返回顺序，这些都将由 **plumber** 来控制和保证，用户只需要在 **controller** 层通过框架提供的 **pb-barrier** 和 **pb-pipe** 这两个页面标签，来设置哪些 pagelet 为 **barrier** 类型，它们作为第一次 response 返回，哪些 pagelet 为 **pipe** 类型以 BigPipe 的方式后续继续返回。

一个 request 经由 struts/spring mvc 等 web 框架转发进入 **plumber** 的运行环境后，执行代码即为需要用户来实现的一个 **controller** 加多个 **barrier** 或 **pipe** 。




##Getting Started

###1) 添加 pom 依赖

	<dependency>
	  <groupId>com.dianping</groupId>
	  <artifactId>plumber</artifactId>
	  <version>1.0.0-SNAPSHOT</version>
	</dependency>
	
###2) 添加 plumber.yaml 配置
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
	        


###3）添加 spring 配置

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

**业务逻辑 DemoController** :

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
 
 
 
**controller Spring 配置** :
 
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
    
demo.ftl 将作为所有 **barrier** 类型 pagelet 的容器页面，待所有的 **barrier** 执行完成并将其渲染结果填充到该页面后，作为第一次 response 返回。
   
**plumber** 提供了 **pb-barrier** 和 **pb-pipe** 两种页面属性，分别对应 barrier 和 pipe 两种返回方式， **plumber** 会自动解析该模板页面，识别出其中这两种类型的 pagelet 。

例如这里的 pb-barrier="headBarrier" ，**plumber** 将识别出 demo.ftl 中有一个 **barrier** 类型的 pagelet，然后从 **spring** 容器中需找一个 id 为 headBarrier 的 pagelet ，它的执行渲染结果将以一个名为 headBarrier 的变量填充到 demo.ftl 中。

headBarrier 和 rightBarrier 将以并发的方式得到执行，待他们都执行完成，渲染结果填充到 demo.ftl 中后，demo.ftl 将作为第一次 response 发送到客户端。

而名为 mainPipe 这个 **pipe** 类型的 pagelet 将继续执行，待它执行完成后，将以 [**chunked**](http://zh.wikipedia.org/wiki/%E5%88%86%E5%9D%97%E4%BC%A0%E8%BE%93%E7%BC%96%E7%A0%81) 的方式继续往客户端发送。如果你有多个 **pipe** 类型的 pagelet 的话，他们将无干扰的在各自执行完成后自行返回给客户端。

也许你已经注意到在 demo.ftl 中，我们未曾将 < body > 和 < html > 标签闭合，这是因为在这两个标签闭合后，浏览器将不再接受服务端的 response ，那样即使我们的 **pipe** 类型的 response 发送到客户端，客户端也不会接受加以解析。

所以如果你的页面中用到了 **pipe** 类型的 pagelet ，请不要为你的容器页面添加上述闭合标签， **plumber** 会在该页面的所有 **pipe** 类型的 pagelet 成功返回后自行补上该闭合标签。

而如果你的页面只包含 **barrier** 类型的 pagelet ，那还是在你的容器页面中，直接将标签闭合吧。




#####PlumberBarrier

	public class HeadBarrier extends PlumberBarrier {

        private Logger logger = Logger.getLogger(RightBarrier.class);

        @Override
        public ResultType execute(Map<String, Object> paramsFromRequest, Map<String, Object> paramsFromController, Map<String, Object> modelForView) {

            String demoDesc = paramsFromRequest.get("demoDesc");

            String param = paramsFromController.get("param");

            modelForView.put("msg", "Get HeadBarrier Content! "+ param + " " + demoDesc);

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                logger.error(e);
            }

            return ResultType.SUCCESS;
        }

    }
    

    <bean id="headBarrier" class="com.dianping.plumber.HeadBarrier">
        <property name="viewPath" value="/view/headBarrier.ftl" />
    </bean>
    

    headBarrier.ftl:
    ${msg}
    
    

#####PlumberPipe
	public class MainPipe extends PlumberPipe {

        private Logger logger = Logger.getLogger(MainPipe.class);

        @Override
        public ResultType execute(Map<String, Object> paramsFromRequest, Map<String, Object> paramsFromController, Map<String, Object> modelForView) {

            try {
                Thread.sleep(4000);
                SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
                modelForView.put("msg", "Get MainPipe Content! " + time.format(new Date()));
            } catch (InterruptedException e) {
                logger.error(e);
            }

            return ResultType.SUCCESS;
        }
    }


    <bean id="mainPipe" class="com.dianping.plumber.MainPipe">
        <property name="viewPath" value="/view/mainPipe.ftl" />
    </bean>
    

    mainPipe.ftl:
    <script>
        $("#main").html("${msg}");
    </script>

当页面存在多个 **pipe** 类型的 pagelet 时，因我们无法保证它们的先后执行顺序，所以一般的处理方式是，在容器页面中设置一个 placeholder 的 dom 节点，如 demo.ftl 中的 ：

	<div id="main" pb-pipe="mainPipe">
        main
    </div>
而将 **pipe** 的执行结果包在一段 javascript 中， 通过 javascript append 到 placeholder 的 dom 节点中，从而保证 **pipe** 类型 pagelet 的执行结果能放置到你预期的页面位置，当然，这可能对你的页面 SEO 有一定的影响。


如果你跟着我们一步一步做，至此你应该已经可以将这个 demo 部署到任意 web 容器，run 起来看一下执行结果了。

同时，你也可以打开浏览器控制台，看一下这个 request 请求，是不是和我一样，response header 中包含 Transfer-Encoding:chunked。 事实上 **plumber** 中 BigPipe 的实现正是基于 http [**chunked**](http://zh.wikipedia.org/wiki/%E5%88%86%E5%9D%97%E4%BC%A0%E8%BE%93%E7%BC%96%E7%A0%81) 来达成的。

上述 demo 的所有代码，你均可以在 [plumber-tutorial](https://github.com/zhongliangjun1/plumber-tutorial) 中找到。




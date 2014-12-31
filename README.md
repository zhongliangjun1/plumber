#plumber

**plumber** 是一款实现页面 pagelet 并发执行的 java web 框架，并支持 BigPipe 的返回方式，以尽可能一切并发的方式提高你的页面响应速度。

##Core Features

* 支持将页面划分成多个可以并发执行的 pagelet , 每个 pagelet 有着自身独立的 mvc , 不同页面可以复用这些 pagelet 。

* pagelet 提供同步 ( **barrier** ) 和异步 ( **pipe** ) 两种返回方式， **barrier** 方式实现的所有 pagelet 将作为页面的首次内容返回， **pipe** 方式实现的 pagelet 将在自身执行完成后以 [**BigPipe**](http://www.cubrid.org/blog/dev-platform/faster-web-page-loading-with-facebook-bigpipe/) 的方式返回。

##Getting Started

###1) 添加pom依赖

	<dependency>
	  <groupId>com.dianping</groupId>
	  <artifactId>plumber</artifactId>
	  <version>1.0.0-SNAPSHOT</version>
	</dependency>
	
###2) plumber.yaml
在 **resources** 下添加 plumber.yaml 配置文件 ：

	configOverriderFactory: # for implementing configuration external
	env: dev
	view:
	    encoding: UTF-8
	    viewSourceLoaderFactory: com.dianping.plumber.view.support.loader.ViewSourceUnderWebContextLoaderFactory
	    viewRendererFactory: com.dianping.plumber.view.support.renderer.freemarker.FreemarkerRendererFactory
	concurrent:
	    timeout: 1000
	    threadPool:
	        corePoolSize: 50
	        maximumPoolSize: 50
	        keepAliveTime: 0
	        blockingQueueCapacity: 1000
	        
配置介绍：

* **configOverriderFactory** 用于实现配置外部化。你可以提供一个 PlumberConfigOverriderFactory 的实现，**plumer** 将调用该 factory 获取你在 PlumberConfigOverrider 中重写的属性配置。

* **env** 配置为 dev 环境时，**plumber** 会直接将抛出异常的 pagelet 堆栈信息作为模块内容输出到页面上，便于查看出错信息，配置为 product 时，**plumber** 将丢弃该 pagelet ，输出其余 pagelet 内容。你可以通过 **configOverriderFactory** 确保生产环境对该属性的覆盖，避免 dev/product 切换需频繁修改该配置。

* **encoding** 你的页面模板文件编码方式

* **viewSourceLoaderFactory** 页面模板的 loader 工厂类。 **plumber** 默认提供了从 classpath 和 WEB-INF 下加载的两种默认实现： ViewSourceUnderClassPathLoaderFactory 和 ViewSourceUnderWebContextLoaderFactory。需要注意的是，使用 ViewSourceUnderWebContextLoaderFactory 时需要在 web.xml 中将 ViewSourceUnderWebContextLoader 添加为listener，且须在 spring 的 ContextLoaderListener 之前。

* **viewRendererFactory** 页面模板引擎的工厂类。 **plumber** 默认提供了对 freemarker 引擎的支持，当然你也可以使用任意其他模板引擎，提供一个 ViewRendererFactory 的实现即可。

* **concurrent** plumber 线程池的相关配置，你可以根据自己的业务特性做相应调整。






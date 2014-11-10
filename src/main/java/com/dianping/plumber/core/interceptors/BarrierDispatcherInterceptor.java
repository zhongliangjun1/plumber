package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.*;
import com.dianping.plumber.utils.CollectionUtils;
import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: PM10:42
 * To change this template use File | Settings | File Templates.
 */
public class BarrierDispatcherInterceptor implements Interceptor {


    @Override
    public ResultType intercept(InvocationContext invocation) throws Exception {
        String controllerName = invocation.getControllerName();
        ApplicationContext applicationContext = invocation.getApplicationContext();
        PlumberController controller = (PlumberController) applicationContext.getBean(controllerName);
        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo.getPlumberControllerDefinition(controllerName);
        if ( CollectionUtils.isEmpty(controllerDefinition.getBarrierNames()) ) {
            return invocation.invoke();
        } else {
            controllerDefinition.getBarrierNames();
        }

        return null;
    }


}

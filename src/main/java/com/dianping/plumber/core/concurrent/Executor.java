package com.dianping.plumber.core.concurrent;

import com.dianping.plumber.config.PlumberConfig;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ltebean
 */
public class Executor {

    private ThreadPoolExecutor executorService;

    private Status status;

    private Logger logger = Logger.getLogger(Executor.class);

    enum Status {
        ALIVE, SHUTDOWN
    };

    private Executor() {
        init();
    }

    private static final Executor instance = new Executor();

    public static ExecutorService getInstance(){
        return instance.executorService;
    }

    public void init() {
        int corePoolSize= PlumberConfig.getConcurrentCorePoolSize();
        int maximumPoolSize= PlumberConfig.getConcurrentMaximumPoolSize();
        int keepAliveTime= PlumberConfig.getConcurrentKeepAliveTime();
        int blockingQueueCapacity= PlumberConfig.getConcurrentBlockingQueueCapacity();

        if (executorService == null) {
            executorService = new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(blockingQueueCapacity),
                    new ThreadPoolExecutor.CallerRunsPolicy());

            logger.info("executorService has been initialized");

            // adds a shutdown hook to shut down the executorService
            Thread shutdownHook = new Thread() {
                @Override
                public void run() {
                    synchronized (this) {
                        if (status == Status.ALIVE) {
                            executorService.shutdown();
                            status = Status.SHUTDOWN;
                            logger.info("excecutorService has been shut down");
                        }
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            logger.info("successfully add shutdown hook");
        }
    }

}

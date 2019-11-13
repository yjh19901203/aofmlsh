package com.sh.mlshcommon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

    private static Logger log = LoggerFactory.getLogger("ThreadPoolUtil");

   private static ThreadPoolExecutor executor = new ThreadPoolExecutor(5,30,60,TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static Future<?> submit(Runnable runnable){
        return executor.submit(runnable);
    }

    public static void execute(Runnable runnable){
        log.info("======当前总线成数：{}，活跃线程数：{}",executor.getTaskCount(),executor.getActiveCount());
        executor.execute(runnable);
    }
}

package com.sh.mlshcommon.util;

import java.util.concurrent.Semaphore;

public class SemaphoreUtil {
    ThreadLocal semaphoreThreadLocal = new ThreadLocal<Semaphore>();
    public void getInstance(){

    }
}

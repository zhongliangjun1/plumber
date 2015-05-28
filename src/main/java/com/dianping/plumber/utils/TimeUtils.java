/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.utils;

import java.util.Date;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: TimeUtils.java, v 0.1 5/24/15 11:17 AM liangjun.zhong Exp $$
 */
public abstract class TimeUtils {

    public static Date getCurrentTime() {
        return new Date();
    }

    public static long getCostTime(Date startTime) {
        Date now = getCurrentTime();
        return now.getTime() - startTime.getTime();
    }

    public static boolean isTimeout(Date startTime, long timeLimit) {
        return (getCostTime(startTime) - timeLimit) >= 0;
    }

    public static long getRemainingTime(Date startTime, long timeLimit) {
        long remainingTime = timeLimit - getCostTime(startTime);
        if (remainingTime < 0)
            remainingTime = 0;
        return remainingTime;
    }

}

package com.dianping.plumber.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 15/2/10
 * Time: PM5:42
 * To change this template use File | Settings | File Templates.
 */
public class MapUtils {

    public static ConcurrentHashMap convert(Map map) {

        ConcurrentHashMap result = new ConcurrentHashMap();
        if ( map==null || map.isEmpty() )
            return result;

        convert(map, result);

        return result;
    }

    public static void convert(Map from, ConcurrentHashMap to) {

        if ( from==null || to==null )
            throw new IllegalArgumentException();

        Set keys = from.keySet();

        for ( Object key : keys ) {
            if ( key!=null && from.get(key)!=null ) {
                to.put(key, from.get(key));
            }
        }

    }


}

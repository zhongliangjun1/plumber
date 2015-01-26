package com.dianping.plumber.test;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 15/1/26
 * Time: PM8:29
 * To change this template use File | Settings | File Templates.
 */
public class ListTest {

    public static void sort(List<Integer> list) {
        Collections.sort(list);
        System.out.println(list);
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(2);
        list.add(1);
        list.add(3);
        list.add(2);
        sort(list);
    }

}

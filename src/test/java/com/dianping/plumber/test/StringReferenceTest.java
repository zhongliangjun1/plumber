package com.dianping.plumber.test;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: AM1:47
 * To change this template use File | Settings | File Templates.
 */
public class StringReferenceTest {

    private static void test(String s) {
        s = "B";
    }

    public static void main(String[] args) {
        String s = "A";
        test(s);
        System.out.println(s);
    }

}

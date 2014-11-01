package com.dianping.plumber.utils;

import com.dianping.plumber.core.PlumberConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-8-10
 * Time: PM12:01
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static String inputStreamToString(InputStream inputStream, String encoding) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int BUFFER_SIZE = PlumberConstants.DEFAULT_BUFFER_SIZE;
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        while((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1)
            outputStream.write(data, 0, count);

        String str = new String(outputStream.toByteArray(), encoding);
        inputStream.close();
        outputStream.close();
        return str;
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

}

package com.dianping.plumber.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-29
 * Time: PM8:33
 * To change this template use File | Settings | File Templates.
 */
public class ResourceUtil {

    public static String getResource(String path) throws IOException {
        ClassPathResource loader = new ClassPathResource(path, ResourceUtil.class.getClassLoader());
        InputStream inputStream = loader.getInputStream();

        String scriptText = inputStreamToString(inputStream);
        return scriptText;
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int BUFFER_SIZE = 1024;
        byte[] data = new byte[BUFFER_SIZE];
        int count;
        while((count = inputStream.read(data,0,BUFFER_SIZE)) != -1)
            outputStream.write(data, 0, count);

        String str = new String(outputStream.toByteArray(),"UTF-8");
        inputStream.close();
        outputStream.close();
        return str;
    }

}

package com.dianping.plumber.view;

import com.dianping.plumber.exception.ViewSourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-31
 * Time: AM9:58
 * To change this template use File | Settings | File Templates.
 */
public class ViewParser {

    public static List<String> recognizePipeNames(String viewSource) {
        if ( viewSource==null )
            throw new ViewSourceNotFoundException();
        List<String> pipeNames = new ArrayList<String>();
        String reg = "pb-pipe\\s*=\\s*[\"\'][A-Za-z]+[\"\']";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(viewSource);
        while ( matcher.find() ) {
            String info = matcher.group();
            info = info.replaceAll(" ", "");
            info = info.replaceAll("=", "");
            info = info.replaceAll("pb-pipe", "");
            info = info.replaceAll("\"", "");
            String pipeName = info.replaceAll("\'", "");
            pipeNames.add(pipeName);
        }
        return pipeNames ;
    }


}

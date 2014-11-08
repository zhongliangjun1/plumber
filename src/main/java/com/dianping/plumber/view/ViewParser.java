package com.dianping.plumber.view;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberPageComponentEnum;
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

    public static List<String> recognizeBarrierNames(String viewSource) {
        return recognizePageletNames(PlumberPageComponentEnum.PlumberBarrier, viewSource) ;
    }

    public static List<String> recognizePipeNames(String viewSource) {
        return recognizePageletNames(PlumberPageComponentEnum.PlumberPipe, viewSource) ;
    }

    public static List<String> recognizePageletNames(PlumberPageComponentEnum pageComponentEnum, String viewSource) {

        if ( pageComponentEnum==null || pageComponentEnum==PlumberPageComponentEnum.PlumberController )
            throw new IllegalArgumentException();
        if ( viewSource==null )
            throw new ViewSourceNotFoundException();

        String reg = "\\s*=\\s*[\"\'][A-Za-z]+[\"\']";
        String placeholder;
        if ( pageComponentEnum==PlumberPageComponentEnum.PlumberBarrier ) {
            placeholder = PlumberConfig.getBarrierViewPlaceHolder();
            reg = placeholder + reg; // "pb-barrier\\s*=\\s*[\"\'][A-Za-z]+[\"\']";
        } else {
            placeholder = PlumberConfig.getPipeViewPlaceHolder();
            reg = placeholder + reg; // "pb-pipe\\s*=\\s*[\"\'][A-Za-z]+[\"\']";
        }

        List<String> names = new ArrayList<String>();

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(viewSource);
        while ( matcher.find() ) {
            String info = matcher.group();
            info = info.replaceAll(" ", "");
            info = info.replaceAll("=", "");
            info = info.replaceAll(placeholder, "");
            info = info.replaceAll("\"", "");
            String pipeName = info.replaceAll("\'", "");
            names.add(pipeName);
        }
        return names ;
    }


}

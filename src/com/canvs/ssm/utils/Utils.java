package com.canvs.ssm.utils;


import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {
    public static Date dateFormat(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parse = simpleDateFormat.parse(date);
        return new Date(parse.getTime());
    }
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
    public static boolean isStaticResource(String uri){
        return uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".gif") || uri.endsWith(".bmp");
    }
}

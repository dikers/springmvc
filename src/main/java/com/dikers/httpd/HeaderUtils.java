package com.dikers.httpd;

import com.dikers.httpd.exception.InvalidHeaderException;

import java.util.Objects;

public class HeaderUtils {

    private static final String KEY_VALUE_SEPARATOR = ":";

    private static final String CRLF = "\r\n";

    // 解析消息头
    public static Headers parseHeader(String headerStr) {
        if (Objects.isNull(headerStr) || headerStr.isEmpty()) {
            throw new InvalidHeaderException();
        }

        int index = headerStr.indexOf(CRLF);
        if (index == -1) {
            throw new InvalidHeaderException();
        }

        Headers headers = new Headers();
        String firstLine = headerStr.substring(0, index);
        String[] parts = firstLine.split(" ");

        /*
         * 请求头的第一行必须由三部分构成，分别为 METHOD PATH VERSION
         * 比如：
         *     GET /index.html HTTP/1.1
         */
        if (parts.length < 3) {
            throw new InvalidHeaderException();
        }

        headers.setMethod(parts[0]);

        headers.setPath(getPathAndParams(headers, parts[1]));

        headers.setVersion(parts[2]);

        parts = headerStr.split(CRLF);
        for (String part : parts) {
            index = part.indexOf(KEY_VALUE_SEPARATOR);
            if (index == -1) {
                continue;
            }
            String key = part.substring(0, index);
            if (index == -1 || index + 1 >= part.length()) {
                headers.set(key, "");
                continue;
            }
            String value = part.substring(index + 1);
            headers.set(key, value);
        }
        headers.setBody(parts[parts.length-1]);
        return headers;
    }

    private static String getPathAndParams(Headers headers, String path){

        int start = path.indexOf("?");
        String temp = path.substring(start+1 , path.length());

        String [] paramStr = temp.split("&");

        for(String string: paramStr){

            String[] values = string.split("=");
            if(values != null || values.length == 2){
                headers.setParam(values[0], values[1]);
            }

        }


        return path.substring(0  ,start);
    }
}

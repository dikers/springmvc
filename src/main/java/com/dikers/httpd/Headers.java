package com.dikers.httpd;

import java.util.HashMap;
import java.util.Map;

/**
 * Headers
 *
 * @author dikers
 * @date 2018-03-26 22:29:57
 */
public class Headers {

    private String method;

    private String path;

    private String version;

    private String body;

    private Map<String, String> paramMap = new HashMap<>();

    private Map<String, String> headerMap = new HashMap<>();





    public Headers() {

    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getParamMap(){
        return paramMap;
    }

    public String getParam(String param){
        return paramMap.get(param);
    }
    public void setParam(String param, String value){
        paramMap.put(param, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void set(String key, String value) {
        headerMap.put(key, value);
    }

    public String get(String key) {
        return headerMap.get(key);
    }

    @Override
    public String toString() {
        return "Headers{" +
            "method='" + method + '\'' +
            ", path='" + path + '\'' +
            ", version='" + version + '\'' +
            ", headerMap=" + headerMap +
            '}';
    }
}

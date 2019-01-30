package com.dikers.httpd;

/**
 * StatusCodeEnum
 *
 * @author dikers
 * @date 2018-03-27 12:29:43
 */
public enum StatusCodeEnum {
    /**
     * 200 ok
     */
    OK(200, "OK"),
    /**
     * 400 请求格式不正确
     */
    BAD_REQUEST(400, "Bad Request"),
    /**
     * 403 禁止访问
     */
    FORBIDDEN(403, "Forbidden"),
    /**
     * 404 没有找到
     */
    NOT_FOUND(404, "Not Found"),
    /**
     * 500 服务器内部错误。
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private int code;

    private String phrase;

    StatusCodeEnum(int code, String phrase) {
        this.code = code;
        this.phrase = phrase;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public static String queryPhrase(int code) {
        for (StatusCodeEnum statusCodeEnum : StatusCodeEnum.values()) {
            if (statusCodeEnum.getCode() == code) {
                return statusCodeEnum.getPhrase();
            }
        }

        return null;
    }
}

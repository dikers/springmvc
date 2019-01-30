package com.dikers.exception;

/**
 * @author  dikers
 * @date  2019-02-25
 *
 * 自定义异常
 */
public class DuplicateBeanException extends RuntimeException {


    private Integer code;

    public DuplicateBeanException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

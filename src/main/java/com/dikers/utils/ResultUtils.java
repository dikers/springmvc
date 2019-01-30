package com.dikers.utils;


import com.dikers.vo.ResultVo;

public class ResultUtils {

    public static ResultVo success(Object data) {
        return success( 200, "OK", data);
    }


    public static ResultVo success(int status, String message, Object data) {

        ResultVo resultVo = new ResultVo();
        resultVo.setStatus(status);
        resultVo.setMsg(message);
        resultVo.setData(data);
        return resultVo;

    }


    public static ResultVo fail(int status, String errorMsg) {
        ResultVo resultVo = new ResultVo();
        resultVo.setStatus(status);
        resultVo.setMsg(errorMsg);
        return resultVo;

    }

    public static ResultVo fail(int status, String errorMsg,Object data) {
        ResultVo resultVo = new ResultVo();
        resultVo.setStatus(status);
        resultVo.setMsg(errorMsg);
        resultVo.setData(data);
        return resultVo;

    }


}

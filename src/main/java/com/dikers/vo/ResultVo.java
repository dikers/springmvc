package com.dikers.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import java.io.Serializable;

/**
 * ajax调用统一返回该结构
 * <p>
 * 返回信息
 */
@Data
public class ResultVo<T> implements Serializable {
    private static final long serialVersionUID = 4712972757347990461L;

    private int status = 500;
    private String msg = "";
    private T data = null;

    public ResultVo() {
        super();
    }

    public ResultVo(int status) {
        this.status = status;
    }



    public ResultVo(int status, String msg) {

        this.status = status;
        this.msg = msg;

    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

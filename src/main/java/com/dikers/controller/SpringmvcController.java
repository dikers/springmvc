package com.dikers.controller;

import com.alibaba.fastjson.JSON;
import com.dikers.annotation.Autowired;
import com.dikers.annotation.Controller;
import com.dikers.annotation.RequestMapping;
import com.dikers.httpd.Headers;
import com.dikers.service.MyService;
import com.dikers.utils.ResultUtils;
import com.dikers.vo.ResultVo;


/**
 * Created by dikers on 2019/1/30.
 * 测试用的Controller
 */
@Controller
public class SpringmvcController {


    @Autowired
    MyService myService;

    public void setMyService(MyService myService) {
        this.myService = myService;
    }

    @RequestMapping("insert")
    public ResultVo insert(Headers headers) {
        myService.insert(headers.getBody() +"  params: " + JSON.toJSONString(headers.getParamMap()));

        return ResultUtils.success("insert ok");
    }

    @RequestMapping("delete")
    public ResultVo delete(Headers headers) {
        myService.delete(headers.getBody()+"  params: "  + JSON.toJSONString(headers.getParamMap()));
        return ResultUtils.success("delete ok");
    }

    @RequestMapping("update")
    public ResultVo update(Headers headers) {
        myService.update(headers.getBody()+"  params: " + JSON.toJSONString(headers.getParamMap()));
        return ResultUtils.success("update ok");
    }

    @RequestMapping("select")
    public ResultVo select(Headers headers) {
        myService.select(headers.getBody()+"  params: "  + JSON.toJSONString(headers.getParamMap()));
        return ResultUtils.success("select  ok");
    }
}
package com.ceit.desktop.controller;

import com.ceit.desktop.service.TerminalManegementService;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;

import java.util.Map;

@Controller("/terminalManagement")
public class TerminalManagementController {

    @Autowired
    private TerminalManegementService terminalManegementService;

    @Autowired
    private SimpleJDBC simpleJDBC;

    @RequestMapping("/terminalList")
    public Result terminalList(Map<String, Object> reqBody){
        return terminalManegementService.terminalList(reqBody);
    }

    //获取终端状态
    @RequestMapping("/terminalStatus")
    public Result terminalStatus(Map<String, Object> reqBody){
        return terminalManegementService.terminalStatus(reqBody);
    }

    //获取硬件认证信息
    @RequestMapping("/harawareCheckInfo")
    public Result harawareCheckInfo(Map<String, Object> reqBody){
       return terminalManegementService.harawareCheckInfo(reqBody);
    }

    //获取硬件认证信息
    @RequestMapping("/softwareCheckInfo")
    public Result softwareCheckInfo(Map<String, Object> reqBody){
        return terminalManegementService.harawareCheckInfo(reqBody);
    }
}

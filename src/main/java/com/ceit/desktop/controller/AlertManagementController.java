package com.ceit.desktop.controller;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.util.Map;

@Controller("/alertRequest")
public class AlertManagementController {

    @Autowired
    private SimpleJDBC simpleJDBC;

    //硬件异常信息
    @RequestMapping("/hardwareAlert")
    public Result getHardwareAlertLog(Map<String, Object> reqBody){
        String selectFieldNames = "dev_check_info.*, device_cert.username, device_cert.dev_name,  device_cert.cert_time, sys_organization.name";
        //条件查询
        SqlUtil.changeOptionFieldName(reqBody,"device_ip","dev_check_info.device_ip");
        String[] optionNames ={"dev_name,dev_check_info.device_ip,type"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("dev_check_info,device_cert,sys_organization")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .setWhere("dev_check_info.device_ip = device_cert.device_ip and device_cert.org_id = sys_organization.id")
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

    //软件异常信息
    @RequestMapping("/softwareAlert")
    public Result getSoftwareAlertLog(Map<String, Object> reqBody){
        String selectFieldNames = "soft_check_info.*, device_cert.username, device_cert.dev_name, sys_organization.name";
        //条件查询
        //SqlUtil.changeOptionFieldName(reqBody,"device_ip","dev_check_info.device_ip");
        //String[] optionNames ={"dev_name,dev_check_info.device_ip,type"};
        String[] optionNames ={"dev_name,soft_check_info.device_ip,type"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("soft_check_info left join device_cert on soft_check_info.device_ip = device_cert.device_ip left join sys_organization on device_cert.org_id = sys_organization.id")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

    //流量异常信息
    @RequestMapping("/flowAlert")
    public Result getFlowAlertLog(Map<String, Object> reqBody){
        String selectFieldNames = "dev_check_info.*, device_cert.username, device_cert.dev_name, sys_organization.name";
        //条件查询
        SqlUtil.changeOptionFieldName(reqBody,"device_ip","dev_check_info.device_ip");
        String[] optionNames ={"dev_name,dev_check_info.device_ip,type"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("dev_check_info,device_cert,sys_organization")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .setWhere("dev_check_info.device_ip = device_cert.device_ip and device_cert.org_id = sys_organization.id")
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }
}

package com.ceit.desktop.service;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Component;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.util.Map;

@Component
public class TerminalManegementService {

    @Autowired
    SimpleJDBC simpleJDBC;

    //获取全部终端列表
    public Result terminalList(Map<String, Object> reqBody){
        String device_name = null;
        String device_ip = null;
        String org_name = null;
        int pageSize = (int) reqBody.get("pageSize");
        int pageNow = (int) reqBody.get("pageNow");

        String sql = "(SELECT device_cert.dev_name,sys_organization.name as org_name,device_cert.device_ip,device_cert.dev_id,device_cert.dev_reg_status as status,device_cert.dev_reg_time as time from device_cert,sys_organization where device_cert.org_id = sys_organization.id) " +
                "union all " +
                "(SELECT '' as dev_name,'' as org_name,'' as device_ip,device_id as dev_id,status,time from unregister_device)" +
                "limit ?,?";
        String countSql = "SELECT count(*) FROM " +
                "((SELECT device_cert.dev_name,sys_organization.name as org_name,device_cert.device_ip,device_cert.dev_id,device_cert.dev_reg_status as status,device_cert.dev_reg_time as time from device_cert,sys_organization where device_cert.org_id = sys_organization.id) union all \n" +
                "(SELECT '' as dev_name,'' as org_name,'' as device_ip,device_id as dev_id,status,time from unregister_device)) as a";
        if (reqBody.containsKey("device_name")){

        }
        String jsonData = simpleJDBC.selectForJsonArray(sql,(pageNow-1)*pageSize,pageNow*pageSize);
        Number count = (Number) simpleJDBC.selectForOneNode(countSql);

        jsonData = "{\"totalCount\":" + count.intValue() + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

    //获取终端状态
    public Result terminalStatus(Map<String, Object> reqBody){
        String searchFiledName = "org_id";
        String selectFieldNames = "device_cert.*,sys_organization.name as dept_name";
        String[] optionNames ={"dev_name", "device_ip"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("device_cert left join sys_organization on device_cert.org_id = sys_organization.id")
                .setAcceptOptions(optionNames)
                .setSearchFields(searchFiledName)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

    //获取硬件认证信息
    public Result harawareCheckInfo(Map<String, Object> reqBody){
        String device_ip = reqBody.get("device_ip").toString();
        String sql = "select dev_check_info.* from dev_check_info where device_ip=?";
        String jsonData = simpleJDBC.selectForJsonArray(sql,device_ip);
        String countSql = "select count(*) from dev_check_info where device_ip=?";
        String count = simpleJDBC.selectForJsonArray(countSql,device_ip);
        String data = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,data);
    }

    //获取硬件认证信息
    public Result softwareCheckInfo(Map<String, Object> reqBody){
        String device_ip = reqBody.get("device_ip").toString();
        String sql = "select soft_check_info.* from soft_check_info where device_ip=?";
        String jsonData = simpleJDBC.selectForJsonArray(sql,device_ip);
        String countSql = "select count(*) from soft_check_info where device_ip=?";
        String count = simpleJDBC.selectForJsonArray(countSql,device_ip);
        String data = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,data);
    }
}

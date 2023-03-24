package com.ceit.desktop.controller;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

//入网管理
@Controller("/networkAccess")
public class NetworkAccessController {

    @Autowired
    private SimpleJDBC simpleJDBC;

    //获取终端状态
    @RequestMapping("/getDeviceBlackList")
    public Result getDeviceBlackList(Map<String, Object> reqBody){
        String searchFiledName = "dev_name,device_ip,name";
        String selectFieldNames = "radblacklist.date as time,radblacklist.reason,device_cert.*,sys_organization.name as org_name";
        String[] optionNames ={"dev_name","device_ip","name"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("radblacklist left join device_cert on radblacklist.username = device_cert.username left join sys_organization on device_cert.org_id = sys_organization.id")
                .setAcceptOptions(optionNames)
                .setSearchFields(searchFiledName)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

    @RequestMapping("/deviceRevoke")
    public Result deviceRevoke(Map<String,Object> reqBody){
        System.out.println(reqBody.entrySet());
        String str = reqBody.get("usernames").toString();
        String[] usernames = str.split(",");
        String sql = "delete from radblacklist where username = ?";
        int count = 0;
        for (int i = 0;i < usernames.length;i++ ){
            String username = usernames[i];
            int res = simpleJDBC.update(sql,username);
            if (res == 1){
                count++;
            }
        }

        if (count > 0){
            return new Result("成功撤销"+count+"条记录",200,"撤销成功");
        }
        return new Result("error",200,"撤销失败");
    }

    //查找所有未添加到黑名单中的设备
    @RequestMapping("/getDevice")
    public Result getDevice(Map<String,Object> reqBody){
        System.out.println("1111111111111111111");
        String sql = "select * from device_cert where username not in (select username from radblacklist)";
        List res = simpleJDBC.selectForList(sql);
        if (res.size()==0){
            return new Result("error",200,null);
        }
        return new Result("success",200,res);
    }

    //批量添加终端至入网黑名单
    @RequestMapping("/insertDevice")
    public Result insertDevice(Map<String,Object> reqBody){
        List<String> deviceList = (List) reqBody.get("device_ips");
        String sql = "insert into radblacklist (username,date,reason) value (?,?,?)";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        int count = 0;
        for (String username:deviceList) {
            int res = simpleJDBC.update(sql,username,time,"管理员手动加入");
            if (res ==1 ){
                count++;
            }
        }
        return new Result("成功添加"+count+"条终端",200,"success");
    }

    //获取用户入网结果
    @RequestMapping("/getUserNetAccessList")
    public Result getUserNetAccessList(Map<String, Object> reqBody){
        String searchFiledName = "username,device_ip";
        String selectFieldNames = "radpostauth.*";
        String[] optionNames ={"username","device_ip"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("radpostauth")
                .setAcceptOptions(optionNames)
                .setSearchFields(searchFiledName)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }
}

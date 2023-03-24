package com.ceit.desktop.controller;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.util.Map;

@Controller("/soft")
public class SoftController {
    @Autowired
    private SimpleJDBC simpleJDBC;

    // 获取软件列表
    @RequestMapping("/getSoftList")
    public Result getSoftList(Map<String, Object> reqBody) {
        String selectFieldNames = "dev_sw_info.*";
        String[] optionNames ={"filename", "hash"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("dev_sw_info")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }


    // 获取终端已安装的软件列表
    @RequestMapping("/getDeviceSoftList")
    public Result getDeviceSoftList(Map<String, Object> reqBody) {
        System.out.println("page" + reqBody.get("pageNow"));
        System.out.println("page" + reqBody.get("pageSize"));
        String searchFiledName = "dev_ip";
        String selectFieldNames = "dev_sw_info.*,device_cert.dev_name,device_cert.username as dev_hash";
        String[] optionNames ={"filename", "dev_ip","dev_name"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        SqlUtil.changeSearchFieldName(reqBody,"device_ip","dev_ip");
        String jsonData = sqlUtil.setTable("dev_sw_info,device_cert")
                .setAcceptOptions(optionNames)
                .setSearchFields(searchFiledName)
                .setWhere("dev_sw_info.dev_ip = device_cert.device_ip")
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

//    @RequestMapping("/insert")
//    public Result insert(Map<String, Object> reqBody) {
//        Object o = reqBody.get("is_default");
//        if (o == null){
//            reqBody.put("is_default",0);
//        } else {
//            //当o不是空时，说明is_default的值为1
//            String sql = "update sys_auth_policy set is_default = 0 where is_default = 1";
//            simpleJDBC.update(sql);
//        }
//        SqlUtil sqlUtil = new SqlUtil(reqBody);
//        int ret = sqlUtil.setTable("sys_auth_policy")
//                .setFields("name","methods","description","is_default")
//                .setSearchFields("id")
//                .insert();
//        if (ret != 0){
//            return new Result("添加成功", 200, "success");
//        }
//        return new Result("添加失败", 200, "error");
//    }
//
//    @RequestMapping("/update")
//    public Result update(Map<String, Object> reqBody) {
//        Object o = reqBody.get("is_default");
//        if (o == null){
//            reqBody.put("is_default",0);
//        } else {
//            //当o不是空时，说明is_default的值为1
//            String sql = "update sys_auth_policy set is_default = 0 where is_default = 1";
//            simpleJDBC.update(sql);
//        }
//        SqlUtil sqlUtil = new SqlUtil(reqBody);
//        int ret = sqlUtil.setTable("sys_auth_policy")
//                .setFields("name","methods","description","is_default")
//                .setWhere("id=?")
//                .update();
//
//        if (ret != 0){
//            return new Result("修改成功", 200, "success");
//        }
//        return new Result("修改失败", 200, "error");
//    }
//
//    @RequestMapping("/delete")
//    public Result delete(Map<String, Object> reqBody) {
//        String str = reqBody.get("ids").toString();
//        String[] ids = str.split(",");
//        int rSet = 0;
//        for (int i = 0; i < ids.length; i++) {
//            Integer id = Integer.parseInt(ids[i]);
//            rSet = simpleJDBC.update("delete from sys_auth_policy where id=?", id);
//        }
//        if (rSet != 0){
//            return new Result("删除成功", 200, "success");
//        }
//        return new Result("删除失败", 200, "error");
//    }
}


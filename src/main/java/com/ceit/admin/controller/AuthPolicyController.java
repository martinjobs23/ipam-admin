package com.ceit.admin.controller;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.util.Map;

@Controller("/authPolicy")
public class AuthPolicyController {
    @Autowired
    private SimpleJDBC simpleJDBC;

    // 获取认证策略列表
    @RequestMapping("/getList")
    public String getList(Map<String, Object> reqBody) {
        String[] optionNames ={"name", "methods", "description", "is_default"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("sys_auth_policy")
                .setAcceptOptions(optionNames)
                .selectForJsonArray();

        return jsonData;
    }

    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {
        Object o = reqBody.get("is_default");
        if (o == null){
            reqBody.put("is_default",0);
        } else {
            //当o不是空时，说明is_default的值为1
            String sql = "update sys_auth_policy set is_default = 0 where is_default = 1";
            simpleJDBC.update(sql);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("sys_auth_policy")
                .setFields("name","methods","description","is_default")
                .setSearchFields("id")
                .insert();
        if (ret != 0){
            return new Result("添加成功", 200, "success");
        }
        return new Result("添加失败", 200, "error");
    }

    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {
        Object o = reqBody.get("is_default");
        if (o == null){
            reqBody.put("is_default",0);
        } else {
            //当o不是空时，说明is_default的值为1
            String sql = "update sys_auth_policy set is_default = 0 where is_default = 1";
            simpleJDBC.update(sql);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("sys_auth_policy")
                .setFields("name","methods","description","is_default")
                .setWhere("id=?")
                .update();

        if (ret != 0){
            return new Result("修改成功", 200, "success");
        }
        return new Result("修改失败", 200, "error");
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("ids").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        for (int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("delete from sys_auth_policy where id=?", id);
        }
        if (rSet != 0){
            return new Result("删除成功", 200, "success");
        }
        return new Result("删除失败", 200, "error");
    }
}

package com.ceit.admin.controller;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SqlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller("/userAuth")
public class UserAuthController {
    @Autowired
    private SimpleJDBC simpleJDBC;

    private final String tableName = "sys_role_user";
    private final String[] selectFieldNames = { "user_id" };
    private final String[] searchFiledNames = { "role_id" };


    @RequestMapping("/list")
    public Result list(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        SqlUtil.changeSearchFieldName(reqBody, "roleId", "role_id");
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledNames)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        return new Result("ok", 200, jsonData);
    }


    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {

        String roleId = reqBody.get("roleId").toString();
        String str = reqBody.get("userIds").toString();

        String getStr = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
        String[] userIds = getStr.split(",");

        String selectSql = "select * from sys_role_user where role_id = ? ";
        String tmp = simpleJDBC.selectForJsonObject(selectSql,roleId);
        if (tmp!=null) {
            simpleJDBC.update("delete from sys_role_user where role_id=?", roleId);
        }
        int rSet = 0;
        //new
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        reqBody.put("role_id",roleId);
        reqBody.put("user_id", Arrays.asList(userIds));
        rSet = sqlUtil.setTable("sys_role_user")
                .setFields("role_id","user_id")
                .insert();
        return new Result("ok", 200, rSet);
    }

}

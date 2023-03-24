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

@Controller("/apiAuth")
public class ApiAuthController {
    @Autowired
    private SimpleJDBC simpleJDBC;

    private final String tableName = "sys_role_api";
    private final String[] selectFieldNames = { "api_id" };
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

        String roleId = reqBody.get("role_id").toString();
        String str = reqBody.get("api_ids").toString();

        String getStr = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
        String[] apiIds = getStr.split(",");

        String selectSql = "select * from sys_role_api where role_id = ? ";
        String tmp = simpleJDBC.selectForJsonObject(selectSql,roleId);
        if (tmp!=null) {
            simpleJDBC.update("delete from sys_role_api where role_id=?", roleId);
        }
        int rSet = 0;
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        reqBody.put("role_id",roleId);
        reqBody.put("api_id", Arrays.asList(apiIds));
        rSet = sqlUtil.setTable("sys_role_api")
                .setFields("role_id","api_id")
                .insert();
        return new Result("ok", 200, rSet);
    }

}

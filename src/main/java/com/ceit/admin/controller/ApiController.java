package com.ceit.admin.controller;

import java.util.Map;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SqlUtil;

@Controller("/api")
public class ApiController {

    @Autowired
    private SimpleJDBC simpleJDBC;
    private final String[] optionNames ={"name", "path", "description"};
    private final String searchFiledName = "id";
    private final String tableName = "sys_api";
    @Autowired
    private JSON json;
 


    /**
     * 树形展示数组
     */
    @RequestMapping("/tree")
    public Result treeData(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonArray();

        return new Result(ResultCode.SUCCESS_TOTREE, jsonData);
    }
   
    @RequestMapping("/page")
    public Result page(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonArray();

        int count = sqlUtil.selectForTotalCount();

        // 把客户端需要分页的数据标准化
        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";

        return new Result(ResultCode.SUCCESS_TOTREE, jsonData);
    }

    // insert api
    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {
        if (reqBody.get("children") != null) {
            reqBody.remove("children");
        }
        // 一级菜单需将pid设为0
        if (reqBody.get("pid") == null) {
            reqBody.put("pid", 0);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        System.out.println("reqBody:"+reqBody);
        int ret = sqlUtil.setTable("sys_api")
                .setFields("path","name","description","pid")
                .setSearchFields("id")
                .insert();

        return new Result("ok", 200, ret);
    }

    // delete api
    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] apiIds = str.split(",");
        int rSet = 0;
        for (int i = 0; i < apiIds.length; i++) {
            Integer id = Integer.parseInt(apiIds[i]);
            rSet = simpleJDBC.update("delete from sys_api where id=?", id);

            //删除相关的表中数据
            simpleJDBC.update("delete from sys_role_api where api_id=?", id);
            simpleJDBC.update("delete from sys_menu_api where api_id=?", id);
        }
        return new Result("success", 200, rSet);
    }

    // update api
    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {
        if (reqBody.get("children") != null) {
            reqBody.remove("children");
        }
        // 一级菜单需将pid设为0
        if (reqBody.get("pid") == null) {
            reqBody.put("pid", 0);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
//        int ret = sqlUtil.setTable("sys_api")
//                .setFields("path","name","description","pid","sort","id")
//                .setSearchFields("id")
//                .update();
//
//        return new Result("ok", 200, ret);
        int rSet = new SqlUtil(reqBody)
                .setTable("sys_api")
                .setFields("path","name","description","pid","sort")
                .setWhere("id = ?")
                .update();
        return new Result(ResultCode.SUCCESS, rSet);
    }
}

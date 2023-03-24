package com.ceit.ipam;

import java.util.Map;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

@Controller("/ipam/alloc")
public class AllocController {

    @Autowired
    private SimpleJDBC simpleJDBC;

    @Autowired
    private JSON json;
 
    @RequestMapping("/page")
    public Result page(Map<String, Object> reqBody){

        String tableName = "ipam_alloc";
        String[] optionNames ={"ip", "org_name","user_name","description"}; //可以选择的查询条件
        String searchFiledName = "subnet_id";

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();

        // 把客户端需要分页的数据标准化
        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";

        return new Result("ok", 200, jsonData);

    }

    // 插入
    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {
        if (reqBody.get("children") != null) {
            reqBody.remove("children");
        }
        if (reqBody.get("pid") == null) {
            reqBody.put("pid", 0);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("ipam_alloc")
                .setFields("subnet_id", "ip", "mac", "org_id", "user_id", "org_name", "user_name", "description" )
                .insert();
        return new Result("success", 200, ret);
    }

    // 修改
    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {
        if (reqBody.get("children") != null) {
            reqBody.remove("children");
        }
        if (reqBody.get("pid") == null) {
            reqBody.put("pid", 0);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("ipam_alloc")
        .setFields("subnet_id", "ip", "mac", "org_id", "user_id", "org_name", "user_name", "description" )
                .setWhere("id = ?")
                .update();
        return new Result("success", 200, ret);
    }

    @RequestMapping("/delete")
    public Result deleteByIds(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;

        StringBuilder sql = new StringBuilder();
        sql.append("delete from ipam_alloc where id in (");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0)
                sql.append(",");
            sql.append("?");
        }
        sql.append(")");

        rSet = simpleJDBC.update(sql.toString(), ids);
        return new Result("success", 200, rSet);

    }

}

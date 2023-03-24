package com.ceit.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ceit.admin.common.ScopeTest;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.ioc.annotations.RequestParam;
import com.ceit.ioc.annotations.Scope;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SearchOptionPageUtil;
import com.ceit.utils.SqlUtil;

@Controller("/summer")
@Scope("Prototype")
public class TestController {

        @Autowired
        private ScopeTest scopeTest;

        @Autowired
        private SimpleJDBC simpleJDBC;

        @Autowired
        private JSON json;

        private int i = 0;

        @RequestMapping("/Hello")
        public Result name(Map<String, Object> reqBody, HttpServletRequest request, @RequestParam("name") String name,
                        @RequestParam("num") Integer num) {
                String sql = "select * from sys_user";
                List<Map<String, Object>> uMap = simpleJDBC.selectForList(sql);
                String hahah = json.list2Json((List) uMap);
                return new Result(ResultCode.SUCCESS, i++);

        }

        @RequestMapping("/roleList")
        public Result roleList(Map<String, Object> reqBody) {
                // old
                String sql;
                String[] selectFieldNames = { "count(*)" };
                String selectTableName = "sys_user";
                String[] searchFiledNames = { "org_id" };
                String[] acceptOptionNames = { "name", "id_number", "email", "mobile" };
                String groupBy = null;
                String orderBy = null;
                List<Object> sqlParamObjList = new ArrayList<Object>();
                SqlUtil.changeSearchFieldName(reqBody, "orgId", "org_id");
                reqBody.remove("pageNow");
                reqBody.remove("pageSize");
                sql = SearchOptionPageUtil.getSelectSql(selectTableName, selectFieldNames, null,
                                searchFiledNames,
                                acceptOptionNames,groupBy, orderBy, reqBody, sqlParamObjList);
                Object count = simpleJDBC.selectForOneNode(sql, sqlParamObjList.toArray());

                System.out.println("count : " + count);

                // new
                SqlUtil.changeSearchFieldName(reqBody, "orgId", "org_id");

                Object count1 = new SqlUtil(reqBody)
                                .setTable("sys_user")
                                .setFields("count(*)")
                                .setSearchFields("org_id")
                                .setAcceptOptions("name", "id_number", "email", "mobile")
                                .selectForOneNode();

                System.out.println("count1 : " + count1);

                // old
                String updateTableName = "sys_menu";
                String[] updateFieldNames = { "sort", "name" };
                String[] whereConditions  = { "id=?", "pid=?" };
 
                List<Object> sqlParamObjList2 = new ArrayList<>();
                sql = SearchOptionPageUtil.getUpdateSql(updateTableName, updateFieldNames, whereConditions , searchFiledNames, reqBody,
                                sqlParamObjList2);
                // Integer result = SimpleJDBC.getInstance().update(sql,
                // sqlParamObjList2.toArray());
                System.out.println("result : " + sql);

                // new
                String result1 = new SqlUtil(reqBody)
                                .setTable("sys_menu")
                                .setFields("sort", "name")
                                .setWhere("id=? and pid=?")
                                .getUpdate();
                System.out.println("result1 : " + result1);

                return new Result("success", 200, simpleJDBC.selectForJsonArray("select * from login.sys_role"));
        }
}

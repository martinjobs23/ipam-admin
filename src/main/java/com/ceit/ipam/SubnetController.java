package com.ceit.ipam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ceit.admin.model.UserInfo;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SearchOptionPageUtil;
import com.ceit.utils.SqlUtil;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/ipam/subnet")
public class SubnetController {

    private  final Logger logger = LoggerFactory.getLogger(SubnetController.class);

    @Autowired
    private SimpleJDBC simpleJDBC;

    @Autowired
    private JSON json;

    
    //根据角色只处理部门数据
    private String filterOrgData(HttpServletRequest request) {

        String where = null;

        // 1.判断是否已成功登录
        Object object = request.getSession().getAttribute("userInfo");
        if (object == null) {
            // 未登录, 不应该发生
            logger.error("filterOrgData userInfo=null, NO login");
            return where;
        }

        UserInfo userInfo = (UserInfo) object;

        //获取部门和角色信息
        if(userInfo.getRoleIds() == null)
        {
            //角色ID
            List<Map<String, Object>> list = simpleJDBC.selectForList("SELECT role_id FROM sys_role_user WHERE user_id="+userInfo.id);
            String roleIds="";
            for (Map<String,Object> map : list) {
                roleIds += map.get("role_id")+",";
            }
            //去掉最后逗号
            if(roleIds.endsWith(",")) 
                roleIds = roleIds.substring(0, roleIds.length()-1);

            userInfo.setRoleIds(roleIds);

            //角色名称
            list= simpleJDBC.selectForList("SELECT name FROM sys_role WHERE id in ("+roleIds+")");
            String roleNames="";
            for (Map<String,Object> map : list) {
                roleNames += map.get("name")+",";
            }
            userInfo.setRoleNames(roleNames);
        }

        String roleNames = userInfo.getRoleNames();

        logger.debug("filterOrgData getRoleIds:" + userInfo.getRoleIds());
        logger.debug("filterOrgData roleNames:" + userInfo.getRoleNames());

        //FIXME: 目前硬编码了部门管理员；子组织的过滤会有问题
        if(roleNames.contains("部门"))
        {
            if(userInfo.getOrgId() == 0)
            {
                Object orgId = simpleJDBC.selectForOneNode("SELECT org_id FROM sys_user where account=?", userInfo.username);
                userInfo.setOrgId((Integer)orgId);

                //部门名称
                Object orgName = simpleJDBC.selectForOneNode("SELECT name FROM sys_organization where id="+orgId);
                userInfo.setOrgName((String)orgName);
            }

            logger.debug("filterOrgData userInfo.getOrgId:" + userInfo.getOrgId());
 
            //FIXME: 直接设置org_id和1级子部门，应该递归所有的子部门，或者使用路径匹配
            List<Map<String, Object>> list = simpleJDBC.selectForList("SELECT id from sys_organization where pid="+userInfo.getOrgId());
            String orgIds="" + userInfo.getOrgId();
            for (Map<String,Object> map : list) {
                orgIds += "," + map.get("id");
            }

            where = "org_id in ("+orgIds +")";
        }

        return where;
    }

    //检查数据部门权限, 成功返回null, 错误返回错误信息
    private String checkOrgData(HttpServletRequest request, Map<String, Object> reqBody) {
        // 1.判断是否已成功登录
        Object object = request.getSession().getAttribute("userInfo");
        if (object == null) {
            // 未登录, 不应该发生
            logger.error("filterOrgData userInfo=null, NO login");
            return "用户未登录或已过期,请重新登录";
        }

        UserInfo userInfo = (UserInfo) object;

        //获取部门和角色信息
        if(userInfo.getRoleIds() == null)
        {
            //角色ID
            List<Map<String, Object>> list = simpleJDBC.selectForList("SELECT role_id FROM sys_role_user WHERE user_id="+userInfo.id);
            String roleIds="";
            for (Map<String,Object> map : list) {
                roleIds += map.get("role_id")+",";
            }
            //去掉最后逗号
            if(roleIds.endsWith(",")) 
                roleIds = roleIds.substring(0, roleIds.length()-1);

            userInfo.setRoleIds(roleIds);

            //角色名称
            list= simpleJDBC.selectForList("SELECT name FROM sys_role WHERE id in ("+roleIds+")");
            String roleNames="";
            for (Map<String,Object> map : list) {
                roleNames += map.get("name")+",";
            }
            userInfo.setRoleNames(roleNames);
        }

        String roleNames = userInfo.getRoleNames();

        logger.debug("checkOrgData getRoleIds:" + userInfo.getRoleIds());
        logger.debug("checkOrgData roleNames:" + userInfo.getRoleNames());

        //FIXME: 目前硬编码了部门管理员；子组织的过滤会有问题
        if(roleNames.contains("部门"))
        {
            if(userInfo.getOrgId() == 0)
            {
                Object orgId = simpleJDBC.selectForOneNode("SELECT org_id FROM sys_user where account=?", userInfo.username);
                userInfo.setOrgId((Integer)orgId);

                //部门名称
                Object orgName = simpleJDBC.selectForOneNode("SELECT name FROM sys_organization where id="+orgId);
                userInfo.setOrgName((String)orgName);
            }

            //要修改成的部门id
            Object org_id = reqBody.get("org_id");

            logger.debug("checkOrgData userInfo.getOrgId:" + userInfo.getOrgId());
 
            //FIXME: 直接设置org_id和1级子部门，应该递归所有的子部门，或者使用路径匹配
            List<Map<String, Object>> list = simpleJDBC.selectForList("SELECT id from sys_organization where pid="+userInfo.getOrgId());

            if(org_id==null || org_id.toString()=="0")
                reqBody.put("org_id", userInfo.getOrgId());
            else
            {
                if(org_id.equals(userInfo.getOrgId()))
                    return null;

                for (Map<String,Object> map : list) {
                    Object id=map.get("id");
                    if(org_id.equals(id))
                        return null;
                }

                return "没有权限";
            }
        }

        return null;
    }

    @RequestMapping("/navigate")
    public Result navigate(Map<String, Object> reqBody, HttpServletRequest request) {

        String sql;
        String[] selectFieldNames = { "*" };
        String selectTableName = "ipam_subnet";
        String groupBy = null;
        String orderBy = "pid,sort";
        String[] searchFiledNames = { "id" };
        String[] acceptOptionNames = { "name", "subnet", "description" };

        List<Object> sqlParamObjList = new ArrayList<Object>();

        //过滤数据权限,查询条件中添加org_id
        String[] wheres =null;
        String where = filterOrgData(request);
        if(where !=null)
        {
            wheres = new String[1];
            wheres[0]=where;
        }

        sql = SearchOptionPageUtil.getSelectSql(selectTableName, selectFieldNames, wheres, searchFiledNames,
                acceptOptionNames, groupBy, orderBy, reqBody, sqlParamObjList);

        String jsonData = simpleJDBC.selectForJsonArray(sql, sqlParamObjList.toArray());

        return new Result(ResultCode.SUCCESS_TOTREE, jsonData);

    }

    // 插入
    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        if (reqBody.get("children") != null) {
            reqBody.remove("children");
        }
        if (reqBody.get("pid") == null) {
            reqBody.put("pid", 0);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("ipam_subnet")
                .setFields("pid", "name", "subnet", "mask", "is_reserved", "description", "sort", "noscan", "org_id")
                .insert();
        return new Result("success", 200, ret);
    }

    // 修改
    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        if (reqBody.get("children") != null) {
            reqBody.remove("children");
        }
        if (reqBody.get("pid") == null) {
            reqBody.put("pid", 0);
        }
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("ipam_subnet")
                .setFields("pid", "name", "subnet", "mask", "is_reserved", "description", "sort", "noscan", "org_id")
                .setWhere("id = ?")
                .update();
        return new Result("success", 200, ret);
    }

    @RequestMapping("/delete")
    public Result deleteByIds(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;

        StringBuilder sql = new StringBuilder();
        sql.append("delete from ipam_subnet where id in (");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0)
                sql.append(",");
            sql.append("?");
        }
        sql.append(")");

        rSet = simpleJDBC.update(sql.toString(), ids);
        return new Result("success", 200, rSet);

    }

    // update sort
    @RequestMapping("/sort")
    public Result sort(Map<String, Object> reqBody) {

        int rSet = simpleJDBC.update(
                "update ipam_subnet set sort=? where id=?",
                reqBody.get("sort"),
                reqBody.get("id"));
        return new Result(ResultCode.SUCCESS, rSet);
    }

}

package com.ceit.asset;

import com.ceit.admin.model.UserInfo;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/asset/info")
public class AssetInfoController {

    private  final Logger logger = LoggerFactory.getLogger(AssetInfoController.class);

    @Autowired
    private SimpleJDBC simpleJDBC;

    private final int ASSET_TYPE_COMPUTER =1;
    private final int ASSET_TYPE_PRINTER =2;
    private final int ASSET_TYPE_USBDISK =3;
    private final int ASSET_TYPE_SERVER =6;

    private final int ASSET_NO_VERIFY_IMPORT = 0;  //0-导入未校核 
    private final int ASSET_NO_VERIFY_ACQU = 1;  //1 自动采集未校核
    private final int ASSET_NEED_VERIFY_OK = 2;
    private final int ASSET_NEED_VERIFY_IMPORT = 3;
    private final int ASSET_NEED_VERIFY_ACQU = 4;

    private String tableName = "asset_info";
    private String[] optionNames = { "number", "type", "name", "model", "SN", "location", "org_name", "user_name", "manager_name", "ip" };
    private String[] setFileds = { "number", "type_id",  "type", "name", "model", "secret_level", "SN","purpose","use_state", "location", 
        "org_id", "org_name", "user_id", "user_name","add_time", "ip", "mac", "os", "disk_sn", "install_date", "bootup_time" , "size" 
        , "verify_state", "verify_time", "verify_user_id", "verify_user_name", "manager_id", "manager_name"};

    private String processType(Map<String, Object> reqBody) {
        Object type = reqBody.get("type");
        String typeStr = null;
        if ("p".equals(type)) {
            typeStr = "办公自动化";
        } else if ("c".equals(type)) {
            typeStr = "桌面终端";
        } else if ("s".equals(type)) {
            typeStr = "服务器";
        } else if ("n".equals(type)) {
            typeStr = "网络设备";
        } else if ("u".equals(type)) {
            typeStr = "移动存储";
        } else if ("v".equals(type)) {
            typeStr = "声像设备";
        } else if (type != null) {
            reqBody.remove("type");
        }

        if(typeStr!=null) {
            reqBody.put("type", typeStr);
        }

        return typeStr;
    }

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
            if(roleIds.endsWith(",")) {
                roleIds = roleIds.substring(0, roleIds.length()-1);
            }

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
            if(roleIds.endsWith(",")) {
                roleIds = roleIds.substring(0, roleIds.length()-1);
            }

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

            if(org_id==null || org_id.toString()=="0") {
                reqBody.put("org_id", userInfo.getOrgId());
            } else
            {
                if(org_id.equals(userInfo.getOrgId())) {
                    return null;
                }

                for (Map<String,Object> map : list) {
                    Object id=map.get("id");
                    if(org_id.equals(id)) {
                        return null;
                    }
                }

                return "没有权限";
            }
        }

        return null;
    }

    // 获取分页信息
    @RequestMapping("/page")
    public String page(Map<String, Object> reqBody, HttpServletRequest request) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);

        //类型处理
        Object typeId = reqBody.get("type_id");
        if(typeId!=null && !typeId.toString().trim().equals("")) {
            sqlUtil.setSearchFields( "type_id" );
        }

        //过滤数据权限,查询条件中添加org_id
        String where = filterOrgData(request);

        int count = sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setWhere(where)
                .setOrderBy("verify_state DESC,add_time DESC")
                .selectForTotalCount();

        String jsonData ="[]";
        if(count>0) {
            jsonData = sqlUtil.selectForJsonArray();
        }

        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";
        return jsonData;
    }

    //获取资产类型
    @RequestMapping("/typelist")
    public String typelist(Map<String, Object> reqBody) {

        String jsonData = simpleJDBC.selectForJsonArray("select * from asset_type");

        return jsonData;
    }

    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200,  -1);
        }

        //自动添加一些字段
        reqBody.put("add_time",new Date());

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .insert();
        if (ret >0) {
            return new Result("添加成功", 200, 1);
        }
        return new Result("添加失败", 200, 0);
    }

    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        //自动添加一些字段
        reqBody.put("add_time",new Date());

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .setSearchFields("id")
                .update();
        if (ret >= 0) {
            return new Result("更新成功", 200, ret);
        }
        return new Result("更新失败", 200, -1);
    }

    @RequestMapping("/verify")
    public Result verify(Map<String, Object> reqBody, HttpServletRequest request) {

        int id = (int)reqBody.get("id");

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");

        //自动添加一些字段
        reqBody.put("verify_time",new Date());
        reqBody.put("verify_state", ASSET_NEED_VERIFY_OK);
        reqBody.put("verify_user_id", userInfo.id);
        reqBody.put("verify_user_name", userInfo.username);

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .setSearchFields("id")
                .update();
        if (ret >= 0) {
 
            //删除原有的需要校核数据
            simpleJDBC.update("DELETE FROM asset_info_verify WHERE id="+ id );

            //返回验证状态
            return new Result("设置验证成功", 200, ASSET_NEED_VERIFY_OK);

        }
        return new Result("更新失败", 200, -1);
    }

    
    @RequestMapping("/verifyok")
    public Result verifyok(Map<String, Object> reqBody, HttpServletRequest request) {

        //TODO: 删除时判断数据权限
        
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        int count =0;
        for (int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("update asset_info set verify_state="+ASSET_NEED_VERIFY_OK +" where id=?", id);
           
            if (rSet >= 0)
            {
                count++;
            //删除原有的需要校核数据
            simpleJDBC.update("DELETE FROM asset_info_verify WHERE id="+id);

            }
        }
        if (count >0) {
            return new Result("设置成功", 200, count);
        }
        return new Result("设置失败", 200, "error");
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody, HttpServletRequest request) {

        //TODO: 删除时判断数据权限
        
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        for (int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("delete from " + tableName + " where id=?", id);
        }
        if (rSet != 0) {
            return new Result("删除成功", 200, "success");
        }
        return new Result("删除失败", 200, "error");
    }

    /*
    @RequestMapping("/upload")
    public Result uploadAsset(Map<String, Object> reqBody, HttpServletRequest request) {

        Map<String,String> fileMap = FileUtil.Upload(request, "asset");
        if(fileMap ==null)
        {
            return new Result("上传文件失败", 500, null);
        }

        for (Map.Entry<String,String> item: fileMap.entrySet()) {
            // 解析
            String filename = item.getKey();
            String savename = item.getValue();
            logger.info("Save " + filename + " to "+ savename);

        }

        //
        return new Result("导入成功", 200, null);
    }
    */

    //自动填充类型id和名称，返回id
    private int fillTypeStringId(Map<String, Object> reqBody)
    {
        int type_id=0;

        Object objTypeId = reqBody.get("type_id");
        Object objTypeString = reqBody.get("type");

        Object objTypeTrue =null;
        Object objTypeIdTrue =null;

        //两个都不为空，暂时不处理
        if(objTypeId !=null && objTypeId.toString().trim().length() > 0  
        && objTypeString !=null && objTypeString.toString().trim().length() > 0)
        {
            return (int)objTypeId;
        }

        if(objTypeId !=null && objTypeId.toString().trim().length() > 0 )
        {
            objTypeTrue = simpleJDBC.selectForOneNode("select name from asset_type where id=?",objTypeId);
            if(objTypeTrue ==null) 
            {
                //reqBody.put("type", "未知设备");
                reqBody.put("type_id", 0);
            }
            else {
                reqBody.put("type", objTypeTrue);
            }
        }
        else if(objTypeString !=null && objTypeString.toString().trim().length() > 0 )
        {
            //根据名称查询id
            objTypeIdTrue = simpleJDBC.selectForOneNode("select id from asset_type where name=?",objTypeString);
            if(objTypeIdTrue ==null) 
            {
                //reqBody.put("type", "未知设备");
                reqBody.put("type_id", 0);
            }
            else {
                reqBody.put("type_id", objTypeIdTrue);
            }
        }
        else
        {
            reqBody.put("type", "未知设备");
            reqBody.put("type_id", 0);
            type_id =0;
        }
        
        return type_id;
    }

    //自动填充类型id和名称，返回String
    private String fillTypeIdString(Map<String, Object> reqBody)
    {
        String type_name="未知设备";

        Object objTypeId = reqBody.get("type_id");
        Object objTypeString = reqBody.get("type");

        Object objTypeTrue =null;
        Object objTypeIdTrue =null;

        //两个都不为空，暂时不处理
        if(objTypeId !=null && objTypeId.toString().trim().length() > 0  
        && objTypeString !=null && objTypeString.toString().trim().length() > 0)
        {
            return objTypeString.toString().trim();
        }

        if(objTypeId !=null && objTypeId.toString().trim().length() > 0 )
        {
            objTypeTrue = simpleJDBC.selectForOneNode("select name from asset_type where id=?",objTypeId);
            if(objTypeTrue !=null) 
            {
                reqBody.put("type", objTypeTrue);
                type_name = objTypeTrue.toString();
            }
            else
            {
                //type_id 不存在
                type_name = "未知设备"; 
                reqBody.put("type", type_name);
                reqBody.put("type_id", 0);
            }
        }
        else if(objTypeString !=null && objTypeString.toString().trim().length() > 0 )
        {
            //根据名称查询id
            objTypeIdTrue = simpleJDBC.selectForOneNode("select id from asset_type where name=?",objTypeString);
            if(objTypeIdTrue !=null) 
            {
                reqBody.put("type_id", objTypeIdTrue);
                type_name = objTypeIdTrue.toString();
            }
            else
            {
                //名称不对暂时不改
                type_name = objTypeString.toString();
                //reqBody.put("type", "未知设备");
                reqBody.put("type_id", 0);
            }
                
            
        }
        else
        {
            reqBody.put("type", "未知设备");
            reqBody.put("type_id", 0);
        }
 
        return type_name;
    }
    
    //获取ID，>0正常 如果不存在返回0，错误返回-1
    private int getExistedId(Map<String, Object> reqBody)
    {
        Object objId = null;

        //靳浩翔提议，如果是桌面终端，根据MAC地址，其他类型根据序列号
        Object objMac = reqBody.get("mac");
        if(objMac !=null && objMac.toString().trim().length() > 0)
        {
            String mac = objMac.toString().trim().replace("-", ":");
            objId = simpleJDBC.selectForOneNode("select id from asset_info where mac=?",mac);
            if(objId!=null) {
                return (int)objId;
            }
        }

        //自动填充类型id和名称
        String type_name = fillTypeIdString(reqBody);

        //如果是打印机，根据IP地址判断;如果IP为空，根据name和terminal_id 
        if(type_name.contains("办公自动化"))
        {
            Object objIP = reqBody.get("ip");
            if(objIP !=null && objIP.toString().trim().length() > 0)
            {
                objId = simpleJDBC.selectForOneNode("select id from asset_info where ip=?",objIP);
            }
            else
            {
                objId = simpleJDBC.selectForOneNode("select id from asset_info where name=? and terminal_id=?",
                    reqBody.get("name"),reqBody.get("terminal_id"));
            }

            if(objId!=null) {
                return (int)objId;
            }
        }

        //序列号判断
        Object objSN = reqBody.get("sn");
        if(objSN !=null && objSN.toString().trim().length() > 0)
        {
            objId = simpleJDBC.selectForOneNode("select id from asset_info where SN=?",objSN);
            if(objId!=null) {
                return (int)objId;
            }
        }

                //编号存在
        Object objNumber = reqBody.get("number");
        if(objNumber !=null && objNumber.toString().trim().length() > 0)
        {
            objId = simpleJDBC.selectForOneNode("select id from asset_info where number=?",objNumber);
            if(objId!=null) {
                return (int)objId;
            }
        }

        return 0;
    }

    //获取需要验证
    @RequestMapping("/verifydata")
    public Result verifydata(Map<String, Object> reqBody, HttpServletRequest request) 
    {
        SqlUtil sqlUtil = new SqlUtil(reqBody);
 
        String jsonData = sqlUtil.setTable("asset_info_verify")
            .setWhere("id=" + reqBody.get("id"))
            .selectForJsonObject();

        if(jsonData!=null && jsonData.length() > 2) {
            return new Result("ok", 200, jsonData);
        } else {
            return new Result("获取待验证数据失败", 200, null);
        }
    }

    //
    private Result completeImportData(int id, Map<String, Object> reqBody, HttpServletRequest request) 
    {
        String sql;
        int ret;
        int verify_state =0;
        boolean need_update =false;
        Map<String, Object> infoMap;

        //原来的校验状态
        Object objVerifyState  = simpleJDBC.selectForOneNode("SELECT verify_state FROM asset_info where id="+id);
        if(objVerifyState ==null) {
            verify_state = ASSET_NO_VERIFY_IMPORT;
        } else {
            verify_state = (int)objVerifyState;
        }

        //先读出所有字段
        sql = "SELECT fields FROM asset_type where id=?";
        Object objType  = reqBody.get("type_id");
        Object objTypeName = reqBody.get("type_name");
        if(objType==null && objTypeName ==null) {
            objType = 0;
        } else if(objTypeName!=null)
        {
            sql = "SELECT fields FROM asset_type where name=?";
            objType = objTypeName;
        }
            
        Object objFieldNames = simpleJDBC.selectForOneNode(sql,objType);

        //自动计算出的的字段 自动补全，不算冲突
        String fieldNames = objFieldNames.toString().trim();
        if( !(fieldNames+",").contains("type_id"))
        {
            fieldNames += ",type_id";
        }

        if( !(fieldNames+",").contains("org_id"))
        {
            fieldNames += ",org_id";
        }

        if( !(fieldNames+",").contains("user_id"))
        {
            fieldNames += ",user_id";
        }

        if( !(fieldNames+",").contains("manager_id"))
        {
            fieldNames += ",manager_id";
        }

        //服务器端查找 用户相关信息
        String userName = (String)reqBody.get("user_name");
        if(userName !=null && userName.trim().length() >0 )
        {
            Object objUserId = simpleJDBC.selectForOneNode("SELECT id FROM sys_user WHERE name=?", userName);
            if(objUserId==null) {
                reqBody.put("user_id", 0);
            } else {
                reqBody.put("user_id", objUserId);
            }
        }

        String managerName = (String)reqBody.get("manager_name");
        if(managerName !=null && managerName.trim().length() >0 )
        {
            Object objUserId = simpleJDBC.selectForOneNode("SELECT id FROM sys_user WHERE name=?", managerName);
            if(objUserId==null) {
                reqBody.put("manager_id", 0);
            } else {
                reqBody.put("manager_id", objUserId);
            }
        }
        
        //对比数据库数据 导入数据
        sql = "SELECT " + fieldNames + " from asset_info where id=" +id;
        infoMap = simpleJDBC.selectForMap(sql);
        for (Map.Entry<String,Object> item: infoMap.entrySet()) {
            // 解析
            String fieldname = item.getKey();
            Object fieldvalue = item.getValue();
            
            Object importvalue = reqBody.get(fieldname);

            //数据库中数据为空
            if(fieldvalue==null || fieldvalue.toString().trim().length()==0)
            {
                if(importvalue==null || importvalue.toString().trim().length()==0)
                {
                    //导入数据也为空,不处理
                }
                else {
                    //导入数据不为空，导入数据补齐
                    infoMap.put(fieldname, importvalue);
                    need_update = true;
                    logger.info("AUTO_UPDATE Import Data id="+id+ "," +fieldname+" ImportValue=" +importvalue);
                }
            }
            else
            {
                //数据库中数据不为空
                if(importvalue==null || importvalue.toString().trim().length()==0)
                {
                    //导入数据为空,不处理，使用数据库中数据
                }
                else {
                    //导入数据不为空，判断数据是否相同，注意时间类型对比
                    String dbvalue;

                    if (fieldvalue instanceof Timestamp) {
                        // 日期转化为字符串，应该都转换成当前时区时间
                        dbvalue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp)fieldvalue);
                    } else if (fieldvalue instanceof Date) {
                        // 日期转化为字符串，应该都转换成当前时区时间
                        dbvalue = new SimpleDateFormat("yyyy-MM-dd").format((Date)fieldvalue);
                    } else if (fieldvalue instanceof LocalDateTime) {
                        // 日期转化为字符串，应该都转换成当前时区时间
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        dbvalue = df.format((LocalDateTime)fieldvalue) ;
                    } else if (fieldvalue instanceof OffsetDateTime) {
                        // 日期转化为字符串，应该都转换成当前时区时间
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        dbvalue = df.format((OffsetDateTime)fieldvalue) ;
                    } else if (fieldvalue instanceof ZonedDateTime) {
                        // 日期转化为字符串，应该都转换成当前时区时间
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        dbvalue = df.format((ZonedDateTime)fieldvalue) ;
                    }
                    else {
                        dbvalue = fieldvalue.toString().trim();
                    }

                    if(dbvalue.equals( importvalue.toString().trim()))
                    {
                        //数据相同,不处理
                    }
                    else
                    {
                        //自动计算出的的字段 自动补全，不算冲突
                        if( fieldname.equals("org_id") ||fieldname.equals("user_id") ||fieldname.equals("manager_id"))
                        {
                            need_update = true;
                            logger.info("AUTO_UPDATE Import Data id="+id+ "," +fieldname+" DB_Value=" +fieldvalue+",ImportValue="+importvalue);    
                        }
                        else {
                            //不相同，冲突，需要校核
                            verify_state = ASSET_NEED_VERIFY_IMPORT; //2 导入冲突需要校核
                            infoMap.put(fieldname, importvalue);
                            logger.info("NEED_VERIFY Import Data id="+id+ "," +fieldname+" DB_Value=" +fieldvalue+",ImportValue="+importvalue);    
                        }
                    }
                }
            }
        }

        
        //自动添加一些字段
        infoMap.put("id",id);
        infoMap.put("add_time",new Date());
        infoMap.put("verify_state",verify_state);

        if(verify_state == ASSET_NEED_VERIFY_IMPORT || verify_state == ASSET_NEED_VERIFY_ACQU)
        {
            //需要校核
    
            //删除原有的需要校核数据
            simpleJDBC.update("DELETE FROM asset_info_verify WHERE id="+id);

            String[] setVerifyFileds = {"id", "number", "type_id",  "type", "name", "model", "secret_level", "SN","purpose","use_state", "location", 
            "org_id", "org_name", "user_id", "user_name","add_time", "ip", "mac", "os", "disk_sn", "install_date", "bootup_time" , "size" 
            , "verify_state", "verify_time", "verify_user_id", "manager_id", "manager_name"};

            //添加新的校核数据
            SqlUtil sqlUtil = new SqlUtil(infoMap);
            ret = sqlUtil.setTable("asset_info_verify")
                    .setFields(setVerifyFileds)
                    .insert();
            if (ret >0) {
                simpleJDBC.update("UPDATE asset_info SET verify_state="+verify_state+" WHERE id="+id);
                return new Result("添加待校核资产"+id+"成功", 200, 1);
            }
            return new Result("添加待校核资产"+id+"失败", 200, -1);
        }
        else
        {
            //删除原有的需要校核数据
            simpleJDBC.update("DELETE FROM asset_info_verify WHERE id="+id);

            //更新就行
            if(need_update) {
                return update(infoMap, request);
            } else
            {
                //只更新时间
                simpleJDBC.update("UPDATE asset_info SET add_time=NOW() WHERE id="+id);
                return new Result("更新资产ID=" + id +"信息", 200, 0);
            }

        }

        //return new Result("资产ID=" + id +"补齐", 200, 1);
    }

    @RequestMapping("/import")
    public Result importAsset(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        //判断唯一标识是否已经存在
        int id= getExistedId(reqBody);
        
        if(id > 0 )
        {
            String dataPro = (String)reqBody.get("dataPro");
            if(dataPro == null) {
                dataPro ="cover";
            }

            //根据策略\
            if(dataPro.equals("ignore")) {
                return new Result("资产ID=" + id +"忽略", 200, 0);
            } else if(dataPro.equals("cover"))
            {
                reqBody.put("id",id);
                return update(reqBody,request);
            }
            else {
                //补齐，读出各个字段，只修改有变化的,如果有冲突，需要人工校验
                reqBody.put("id",id);
                return completeImportData(id, reqBody,request);
            }    
        }
        else if(id ==0 ) {
            return insert(reqBody,request);
        } else {
            return new Result("getExistedId错误", 200, -1);
        }
    }

    //自动设置编号
    @RequestMapping("/number")
    public Result setNumber(Map<String, Object> reqBody) {

        int ret =0;

        Map<Integer,String> orgCodeMap = new HashMap<Integer,String>();
        Map<Integer,String> orgNameMap = new HashMap<Integer,String>();

        //最大数值
        Map<String,Integer> maxIdMap = new HashMap<String,Integer>();

        //编号格式  部门代码-类型代码-5位数字
        String sql = "select id, code from asset_type where code is not null";
        Map<Object, Map<String, Object>> typeListMap = simpleJDBC.selectForListMap(sql, "id");
        if (typeListMap.size() == 0) {
            return new Result("获取类型代码失败", 500, -1);
        }

        for ( Entry<Object, Map<String, Object>> entry : typeListMap.entrySet()) {
            Integer type_id = (Integer)entry.getKey();
            String type_code = (String)entry.getValue().get("code");

            //查询部门信息，如果部门为空，无法分配
            sql = "SELECT id,org_id,org_name from asset_info where (`number` is null or number='') and (org_id is not null or org_name is not null) and type_id=" + type_id;
            Map<Object, Map<String, Object>> assetListMap = simpleJDBC.selectForListMap(sql, "id");
            for ( Entry<Object, Map<String, Object>> assetEntry : assetListMap.entrySet()) {
                Integer asset_id = (Integer)assetEntry.getKey();
                Integer org_id = (Integer)assetEntry.getValue().get("org_id");
                String org_name = (String)assetEntry.getValue().get("org_name");
                String org_code = null;
                if(org_id ==null || org_id==0)
                {
                    Map<String, Object> orgMap= simpleJDBC.selectForMap("select id,code,name from sys_organization where name like ?", "%"+org_name+"%"); 
                    org_id = (Integer)orgMap.get("id");
                    org_name = (String)orgMap.get("name");
                    org_code = (String)orgMap.get("code");
                }

                if(org_id ==null || org_id==0)
                {
                    logger.error( "资产 " + asset_id+ " 的组织机构Id不存在：" + org_id);
                    continue;
                }

                if(org_code==null) {
                    org_code = orgCodeMap.get(org_id);
                }
                if(org_name==null) {
                    org_name= orgNameMap.get(org_id);
                }
                                  
                if(org_code==null || org_code.length()==0)
                {
                    Map<String, Object> orgMap= simpleJDBC.selectForMap("select code,name from sys_organization where id="+org_id ); 
                    org_name = (String)orgMap.get("name");
                    org_code = (String)orgMap.get("code");

                    if(org_code==null || org_code.length()==0)
                    {
                        logger.error( "组织机构 " + org_id+ " 不存在或编码不存在");
                        continue;                    
                    }
                }

                orgCodeMap.put(org_id, org_code);
                orgNameMap.put(org_id, org_name);

                //查询本机构本类中最大数字
                String number = org_code +"-" + type_code+"-";
                Integer max_id = maxIdMap.get(number);
                if(max_id==null)
                {
                    String maxIdString =  (String)simpleJDBC.selectForOneNode("SELECT max(number) from asset_info where number like ?", number+"%"); 
                    if(maxIdString==null) {
                        max_id =0;
                    } else {
                        String[] strList = maxIdString.split("-");
                        if(strList.length<3) {
                            max_id =0;
                        } else {
                            max_id =Integer.parseInt(strList[2]);
                        }
                    }    
                }

                max_id ++;
                maxIdMap.put(number,max_id);

                number += String.format("%05d", max_id);
                
                logger.info( "资产 " + asset_id+ ", number: " + number+ ", org_id="+org_id + ", org_name="+org_name );

                //更新数据
                sql="update asset_info set number=?,org_id=?,org_name=? where id="+asset_id;
                if(simpleJDBC.update(sql, number, org_id, org_name)>0) {
                    ret++;
                }
            }
        }
  
        return new Result("自动设置资产编号完成", 200, ret);
    }

    //处理自动采集报告的数据
    private Result completeReportData(int id, String[] updateFileds,Map<String, Object> reqBody, HttpServletRequest request) 
    {

        String sql;
        int ret;
        String verifyFields;
        int verify_state =0;
        boolean need_update =false;
        Map<String, Object> infoMap;

        //原来的校验状态
        Object objVerifyState  = simpleJDBC.selectForOneNode("SELECT verify_state FROM asset_info where id="+id);
        if(objVerifyState ==null) {
            verify_state = ASSET_NO_VERIFY_ACQU;
        } else {
            verify_state = (int)objVerifyState;
        }

        if( verify_state == ASSET_NO_VERIFY_ACQU )
        {
            //不需要对比，直接更新
            logger.info("AUTO_UPDATE id="+id);  
            
            SqlUtil sqlUtil = new SqlUtil(reqBody);
            ret = sqlUtil.setTable(tableName)
                    .setFields(setFileds)
                    .setWhere("id="+id)
                    .update();

            if (ret >0) {
                return new Result("更新资产ID=" + id +"信息", 200, 0);
            }
            return new Result("更新资产ID"+id+"失败", 200, -1);
        }

        int type_id  = (int)reqBody.get("type_id");

        // FIXME: 资产类型硬编码
        if (type_id == ASSET_TYPE_PRINTER) {
            // type,name,model,SN,use_state,ip,mac,os,disk_sn,install_date,bootup_time
            verifyFields = "type_id,type,name,model,use_state,ip";
        } else if (type_id == ASSET_TYPE_USBDISK) {
            // type,name,model,SN,use_state,ip,mac,os,disk_sn,install_date,bootup_time
            verifyFields = "type_id,type,name,model,SN,use_state";
        } else if (type_id == ASSET_TYPE_COMPUTER || type_id == ASSET_TYPE_SERVER) {
            // type,name,model,SN,use_state,ip,mac,os,disk_sn,install_date,bootup_time
            verifyFields = "type_id,type,name,model,SN,use_state,ip,mac,os,disk_sn,install_date,bootup_time";
        } else {
            verifyFields = "type_id,type,name,model,SN,use_state,ip,mac,os,disk_sn,install_date,bootup_time";
        }

        // 对比桌面终端 数据库数据
        sql = "SELECT " + verifyFields + " from asset_info where id=" + id;
        infoMap = simpleJDBC.selectForMap(sql);
        for (Map.Entry<String, Object> item : infoMap.entrySet()) {
            // 解析
            String fieldname = item.getKey();
            Object fieldvalue = item.getValue();

            Object reportvalue = reqBody.get(fieldname);

            // 数据库中数据为空
            if (fieldvalue == null || fieldvalue.toString().trim().length() == 0) {
                if (reportvalue == null || reportvalue.toString().trim().length() == 0) {
                    // 导入数据也为空,不处理
                } else {
                    // 导入数据不为空，导入数据补齐
                    infoMap.put(fieldname, reportvalue);
                    need_update = true;
                    logger.info("AUTO_UPDATE id=" + id + ", Field=" + fieldname + "  Report Value=" + reportvalue);
                }
            } else {
                // 数据库中数据不为空
                if (reportvalue == null || reportvalue.toString().trim().length() == 0) {
                    // 导入数据为空,不处理，使用数据库中数据
                } else {
                    // 导入数据不为空，判断数据是否相同，注意时间类型对比
                    String dbvalue = fieldvalue.toString().trim();

                    if (dbvalue.equals(reportvalue.toString().trim())) {
                        // 数据相同,不处理
                    } else {

                        // 不相同，冲突，需要校核
                        verify_state = ASSET_NEED_VERIFY_ACQU; // 4 自动采集的数据冲突需要校核
                        infoMap.put(fieldname, reportvalue);
                        logger.info("NEED_VERIFY id=" + id + ", Field=" + fieldname + ", Db Data=" + fieldvalue
                                + "  Report Value=" + reportvalue);

                    }
                }
            }
        }

        //自动添加一些字段
        infoMap.put("id",id);
        infoMap.put("add_time",new Date());
        infoMap.put("verify_state",verify_state);

        if(verify_state == ASSET_NEED_VERIFY_IMPORT || verify_state == ASSET_NEED_VERIFY_ACQU)
        {
            //是否已经存在需要校核数据
            sql = "SELECT count(*) from asset_info_verify where id=" +id;
            Object existedVerify = simpleJDBC.selectForOneNode(sql);
            if(existedVerify!=null && existedVerify.toString().equals("1")) 
            {
                //更新校验数据
                SqlUtil sqlUtil = new SqlUtil(infoMap);
                ret = sqlUtil.setTable("asset_info_verify")
                        .setFields(updateFileds)
                        .setWhere("id="+id)
                        .update();
            }
            else
            {
                logger.info("existedVerify="+existedVerify);
                //插入新的校核数据
                SqlUtil sqlUtil = new SqlUtil(infoMap);
                ret = sqlUtil.setTable("asset_info_verify")
                        .setFields(updateFileds)
                        .insert();
            }

            if (ret >0) {
                simpleJDBC.update("UPDATE asset_info SET verify_state="+verify_state+" WHERE id="+id);
                return new Result("添加待校核资产"+id+"成功", 200, 1);
            }
            return new Result("添加待校核资产"+id+"失败", 200, -1);
        }
        else
        {
            //删除原有的需要校核数据
            simpleJDBC.update("DELETE FROM asset_info_verify WHERE id="+id);

            //更新就行
            if(need_update)
            {
                SqlUtil sqlUtil = new SqlUtil(infoMap);
                ret = sqlUtil.setTable(tableName)
                        .setFields(updateFileds)
                        .setSearchFields("id")
                        .update();
            }
            else
            {
                //只更新时间
                ret = simpleJDBC.update("UPDATE asset_info SET add_time=NOW() WHERE id="+id);
            }

            if (ret >0) {
                return new Result("更新资产ID=" + id +"信息", 200, 0);
            }
            return new Result("更新资产ID"+id+"失败", 200, -1);
        }

        //return new Result("资产ID=" + id +"补齐", 200, 1);
    }

    @RequestMapping("/report")
    public Result reportAcquAsset(Map<String, Object> reqBody, HttpServletRequest request) {
        Object objTerminalId= reqBody.get("terminal_id"); 
        Object objTypeId= reqBody.get("type_id");
        if(objTypeId==null|| objTerminalId==null) {
            return new Result("缺少类型ID或终端ID", 500, -1);
        }

        String[] updateFileds ; 
        if((int)objTypeId == ASSET_TYPE_PRINTER)
        {
            reqBody.put("purpose","打印");
            updateFileds =new String[] { "id","verify_state","terminal_id", "type_id", "type", "secret_level", "use_state", "purpose", "name", "model", "ip"}; 
        }         
        else if((int)objTypeId == ASSET_TYPE_USBDISK){
            reqBody.put("purpose","拷贝文件");
            updateFileds =new String[] { "id","verify_state","terminal_id", "type_id", "type", "secret_level", "use_state", "purpose", "name", "model", "SN", "size"}; 
        }           
        else if((int)objTypeId == ASSET_TYPE_COMPUTER){
            reqBody.put("purpose","办公");
            updateFileds =new String[] { "id","verify_state","terminal_id", "type_id", "type", "secret_level", "use_state", "purpose", "name", "model", "SN", "ip", "mac", "os", "disk_sn", "install_date", "bootup_time"}; 
        }
        else if((int)objTypeId == ASSET_TYPE_SERVER){
            reqBody.put("purpose","服务器");
            updateFileds =new String[] { "id","verify_state","terminal_id", "type_id", "type", "secret_level", "use_state", "purpose", "name", "model", "SN", "ip", "mac", "os", "disk_sn", "install_date", "bootup_time"}; 
        }
        else
        {
            return new Result("不支持的资产类型", 500, -1);
        }
                    
        Object objTypeName= simpleJDBC.selectForOneNode("select name from asset_type where id=?",objTypeId);

        reqBody.put("type",objTypeName);
        reqBody.put("secret_level","非密");
        reqBody.put("use_state","在用");

        //判断唯一标识是否已经存在
        int id= getExistedId(reqBody);

        if( id<=0  ){

            reqBody.put("add_time",new Date());
            reqBody.put("verify_state",ASSET_NO_VERIFY_ACQU);
 
            //不存在，自动添加      
            SqlUtil sqlUtil = new SqlUtil(reqBody);
            id = sqlUtil.setTable(tableName)
                    .setFields(updateFileds)
                    .insertAutoIncKey();
        }
        else
        {
            //自动添加一些字段       
            reqBody.put("id",id);

            //更新,判断是否需要校核
            return completeReportData(id, updateFileds, reqBody, request);
        }

        //
        if ( id > 0) {
            return new Result("资产上报成功", 200, id);
        }
        return new Result("资产上报失败", 500, id);
    }
}

package com.ceit.meeting;

import com.ceit.admin.model.UserInfo;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SqlUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/meeting/info")
public class InfoController {
    
    private  final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    private SimpleJDBC simpleJDBC;
    
    private String tableName = "meeting_info";
    private String[] setFileds = {"name","place","room_id","sponsor","org_id","org_name","start_time","end_time","number","status"};

    //状态按照时间 0  未开始 1正开会 2 已经结束
    private final int STATUS_WAIITING = 0;
    private final int STATUS_RUNNING =1;
    private final int STATUS_FINISHED =2;
    private static LocalDateTime lastCheckTime = LocalDateTime.now().minus(Duration.ofDays(30));

    public static int lastMaxCount =0; 
    //记录每个会议终端上次查询的count
    //FIXME: 暂时只记录一个
    public static int last_send_count =0;
    public static int last_clean_count =0;
    
    //自动设置超时的会议状态，每隔1分钟
    private void autoSetMeetingStatus(boolean doItNow)
    {
        LocalDateTime now = LocalDateTime.now();
        long minutes = Duration.between(lastCheckTime, now).toMinutes();
        //测试：每次都检查
        if(doItNow || minutes > 1)
        {
            lastCheckTime =now;
            //把超时时间大于10小时  10*60 的会议自动设置为结束
            simpleJDBC.update("update "+tableName+" set status ="+STATUS_FINISHED+" WHERE status<2 and timestampdiff(MINUTE,end_time ,NOW())>600");

            //如果开始时间大于当前时间，且状态为未开始的，自动设置为 正在进行
            simpleJDBC.update("update "+tableName+" set status ="+STATUS_RUNNING+" WHERE status=0 and start_time<NOW()");
        }
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

    //获取会议信息
    @RequestMapping("/list")
    public String list(Map<String, Object> reqBody, HttpServletRequest request) {
        //自动设置超时的会议状态
        autoSetMeetingStatus(false);

        String[] optionNames = {"name","place","sponsor","org_name","start_time","end_time","number","status"};

        //过滤数据权限,查询条件中添加org_id
        String where = filterOrgData(request);

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setOrderBy("status,start_time desc")
                .setSearchFields("status")
                .setWhere(where)
                .selectForJsonArray();

        return jsonData;
    }

    //模拟获取第三方会议预约信息
    @RequestMapping("/page")
    public String page(Map<String, Object> reqBody, HttpServletRequest request) {

        //自动设置超时的会议状态
        autoSetMeetingStatus(false);

        String[] optionNames = {"name","place","sponsor","org_name","start_time","end_time","number","status"};

        //过滤数据权限,查询条件中添加org_id
        String where = filterOrgData(request);

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setOrderBy("status,start_time desc")
                .setSearchFields("status")
                .setWhere(where)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";
        return jsonData;
    }

    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        reqBody.put("status",0);
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .insert();

        autoSetMeetingStatus(true);

        //会议信息有变化，更新缓存时间
        FileController.recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (ret != 0) {
            return  new Result("添加成功",200, "success");
        }
        return new Result("添加失败",200,"error");
    }

    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .setWhere("id=?")
                .update();

        autoSetMeetingStatus(true);

        //会议信息有变化，更新缓存时间
        FileController.recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (ret != 0) {
            return new Result("更新成功",200,"success");
        }
        return new Result("更新失败",200,"error");
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody, HttpServletRequest request) {

        String checkErrorMsg=checkOrgData(request,reqBody);
        if(checkErrorMsg != null)
        {
            return new Result("检查部门数据权限失败,"+checkErrorMsg, 200, -1);
        }

        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        for ( int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("delete from "+tableName+" where id=?",id);
        }

        autoSetMeetingStatus(true);
        
        //会议文件有删除，更新缓存时间
        FileController.recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (rSet != 0) {
            return new Result("删除成功",200,"success");
        }
        return new Result("删除失败",200,"error");
    }

    // update sort
    @RequestMapping("/sort")
    public Result sort(Map<String, Object> reqBody) {

        int rSet = simpleJDBC.update(
                "update "+tableName+" set sort=? where id=?",
                reqBody.get("sort"),
                reqBody.get("id"));
        return new Result(ResultCode.SUCCESS, rSet);
    }

     //获取有变化的会议信息
    @RequestMapping("/changelist")
    public Result changelist(Map<String, Object> reqBody) {

        //会议服务器状态更新
        if(reqBody.get("server_ip")!=null)
            simpleJDBC.update("update meeting_room set server_online=1,server_online_time=NOW() WHERE server_ip=?",reqBody.get("server_ip"));

        Object sendtimeObj = reqBody.get("send_count");
        Object cleantimeObj = reqBody.get("clean_count");

        //如果没有新的变化，查询条件也没有变化，直接返回
        int max= FileController.GetMaxCount();
        if(lastMaxCount >=max)
        {
            //logger.debug("lastMaxCount="+ lastMaxCount+", max=" +max);

            if(last_send_count == (int) sendtimeObj && last_clean_count ==(int) cleantimeObj)
                return new Result(FileController.recountTime,200,"[]");
        }
        else
        {
            lastMaxCount =max;   
            //logger.debug("lastMaxCount=max " +max); 
        }
   
        last_send_count = (int) sendtimeObj;
        last_clean_count =(int) cleantimeObj;

        //自动设置超时的会议状态
        autoSetMeetingStatus(false);

        //logger.debug("last_send_count="+ last_send_count+", last_clean_count=" +last_clean_count);

        String sql="select * from meeting_info where send_count>? or clean_count>?";     
        String jsonData = simpleJDBC.selectForJsonArray(sql,  sendtimeObj,  cleantimeObj);

        return new Result(FileController.recountTime,200,jsonData);
    }

    //文件数量
    @RequestMapping("/totalfile")
    public Result onlinepad(Map<String, Object> reqBody) {

        Map<String, Object> map = simpleJDBC.selectForMap(
                "SELECT count(*) as totalfile,sum(size) as totalsize from meeting_file where meeting_id=?", reqBody.get("id"));

        if(map!=null)
        {
            Number totalfile = (Number)map.get("totalfile");
            if(totalfile!=null && totalfile.intValue() > 0)
            {
                Object totalsend=simpleJDBC.selectForOneNode("SELECT count(send_count) from meeting_pad_file where meeting_id=? group by file_id", reqBody.get("id"));
                if(totalsend!=null) map.put("totalsend",totalsend);

                Object totalaccepted=simpleJDBC.selectForOneNode("SELECT count(accepted_count) from meeting_pad_file where accepted_count>0 and meeting_id=? group by file_id", reqBody.get("id"));
                if(totalsend!=null) map.put("totalaccepted",totalaccepted);
        
                Object totalclean=simpleJDBC.selectForOneNode("SELECT count(clean_count) from meeting_pad_file where clean_count>0 and meeting_id=? group by file_id", reqBody.get("id"));
                if(totalsend!=null) map.put("totalclean",totalclean);
        
                Object totaldeleted=simpleJDBC.selectForOneNode("SELECT count(deleted_count) from meeting_pad_file where deleted_count>0 and meeting_id=? group by file_id", reqBody.get("id"));
                if(totalsend!=null) map.put("totaldeleted",totaldeleted);    
            }        
        }

        return new Result(ResultCode.SUCCESS, map);
    }
}

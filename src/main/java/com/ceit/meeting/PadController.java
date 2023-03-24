package com.ceit.meeting;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SqlUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller("/meeting/pad")
public class PadController {

    @Autowired
    private SimpleJDBC simpleJDBC;

    private String tableName = "meeting_pad";
    private String[] setFileds = { "name", "ip", "mac", "os", "model", "online", "agent_id" , "server_ip" , "online_ip" };
    private String[] optionNames = { "name", "ip",  "online" };

    private String getServerIp(Map<String, Object> reqBody) {
        int room_id = 0;

        try {
            // 根据meeting_id或者room_id查找server_ip
            if (reqBody.get("room_id") != null) {
                room_id = Integer.parseInt( reqBody.get("room_id").toString());
            }
            else
            {
                //System.out.println("PadController getServerIp room_id==null");
                return null;
            }

            // 没必须再查会议信息
            /*
             * else if(reqBody.get("meeting_id")!=null)
             * {
             * int meeting_id = Integer.parseInt((String) reqBody.get("meeting_id"));
             * String sql ="xxxx" ;
             * room_id = simpleJDBC.selectForOneNode(sql)
             * }
             */

            String sql = "select server_ip from meeting_room where id=" + room_id;
            Object obj = simpleJDBC.selectForOneNode(sql);
            if (obj != null && obj.toString().length() > 0)
                return obj.toString();
        } catch (Exception err) {
            System.out.println("PadController getServerIp Error: "+ err.getMessage());
        }

        return null;
    }

    // 模拟获取第三方会议预约信息
    @RequestMapping("/page")
    public String page(Map<String, Object> reqBody) {

        // 根据meeting_id或者room_id查找对应会议的server_ip
        String server_ip = getServerIp(reqBody);
        if (server_ip != null)
        {
            //不在线的，历史连接过的也显示
            //reqBody.put("online", 1);
            reqBody.put("server_ip", server_ip);
        }

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setSearchFields("server_ip","online")
                .setOrderBy("online desc,ip")
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();

        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";
        return jsonData;
    }

    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {
        reqBody.put("online", 0);
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .insert();
        if (ret != 0) {
            return new Result("添加成功", 200, "success");
        }
        return new Result("添加失败", 200, "error");
    }

    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .setWhere("id=?")
                .update();
        if (ret != 0) {
            return new Result("更新成功", 200, "success");
        }
        return new Result("更新失败", 200, "error");
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        for (int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("delete from " + tableName + " where id=?", id);
        }
        if (rSet != 0) {

            //会议文件有变化，更新缓存时间
            FileController.recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            return new Result("删除成功", 200, "success");
        }
        return new Result("删除失败", 200, "error");
    }

    // update sort
    @RequestMapping("/sort")
    public Result sort(Map<String, Object> reqBody) {

        int rSet = simpleJDBC.update(
                "update " + tableName + " set sort=? where id=?",
                reqBody.get("sort"),
                reqBody.get("id"));
        return new Result(ResultCode.SUCCESS, rSet);
    }

    //分发服务器 报告终端状态
    @RequestMapping("/report")
    public Result report(Map<String, Object> reqBody, HttpServletRequest request) {

        Object objId= reqBody.get("id");
        if( objId==null)
        {         
            objId= simpleJDBC.selectForOneNode("select id from meeting_pad where agent_id=?",  reqBody.get("agent_id"));
            if( objId!=null)
            {
                reqBody.put("id",objId);
            }
            else
            {
                //查询失败, 插入新纪录,使用terminal_id
                if(reqBody.get("terminal_id")!=null)
                    reqBody.put("id",reqBody.get("terminal_id"));
            }
        }
 
        //server_ip
        if(reqBody.get("server_ip")  == null)
        {
            reqBody.put("server_ip",request.getRemoteAddr());
        }

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        if(objId==null){
            //不存在，自动添加
            objId = sqlUtil.setTable(tableName)
                    .setFields(setFileds)
                    .insertAutoIncKey();
        }
        else
        {
            reqBody.put("online_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            //更新状态
            String[] setFileds = {  "online", "online_time", "online_ip", "server_ip" };
            int ret=sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .setWhere("id=?")
                .update();

            if(ret==0)
            {
                //不存在
                simpleJDBC.update("INSERT INTO meeting_pad (id,name,ip,mac,os,model,online,agent_id,server_ip,online_time,online_ip) "+
                    "select id,name,ip,mac,os,model,?,agent_id,?,Now(),online_ip from terminal_info where id=?", 
                    reqBody.get("online") ,reqBody.get("server_ip") ,objId);
            }
                
        }     

        if ( (int)objId > 0) {
            return new Result("report成功", 200, objId);
        }
        return new Result("report失败", 500, objId);
    }

}

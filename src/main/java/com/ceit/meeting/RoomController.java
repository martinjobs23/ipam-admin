package com.ceit.meeting;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SqlUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Controller("/meeting/room")
public class RoomController {
    
    @Autowired
    private SimpleJDBC simpleJDBC;

    private String tableName = "meeting_room";
    private String[] setFileds = {"name", "location", "capacity", "server_ip", "sort"};
    private String[] optionNames = {"name", "location", "capacity", "server_ip", "sort"};

    //自动设置serverIp在线状态
    private void autoSetMeetingStatus()
    {
        //把超时时间大于10小时  10*60 的会议自动设置为结束
        simpleJDBC.update("update meeting_room set server_online=0 WHERE server_online=1 and timestampdiff(SECOND,server_online_time ,NOW())>10");           
 
    }
    //模拟获取第三方会议预约信息
    @RequestMapping("/page")
    public String page(Map<String, Object> reqBody) {

        autoSetMeetingStatus();

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setOrderBy("sort")
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        
        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";
        return jsonData;
    }

    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {
        reqBody.put("status",0);
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .insert();
        if (ret != 0) {
            return  new Result("添加成功",200, "success");
        }
        return new Result("添加失败",200,"error");
    }

    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable(tableName)
                .setFields(setFileds)
                .setWhere("id=?")
                .update();
        if (ret != 0) {
            return new Result("更新成功",200,"success");
        }
        return new Result("更新失败",200,"error");
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        for ( int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("delete from "+tableName+" where id=?",id);
        }
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

     //在线终端数量
     @RequestMapping("/onlinepad")
     public Result onlinepad(Map<String, Object> reqBody) {
 
        Object ret = simpleJDBC.selectForOneNode(
                 "select count(*) from meeting_pad where online=1 and server_ip=?", reqBody.get("server_ip"));
         return new Result(ResultCode.SUCCESS, ret);
     }
}

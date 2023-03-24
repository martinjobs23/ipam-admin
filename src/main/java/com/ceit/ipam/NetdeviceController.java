package com.ceit.ipam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.PostConstruct;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/ipam/netdevice")
public class NetdeviceController {

    private static Logger logger = LoggerFactory.getLogger(NetdeviceController.class);

    @Autowired
    private SimpleJDBC simpleJDBC;

    @Autowired
    private JSON json;

    @RequestMapping("/page")
    public Result page(Map<String, Object> reqBody) {

        String tableName = "ipam_netdevice";
        String[] selectFieldNames = { "id", "name", "ip", "port", "protocol", "username", "model", "sn", "os", "type",
                "check_time", "check_cmd", "check_regexp", "check_result", "subnet_id" };
        String[] optionNames = { "ip", "name", "model", "os", "sn","type", "protocol", "username", "check_time" }; // 可以选择的查询条件
        String searchFiledName = "subnet_id";

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setFields(selectFieldNames)
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

        String[] setFieldNames = { "name", "ip", "port", "protocol", "username", "password", "model", "sn","os", "type",
                "check_state", "check_time", "check_cmd", "check_regexp", "check_result", "subnet_id" };

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("ipam_netdevice")
                .setFields(setFieldNames)
                .insert();
        return new Result("success", 200, ret);
    }

    // 修改
    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {

        Object password = reqBody.get("password");
        if (password != null && ((String) password).length() == 0) {
            reqBody.remove("password");
        }

        String[] setFieldNames = { "name", "ip", "port", "protocol", "username", "password", "model","sn", "os", "type",
                "check_cmd", "check_regexp", "subnet_id" };

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("ipam_netdevice")
                .setFields(setFieldNames)
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
        sql.append("delete from ipam_netdevice where id in (");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0)
                sql.append(",");
            sql.append("?");
        }
        sql.append(")");

        rSet = simpleJDBC.update(sql.toString(), ids);
        return new Result("success", 200, rSet);

    }

    @RequestMapping("/check")
    public Result checkByIds(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;

        StringBuilder sql = new StringBuilder();
        sql.append("update ipam_netdevice set check_state=1 where check_state<>2 and id in (");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0)
                sql.append(",");
            sql.append("?");
        }
        sql.append(")");

        rSet = simpleJDBC.update(sql.toString(), ids);

        taskChanged++;

        return new Result("success", 200, rSet);

    }

    public static int taskChanged = 0;

    public void checkTask() {

        //logger.info("checkTask Running ...");

        // 如果有修改，重新执行
        if (taskChanged != 0) {
            taskChanged = 0;

            logger.info("checkTask taskChanged ...");

            // 读取数据库
            List<Map<String, Object>> mapList = simpleJDBC.selectForList(
                    "select id,ip,port,protocol,username,password from ipam_netdevice where check_state=1");
            for (Map<String, Object> row : mapList) {
                String protocol = (String) row.get("protocol");
                if (protocol != null && protocol.equals("ssh")) {

                  NetDeviceInfo info =new NetDeviceInfo();
                  info.ip =(String) row.get("ip");
                  info.port = (Integer) row.get("port");
                  info.username = (String) row.get("username");
                  info.password = (String) row.get("password");  
          
                  int id =(Integer) row.get("id");
                  simpleJDBC.update( "update ipam_netdevice set check_state=2 where id=" + id);

                  if(info.CheckSsh())
                  {
                    simpleJDBC.update(
                      "update ipam_netdevice set model=?,os=?,sn=?,check_state=3,check_time=now(),check_result=? where id="+ id,
                          info.model, info.os,info.sn, info.result);
                  }
                  else
                  {
                    simpleJDBC.update(
                      "update ipam_netdevice set check_state=4,check_time=now(),check_result=? where id=" + id,
                         info.result);
                  }
                }
 
            }
        }

        //logger.info("checkTask Exit.");
    }

    @PostConstruct
    public void scheduleCheckTask() {
        // 创建任务队列
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3); // 10 为线程数量

        logger.info("Start scheduleCheckTask ...");

        // 执行任务-- 采集设备信息
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            checkTask();
        }, 0, 2, TimeUnit.SECONDS); // 0s 后开始执行，每 2s 执行一次

    }
}

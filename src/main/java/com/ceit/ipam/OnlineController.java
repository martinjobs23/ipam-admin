package com.ceit.ipam;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

@Controller("/ipam/online")
public class OnlineController {

    private  final Logger logger = LoggerFactory.getLogger(OnlineController.class);
    
    @Autowired
    private SimpleJDBC simpleJDBC;

    @Autowired
    private JSON json;

    @RequestMapping("/page")
    public Result page(Map<String, Object> reqBody) {

        String tableName = "view_ipam_online_alloc";
        String[] optionNames = { "ip", "mac",  "user_name", "org_name", "online", "os", "check_time", "checker" }; // 可以选择的查询条件
        String searchFiledName = "subnet_id";

        if ("online".equals(reqBody.get("option"))) {
            if ("是".equals(reqBody.get("condition")) || "在线".equals(reqBody.get("condition")))
                reqBody.put("condition", 1);
            else if ("否".equals(reqBody.get("condition")) || "不在线".equals(reqBody.get("condition")))
                reqBody.put("condition", 0);
        }

        SqlUtil sqlUtil = new SqlUtil(reqBody)
                    .setTable(tableName)
                    .setAcceptOptions(optionNames)
                    .setSearchFields(searchFiledName)
                    .setOrderBy("online desc,ip");
    
        int count = sqlUtil.selectForTotalCount();
        if(count <=0)
        {
            return new Result("ok", 200, "{\"totalCount\": 0 ,\"pageData\":[]}");
        }


        LocalDateTime startTime;
        Duration duration;

        /* 方式1 事先建立视图，直接查询视图 254条记录 40 ms 500条120ms左右
        CREATE OR REPLACE
            ALGORITHM = UNDEFINED VIEW `view_ipam_online_alloc` AS
            select
                `O`.`id` AS `id`,
                `O`.`ip` AS `ip`,
                `O`.`mac` AS `mac`,
                `O`.`check_time` AS `check_time`,
                `O`.`checker` AS `checker`,
                `O`.`os` AS `os`,
                `O`.`online` AS `online`,
                `O`.`subnet_id` AS `subnet_id`,
                `A`.`mac` AS `mac_alloc`,
                `A`.`org_id` AS `org_id`,
                `A`.`user_id` AS `user_id`,
                `A`.`org_name` AS `org_name`,
                `A`.`user_name` AS `user_name`
            from
                (`ipam_online` `O`
            left join `ipam_alloc` `A` on
                ((`O`.`ip` = `A`.`ip`)))
        */
        startTime = LocalDateTime.now();
        String jsonData = sqlUtil.selectForJsonArray();
 
        duration = Duration.between(startTime, LocalDateTime.now());
        System.out.println("OnlineController Page Query View Cost Time: " + duration.toMillis());
 
        /* 方式2 一次查询 左连接 left join
        select O.*,A.mac,A.org_id,A.user_id ,A.org_name ,A.user_name  from ipam_online O left join ipam_alloc A on O.ip=A.ip WHERE O.subnet_id in (1,2,3) order by online desc, ip
        */

        /* 方式3 二次性查询 254条记录 88 ms 500条 92
        Map<Object, Map<String, Object>> onlineListMap = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForListMap("ip");

        startTime = LocalDateTime.now();

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (onlineListMap != null && onlineListMap.size() > 0) {
            count = sqlUtil.selectForTotalCount();

            // 查询在线IP分配的部门和责任人情况
            onlineListMap.entrySet().stream().forEach((rowMap) -> {
                Object ip = rowMap.getKey();
                sb.append("'");
                sb.append(ip.toString());
                sb.append("',");
            });
            if (sb.length() > 1)
                sb.delete(sb.length() - 1, sb.length());

            sb.append(")");

            //查询所有的IP的分配信息
            List<Map<String, Object>> allocList = simpleJDBC.selectForList(
                    "select ip, mac as mac_alloc,org_id,org_name,user_id,user_name from ipam_alloc where ip in "
                            + sb.toString());
            for (Map<String, Object> allocMap : allocList) {
                Object allocIp = allocMap.get("ip");

                //添加IP分配信息
                Map<String, Object> rowValue = onlineListMap.get(allocIp);
                rowValue.putAll(allocMap); 
            }

            jsonData = new JSON().mapListMap2Json(onlineListMap);
        }

        duration = Duration.between(startTime, LocalDateTime.now());
        System.out.println("OnlineController Page All In One Cost Time: " + duration.toMillis());
        */

        // 方式4，单个查询, 254条记录 8077 ms, 500条记录 13497
        /*
         * startTime = LocalDateTime.now();
         * 
         * if(list!=null && list.size() >0)
         * {
         * count = sqlUtil.selectForTotalCount();
         * 
         * // 查询在线IP分配的部门和责任人情况
         * for (Map<String,Object> map : list) {
         * Object ip = map.get("ip");
         * Map<String,Object> item = simpleJDBC.
         * selectForMap("select mac as mac_alloc,org_id,org_name,user_id,user_name from ipam_alloc where ip=?"
         * , ip);
         * if(item!=null)
         * {
         * map.putAll(item);
         * }
         * }
         * 
         * jsonData = new JSON().mapList2Json(list);
         * }
         * 
         * duration = Duration.between(startTime, LocalDateTime.now());
         * System.out.println("OnlineController Page One By One  Cost Time: " +
         * duration.toMillis());
         */

        // 把客户端需要分页的数据标准化
        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";

        return new Result("ok", 200, jsonData);

    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;

        StringBuilder sql = new StringBuilder();
        sql.append("delete from ipam_online where id in (");
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

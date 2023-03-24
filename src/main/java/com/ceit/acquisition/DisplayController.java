package com.ceit.acquisition;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.JDBCTemplate;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;

@Controller("/display")
public class DisplayController {
    @Autowired
    private SimpleJDBC simpleJDBC;

    @Autowired
    private JSON json;

    @RequestMapping("/logList")
    public Result logList(Map<String, Object> reqBody) {
        String menusql = "select * from dataCollect.logTable";
        String tmp = simpleJDBC.selectForJsonArray(menusql);
        return new Result(ResultCode.SUCCESS,tmp);
    }

    @RequestMapping("/logQuery")
    public Result logQuery(Map<String, Object> reqBody) {
        String logSql = "select * from dataCollect.logTable s ";
        if (reqBody.isEmpty() || (reqBody.get("option").equals("*") && reqBody.get("condition")==null)) {
            String tmp = simpleJDBC.selectForJsonArray(logSql);
            return new Result(ResultCode.SUCCESS, tmp);
        } else { // 查询
            logSql += "where ";
            // 拼接查询条件
            if (reqBody.get("condition") != null) {
                if (reqBody.get("option").equals("*")) {
                    logSql += " ( s.policyName like ?"
                            + " or s.destIP like ?"
                            + " or s.srcIP like ?"
                            + " or s.destPort like ?"
                            + " or s.protocol like ?)";
                } else {
                    if (reqBody.get("option").equals("policyName")){
                        logSql += " s.policyName like ?";
                    }
                    if (reqBody.get("option").equals("destIP")){
                        logSql += " s.destIP like ?";
                    }
                    if (reqBody.get("option").equals("srcIP")){
                        logSql += " s.srcIP like ?";
                    }
                    if (reqBody.get("option").equals("destPort")){
                        logSql += " s.destPort like ?";
                    }
                    if (reqBody.get("option").equals("protocol")){
                        logSql += " s.protocol like ?";
                    }
                }
            }
            String tmp = null;
            if (reqBody.get("option").equals("*")){
                tmp = simpleJDBC.selectForJsonArray(logSql,"%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%");
            } else {
                tmp = simpleJDBC.selectForJsonArray(logSql,"%"+reqBody.get("condition")+"%");
            }
            return new Result(ResultCode.SUCCESS, tmp);
        }
    }

    @RequestMapping("/deviceList")
    public Result deviceList(Map<String, Object> reqBody) {
        String menusql = "select * from dataCollect.deviceTable";
        String tmp = simpleJDBC.selectForJsonArray(menusql);
        System.out.println(tmp);
        return new Result(ResultCode.SUCCESS,tmp);
    }

    @RequestMapping("/deviceQuery")
    public Result deviceQuery(Map<String, Object> reqBody) {
        String deviceSql = "select * from dataCollect.deviceTable s ";
        if (reqBody.isEmpty() || (reqBody.get("option").equals("*") && reqBody.get("condition")==null)) {
            String tmp = simpleJDBC.selectForJsonArray(deviceSql);
            return new Result(ResultCode.SUCCESS, tmp);
        } else { // 查询
            deviceSql += "where ";
            // 拼接查询条件
            if (reqBody.get("condition") != null) {
                if (reqBody.get("option").equals("*")) {
                    deviceSql += " ( s.policyName like ?"
                            + " or s.destIP like ?"
                            + " or s.srcIP like ?"
                            + " or s.destPort like ?"
                            + " or s.protocol like ?)";
                } else {
                    if (reqBody.get("option").equals("policyName")){
                        deviceSql += " s.policyName like ?";
                    }
                    if (reqBody.get("option").equals("destIP")){
                        deviceSql += " s.destIP like ?";
                    }
                    if (reqBody.get("option").equals("srcIP")){
                        deviceSql += " s.srcIP like ?";
                    }
                    if (reqBody.get("option").equals("destPort")){
                        deviceSql += " s.destPort like ?";
                    }
                    if (reqBody.get("option").equals("protocol")){
                        deviceSql += " s.protocol like ?";
                    }
                }
            }
            String tmp = null;
            if (reqBody.get("option").equals("*")){
                tmp = simpleJDBC.selectForJsonArray(deviceSql,"%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%");
            } else {
                tmp = simpleJDBC.selectForJsonArray(deviceSql,"%"+reqBody.get("condition")+"%");
            }
            return new Result(ResultCode.SUCCESS, tmp);
        }
    }

    @RequestMapping("/flowList")
    public Result flowList(Map<String, Object> reqBody) {
        String menusql = "select * from dataCollect.flowTable";
        String tmp = simpleJDBC.selectForJsonArray(menusql);
        System.out.println(tmp);
        return new Result(ResultCode.SUCCESS,tmp);
    }

    @RequestMapping("/flowQuery")
    public Result flowQuery(Map<String, Object> reqBody) {
        String flowSql = "select * from dataCollect.flowTable s ";
        if (reqBody.isEmpty() || (reqBody.get("option").equals("*") && reqBody.get("condition")==null)) {
            String tmp = simpleJDBC.selectForJsonArray(flowSql);
            return new Result(ResultCode.SUCCESS, tmp);
        } else { // 查询
            flowSql += "where ";
            // 拼接查询条件
            if (reqBody.get("condition") != null) {
                if (reqBody.get("option").equals("*")) {
                    flowSql += " ( s.policyName like ?"
                            + " or s.destIP like ?"
                            + " or s.srcIP like ?"
                            + " or s.destPort like ?"
                            + " or s.protocol like ?)";
                } else {
                    if (reqBody.get("option").equals("policyName")){
                        flowSql += " s.policyName like ?";
                    }
                    if (reqBody.get("option").equals("destIP")){
                        flowSql += " s.destIP like ?";
                    }
                    if (reqBody.get("option").equals("srcIP")){
                        flowSql += " s.srcIP like ?";
                    }
                    if (reqBody.get("option").equals("destPort")){
                        flowSql += " s.destPort like ?";
                    }
                    if (reqBody.get("option").equals("protocol")){
                        flowSql += " s.protocol like ?";
                    }
                }
            }
            String tmp = null;
            if (reqBody.get("option").equals("*")){
                tmp = simpleJDBC.selectForJsonArray(flowSql,"%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%","%"+reqBody.get("condition")+"%");
            } else {
                tmp = simpleJDBC.selectForJsonArray(flowSql,"%"+reqBody.get("condition")+"%");
            }
            return new Result(ResultCode.SUCCESS, tmp);
        }
    }

}

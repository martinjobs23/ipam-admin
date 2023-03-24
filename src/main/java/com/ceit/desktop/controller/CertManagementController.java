package com.ceit.desktop.controller;

import com.ceit.desktop.service.CertRequestService;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.util.Map;

/**
* 处理证书请求
* @author hello world
* @since 2021-05-17
* @version v1.0
*/

@Controller("/certRequest")
public class CertManagementController {

    @Autowired
    CertRequestService certRequestService;
    @Autowired
    private SimpleJDBC simpleJDBC;
    /**
    * 查询分页数据
    */
    @RequestMapping(value = "/getList")
    public String getList(Map<String, Object> reqBody){
        String selectFieldNames = "desktop_cert_request.*, sys_organization.name as dept_name";
        String[] optionNames ={"SN", "mac"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("desktop_cert_request left join sys_organization on desktop_cert_request.org_id = sys_organization.id")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return jsonData;
    }

    /**
     * 请求参数 ：
     * clientGroupName: "测试专用1"
     * createTime: "2021-07-26 15:25:07"
     * id: 44
     * isHandle: "已完成"
     * isOld: "旧"
     * optType: "证书申请"
     * tfNumber: "4b373935303100005661"
     * transId: "35dbd45b"
     * 新增
     * 1、涉及client表
     * 2、修改证书请求状态
     * 3、产生req 、 key
     * 4、修改cert表
     */
    /**
     * 管理员审核终端设备，为终端分配部门
     * @param reqBody
     * @return
     */
    @RequestMapping("/audit")
    public Result audit(Map<String, Object> reqBody){
        return certRequestService.audit(reqBody);
    }

    //未注册设备查询
    @RequestMapping(value = "/getUncheckList")
    public String getUncheckList(Map<String, Object> reqBody){
        String selectFieldNames = "unregister_device.*";
        //条件查询
        String[] optionNames ={"dev_name"};

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("unregister_device")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return jsonData;
    }

    //已注册设备查询
    @RequestMapping(value = "/getCheckList")
    public String getCheckList(Map<String, Object> reqBody){
        String selectFieldNames = "radcheck.*,device_cert.*,sys_organization.name";
        //条件查询
        String[] optionNames ={"device_cert.username"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("radcheck,device_cert,sys_organization")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .setWhere("radcheck.username = device_cert.username and device_cert.org_id = sys_organization.id")
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return jsonData;
    }

    //设备注册审核通过
    @RequestMapping(value = "/radCheck")
    public Result radCheck(Map<String, Object> reqBody){
        return certRequestService.radCheck(reqBody);
    }

    //设备注册撤销
    @RequestMapping(value = "/radRevoke")
    public Result radRevoke(Map<String, Object> reqBody){
        return certRequestService.radRevoke(reqBody);
    }

    //已上传软件查询
    @RequestMapping(value = "/getSoftList")
    public String getSoftList(Map<String, Object> reqBody){
        String selectFieldNames = "softcheck.*,sys_organization.name";
        //条件查询
        String[] optionNames ={"soft_name,soft_type"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("softcheck,sys_organization")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .setWhere("softcheck.soft_type = sys_organization.id")
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return jsonData;
    }

//    //软件上传注册
//    @RequestMapping(value = "/softUpload")
//    public Result softUpload(Map<String, Object> reqBody, HttpServletRequest request) {
//        return certRequestService.softUpload(reqBody,request);
//    }

//    //软件下载
//    @RequestMapping("/download")
//    public Result softDownload(Map<String, Object> reqBody,HttpServletRequest request, HttpServletResponse response) {
//        return certRequestService.softDownload(reqBody,request,response);
//    }

    //告警日志
    @RequestMapping("/alertLog")
    public Result getAlertLog(Map<String, Object> reqBody){
        String selectFieldNames = "dev_check_info.*";
        //条件查询
        String[] optionNames ={"device_name,device_ip,type"};
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("dev_check_info")
                .setAcceptOptions(optionNames)
                .setFields(selectFieldNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();
        jsonData = "{\"totalCount\":" + count + ",\"jsonData\":" + jsonData + "}";
        return new Result("success",200,jsonData);
    }

}

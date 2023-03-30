package com.ceit.desktop.controller;

//import com.ceit.desktop.grpc.ca.*;
import com.ceit.desktop.service.CertRequestService;
import com.ceit.desktop.util.Hash;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
* 处理证书请求
* @author hello world
* @since 2021-05-17
* @version v1.0
*/

@Controller("/certRequest")
public class CertRequestController {

    @Autowired
    CertRequestService certRequestService;
    @Autowired
    private SimpleJDBC simpleJDBC;
    @Autowired
    private Hash hash;

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

//    //终端申请设备证书
//    @RequestMapping("/certApply")
//    public void audit(HttpServletRequest request, HttpServletResponse response){
//        Map<String,Object> caApplyMap = new HashMap<>();
//        try{
//            //获取信令服务器发来的信息
//            ServletInputStream sis = request.getInputStream();
//            CAApplyRequest caApplyRequest= CAApplyRequest.parseFrom(sis);
//            caApplyMap.put("CN",caApplyRequest.getServiceCode());
//
//            Result result = certRequestService.equipAudit(caApplyMap);
//
//            CAApplyResponse caApplyResponse;
//            Map<String, Object> certMap = (Map<String, Object>) result.getData();
//            Map<String, Object> certData = (Map<String, Object>) certMap.get("data");
//            if (result.getCode()==200) {
//                CAApplyData caApplyData = CAApplyData.newBuilder()
//                        .setCert(certData.get("cert").toString())
//                        .setCertSn(certData.get("certSn").toString())
//                        .setBufP7(certData.get("bufP7").toString())
//                        .build();
//                caApplyResponse = CAApplyResponse.newBuilder()
//                        .setTransId(certMap.get("transId").toString())
//                        .setStatus(certMap.get("status").toString())
//                        .setMessage(certMap.get("message").toString())
//                        .setData(caApplyData.toByteString())
//                        .build();
//            } else {
//                caApplyResponse = CAApplyResponse.newBuilder()
//                        .setStatus(certMap.get("status").toString())
//                        .setMessage(certMap.get("message").toString())
//                        .build();
//            }
//            response.reset();
//            ServletOutputStream sos = response.getOutputStream();
//            sos.write(caApplyResponse.toByteArray());
//            sos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 新设备设备证书申请
     */
    /*@ApiOperation(value = "新设备设备证书申请")
    @RequestMapping(value = "/newCertReq", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String newCertReq(@RequestBody AqjrCertRequest request){
        boolean flag = false;
        String returnStr = "";
        try {
            //        System.out.println("certUpdate : " + request);
            String tranId = idFactory.createTransId();
            request.setTransId(tranId);
            logger.debug("request : " + request);
            flag = crtRequestService.save(request);
//        AqjrCertRequest certReq = JSONObject.parseObject(request, AqjrCertRequest.class);
            logger.debug(request.toString());


        } catch(Exception e) {
            logger.error(e.getMessage());
        }
        return (flag ? "网关已收到证书请求,等待管理员审核后颁发证书" : "网关保存信息失败");
    }*/

    /**
    * 检查证书处理状态
     * 按照正常来说 map:  key1  cert key2 priKey
     * 失败
    */
    /*@ApiOperation(value = "检查证书处理状态")
    @RequestMapping(value = "/checkState")
    public Map<String, String> checkState(@RequestParam("tfNumber") String tfNumber){
        boolean flag = false;
        String returnStr = "";
        ResultCode code = null;
        Map<String, String> map = new HashMap<>();
        if(tfNumber == null) {
            code = ResultCode.PARAM_NOT_COMPLETE;
            returnStr = "终端申请没有加密卡信息，请检查是否正常接入加密卡！";
        } else {
            try {
                logger.info("  tfNumber : " + tfNumber );
                AqjrCertRequest request = crtRequestService.getState(tfNumber);
                String handleResult = request.getIsHandle();
                if(handleResult.equals("待处理")) {
                    returnStr = "证书请求管理员正在审核中...";
                    code = ResultCode.CERT_NOT_AUDIT;
                } else if(handleResult.equals("已完成")) { //此时应该自动分配一个终端名称  证书生成完毕了
                    returnStr = "证书已完成签发";
                    String certUser = request.getClientName();

                    code = ResultCode.CERT_GEN_SUCCESS;
                    logger.info("使用者 ： {}", certUser);
                    //然后再查询证书信息返回
                    map = crtRequestService.getCertMsg(certUser, tfNumber);
                } else if(handleResult.equals("生成失败")) {
                    returnStr = "证书签发失败,请联系管理员处理";
                    //标识是否成功
                    code = ResultCode.CERT_GEN_FAILED;
                }
            } catch(Exception e) {
                //证书请求，证书信息若果为空的处理
                code = ResultCode.CERT_GEN_FAILED;
                String test = e.getMessage();
                logger.error("异常 ： {}", test);
                if(test.equals("证书信息为空")) {
                    returnStr = test;
                } else if(test.equals("证书请求信息为空")) {
                    returnStr = test;
                } else {
                    returnStr = "证书请求出错";
                }
            }
        }
        assert code != null;
        map.put("code", String.valueOf(code.getCode()));
        map.put("returnStr", returnStr);
        logger.debug("checkState ： " + code);
        return map;
    }*/
    /**
     * 证书验证
     * @param
     * @return
     */
//    @RequestMapping("/certCheck")
//    public void certCheck(HttpServletRequest request, HttpServletResponse response){
//        Map<String,Object> caVerifyMap = new HashMap<>();
//        try{
//            //获取信令服务器发来的信息
//            ServletInputStream sis = request.getInputStream();
//            CAVerifyRequest caVerifyRequest = CAVerifyRequest.parseFrom(sis);
//            caVerifyMap.put("transId",caVerifyRequest.getTransId());     //流水号
//            caVerifyMap.put("serviceCode",caVerifyRequest.getServiceCode());     //产品编码
//            caVerifyMap.put("reqDoc",caVerifyRequest.getReqDoc());    //待签原文的base64编码
//            caVerifyMap.put("signValue",caVerifyRequest.getSignValue());    //随机数的签名值
//            caVerifyMap.put("signCert",caVerifyRequest.getSignCert());    //备用字段
//
//            Result result = certRequestService.certCheck(caVerifyMap);
//
//            CAVerifyResponse caVerifyResponse;
//            Map<String, Object> certVerifyMap = (Map<String, Object>) result.getData();
//            Map<String, Object> certVerifyData = (Map<String, Object>) certVerifyMap.get("data");
//            if (result.getCode()==200) {
//                CAVerifyData caVerifyData = CAVerifyData.newBuilder()
//                        .setStatus(certVerifyData.get("status").toString())
//                        .build();
//                caVerifyResponse = CAVerifyResponse.newBuilder()
//                        .setTransId(caVerifyMap.get("transId").toString())
//                        .setStatus(caVerifyMap.get("status").toString())
//                        .setMessage(caVerifyMap.get("message").toString())
//                        .setData(caVerifyData.toByteString())
//                        .build();
//            } else {
//                caVerifyResponse = CAVerifyResponse.newBuilder()
//                        .setStatus(caVerifyMap.get("status").toString())
//                        .setMessage(caVerifyMap.get("message").toString())
//                        .build();
//            }
//            response.reset();
//            ServletOutputStream sos = response.getOutputStream();
//            sos.write(caVerifyResponse.toByteArray());
//            sos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 证书更新
     * @param
     * @return
     */
//    @RequestMapping("/certUpdate")
//    public void certUpdate(HttpServletRequest request,HttpServletResponse response){
//        Map<String,Object> caUpdateMap = new HashMap<>();
//        try{
//            //获取信令服务器发来的信息
//            ServletInputStream sis = request.getInputStream();
//            CAUpdateRequest caUpdateRequest = CAUpdateRequest.parseFrom(sis);
//            caUpdateMap.put("transId",caUpdateRequest.getTransId());     //流水号
//            caUpdateMap.put("serviceCode",caUpdateRequest.getServiceCode());     //产品编码
//            caUpdateMap.put("reqDoc",caUpdateRequest.getCst());    //证书base64编码
//            caUpdateMap.put("signValue",caUpdateRequest.getCertSn());    //证书序列号
//            caUpdateMap.put("signCert",caUpdateRequest.getNewP10());    //原证书所在容器生成的新的证书请求
//            caUpdateMap.put("signCert",caUpdateRequest.getPkcs7());    //老证书对csr（newP10）签名后的base64字符串
//            caUpdateMap.put("signCert",caUpdateRequest.getRaId());    //备用字段
//
//            Result result = certRequestService.certUpdate(caUpdateMap);
//
//            CAUpdateResponse caUpdateResponse;
//            Map<String, Object> certUpdateMap = (Map<String, Object>) result.getData();
//            Map<String, Object> certUpdateData = (Map<String, Object>) certUpdateMap.get("data");
//            if (result.getCode()==200) {
//                CAUpdateData caUpdateData = CAUpdateData.newBuilder()
//                        .setBuf(certUpdateData.get("buf").toString())
//                        .setEncPrivat(certUpdateData.get("pki").toString())
//                        .build();
//                caUpdateResponse = CAUpdateResponse.newBuilder()
//                        .setTransId(certUpdateMap.get("transId").toString())
//                        .setStatus(certUpdateMap.get("status").toString())
//                        .setMessage(certUpdateMap.get("message").toString())
//                        .setData(caUpdateData.toByteString())
//                        .build();
//            } else {
//                caUpdateResponse = CAUpdateResponse.newBuilder()
//                        .setStatus(certUpdateMap.get("status").toString())
//                        .setMessage(certUpdateMap.get("message").toString())
//                        .build();
//            }
//            response.reset();
//            ServletOutputStream sos = response.getOutputStream();
//            sos.write(caUpdateResponse.toByteArray());
//            sos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

//    @RequestMapping("/certRevoke")
//    public void certRevoke(HttpServletRequest request,HttpServletResponse response) {
//        Map<String,Object> caRevokeMap = new HashMap<>();
//        try{
//            //获取信令服务器发来的信息
//            ServletInputStream sis = request.getInputStream();
//            CARevokeRequest caRevokeRequest = CARevokeRequest.parseFrom(sis);
//            caRevokeMap.put("transId",caRevokeRequest.getTransId());     //流水号
//            caRevokeMap.put("serviceCode",caRevokeRequest.getServiceCode());     //产品编码
//            caRevokeMap.put("reqDoc",caRevokeRequest.getCertSn());    //证书序列号
//            caRevokeMap.put("signValue",caRevokeRequest.getRevokeReason());    //吊销原因
//            caRevokeMap.put("signCert",caRevokeRequest.getRaId());    //备用字段
//
//            Result result = certRequestService.certRevoke(caRevokeMap);
//
//            CARevokeResponse caRevokeResponse;
//            Map<String, Object> certRevokeMap = (Map<String, Object>) result.getData();
//            Map<String, Object> certRevokeData = (Map<String, Object>) certRevokeMap.get("data");
//            if (result.getCode()==200) {
//                CARevokeData caRevokeData = CARevokeData.newBuilder()
//                        .setStatus(certRevokeData.get("state").toString())
//                        .build();
//                caRevokeResponse = CARevokeResponse.newBuilder()
//                        .setTransId(certRevokeMap.get("transId").toString())
//                        .setStatus(certRevokeMap.get("status").toString())
//                        .setMessage(certRevokeMap.get("message").toString())
//                        .setData(caRevokeData.toByteString())
//                        .build();
//            } else {
//                caRevokeResponse = CARevokeResponse.newBuilder()
//                        .setStatus(certRevokeMap.get("status").toString())
//                        .setMessage(certRevokeMap.get("message").toString())
//                        .build();
//            }
//            response.reset();
//            ServletOutputStream sos = response.getOutputStream();
//            sos.write(caRevokeResponse.toByteArray());
//            sos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
        String[] optionNames ={"username"};
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

    //软件上传注册
    @RequestMapping(value = "/privateSoftwareUpload")
    public Result privateSoftwareUpload(Map<String, Object> reqBody) {
        return certRequestService.privateSoftwareUpload(reqBody);
    }

    @RequestMapping(value = "/publicSoftwareUpload")
    public Result softRegister(Map<String, Object> reqBody, HttpServletRequest request)  {
        return certRequestService.publicSoftwareUpload(reqBody,request);
    }

    //软件下载
    @RequestMapping("/download")
    public Result softDownload(Map<String, Object> reqBody,HttpServletRequest request, HttpServletResponse response) {
        return certRequestService.softDownload(reqBody,request,response);
    }

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

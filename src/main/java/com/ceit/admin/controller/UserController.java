package com.ceit.admin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ceit.admin.common.utils.SM3Utils;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import com.ceit.response.ResultCode;
import com.ceit.utils.SqlUtil;

@Controller("/user")
public class UserController {

    @Autowired
    private SimpleJDBC simpleJDBC;

    private final String[] optionNames = { "account", "name", "id_number", "email", "tel", "mobile" };
    private final String searchFiledName = "org_id";
    private final String tableName = "sys_user";

    /**
     * 树形展示数组
     */
    @RequestMapping("/tree")
    public Result treeData(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonArray();

        return new Result(ResultCode.SUCCESS_TOTREE, jsonData);
    }

    /**
     * 不分页列表数组
     */
    @RequestMapping("/list")
    public Result listData(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonArray();

        return new Result("ok", 200, jsonData);
    }

    /**
     * 分页列表数组
     */
    @RequestMapping("/page")
    public Result page(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonArray();
        int count = sqlUtil.selectForTotalCount();

        // 把客户端需要分页的数据标准化
        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";

        return new Result("ok", 200, jsonData);
    }

    /**
     * 单个对象数据
     */
    @RequestMapping("/get")
    public Result get(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable(tableName)
                .setSearchFields(searchFiledName)
                .setAcceptOptions(optionNames)
                .selectForJsonObject();
        return new Result("ok", 200, jsonData);
    }

    /**
     * 获取绑定的IP地址
     */
    @RequestMapping("/getIP")
    public Result getIP(Map<String, Object> reqBody) {
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("sys_user_ip")
                .setFields("ip")
                .setSearchFields("user_id")
                .selectForJsonObject();
        return new Result("ok", 200, jsonData);
    }

    /**
     * 获取绑定的微信号
     */
    @RequestMapping("/getWx")
    public Result getWx(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("sys_user_wx")
                .setFields("wx")
                .setSearchFields("user_id")
                .selectForJsonObject();
        return new Result("ok", 200, jsonData);
    }

    /**
     * 获取绑定的MAC地址
     */
    @RequestMapping("/getMAC")
    public Result getMAC(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("sys_user_mac")
                .setFields("mac")
                .setSearchFields("user_id")
                .selectForJsonObject();
        return new Result("ok", 200, jsonData);
    }

    /**
     * 添加IP绑定
     */
    @RequestMapping("/insertIP")
    public Result insertIP(Map<String, Object> reqBody) {
        String ip = reqBody.get("bindData").toString();
        Integer user_id = Integer.parseInt(reqBody.get("user_id").toString());
        String sql = "insert into sys_user_ip (user_id, ip) values(?,?)";
        int ret = simpleJDBC.update(sql, user_id, ip);
        if (ret > 0) {
            return new Result("修改成功", 200, "success");
        }
        return new Result("修改失败", 200, "error");
    }

    /**
     * 添加MAC地址绑定
     */
    @RequestMapping("/insertMAC")
    public Result insertMAC(Map<String, Object> reqBody) {
        String mac = reqBody.get("bindData").toString();
        Integer user_id = Integer.parseInt(reqBody.get("user_id").toString());
        String sql = "insert into sys_user_mac (user_id, mac) values(?,?)";
        int ret = simpleJDBC.update(sql, user_id, mac);
        if (ret > 0) {
            return new Result("修改成功", 200, "success");
        }
        return new Result("修改失败", 200, "error");
    }

    private String encryptPassword(String password) {
        String encrypt = "{SM3}" + SM3Utils.encrypt(password); // 将密码进行sm3加密并拼接
        return encrypt;
    }

    private boolean insertOrUpdatePassword(Integer user_id, String newpass) {

        // FIXME: 验证密码复杂度等

        // 密码是否存在
        String sql = "select count(*) from sys_user_password where user_id=?";
        Object count= simpleJDBC.selectForOneNode(sql, user_id) ;

        if (count == null ||  (Long)count == 0) {
            sql = "insert into sys_user_password "
                    + "(password, user_id, change_time, locked) "
                    + "VALUES(?, ?, NOW(), 0);";
        } else {
            sql = "update sys_user_password set password=?, change_time =NOW() where user_id=?";
        }

        int ret = simpleJDBC.update(sql, encryptPassword(newpass), user_id);
        if (ret > 0) {
            return true;
        }

        return false;
    }

    /**
     * 修改密码
     */
    @RequestMapping("/updatePassword")
    public Result updatePassword(Map<String, Object> reqBody) {

        Integer user_id = Integer.parseInt(reqBody.get("id").toString());
        String password = reqBody.get("password").toString();

        // FIXME: 验证密码复杂度等

        if (insertOrUpdatePassword(user_id, password)) {
            return new Result("修改成功", 200, "success");
        }

        return new Result("修改密码失败", 200, "error");
    }

    /**
     * 修改绑定的IP地址
     */
    @RequestMapping("/updateIP")
    public Result updateIP(Map<String, Object> reqBody) {
        String ip = reqBody.get("bindData").toString();
        Integer user_id = Integer.parseInt(reqBody.get("user_id").toString());
        String sql = "update sys_user_ip set ip = ? where user_id = ?";
        int ret = simpleJDBC.update(sql, ip, user_id);
        if (ret > 0) {
            return new Result("修改成功", 200, "success");
        }
        return new Result("修改绑定的IP地址失败", 200, "error");
    }

    /**
     * 修改绑定的IP地址
     */
    @RequestMapping("/updateMAC")
    public Result updateMAC(Map<String, Object> reqBody) {
        String mac = reqBody.get("bindData").toString();
        Integer user_id = Integer.parseInt(reqBody.get("user_id").toString());
        String sql = "update sys_user_mac set mac = ? where user_id = ?";
        int ret = simpleJDBC.update(sql, mac, user_id);
        if (ret > 0) {
            return new Result("修改成功", 200, "success");
        }
        return new Result("修改绑定的MAC地址失败", 200, "error");
    }

    @RequestMapping("/insert")
    public Result insert(Map<String, Object> reqBody) {

        if (reqBody.get("account") == null)
            return new Result("修改失败:账号不能为空", 200, "error");

        // account不能重复,或者不允许修改账号
        Object obj = simpleJDBC.selectForOneNode("SELECT count(*) FROM sys_user WHERE account=?",
                reqBody.get("account"));
        if (obj != null && (Long) obj > 0) {
            return new Result("修改失败:账号已存在", 200, "error");
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String create_time = df.format(System.currentTimeMillis());
        reqBody.put("create_time", create_time);
        SqlUtil sqlUtil = new SqlUtil(reqBody);
        Integer user_id = sqlUtil.setTable("sys_user")
                .setFields("account", "name", "id_number", "description", "email", "org_id", "sex", "creator_id",
                        "disabled", "create_time", "start_time", "end_time", "mobile")
                .setSearchFields("id")
                .insertAutoIncKey();
        
        // 添加密码
        String newpass = (String) reqBody.get("password");
        if (newpass != null && newpass.length() > 0) {
            if (insertOrUpdatePassword(user_id, newpass) == false) {
                return new Result("添加密码失败", 200, "success");
            }
        }

        return new Result("ok", 200, user_id);
    }

    @RequestMapping("/update")
    public Result update(Map<String, Object> reqBody) {

        // account不能重复,或者不允许修改账号
        if (reqBody.get("account") != null) {
            Object obj = simpleJDBC.selectForOneNode("SELECT count(*) FROM sys_user WHERE id<>? AND account=?",
                    reqBody.get("id"), reqBody.get("account"));
            if (obj != null && (Long) obj > 0) {
                return new Result("修改失败:账号已存在", 200, "error");
            }
        }

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        int ret = sqlUtil.setTable("sys_user")
                .setFields("account", "name", "id_number", "description", "email", "org_id", "sex", "creator_id",
                        "disabled", "create_time", "start_time", "end_time", "mobile")
                .setWhere("id=?")
                .update();

        // 修改密码
        String newpass = (String) reqBody.get("password");
        if (newpass != null && newpass.length() > 0) {
            Integer user_id = Integer.parseInt(reqBody.get("id").toString());
            if (insertOrUpdatePassword(user_id, newpass) == false) {
                return new Result("修改密码失败", 200, "success");
            }
        }

        return new Result("ok", 200, ret);
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("ids").toString();
        String[] userIds = str.split(",");
        int rSet = 0;
        for (int i = 0; i < userIds.length; i++) {
            Integer id = Integer.parseInt(userIds[i]);
            rSet = simpleJDBC.update("delete from sys_user where id=?", id);
        }
        return new Result("success", 200, rSet);

    }
}

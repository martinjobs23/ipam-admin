package com.ceit.admin.service;


import com.ceit.admin.common.utils.SM3Utils;
import com.ceit.admin.model.UserInfo;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Component;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

@Component
public class PasswordService {
    
    @Autowired
    private SimpleJDBC simpleJDBC;

    /**
     *  账号密码登录
     * @param reqBody
     * @param request
     * @return
     */
    public Result passwordLogin(Map<String, Object> reqBody, HttpServletRequest request){
        Result result = new Result();
        result.setCode(200);
        String account  = (String) reqBody.get("account");
//        System.out.println("account:" + reqBody.get("account"));
        String password  = (String) reqBody.get("password");
        //1.用户名、密码是否为空
        if (account == null || account.equals("") || password == null || password.equals("")){
            result.setMsg("用户名或密码不能为空");
            result.setData("error");
            return result;
        }
        /**
         * 分开查找用户 先看有无账号，再看有无密码
         */
        String str = "select * from sys_user where account = ?";
        Map<String, Object> map = simpleJDBC.selectForMap(str, account);
        if (map==null){
            result.setMsg("访问数据库失败，请重试或联系管理员");
            result.setData("error");
            return result;
        }

        if (map.isEmpty()){
            result.setMsg("账号不存在");
            result.setData("error");
            return result;
        }

        String sql = "select u.*, up.password, up.locked, up.change_time, up.fail_count, up.fail_time from sys_user u join sys_user_password up where account = ? and u.id = up.user_id";
        Map<String, Object> userMap = simpleJDBC.selectForMap(sql, account);
        //2.账号是否存在
        if (userMap.isEmpty()){
            result.setMsg("此账号未设置密码，请联系管理员");
            result.setData("error");
            return result;
        }
        //3.查找用户的认证策略 --是否需要密码登录
        Result policyCheckRes = policyCheck(userMap, "pwd", account, request);
        if (policyCheckRes.getMsg() != null && !policyCheckRes.getMsg().equals("")){
            return policyCheckRes;
        }
        UserInfo userInfo = (UserInfo) policyCheckRes.getData();
        // 以下是密码登录方式的检验
        // 4.查看用户是否禁用
        if ((Integer) userMap.get("disabled") == 1){
            result.setMsg("账号已被禁用");
            result.setData("error");
            return result;
        }
        // 5.用户是否锁定
        if ((Integer)userMap.get("locked") == 1){
            Map isUnlocked = isUnLocked(userInfo.id, (Date) userMap.get("fail_time"));
            if (isUnlocked != null){
                result.setMsg((String) isUnlocked.get("msg"));
                result.setData("error");
                return result;
            }
        }
        //6.用户密码是否过期
        sql = "select value from sys_auth_policy_setting where method_code = 'pwd' and code = 'valid_days'";
        Object authPolicySetting = simpleJDBC.selectForOneNode(sql);
        Date nowDate = new Date();
        //若有设置，再判断是否过期
        if (authPolicySetting != null){
            Integer value = Integer.parseInt(authPolicySetting.toString());
            Date changeTime = (Date) userMap.get("change_time");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(changeTime);
            calendar.add(Calendar.DATE, value);
            if (calendar.getTime().getTime() < nowDate.getTime()) {
                result.setMsg("密码已过期");
                result.setData("error");
                return result;
            }
        }
        //7.密码是否正确
        //7.1截取密码中{SM3}后面的字符串
        String realPassword = (String) userMap.get("password");
        realPassword = realPassword.substring(5);
        //7.2获取用户输入的密码的sm3加密值
        String encrypt = SM3Utils.encrypt(password);
        if (!realPassword.equals(encrypt)) {
            //密码错误则更新数据库
            String ip = getIpAddress();//失败ip
            sql = "update sys_user_password set fail_count = fail_count+1,  fail_ip = ?, fail_time = ? where user_id = ?";
            simpleJDBC.update(sql, ip, nowDate, userInfo.id);
            //8.账号是否需要锁定
            sql = "select value from sys_auth_policy_setting where method_code = 'pwd' and code = 'fail_lock_count'";
            Map<String, Object> fail_lock_count = simpleJDBC.selectForMap(sql);
            if (fail_lock_count != null){
                sql = "select fail_count from sys_user_password where user_id = ?";
                Integer failCount = Integer.parseInt(simpleJDBC.selectForOneNode(sql, userInfo.id).toString());
                //若错误次数=最大错误次数 锁定账号
                if (failCount == Integer.parseInt(fail_lock_count.get("value").toString())){
                    sql = "update sys_user_password set locked = 1 where user_id = ?";
                    simpleJDBC.update(sql, userInfo.id);
                    result.setMsg("密码错误，账号已被锁定");
                    result.setData("error");
                    return result;
                }
            }
            if (isVerifyCode(Integer.parseInt(userMap.get("fail_count").toString()) + 1)){
                result.setData("true");
            }
            result.setData("error");
            result.setMsg("密码错误");
        } else {
            //9.登录成功，修改失败次数等字段...
            sql = "update sys_user_password set locked = 0,fail_count = 0,fail_ip = null,fail_time = null where user_id = ?";
            simpleJDBC.update(sql, userInfo.id);
            userInfo.setOkAuthPolicy("pwd");
            request.getSession().setAttribute("userInfo",userInfo);
            //检查是否所有认证方式均完成
            boolean res = userInfo.checkAuth();
            if (res == false){
                result.setCode(100);
                result.setData(userInfo.unAuthPolicy.get(0)); //将没有认证的方式路径发送给前端
            }else {
                String data = "{\"accessToken\":\""+account+"\"}";
                result.setData(data);
            }
        }
        return result;
    }

    /**
     *  公共部分的验证
     * @param userMap
     * @param method
     * @param authString
     * @param request
     * @return
     */
    public Result policyCheck(Map<String, Object> userMap, String method, String authString, HttpServletRequest request){
        Result result = new Result();
        result.setCode(200);
        String data = "error";
        Integer id = Integer.parseInt(userMap.get("id").toString());
        String sql = "select methods from sys_auth_policy where id in (select auth_policy_id from sys_role where id in (select role_id from sys_role_user where user_id = ?))";
        Object methods = simpleJDBC.selectForOneNode(sql, id);
        if (methods == null){
            //是因为什么没找到策略
            //1.第一种情况：因为没有为用户分配角色
            sql = "select * from sys_role_user where user_id = ? ";
            List<Map<String, Object>> maps = simpleJDBC.selectForList(sql, id);
            if (maps.size() == 0){
                return new Result("请联系管理员为您分配角色",200,"error");
            }
            //2.第二种情况：因为没有为角色分配策略，使用默认策略
            sql = "select methods from sys_auth_policy where is_default = 0";
            methods = simpleJDBC.selectForOneNode(sql);
            //没有默认策略，拒绝登陆
            if (methods == null){
                result.setMsg("拒绝登录");
                result.setData(data);
                return result;
            }
        }
        HttpSession session = request.getSession();
        UserInfo user = new UserInfo();
        Object userInfo = session.getAttribute("userInfo");
        if (userInfo != null){
            user = (UserInfo) userInfo;
            if (method.equals("pwd") && !user.username.equals(authString)){
                result.setMsg("账号错误"); //根据登录方式返回不同的错误值
                result.setData(data);
                return result;
            }
            if (method.equals("sms")){
                sql = "select mobile from sys_user where id = ?";
                String mobile = (String) simpleJDBC.selectForOneNode(sql, user.id);
                if (!mobile.equals(authString)){
                    result.setMsg("手机号错误");
                    result.setData(data);
                    return result;
                }
            }
            if (method.equals("wx")){
                sql = "select wx from sys_user_wx where user_id = ?";
                String  wx = (String) simpleJDBC.selectForOneNode(sql, user.id);
                if (!wx.equals(authString)){
                    result.setMsg("微信号错误");
                    result.setData(data);
                    return result;
                }
            }
        } else {
            user.id = id;
            user.username = userMap.get("account").toString();
            user.setAllAuthMethod((String) methods);
            user.setNode((String) methods);
        }
        if (!user.allAuthPolicy.contains(method)){
            result.setMsg("登录方式错误，请使用其它方式登录");
            result.setData(data);
            return result;
        }
        result.setData(user);
        return result;
    }
    /**
     * 查看用户是否需要某种方法登录
     * @param userInfo
     * @param method
     * @return
     */
    public boolean includeAuthPolicy(UserInfo userInfo, String method){
        if (userInfo.allAuthPolicy.contains(method)){
            return true;
        }
        return false;
    }

    /**
     * 查看密码错误之后是否需要验证码
     * @param failCount
     * @return
     */
    public boolean isVerifyCode(Integer failCount) {
        String sql = "select value from sys_auth_policy_setting where method_code = 'pwd' and code = 'fail_captcha_count'";
        Object o = simpleJDBC.selectForOneNode(sql);
        if (o != null){
            if (failCount >= Integer.parseInt(o.toString())){
                return true;
            }
        }
        return false;
    }

    //获取IP地址 linux 和Windows均适用
    public String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }

    /**
     *
     * @Author 苏钰玲
     * @Description:账号是否可以解锁
     * @Date  2021/3/11
     **/
    public Map<String, String> isUnLocked(Integer userId, Date failTime){
        Map<String, String> map = null;
        String sql = "select value from sys_auth_policy_setting where method_code = 'pwd' and code = 'fail_lock_time'";
        Integer value = Integer.parseInt(simpleJDBC.selectForOneNode(sql).toString());
        Date nowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(failTime); //设置时间
        calendar.add(Calendar.SECOND,value);
        failTime = calendar.getTime();  //得到解锁时间
        if (failTime.getTime() < nowDate.getTime()){
            //可以解锁，更新数据库
            sql = "update sys_user_password set locked = 0,fail_count = 0,fail_ip = null,fail_time = null where user_id = ?";
            simpleJDBC.update(sql, userId);
        }else {
            map = new HashMap<>();
            int diffTime = (int) Math.ceil(1.0*(failTime.getTime() - nowDate.getTime())/(1000*60));
            map.put("msg","账户已被锁定，请"+diffTime+"分钟之后再次尝试");
        }
        return map;
    }

}

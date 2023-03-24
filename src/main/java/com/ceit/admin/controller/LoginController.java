package com.ceit.admin.controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.ceit.admin.model.UserInfo;
import com.ceit.admin.service.PasswordService;
import com.ceit.admin.service.SmsService;
import com.ceit.admin.service.WxService;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;

@Controller("/login")
public class LoginController {

    @Autowired
    private SimpleJDBC simpleJDBC;

    @Autowired
    private JSON json;

    @Autowired
    PasswordService passwordService;

    @Autowired
    SmsService smsService;

    @Autowired
    WxService wxService;

   //账号密码方式登录
    @RequestMapping("/passwordLogin")
    public Result passwordLogin(Map<String, Object> reqBody, HttpServletRequest request) {
        return passwordService.passwordLogin(reqBody,request);
    }

    //发送短信验证码
    @RequestMapping("/sendCode")
    public Result sendCode(Map<String, Object> reqBody, HttpServletRequest request) {
        Result result = smsService.sendCode(reqBody, request);
        return result;
    }

    //验证码登录
    @RequestMapping("/smsLogin")
    public Result smsLogin(Map<String, Object> reqBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return smsService.smsLogin(reqBody, request);
    }

    //微信登录
    @RequestMapping("/wxLogin")
    public Result wxLogin(HttpServletRequest request, HttpServletResponse response){
        return wxService.wxLogin(request);
    }


    @RequestMapping("/logout")
    public String logout(Map<String, Object> reqBody, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("userInfo");
        return "{}";
    }

    @RequestMapping("/userInfo")
    public Result userInfo(Map<String, Object> reqBody, HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userInfo");
        if(userInfo==null)
        {
            return new Result("用户信息错误,请重新登录", 100);   
        }
        String tmp = "{\"userId\":" + userInfo.id
                + ",\"username\":\""+ userInfo.username
                + "\", \"avatar\":\"https://i.gtimg.cn/club/item/face/img/2/15922_100.gif\"}";
        return new Result("ok", 200,  tmp);
    }

}

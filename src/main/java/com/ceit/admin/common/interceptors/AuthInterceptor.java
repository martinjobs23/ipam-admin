package com.ceit.admin.common.interceptors;

import com.ceit.admin.model.UserInfo;
import com.ceit.interceptor.HandlerInterceptor;
import com.ceit.ioc.HandlerDefinition;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 拦截器
 */
public class AuthInterceptor implements HandlerInterceptor {

    public static int isDebugNoAuthMode = -1;
    public boolean isDebugNoAuth()
    {
        if(isDebugNoAuthMode==0)
        {
            return false;
        }
        else if(isDebugNoAuthMode==1)
        {
            return true;
        }
        else
        {
            String noauth = System.getProperty("debug.noauth");
            //使用完清除，否则，如果先设置了，后又注释掉，tomcat不重启这个值还在
            System.clearProperty("debug.noauth");  
            if(noauth!=null &&  ( noauth.equals("1") || noauth.equals("true") ))
            {
                isDebugNoAuthMode =1;
                return true;
            }
            else
            {
                isDebugNoAuthMode =0;
                return false;   
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerDefinition handler) {

        //如果是调试模式,不需要认证
        if(isDebugNoAuth())
        {
            System.out.println("!! DEBUG NO AUTH MODE !! path=" + handler.getUrl());
            return true;
        }

        // 1.判断是否已成功登录
        Object object = request.getSession().getAttribute("userInfo");
        if (object == null) {
            // 未登录直接访问
           // setReturn(response, 100);
            try {
                PrintWriter out = response.getWriter();
                out.print(new Result("用户信息错误，请重新登录",100,"login"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        UserInfo userInfo = (UserInfo) object;
        if (userInfo.unAuthPolicy.size() != 0) {
            // 还未认证完成
            //setReturn(response, 100);
            try {
                PrintWriter out = response.getWriter();
                out.print(new Result("用户认证错误，请重新登录",100,"login"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        // 2.查看是否有权限
        // 获取请求方法
        String servletPath = handler.getUrl();
        //System.out.println("preHandle servletPath:" + servletPath);
        String sql = "select path from sys_api where path = ? and id in (select api_id from sys_role_api where role_id in ("
                + "select role_id from sys_role_user where user_id = ?))";
        SimpleJDBC simpleJDBC = SimpleJDBC.getInstance();
        object = simpleJDBC.selectForOneNode(sql, servletPath, userInfo.id);
        // 没有权限
        if (object == null) {
            //setReturn(response, 401);
            try {
                PrintWriter out = response.getWriter();
                out.print(new Result("没有后台接口"+servletPath +"的访问权限",401,"401"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("preHandle object == null return false" );
            return false;
        }

        //System.out.println("preHandle return true" );
        return true;
    }

    // 返回错误信息
    private void setReturn(HttpServletResponse response, int code) {
        // 100是未登录 //401是未授权
        try {
            if (code == 100) {
                response.sendRedirect("http://localhost:8081/#/login");
            } else {
                response.sendRedirect("http://localhost:8081/#/401");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

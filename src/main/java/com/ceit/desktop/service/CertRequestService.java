package com.ceit.desktop.service;

import com.ceit.admin.common.utils.FileUtil;
import com.ceit.desktop.grpc.client.WebToControlClient;
import com.ceit.desktop.grpc.controlCenter.*;
import com.ceit.desktop.util.Hash;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Component;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CertRequestService {

    @Autowired
    SimpleJDBC simpleJDBC;

    @Autowired
    private Hash hash;

    //管理员审核
    public Result audit(Map<String, Object> reqBody) {
        //终端申请证书时，需管理员审核
        Integer is_handle = Integer.parseInt(reqBody.get("is_handle").toString());
        //获取设备Hash值+MAC
        String serial = reqBody.get("serial").toString();
        String sql;
        String dev_name = "";
        String org_id = "";
        String device_ip = "";
        int flag = Integer.parseInt(System.getProperty("certApply"));
            if(is_handle == 1) {
//            //随机生成流水号
//            String tranId = idFactory.createTransId();
////            System.out.println("tranId: " + tranId);
//            //设备名称
                dev_name = reqBody.get("dev_name").toString();
//            //设备mac
//            String device_mac = serial.substring(32);
//            //组织id
                org_id = reqBody.get("org_id").toString();
                device_ip = reqBody.get("device_ip").toString();
            }
            WebToControlClient webToControlClient = new WebToControlClient();
            DevRegisterReply devRegisterReply = webToControlClient.blockingStub.devRegisterCheck(DevRegisterRequest.newBuilder().setIsHandle(is_handle).setSerial(serial).setDevName(dev_name).setOrgId(org_id).setDeviceIp(device_ip).build());
            System.out.println("code: " + devRegisterReply.getCode());
            System.out.println("data: " +devRegisterReply.getData());
            System.out.println("Msg: " +devRegisterReply.getMsg());
            return new Result(devRegisterReply.getMsg(),devRegisterReply.getCode(),devRegisterReply.getData());
    }
    public Result radCheck(Map<String, Object> reqBody) {
        //终端注册管理员审核 is_handle=1审核通过，is_handle=2审核未通过
        Integer is_handle = Integer.parseInt(reqBody.get("is_handle").toString());
        String serial = reqBody.get("device_id").toString();
        String dev_name = "";
        String org_id = "";
        String device_ip = "";
//        String username = serial.substring(0,32);

        if(is_handle == 1) {

            //设备名称
            dev_name = reqBody.get("dev_name").toString();
            //设备mac
//            String device_mac = serial.substring(32);
            //组织id
            org_id = reqBody.get("org_id").toString();
            device_ip = reqBody.get("device_ip").toString();
        }
        //请求管控中心设备注册接口
            WebToControlClient webToControlClient = new WebToControlClient();
            DevRegisterReply devRegisterReply = webToControlClient.blockingStub.devRegisterCheck(DevRegisterRequest.newBuilder().setIsHandle(is_handle).setSerial(serial).setDevName(dev_name).setOrgId(org_id).setDeviceIp(device_ip).build());
            System.out.println("code: " + devRegisterReply.getCode());
            System.out.println("data: " +devRegisterReply.getData());
            System.out.println("Msg: " +devRegisterReply.getMsg());
            return new Result(devRegisterReply.getMsg(),devRegisterReply.getCode(),devRegisterReply.getData());
    }

    //设备注册撤销
    public Result radRevoke(Map<String, Object> reqBody) {
        //设备序列号
        String username = reqBody.get("username").toString();
        //设备mac
        String device_mac = reqBody.get("device_mac").toString();

        //请求管控中心设备注册撤销接口
        WebToControlClient webToControlClient = new WebToControlClient();
        DevUnRegisterReply devUnRegisterReply = webToControlClient.blockingStub.devUnRegister(DevUnRegisterRequest.newBuilder().setUsername(username).setDevicaMac(device_mac).build());
        System.out.println("code: " + devUnRegisterReply.getCode());
        System.out.println("data: " +devUnRegisterReply.getData());
        System.out.println("Msg: " +devUnRegisterReply.getMsg());
        return new Result(devUnRegisterReply.getMsg(),devUnRegisterReply.getCode(),devUnRegisterReply.getData());
    }

    //软件下载
    public Result softDownload(Map<String, Object> reqBody, HttpServletRequest request, HttpServletResponse response) {
//        String str = request.getParameter("id");
//        String[] ids = str.split(",");
//        //下载保存文件的父路径
//        String filefatherPath = System.getProperty("softupload.path");
//        File fileFatherPath = new File(filefatherPath);
//        // 创建父路径 避免路径不存在保错
//        fileFatherPath.mkdirs();
//
//        for (String id : ids){
//            String sql = "select soft_name from softcheck where id = ?";
//            String soft_name = (String) simpleJDBC.selectForOneNode(sql, id);
//            System.out.println("filename:" +soft_name);
//            FileUtil.download(fileFatherPath, soft_name, response);
//        }
//        return new Result("下载成功",200,"success");
        String id = request.getParameter("id");
        //下载保存文件的父路径
        String filefatherPath = System.getProperty("softupload.path");
        File fileFatherPath = new File(filefatherPath);
        // 创建父路径 避免路径不存在保错
        fileFatherPath.mkdirs();
        String sql = "select sw_name from softcheck where id = ?";
        String soft_name = (String) simpleJDBC.selectForOneNode(sql, id);
        System.out.println("filename:" +soft_name);
        File file = new File(fileFatherPath,soft_name);
        if (file.exists()){
            FileUtil.download(fileFatherPath, soft_name, response);
            return new Result("下载成功",200,"success");
        } else {
            return new Result("软件不存在",200,"error");
        }

    }

    //私有软件上传
    public Result privateSoftwareUpload(Map<String, Object> reqBody){
        //String filename, desc, type, sw_public, org, version, time;
        String filename = (String) reqBody.get("sw_name");
        String desc = (String) reqBody.get("sw_desc");
        String type = (String) reqBody.get("sw_type");
        String org_id =  String.valueOf(reqBody.get("sw_organization"));
        String version = (String) reqBody.get("sw_version");
        String hash = (String) reqBody.get("sw_hash");
        String base64String = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAD4ElEQVR4Xu3XW0xbdRwHcDRGE01IfNEHfdVElxiTveiL2YNTN2ccC1sGGG8bojh2TdhAxkmZZHOsc0I3CmWlYEFWGHUttPQCdL1xaVdaSrm1UBDXaSbIbYBAfvr/f7MuS1OyvdEHT/J5OGnO//8953/7Nenf+9f6+jpthqT/AzwqwOrqKtyZmaa5xUW4rLtBfaEgdPp9UNaipvl7i/fdo7W1NYhtL1biB4hMT0PahWJyBYeh2WknlcMCnf19UNryKw1MhEHRbkQILra9WHEDLK+sULleC+2+PpCb9GTyumH09u+ksOggv64SBFUt1XYaQd1lo2DkNmh6uuAf9hVjO0+8ANFxW1pZpnNNDXBJ0wwmXzeNRiZA67HQ6UYpfFSSB2/kHqRXjnwBBcpqFsIB0eeXlpfizovECvD3wjyYvR4a/G0SdB4H3Br309RfEegO+ahIXQXpEgHeKvyWXsraD8+l76aTChmM34mAxd9PswsLsGGAwclJ+F5VRxqXAyo6miB8d4qGI2NgHXKTcF0K0QBvC4fo5W/S4Kl9H9Lzn6SC0mIGob6GApMTkLgB9O5eaLRb6YpJDUVqGQSmQmTyO6HaqqXjyouQIv4OtuZ/TS9m7oMnU3dSUsr7kFMhAT4P2m65IG4AvttVm9tA3WWnvHopZMvPg97rpFprCxQ0llNamQDbREfh1WOfUfKnKfDEnh309N5dIDPqQGlpZ20bIEEDsOXRzDrmGh1WOqoog4yyIlDc1NF5bR0ckJ6ld4TD8PqJL+GFg3vp2bSPYcvhr+iKTgP11g5odtromu0mrCVkAL5B+MNh8I6zmR7oh/0/ngGNy0lKmwkKG+T0rigXtotOQaZEzDrqBEW7gV7LyYTs8lLwjYeobywIcQPwG+9YCGQG3YMVUWXWg314gC5oVOAY8lPqDyLYXSzArjOFtFN0Gt4T8mnLkSyoYuPPiRp+pok//4C4cyAhAtydnYXjsnKq7TBBhvgcbBfyqNKoB294jK6y1cIVKOVwSCqhbQW5kJyxh7IkF8Hs84Cczf6Z+TnYMED0sLAF/HSWHURci6sH0llB8uaxbNh6IodqWNHBJbN9n0tK+YCeYcuOO/CTmLS9XVDMdlWOv9gjD6NNDxDFl6RjcABa3d3AiwpednG8PBPfaIIGNuu5zy+VUKlWDb+w++gxbPC4gW90D/eR2AEe1jMyBBWGVippVsE1u4U0vU64zgpUjq+Yy60auMrKt97REeAvw8W2+9gBouM2PTdHzqEAqOx8R7NAZVsr1HQYyR0cgRlW1MQb73gSP8BGoh1E/7jE/v64HgTYrGvTA/wHKxmKe2fuz0gAAAAASUVORK5CYII=";
        Date date = new Date(); //获取当前时间并写入库
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        String selectSql = "select sw_name FROM soft_cert where sw_hash = ?";
        Object obj = simpleJDBC.selectForOneNode(selectSql,hash);
        if (obj != null) {
            return new Result("error", 100, "软件已经存在，请上传其它版本软件。");
        }
        String sql = "insert into soft_cert (sw_name,sw_desc,sw_type,sw_public,sw_organization,sw_version,sw_time,sw_size,sw_url,sw_hash,sw_register,sw_image) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        int res = simpleJDBC.update(sql,filename,desc,type,"0",org_id,version,time,"0MB","#####",hash,"0",base64String);
        if(res == 0) {
            return new Result("error", 300, "私有软件" + filename + "上传数据库失败。");
        }
        else {
            return new Result("success", 200, "私有软件" + filename + "上传数据库成功。");
        }
    }

    //公有软件上传并认证
    public Result publicSoftwareUpload(Map<String, Object> reqBody, HttpServletRequest request) {
        ServletRequestContext servletRequestContext = new ServletRequestContext(request);
        System.out.println(servletRequestContext);
        // 使用FileItem工场类创建相应工场对象
        FileItemFactory factory = new DiskFileItemFactory();
        // 创建servlet文件上传对象并将指定工场对象传入
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        //中文文件名处理的代码
        fileUpload.setHeaderEncoding("utf-8");
        // 声明文件集合
        List<FileItem> parseRequest = null;

        //设置文件上传保存文件路径：保存在项目运行目录下的upload/supermarket文件夹
        String path = FileUtil.GetUploadPath(request);
        path = Paths.get(path, "supermarket").toString();
        try {
            // 使用servlet文件上传对象解析请求返回文件集合
            parseRequest = fileUpload.parseRequest(request);
            // 遍历文件对象集合 获取数据
            for (FileItem fileItem : parseRequest) {
                // 判断数据类型是不是普通的form表单字段
                if (!fileItem.isFormField()) {
                    // 获取上传文件的文件名
                    String fileName = fileItem.getName();
                    long filesize = fileItem.getSize();
                    String size = sizeRecalculation(filesize);  //字节转换为KB,MB和GB
                    String base64String;
                    // 使用上传文件创建输入流
                    InputStream fileStream = fileItem.getInputStream();
                    InputStream hashfile = fileItem.getInputStream();
                    //String file_size = String.valueOf(fileStream.available());
                    //计算软件hash值
                    String sha256Hash = hash.sha256HashCode32(hashfile);
                    //System.out.println("sha256Hash: " + sha256Hash);

                    String selectSql = "select sw_name FROM soft_cert where sw_hash = ?";
                    Object obj = simpleJDBC.selectForOneNode(selectSql,sha256Hash);
                    //判断软件是否已在软件仓库,o为空时，软件不存在
                    if (obj == null) {
                        WebToControlClient webToControlClient = new WebToControlClient();

                        UploadRespond uploadRespond = webToControlClient.blockingStub.softwareRegister(UploadRequest.newBuilder().setHash(sha256Hash).build());
                        System.out.println("code: " + uploadRespond.getCode());
                        System.out.println("data: " + uploadRespond.getData());
                        System.out.println("Msg: " + uploadRespond.getMsg());
                        // 定义保存的父路径
                        File fileFatherPath = new File(path);
                        // 创建父路径 避免路径不存在保错
                        if (!fileFatherPath.isDirectory()) {
                            fileFatherPath.mkdirs();
                        }
                        // 创建要保存的文件
                        File file = new File(fileFatherPath, fileName);
                        // 创建输出流
                        OutputStream out = new FileOutputStream(file);
                        // 创建字节缓存
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        // 一次读取1kb(1024byte),返回-1表明读取完毕
                        while ((len = fileStream.read(buffer)) != -1) {
                            // 一次写入1kb(1024byte)
                            out.write(buffer, 0, len);
                        }

                        // 冲刷流资源
                        out.flush();
                        // 关闭流
                        out.close();
                        fileStream.close();

                        base64String = IconToBase64String(file);
                        System.out.println(base64String);
                    } else {
                        fileStream.close();
                        return new Result("error",100,"软件已经存在，请上传其它版本软件");
                    }

                    // 文件上传需要写日志
                    //softwareUploadToDatabase(reqBody);
                    String name = request.getParameter("sw_name");
                    String desc = (String) reqBody.get("sw_desc");
                    String type = (String) reqBody.get("sw_type");
                    String sw_public = (String) reqBody.get("sw_public");
                    String org_id =  String.valueOf(reqBody.get("sw_organization"));
                    String version = (String) reqBody.get("sw_version");
                    Date date = new Date(); //获取当前时间并写入库
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = simpleDateFormat.format(date);
                    StringBuilder url = new StringBuilder();
                    url.append(path).append("\\").append(fileName);
                    String sql = "insert into soft_cert (sw_name,sw_desc,sw_type,sw_public,sw_organization,sw_version,sw_time,sw_size,sw_url,sw_hash,sw_register,sw_image) values (?,?,?,?,?,?,?,?,?,?,?,?)";
                    System.out.println("Filename:"+name+"\nDesc:"+desc+"\nType:"+type+"\nOrg_id:"+org_id+"\nVersion:"+version);
                    int res = simpleJDBC.update(sql,name,desc,type,"1",org_id,version,time,size,url.toString(),sha256Hash,"1",base64String);
                    if(res == 0)
                        return new Result("error", 300, "公有软件" + fileName + "上传数据库失败。");
                    else
                        return new Result("success", 200, "公有软件" + fileName + "上传数据库成功。");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result("success", 100, "传输请求错误，上传失败。");
    }

    public Result SoftwareEdit(Map<String, Object> reqBody){
        //String filename, desc, type, sw_public, org, version, time;
        String hash = (String) reqBody.get("sw_hash");
        String selectSql = "select sw_name FROM soft_cert where sw_hash = ?";
        Object obj = simpleJDBC.selectForOneNode(selectSql,hash);
        if (obj == null)
            return new Result("error", 000, "未找到此软件，hash值错误。");
        String filename = (String) reqBody.get("sw_name");
        String desc = (String) reqBody.get("sw_desc");
        String type = (String) reqBody.get("sw_type");
        String org_id =  String.valueOf(reqBody.get("sw_organization"));
        String version = (String) reqBody.get("sw_version");

        String sql = "UPDATE soft_cert SET sw_name=?,sw_desc=?,sw_type=?,sw_organization=?,sw_version=? WHERE sw_hash=?";
        int res = simpleJDBC.update(sql,filename,desc,type,org_id,version,hash);
        if(res == 0)
            return new Result("error", 300, "软件" + filename + "内容编辑失败。");
        else
            return new Result("success", 200, "软件" + filename + "内容编辑成功。");
    }

    public static String IconToBase64String(File file) {
        String base64String = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAD4ElEQVR4Xu3XW0xbdRwHcDRGE01IfNEHfdVElxiTveiL2YNTN2ccC1sGGG8bojh2TdhAxkmZZHOsc0I3CmWlYEFWGHUttPQCdL1xaVdaSrm1UBDXaSbIbYBAfvr/f7MuS1OyvdEHT/J5OGnO//8953/7Nenf+9f6+jpthqT/AzwqwOrqKtyZmaa5xUW4rLtBfaEgdPp9UNaipvl7i/fdo7W1NYhtL1biB4hMT0PahWJyBYeh2WknlcMCnf19UNryKw1MhEHRbkQILra9WHEDLK+sULleC+2+PpCb9GTyumH09u+ksOggv64SBFUt1XYaQd1lo2DkNmh6uuAf9hVjO0+8ANFxW1pZpnNNDXBJ0wwmXzeNRiZA67HQ6UYpfFSSB2/kHqRXjnwBBcpqFsIB0eeXlpfizovECvD3wjyYvR4a/G0SdB4H3Br309RfEegO+ahIXQXpEgHeKvyWXsraD8+l76aTChmM34mAxd9PswsLsGGAwclJ+F5VRxqXAyo6miB8d4qGI2NgHXKTcF0K0QBvC4fo5W/S4Kl9H9Lzn6SC0mIGob6GApMTkLgB9O5eaLRb6YpJDUVqGQSmQmTyO6HaqqXjyouQIv4OtuZ/TS9m7oMnU3dSUsr7kFMhAT4P2m65IG4AvttVm9tA3WWnvHopZMvPg97rpFprCxQ0llNamQDbREfh1WOfUfKnKfDEnh309N5dIDPqQGlpZ20bIEEDsOXRzDrmGh1WOqoog4yyIlDc1NF5bR0ckJ6ld4TD8PqJL+GFg3vp2bSPYcvhr+iKTgP11g5odtromu0mrCVkAL5B+MNh8I6zmR7oh/0/ngGNy0lKmwkKG+T0rigXtotOQaZEzDrqBEW7gV7LyYTs8lLwjYeobywIcQPwG+9YCGQG3YMVUWXWg314gC5oVOAY8lPqDyLYXSzArjOFtFN0Gt4T8mnLkSyoYuPPiRp+pok//4C4cyAhAtydnYXjsnKq7TBBhvgcbBfyqNKoB294jK6y1cIVKOVwSCqhbQW5kJyxh7IkF8Hs84Cczf6Z+TnYMED0sLAF/HSWHURci6sH0llB8uaxbNh6IodqWNHBJbN9n0tK+YCeYcuOO/CTmLS9XVDMdlWOv9gjD6NNDxDFl6RjcABa3d3AiwpednG8PBPfaIIGNuu5zy+VUKlWDb+w++gxbPC4gW90D/eR2AEe1jMyBBWGVippVsE1u4U0vU64zgpUjq+Yy60auMrKt97REeAvw8W2+9gBouM2PTdHzqEAqOx8R7NAZVsr1HQYyR0cgRlW1MQb73gSP8BGoh1E/7jE/v64HgTYrGvTA/wHKxmKe2fuz0gAAAAASUVORK5CYII=";
        // 通过awt.shellFolder获取图标 默认为32 *32(这个办法在使用过程中会报错java: 程序包sun.awt.shell不存在，只能通过另一种方法获取小图标，再把小图标变成32*32的大图标
//            sun.awt.shell.ShellFolder shellFolder = sun.awt.shell.ShellFolder.getShellFolder(file);
//            ImageIcon icon = new ImageIcon(shellFolder.getIcon(true));
        Image icon = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
        //获取小图标后转化为大图标
        int width = 32;
        int height = 32;
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(icon,0,0,width,height,null);
        g.dispose();
        try {
            //图片转base64
            //bi = ImageIO.read(file) ;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bi,"png",outputStream);
            byte[] bytes = outputStream.toByteArray();
            base64String = Base64.getEncoder().encodeToString(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }
        if (base64String.equals("iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAD4ElEQVR4Xu3XW0xbdRwHcDRGE01IfNEHfdVElxiTveiL2YNTN2ccC1sGGG8bojh2TdhAxkmZZHOsc0I3CmWlYEFWGHUttPQCdL1xaVdaSrm1UBDXaSbIbYBAfvr/f7MuS1OyvdEHT/J5OGnO//8953/7Nenf+9f6+jpthqT/AzwqwOrqKtyZmaa5xUW4rLtBfaEgdPp9UNaipvl7i/fdo7W1NYhtL1biB4hMT0PahWJyBYeh2WknlcMCnf19UNryKw1MhEHRbkQILra9WHEDLK+sULleC+2+PpCb9GTyumH09u+ksOggv64SBFUt1XYaQd1lo2DkNmh6uuAf9hVjO0+8ANFxW1pZpnNNDXBJ0wwmXzeNRiZA67HQ6UYpfFSSB2/kHqRXjnwBBcpqFsIB0eeXlpfizovECvD3wjyYvR4a/G0SdB4H3Br309RfEegO+ahIXQXpEgHeKvyWXsraD8+l76aTChmM34mAxd9PswsLsGGAwclJ+F5VRxqXAyo6miB8d4qGI2NgHXKTcF0K0QBvC4fo5W/S4Kl9H9Lzn6SC0mIGob6GApMTkLgB9O5eaLRb6YpJDUVqGQSmQmTyO6HaqqXjyouQIv4OtuZ/TS9m7oMnU3dSUsr7kFMhAT4P2m65IG4AvttVm9tA3WWnvHopZMvPg97rpFprCxQ0llNamQDbREfh1WOfUfKnKfDEnh309N5dIDPqQGlpZ20bIEEDsOXRzDrmGh1WOqoog4yyIlDc1NF5bR0ckJ6ld4TD8PqJL+GFg3vp2bSPYcvhr+iKTgP11g5odtromu0mrCVkAL5B+MNh8I6zmR7oh/0/ngGNy0lKmwkKG+T0rigXtotOQaZEzDrqBEW7gV7LyYTs8lLwjYeobywIcQPwG+9YCGQG3YMVUWXWg314gC5oVOAY8lPqDyLYXSzArjOFtFN0Gt4T8mnLkSyoYuPPiRp+pok//4C4cyAhAtydnYXjsnKq7TBBhvgcbBfyqNKoB294jK6y1cIVKOVwSCqhbQW5kJyxh7IkF8Hs84Cczf6Z+TnYMED0sLAF/HSWHURci6sH0llB8uaxbNh6IodqWNHBJbN9n0tK+YCeYcuOO/CTmLS9XVDMdlWOv9gjD6NNDxDFl6RjcABa3d3AiwpednG8PBPfaIIGNuu5zy+VUKlWDb+w++gxbPC4gW90D/eR2AEe1jMyBBWGVippVsE1u4U0vU64zgpUjq+Yy60auMrKt97REeAvw8W2+9gBouM2PTdHzqEAqOx8R7NAZVsr1HQYyR0cgRlW1MQb73gSP8BGoh1E/7jE/v64HgTYrGvTA/wHKxmKe2fuz0gAAAAASUVORK5CYII="))
        {
            System.out.println("base64String生成失敗，使用初始图标。");
        }
        //        BufferedImage bufferedImage;
//        try {
//        //图片转base64
//            bufferedImage = ImageIO.read(f) ;
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage,"png",outputStream);
//            byte[] bytes = outputStream.toByteArray();
//            base64String = Base64.getEncoder().encodeToString(bytes);
//            System.out.println(base64String);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        return base64String;
    }
    public String sizeRecalculation(long size) {
        int GB = 1024 * 1024 * 1024;    //GB
        int MB = 1024 * 1024;   //MB
        int KB = 1024;  //KB
        long TB = 1024L * 1024L * 1024L * 1024L;    //TB
        DecimalFormat df = new DecimalFormat("0.00");   //格式化小数
        String resultSize = "";
        if (size / TB >= 1) {
            resultSize = df.format(size / (float) TB) + "TB";
        } else if (size / GB >= 1) {
            resultSize = df.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            resultSize = df.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            resultSize = df.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B";
        }
        return resultSize;
    }

}


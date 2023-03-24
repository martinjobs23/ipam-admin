package com.ceit.desktop.service;

import com.ceit.desktop.grpc.client.WebToControlClient;
import com.ceit.desktop.grpc.controlCenter.*;
import com.ceit.desktop.util.Hash;
import com.ceit.desktop.util.SoftwareDetail;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Component;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.response.Result;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import sun.awt.shell.ShellFolder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
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
            com.ceit.admin.common.utils.FileUtil.download(fileFatherPath, soft_name, response);
            return new Result("下载成功",200,"success");
        } else {
            return new Result("软件不存在",200,"error");
        }

    }

    //软件上传
    public Result softwareRegister(Map<String, Object> reqBody, HttpServletRequest request) {
        // 使用FileItem工场类创建相应工场对象
        FileItemFactory factory = new DiskFileItemFactory();
        // 创建servlet文件上传对象并将指定工场对象传入
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        //中文文件名处理的代码
        fileUpload.setHeaderEncoding("utf-8");

        // 声明文件集合
        List<FileItem> parseRequest = null;
        //设置文件上传保存文件路径：保存在项目运行目录下的uploadFile文件夹
        String path = System.getProperty("softupload.path");
        try {
            // 使用servlet文件上传对象解析请求返回文件集合
            parseRequest = fileUpload.parseRequest(request);
            // 遍历文件对象集合 获取数据
            for (FileItem fileItem : parseRequest) {
                // 判断数据类型是不是普通的form表单字段
                if (!fileItem.isFormField()) {
                    // 获取上传文件的文件名
                    String fileName = fileItem.getName();
                    String size = String.valueOf(fileItem.getSize());
                    // 使用上传文件创建输入流
                    InputStream fileStream = fileItem.getInputStream();
                    String file_size = String.valueOf(fileStream.available());


                    //计算软件hash值String md5Hash = hash.md5HashCode32(fileStream);
                    String md5Hash = hash.sha256HashCode32(fileStream);
                    //System.out.println("md5Hash: " + md5Hash);

                    WebToControlClient webToControlClient = new WebToControlClient();
                    UploadRespond uploadRespond = webToControlClient.blockingStub.softwareRegister(UploadRequest.newBuilder().setHash(md5Hash).build());
                    System.out.println("code: " + uploadRespond.getCode());
                    System.out.println("data: " + uploadRespond.getData());
                    System.out.println("Msg: " + uploadRespond.getMsg());

                    //判断软件是否已在软件仓库
                    String selectSql = "select sw_name FROM soft_cert where sw_hash = ?";
                    Object o = simpleJDBC.selectForOneNode(selectSql,md5Hash);
                    //o为空时，软件不存在，
                    if (o == null) {
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
                    } else {
                        fileStream.close();
                        return new Result("error",200,"软件已经存在，请上传其它版本软件");
                    }

                    // 文件上传需要写日志
                    softwareUploadToDatabase(reqBody);
                    String  sql = "insert into soft_cert (sw_name,sw_hash,sw_time) values (?,?,?)";
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String upload_time = df.format(System.currentTimeMillis());

                    //文件上传需要写日志
                    Integer res = simpleJDBC.update(sql,fileName,md5Hash,upload_time);
                    if (res != 0) {
                        return new Result("success",200,fileName + "上传成功。");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result("上传失败",200,"error");
    }

    public Result softwareUploadToDatabase(Map<String, Object> reqBody){
//        String filename, desc, type, sw_public, org, version, time;
        String filename = (String) reqBody.get("sw_name");
        String desc = (String) reqBody.get("sw_desc");
        String type = (String) reqBody.get("sw_type");
        String sw_public = (String) reqBody.get("sw_public");
        String org_id = (String) reqBody.get("sw_organization");
        String version = (String) reqBody.get("sw_version");
        Date date = new Date(); //获取当前时间并写入库
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        String sql = "insert into soft_cert (sw_name,sw_desc,sw_type,sw_public,sw_organization,sw_version,sw_time,sw_size,sw_url,sw_hash,sw_register) values (?,?,?,?,?,?,?,?,?,?,?)";
        int res = simpleJDBC.update(sql,filename,desc,type,sw_public,org_id,version,time,"","","","");
        if(res == 0){
            return new Result("error", 300, "写入数据库失败。");
        }
        else
            return new Result("success", 200, "写入数据库成功。");
//        for (int i = 0; i < list.size(); i++) {
//            UploadList uploadList = list.get(i);
//            //hash = uploadList.getHash();  个人以为hash是需要点击上传后才会需要生成的
//            filename = uploadList.getFilename();
//            desc = uploadList.getDesc();
//            size = uploadList.getSize();
//            //url = uploadList.getUrl();    同hash
//            type = uploadList.getType();
//            org = uploadList.getOrg();
//            version = uploadList.getVersion();
//            //register = uploadList.getRegister();  同hash
    }

//    public Result SoftwareRegister(Map<String, Object> reqBody, HttpServletRequest request) throws FileUploadException, IOException {
//        // 使用FileItem工场类创建相应工场对象
//        FileItemFactory factory = new DiskFileItemFactory();
//        // 创建servlet文件上传对象并将指定工场对象传入
//        ServletFileUpload fileUpload = new ServletFileUpload(factory);
//        //中文文件名处理的代码
//        fileUpload.setHeaderEncoding("utf-8");
//
//        List<FileItem> parseRequest = null;
//        //设置文件上传保存文件路径：保存在项目运行目录下的uploadFile文件夹
//        String path = System.getProperty("softupload.path");
//
//        parseRequest = fileUpload.parseRequest(request);
//        // 遍历文件对象集合 获取数据
//        try {
//            for (FileItem fileItem : parseRequest){
//                if (!fileItem.isFormField()) {
//                // 获取上传文件的文件名
//                    String fileName = fileItem.getName();
//                // 使用上传文件创建输入流
//                    InputStream fileStream = fileItem.getInputStream();
//                    String file_size = String.valueOf(fileStream.available());
//
//                //计算软件hash值String md5Hash = hash.md5HashCode32(fileStream);
//                    String md5Hash = hash.sha256HashCode32(fileStream);
//                    //请求管控中心软件注册
//
//                }
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
    public static void getIcon2() throws FileNotFoundException {
        File file = new File("D:\\Program Files\\TencentDocs\\TencentDocs.exe");
    // 图标保存地址OutputStream inStream = new FileOutputStream(new File("D:\\TencentDocs.exe_64(2).png"));
        try {
        // 通过awt.shellFolder获取图标 默认为32 *32
        ShellFolder shellFolder = ShellFolder.getShellFolder(file);
        ImageIcon icon = new ImageIcon(shellFolder.getIcon(true));

        int width = 32;
        int height = 32;
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(icon.getImage(),0,0,width,height,null);
        g.dispose();
        File f = new File("D:\\TencentDocs.exe_64(2).png");
        try {
            ImageIO.write(bi, "png", f);
        } catch (IOException e) {
            //log.error("写png文件失败",e);
        }
//            BufferedImage imgIcon = (BufferedImage) icon.getImage();
//            // 调整icon图标大小，放大后会模糊
//            imgIcon = resize(imgIcon,256,256);
//            ImageIO.write(imgIcon, "png", inStream);
//            inStream.flush();
//            inStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
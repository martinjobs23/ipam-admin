package com.ceit.meeting;

import com.ceit.admin.common.utils.FileUtil;
import com.ceit.ioc.annotations.Autowired;
import com.ceit.ioc.annotations.Controller;
import com.ceit.ioc.annotations.RequestMapping;
import com.ceit.jdbc.SimpleJDBC;
import com.ceit.json.JSON;
import com.ceit.response.Result;
import com.ceit.utils.SqlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
  
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@Controller("/meeting/file")
public class FileController {

    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpleJDBC simpleJDBC;

    private String tableName = "meeting_file";
    private String[] optionNames = { "name", "hash" };

    static String recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    static AtomicInteger maxAtomicSendCount = new AtomicInteger(0);
    static AtomicInteger maxAtomicCleanCount = new AtomicInteger(0);
    
    @RequestMapping("/upload")
    public Result upload(Map<String, Object> reqBody, HttpServletRequest request) {

        String idString = request.getParameter("meeting_id");
        if (idString == null || idString.length() == 0) {
            return new Result("没有会议ID参数", 500, null);
        }

        int meeting_id = Integer.parseInt(idString);

        Map<String,String> fileMap = FileUtil.Upload(request, "meeting");
        if(fileMap ==null)
        {
            return new Result("上传文件失败", 500, null);
        }

        //暂时只处理单个文件修改上传
        String fileIdStr = request.getParameter("id");
        int fileId =0;
        if (fileIdStr!=null && fileIdStr.length() >0) {
            fileId = Integer.parseInt(fileIdStr);
        }

        for (Map.Entry<String,String> item: fileMap.entrySet()) {
            // 写入数据库
            String filename = item.getKey();
            String savename = item.getValue();
            File f1 = new File(savename);

            String sql = "INSERT INTO meeting_file (meeting_id, name, `size`, hash, `path`, upload_time) VALUES(?,?,?,?,?,?);";
            //如果指定ID，修改
            if(fileId>0)
                sql = "update meeting_file set meeting_id=?, name=?, `size`=?, hash=?, `path`=?, upload_time=? where id="+fileId;
             
            int count = simpleJDBC.update(sql,
                    meeting_id,
                    filename,
                    f1.length(),
                    FileUtil.GetFileMD5(f1),
                    savename,
                    LocalDateTime.now());
            if (count <= 0) {
                f1.delete();
                return new Result("上传失败:写数据库错误", 200, filename);
            }
        }

        //会议文件有变化，更新缓存时间
        FileController.recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 不要返回saveFileName，有路径特殊字符
        return new Result("上传成功", 200, null);
    }
  
    @RequestMapping("/list")
    public String list(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setSearchFields("meeting_id");
                
                        //查询所有文件，判断是否存在
        List<Map<String, Object>> mapList = sqlUtil.selectForList();
        for (Map<String, Object> row : mapList) {
            String filepath = (String)row.get("path");
            if(filepath!=null && new File(filepath).exists())
                row.put("path","1");
            else
                row.put("path","0");  
        }

        return new JSON().mapList2Json(mapList);
    }

    @RequestMapping("/page")
    public String page(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        sqlUtil.setTable(tableName)
                .setAcceptOptions(optionNames)
                .setSearchFields("meeting_id");

        int count = sqlUtil.selectForTotalCount();
        if(count <=0)
            return "{\"totalCount\":" + 0 + ",\"pageData\":[]}";
         
        //查询所有文件，判断是否存在
        List<Map<String, Object>> mapList = sqlUtil.selectForList();
        for (Map<String, Object> row : mapList) {
            String filepath = (String)row.get("path");
            if(filepath!=null && new File(filepath).exists())
                row.put("path","1");
            else
                row.put("path","0");  
        }
    
        String jsonData  = new JSON().mapList2Json(mapList);

        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";
        return jsonData;
    }

    @RequestMapping("/delete")
    public Result delete(Map<String, Object> reqBody) {
        String str = reqBody.get("id").toString();
        String[] ids = str.split(",");
        int rSet = 0;
        for (int i = 0; i < ids.length; i++) {
            Integer id = Integer.parseInt(ids[i]);
            rSet = simpleJDBC.update("delete from " + tableName + " where id=?", id);
        }
        if (rSet != 0) {

            //会议文件有变化，更新缓存时间
            FileController.recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            return new Result("删除成功", 200, "success");
        }
        return new Result("删除失败", 200, "error");
    }

    public static int GetMaxCount()
    {
        return maxAtomicSendCount.get();
    }

    //获取一个最大值+1
    private int GetNewMaxCount()
    {
                //获取最大的次数
                int maxCount =1;
                if(maxAtomicSendCount.get()==0)
                {
                    maxCount =1;         
                    String sql = "select max(send_count) A,max(clean_count) B from meeting_info;";
                    List<Map<String, Object>> countList = simpleJDBC.selectForList(sql);
                    if(countList!=null) 
                    {
                        int maxSendCount=(int)countList.get(0).get("A");
                        int maxCleanCount=(int)countList.get(0).get("B");
                        maxCount = maxSendCount>maxCleanCount?maxSendCount+1:maxCleanCount+1;
                    }
                    int old = maxAtomicSendCount.getAndSet((int)maxCount);
                    if(old>(int)maxCount)
                    {
                        maxAtomicSendCount.set(old+1);
                        maxCount = old+1;
                    }

                    //重新计算
                    recountTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                       
                }
                else {
                    maxCount = maxAtomicSendCount.addAndGet(1);
                }

                return maxCount;
    }

    // 分发会议资料
    @RequestMapping("/send")
    public Result send(Map<String, Object> reqBody) {

        Object idObj = reqBody.get("meeting_id");
        if (idObj == null || idObj.toString().length() == 0) {
            return new Result("没有会议ID参数", 500, null);
        }

        int meeting_id = Integer.parseInt(idObj.toString());
        String str = reqBody.get("pad_id").toString();
        String[] ids = str.split(",");

        int rSet = 0;
        int ret = 0;
        String sql;

        // 获取会议所有的文件
        sql = "select id from meeting_file where meeting_id=?";
        List<Map<String, Object>> fileList = simpleJDBC.selectForList(sql, meeting_id);

        if(fileList==null || fileList.size() ==0)
            return new Result("该会议没有资料可以分发", 200, 0);

        //获取最大的次数
        int maxCount = GetNewMaxCount();

        for (Map<String, Object> fileMap : fileList) {

            Object file_id = fileMap.get("id");

            sql = "select size from meeting_file where id=?";
            Object file_size = simpleJDBC.selectForOneNode(sql, file_id);

            for (String pad_id : ids) {

                // 如果已经分发过，修改文件的分发次数和时间
                sql = "SELECT file_id from meeting_pad_file WHERE pad_id=? and file_id=?";
                Object existed = simpleJDBC.selectForOneNode(sql, pad_id, file_id);

                if (existed != null) {
                    sql = "UPDATE meeting_pad_file SET send_count=?, send_time=NOW(), meeting_id=?, file_size=? where pad_id=? and file_id=?";
                    ret = simpleJDBC.update(sql, maxCount, meeting_id, file_size, pad_id, file_id);
                } else {
                    sql = "INSERT INTO meeting_pad_file "
                            + "(pad_id, file_id, send_count, send_time, accepted_count, accept_time, clean_count, clean_time, meeting_id, file_size) "
                            + "VALUES(?, ?, ?, NOW(), 0, null, 0, null,?,?)";
                    ret = simpleJDBC.update(sql, pad_id, file_id, maxCount, meeting_id, file_size);
                }

                if (ret > 0) {
                    rSet += ret;
                }

            }
        }

        //修改会议的分发次数和时间
        sql = "UPDATE meeting_info SET send_count=?, send_time=NOW() where id=?";
        simpleJDBC.update(sql, maxCount, meeting_id);

        if (rSet > 0) {
            return new Result("分发命令发送成功", 200, rSet);
        }
        return new Result("分发命令发送失败", 200, 0);
    }

    //清理会议资料
    @RequestMapping("/clean")
    public Result clean(Map<String, Object> reqBody) {

        Object idObj = reqBody.get("meeting_id");
        if (idObj == null || idObj.toString().length() == 0) {
            return new Result("没有会议ID参数", 500, null);
        }
 
        int rSet = 0;
        String sql;

        //获取最大的次数
        int maxCount = GetNewMaxCount();

        //获取会议所有的文件
        sql = "UPDATE meeting_pad_file SET clean_count=?,clean_time=NOW() where meeting_id in (?)";
        rSet = simpleJDBC.update(sql, maxCount, idObj);

        //修改会议的清理次数和时间
        sql = "UPDATE meeting_info SET clean_count=?, clean_time=NOW() where id in (?)";
        simpleJDBC.update(sql, maxCount, idObj);

        if (rSet > 0) {
            return new Result("清理命令发送成功", 200, rSet);
        }
        else if (rSet == 0) 
            return new Result("会议没有资料可以清理", 200, 0);
        else
            return new Result("清理命令发送失败", 200, 0);
    }

    // 终端报告接收或者清理状态，应该由分发服务器进行调用，需要设计权限控制方式
    @RequestMapping("/report")
    public Result accept(Map<String, Object> reqBody) {

        Object padIdObj = reqBody.get("pad_id");
        Object fileIdObj = reqBody.get("file_id");
        if (padIdObj == null || padIdObj.toString().length() == 0) {
            return new Result("没有pad_id参数", 500, null);
        }

        int rSet = 0;
        int ret = 0;
        String sql;

        // 修改接收状态
        Object accepted_countObj = reqBody.get("accepted_count");
        Object acceptTimeObj = reqBody.get("accept_time");

        if (accepted_countObj != null) {
            if (acceptTimeObj == null)
                acceptTimeObj = LocalDateTime.now();
            sql = "UPDATE meeting_pad_file SET accepted_count=?, accept_time=? where pad_id=? and file_id=?";
            ret = simpleJDBC.update(sql, accepted_countObj, acceptTimeObj, padIdObj, fileIdObj);
            if (ret > 0) {
                rSet += ret;
            }
        }

        // 修改删除状态
        Object deleteObj = reqBody.get("deleted_count");
        Object deleteTimeObj = reqBody.get("deleted_time");

        if (deleteObj != null) {
            if (deleteTimeObj == null)
                deleteTimeObj = LocalDateTime.now();
            sql = "UPDATE meeting_pad_file SET deleted_count=?, deleted_time=? where pad_id=? and file_id=?";
            ret = simpleJDBC.update(sql, deleteObj, deleteTimeObj, padIdObj, fileIdObj);
            if (ret > 0) {
                rSet += ret;
            }
        }

        if (rSet > 0) {
            return new Result("修改状态成功", 200, "success");
        }
        return new Result("修改状态失败", 200, "error");
    }

    // 获取会议终端对应的文件信息，应该由分发服务器进行调用，需要设计权限控制方式
    @RequestMapping("/padfile")
    public Result padfile(Map<String, Object> reqBody) {

        Object padIdObj = reqBody.get("pad_id");
        Object meetingIdObj = reqBody.get("meeting_id");
        if ((padIdObj == null || padIdObj.toString().length() == 0) 
        && (meetingIdObj == null || meetingIdObj.toString().length() == 0) ) {
            return new Result("pad_id和meeting_id都为空", 500, null);
        }

        logger.info("callled /padfile TEST");

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        String jsonData = sqlUtil.setTable("meeting_pad_file")
                .setSearchFields("pad_id","meeting_id")
                .selectForJsonArray();

        return new Result("ok", 200, jsonData);
    }

    @RequestMapping("/padfilepage")
    public String padfilepage(Map<String, Object> reqBody) {

        SqlUtil sqlUtil = new SqlUtil(reqBody);
        sqlUtil.setTable("meeting_pad_file")
                .setAcceptOptions(optionNames)
                .setSearchFields("meeting_id");

        int count = sqlUtil.selectForTotalCount();
        if(count <=0)
            return "{\"totalCount\":" + 0 + ",\"pageData\":[]}";
         
        //查询所有文件，判断是否存在
        List<Map<String, Object>> mapList = sqlUtil.selectForList();
 
    
        String jsonData  = new JSON().mapList2Json(mapList);

        jsonData = "{\"totalCount\":" + count + ",\"pageData\":" + jsonData + "}";
        return jsonData;
    }

    // 文件下载，应该由分发服务器进行调用，需要设计权限控制方式
    @RequestMapping("/download")
    public Result download(Map<String, Object> reqBody, HttpServletRequest request, HttpServletResponse response) {

        Object fileIdObj = reqBody.get("file_id");
        if (fileIdObj == null || fileIdObj.toString().length() == 0) {
            fileIdObj = request.getParameter("file_id");
            if (fileIdObj == null || fileIdObj.toString().length() == 0) 
            {
                response.setHeader("x_error", "500");
                return new Result("没有file_id", 500, null);
            }
        }

        Map<String, Object> map = simpleJDBC.selectForMap("select name,path from meeting_file where id=?", fileIdObj);
        if (map == null) {
            response.setHeader("x_error", "500");
            return new Result("错误的file_id", 500, null);
        }

        if (map.get("name") == null || map.get("path") == null) {
            response.setHeader("x_error", "500");
            return new Result("文件name或path信息错误", 500, null);
        }

        String fileName = map.get("name").toString();
        String filePath = map.get("path").toString();

        File f1 = new File(filePath);
        if (!f1.exists()) {
            response.setHeader("x_error", "500");
            return new Result("文件不存在", 500, null);
        }

        FileInputStream fis = null;
        ServletOutputStream outputStream = null;
        Result result = new Result("下载成功", 200, "ok");
        try {
            response.addHeader("Content-Length", "" + f1.length());
            response.setContentType("application/octet-stream");
            // 3.设置content-disposition响应头控制浏览器以下载的形式打开文件
            // 下载中文文件时，需要注意的地方就是中文文件名要使用
            // URLEncoder.encode方法进行编码(URLEncoder.encode(fileName, "字符编码"))，否则会出现文件名乱码。
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 4.获取要下载的文件输入流
            fis = new FileInputStream(f1);
            // 5.准备数据缓冲区
            int len = 0;
            byte[] buffer = new byte[1024];
            // 6.通过response对象获取OutputStream流
            outputStream = response.getOutputStream();
            // 7.将FileInputStream流写入到buffer缓冲区
            while ((len = fis.read(buffer)) != -1) {
                // 8.使用OutputStream将缓冲区的数据输出到客户端浏览器
                outputStream.write(buffer, 0, len);
            }
            // 关闭流
            outputStream.close();
            fis.close();
            //return null;
        } catch (Exception e) {

            e.printStackTrace();
            response.setHeader("x_error", "500");
            result = new Result("错误：" + e.getMessage(), 500, null);
        }

        try {
            if (outputStream != null)
                outputStream.close();
            if (fis != null)
                fis.close();
        } catch (Exception e) {
        }

        return result;
    }
}

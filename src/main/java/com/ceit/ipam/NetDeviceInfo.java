package com.ceit.ipam;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ceit.ipam.netdeviceparser.CiscoParser;
import com.ceit.ipam.netdeviceparser.H3CParser;
import com.ceit.ipam.netdeviceparser.HuaweiParser;
import com.ceit.ipam.netdeviceparser.NetDeviceParser;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class NetDeviceInfo {
  public int id;
  public String ip;
  public int port;
  public String username;
  public String password;

  public String vendor; // 厂商
  public String model; // 型号
  public String sn; // 序列号
  public String os; // 序列号

  // 网络获取的信息
  public String banner;
  public boolean success;
  public String result;

  // 检测设置
  public String cmd = "";
  // 缓冲区
  byte[] byteBuffer = null;
  int byteBufferSize = 4096;
  int byteBufferLen = 0;

  List<NetDeviceParser> parserList = new ArrayList<>();

  private static Logger logger = LoggerFactory.getLogger(NetDeviceInfo.class);

  public NetDeviceInfo() {
    parserList.add(new H3CParser());
    parserList.add(new HuaweiParser());
    parserList.add(new CiscoParser());
  }

  private void parseCmdResult() {

    if (byteBufferLen == 0) {
      return;
    }

    if (parserList.size() == 0) {
      return;
    }

    Iterator<NetDeviceParser> iterator = parserList.iterator();
    while (iterator.hasNext()) {
      NetDeviceParser parser = iterator.next();
      int ret = parser.parseCommandResult(cmd, byteBuffer, byteBufferLen);
      if (ret < 0) {
        // 删除自己
        iterator.remove();
      }
    }
  }

  private void getFinalResult() {

    if (parserList.size() == 0) {
      return;
    }

    NetDeviceParser finalParser = null;

    // 查找置信度最高的
    Iterator<NetDeviceParser> iterator = parserList.iterator();
    while (iterator.hasNext()) {
      NetDeviceParser parser = iterator.next();
      if (finalParser == null || finalParser.getConfidence() < parser.getConfidence()) {
        finalParser = parser;
      }
    }

    if (finalParser != null && finalParser.getConfidence() > 0) {
      this.model = finalParser.getModel();
      this.os = finalParser.getOs();
      this.vendor = finalParser.getVendor();
      this.sn = finalParser.getSn();

      logger.info("finalParser vendor=" + this.vendor);
      logger.info("finalParser model=" + this.model);
      logger.info("finalParser os=" + this.os);
      logger.info("finalParser sn=" + this.sn);

    }
    else{
      this.success =false;
    }

  }

  private void getCmdResult(String cmd, InputStream in, OutputStream out, int timeoutMs) throws Exception {
    LocalDateTime starTime = LocalDateTime.now();

    byteBufferLen = 0;
    int i = 0;

    if (byteBuffer == null) {
      byteBuffer = new byte[byteBufferSize];
    }

    try {

      this.cmd = cmd;
      if (cmd != null) {
        out.write(cmd.getBytes());
        out.write("\r\n".getBytes());
        out.flush();
      }

      TimeUnit.MILLISECONDS.sleep(500);

      // 读取命令输出
      while (in.available() > 0) {

        i = in.read(byteBuffer, byteBufferLen, byteBufferSize - byteBufferLen);

        if (i <= 0) {

          // 如果已经读了部分数据
          if (byteBufferLen > 0) {
            i = 0;
            break;
          }

          LocalDateTime now = LocalDateTime.now();
          long ms = Duration.between(starTime, now).toMillis();
          if (ms < timeoutMs) {
            TimeUnit.MILLISECONDS.sleep(100);
            continue;
          } else {
            logger.info("getCmdResult " + cmd + " timeout " + timeoutMs);
          }

          break;
        } else {
          TimeUnit.MILLISECONDS.sleep(100);
        }
      }
    } catch (Exception err) {
      logger.error("getCmdResult Error: " + err.getLocalizedMessage());
    }

    byteBufferLen += i;

    // 计时
    LocalDateTime now = LocalDateTime.now();
    long ms = Duration.between(starTime, now).toMillis();
    logger.info("getCmdResult " + cmd + " time: " + ms);

    if (byteBufferLen > 0) {
      String value ="";
      try {
        value = new String(byteBuffer,0, byteBufferLen, "UTF-8");
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      if(cmd ==null) {
        banner =value.trim();
      } else {
        result =value.trim();
      }
    }

  }

  public boolean CheckSsh() {

    logger.info("CheckSSH ip=" + ip + ":" + port + " username=" + username);

    // java.security.InvalidAlgorithmParameterException: DH key size must be
    // multiple of 64, and can only range from 512 to
    // 8192 (inclusive). The specific key size 1026 is not supported
    Security.insertProviderAt(new BouncyCastleProvider(), 1);

    // 执行SSH命令，获取信息
    Session session = null;
    ChannelShell channel = null;
    InputStream in = null;
    OutputStream out = null;

    // Watchdog watchdog = new Watchdog(120000);//2分钟超时

    try {

      JSch jsch = new JSch();
      session = jsch.getSession(username, ip, port);
      session.setPassword(password);

      Properties prop = new Properties();
      // File file = new File(SystemUtils.getUserHome() + "/.ssh/id_rsa");
      // String knownHosts = SystemUtils.getUserHome() +
      // "/.ssh/known_hosts".replace('/', File.separatorChar);
      // jsch.setKnownHosts(knownHosts)
      // jsch.addIdentity(file.getPath())
      // prop.put("PreferredAuthentications", "publickey");
      // prop.put("PreferredAuthentications", "password");

      // 设置StrictHostKeyChecking 代表公钥检查机制，为no表示最不安全的级别（比如不提示一些安全警告）
      prop.put("StrictHostKeyChecking", "no");
      session.setConfig(prop);

      session.connect(10000);
      logger.info("Connected.");

      channel = (ChannelShell) session.openChannel("shell");
      channel.setPty(true);

      // get I/O streams
      in = channel.getInputStream();
      out = channel.getOutputStream();
      // BufferedReader reader = new BufferedReader(new InputStreamReader(in,
      // StandardCharsets.UTF_8));

      // Thread thread = Thread.currentThread();
      // watchdog.addTimeoutObserver(w -> thread.interrupt());

      channel.connect();
      // watchdog.start();
      logger.info("ChannelShell Opened.");

      // 读取banner 最长等待2s，
      getCmdResult(null, in, out, 2000);
      // 解析banner
      parseCmdResult();

      // 是否需要登录

      // 读取数据 最长等待2s，
      getCmdResult("display version", in, out, 2000);
      parseCmdResult();

      // 显示序列号 Huawei
      getCmdResult("display sn", in, out, 2000);
      parseCmdResult();

      // 显示序列号 H3C
      getCmdResult("display device manuinfo", in, out, 2000);
      parseCmdResult();

      // 两分钟超时，无论什么代码，永久运行下去并不是我们期望的结果，
      // 加超时好处多多，至少能防止内存泄漏，也能符合我们的预期，程序结束，相关的命令也结束。
      // 如果程序是前台进程，不能break掉，那么可以使用nohup去启动，或者使用子shell，但外层我们的程序一定要能结束。
      // watchdog.stop();
      channel.disconnect();
      session.disconnect();

      success = true;

      logger.info(this.banner);

    } catch (Exception e) {
      e.printStackTrace();

      success = false;
      result = e.getLocalizedMessage();

    } finally {
      try {
        if (in != null) {
          in.close();
        }
        if (out != null) {
          out.close();
        }
      } catch (Exception e) {
        //
      }

      if (channel != null) {
        channel.disconnect();
      }
      if (session != null) {
        session.disconnect();
      }

      // watchdog.stop();
    }

    getFinalResult();

    if(result==null || result.length()==0)
    {
      result =banner;
    }

    logger.info("success =" + success);
    logger.info("banner =" + banner);
    logger.info("result =" + result);

    return success;
  }
}

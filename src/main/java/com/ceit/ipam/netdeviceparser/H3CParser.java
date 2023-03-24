package com.ceit.ipam.netdeviceparser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H3CParser implements NetDeviceParser {

    private String vendor; // 厂商
    private String model; // 型号
    private String sn; // 序列号
    private String os; // 操作系统

    public String getVendor() {
        return vendor;
    }

    public String getModel() {
        return model;
    }

    public String getSn() {
        return sn;
    }

    public String getOs() {
        return os;
    }

    public int getConfidence() {
        return vendorConfidence;
    }

    private int vendorConfidence = 0;

    private static Logger logger = LoggerFactory.getLogger(H3CParser.class);

    public int parseCommandResult(String cmd, String result) {

        byte[] bytes;
        try {
            bytes = result.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }

        return parseCommandResult(cmd, bytes, bytes.length);

    }

    public int parseCommandResult(String cmd, byte[] byteBuffer, int byteBufferLen) {
        if (byteBufferLen == 0)
            return 0;

        BufferedReader br = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(byteBuffer, 0, byteBufferLen)));

        if (cmd==null || cmd.length() == 0) {
            return parseBanner(br);
        } else if (cmd.equalsIgnoreCase("display version")) {
            return parseCommandResult_Version(br);
        } else if (cmd.equalsIgnoreCase("display sn")) {
            return parseCommandResult_SN(br);
        } else if (cmd.equalsIgnoreCase("display device manuinfo")) {
            return parseCommandResult_SN(br);
        }

        return 0;
    }
    // 明确厂商 1 不确定 0 确定不是 -1
    private int parseBanner(BufferedReader br) {

        int index;
        String line;

        try {
            while ((line = br.readLine()) != null) {

                logger.info(line);

                if (line.contains("H3C")) {
                    vendorConfidence++;
                    continue;
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (vendorConfidence > 0)
            vendor = "H3C";

        logger.info("vendorConfidence=" + vendorConfidence + " vendor=" + vendor);

        return vendorConfidence;
    }

    private int parseCommandResult_Version(BufferedReader br) {
 
        int index;
        String line;

        try {
            while ((line = br.readLine()) != null) {

                logger.info(line);

                if (line.contains("Comware Software") &&
                line.contains("Version")               
                ) {
                    os = line;
                    vendorConfidence++;
                    logger.info("vendorConfidence=" + vendorConfidence + " os=" + os);

                    continue;
                }

                // 下面检查只针对该厂商设备才进行
                if (vendorConfidence > 0) {
                    if (line.contains(" uptime is ") && line.contains(" week") && line.contains(" day")) {
                        index = line.indexOf(" uptime is ");
                        if (index > 0) {
                            vendorConfidence++;
                            model = line.substring(0,index).trim();
                            logger.info("parseResultH3C model=" + model);
                        }
                        continue;
                    }

                    if ( (model ==null || model.length() ==0 )
                         && line.contains(" with ") && line.contains(" Processor") ) {
                        index = line.indexOf(" with ");
                        if (index > 0) {
                            vendorConfidence++;
                            model = line.substring(0,index).trim();
                            logger.info("parseResultH3C model=" + model);
                        }
                        continue;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (vendorConfidence > 0)
            vendor = "H3C";

        return vendorConfidence;
    }

    private int parseCommandResult_SN(BufferedReader br) {

        boolean isH3C = false;

        String find;
        int index;
        String line;

        try {
            while ((line = br.readLine()) != null) {

                logger.info(line);

                if(line.contains("DEVICE SERIAL NUMBER") || line.contains("DEVICE_SERIAL_NUMBER"))
                {
                    index = line.indexOf(":");
                    if (index > 0) {
                        vendorConfidence++;
                        sn = line.substring(index + 1).trim();
                        logger.info("parseResultH3C sn=" + sn);
                    }
                    continue;
                }
 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // banner设置错误或者为空
        if (!"H3C".equals(vendor) && isH3C)
            vendor = "H3C";

        return vendorConfidence;
    }

}

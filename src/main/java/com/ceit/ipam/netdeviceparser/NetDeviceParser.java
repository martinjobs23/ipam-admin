package com.ceit.ipam.netdeviceparser;

//根据返回的结果分析厂商和信息
public interface NetDeviceParser {

    public String getVendor(); 
    public String getModel();
    public String getSn();
    public String getOs();

    //返回置信度 >0 属于该厂商置信度 =0 不确定 <0 不是该厂商的置信度
    public int getConfidence();

    public int parseCommandResult(String cmd, String result);
    public int parseCommandResult(String cmd, byte[] byteBuffer, int byteBufferLen);
}

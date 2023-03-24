package com.ceit.desktop.util;


import java.sql.Date;

public class SoftwareDetail {
    private String hash = null;
    private String filename, desc, size, url, type, org, version, sw_public, register, image;
    String time;
    public SoftwareDetail(){}

    public SoftwareDetail(String filename, String desc, String type, String org, String version, String sw_public) {
        this.filename = filename;
        this.desc = desc;
        this.type = type;
        this.org = org;
        this.version = version;
        this.sw_public = sw_public;
    }

    public SoftwareDetail(String hash, String filename, String desc, String size, String url, String type, String org, String version, String sw_public, String register, String image) {
        this.filename = filename;
        this.desc = desc;
        this.type = type;
        this.org = org;
        this.version = version;
        this.sw_public = sw_public;
        this.hash = hash;
        this.size = size;
        this.url = url;
        this.register = register;
        this.image = image;
    }
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSw_public() {
        return sw_public;
    }

    public void setSw_public(String sw_public) {
        this.sw_public = sw_public;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "SoftwareDetail{" +
                "hash='" + hash + '\'' +
                ", filename='" + filename + '\'' +
                ", desc='" + desc + '\'' +
                ", size='" + size + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", org='" + org + '\'' +
                ", version='" + version + '\'' +
                ", sw_public='" + sw_public + '\'' +
                ", register='" + register + '\'' +
                ", time=" + time +
                '}';
    }
}

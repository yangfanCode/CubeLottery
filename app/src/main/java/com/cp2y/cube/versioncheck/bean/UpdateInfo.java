package com.cp2y.cube.versioncheck.bean;

/**
 * Created by yangfan on 2017/9/23.
 * nrainyseason@163.com
 */

public class UpdateInfo {

    /**
     * buildVersion (integer, optional): 应用构建版本，用于判断是否升级 ,
     * createdAt (string, optional),
     * description (string, optional): 版本描述 ,
     * device (integer, optional): 3=安卓，4=ios ,
     * downloadUrl (string, optional): 下载地址 ,
     * forceUpdate (integer, optional): 是否强制 ,
     * id (string, optional),
     * issueDate (string, optional): 发布时间 ,
     * pkgSize (integer, optional): 包大小 ,
     * product (integer, optional): 1=淘海房 ,
     * releaseVersion (string, optional): 发布版本用于显示 ,
     * updatedAt (string, optional)
     * <p>
     * <p>
     * id : 597aac6230a4e77bcb7c1635
     * updatedAt : 1501211746742
     * createdAt : 1501211746742
     * product :
     * device : 3
     * releaseVersion : 1.0.0
     * buildVersion : 1
     * pkgSize : 12331
     * description : 测试
     * issueDate : 1501211746742
     * downloadUrl : https://sjflsjfslsjlsjfsfs
     * forceUpdate : 1
     * version :
     */

    public String id;
    public long updatedAt;
    public long createdAt;
    public String product;
    public int device;
    public String releaseVersion;
    public int buildVersion;
    public int pkgSize;
    public String description;
    public long issueDate;
    public String downloadUrl;
    public int forceUpdate;
    public String version;
}

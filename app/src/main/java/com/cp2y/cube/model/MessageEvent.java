package com.cp2y.cube.model;

/**
 * Created by yangfan on 2018/1/23.
 */

public class MessageEvent {
    public String message;
    public int position;
    public int type;
    public static final int GUANZHU_RANK = 1;//关注 to 榜单
    public static final int PERSONAL_GUANZHU = 2;//个人主页关注数据更新
    public static final int PERSONAL_QUXIAOGUANZHU = 3;//个人主页关注取消关注数据更新
    public static final int PERSONAL_OPENACT = 4;//个人主页点击头像刷新数据

    public MessageEvent(String message) {
        this.message = message;
    }

    public MessageEvent(int type) {
        this.type = type;
    }

    public MessageEvent(String message, int position) {
        this.message = message;
        this.position = position;
    }
    public MessageEvent(int type,int position) {
        this.type=type;
        this.position = position;
    }
}

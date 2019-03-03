package com.cp2y.cube.model.jpush;

/**
 * Created by yangfan on 2018/1/15.
 */

public enum JpushExtrasType {
    type0("0","号码库"),
    type1("1","开奖详情");

//    INFORMATION_RECOMMEND("INFORMATION_RECOMMEND","资讯消息"),
//    SYSTEM("SYSTEM","系统消息");
//    QA_Answer("QA_Answer", "问题被回答"),
//    QA_Favor("QA_Favor", "问题被收藏"),
//    QA_Like("QA_Like", "回答被点赞"),
//
//    Policy("Policy", "重大政策"),
//    House_Favor_Offline("House_Favor_Offline", "用户收藏房源被出售"),
//    Festival("Festival", "固定节日"),
//    Comment_House("Comment_House", "评论的房源再被评论"),
//    House_Recommend("House_Recommend", "房源推荐"),
//    Information_Recommend("Information_Recommend", "资讯推荐"),
//
//    Broker_Audit("Broker_Audit", "经纪人认证成功"),
//    House_Claim_Offline("House_Claim_Offline", "经纪人认领房源下架"),
//    Broker_Sell("Broker_Sell", "经纪人卖房"),
//
//    User_Attent_Broker("User_Attent_Broker", "用户关注经纪人"),
//    Broker_Attent_User("Broker_Attent_User", "经纪人关注用户"),
//
//    Broker_Claim_Remind("Broker_Claim_Remind", "经纪人5套房源认领已满，再去认领时");


    private String code;
    private String desc;

    JpushExtrasType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public static JpushExtrasType createWithCode(String code) {
        if (code == null) return null;
        for (JpushExtrasType jpushExtrasType : JpushExtrasType.values()) {
            if (jpushExtrasType.code.equals(code)) {
                return jpushExtrasType;
            }
        }
        return null;
    }

}

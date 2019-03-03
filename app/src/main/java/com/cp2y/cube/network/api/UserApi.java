package com.cp2y.cube.network.api;

import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.LoginModel;

import rx.Observable;

/**
 * Created by js on 2016/11/29.
 */
public interface UserApi {

    /**用户登陆**/
    Observable<?> login(String username, String password);
    /**微信登录**/
    Observable<LoginModel> WeChartLogin(String openid, String unionid, String pin, String headUrl,String nikeName,int platform);
    /**手机号登录验证码**/
    Observable<FlagModel> getLoginCode(String mobile, int boundFlag);
    /**手机号登录**/
    Observable<LoginModel> PhoneLogin(String mobile, String code, String pin,int platform);
    /**获取登录定制结果**/
    Observable<CustomModel> getLoginCustom(int userId);
    /**上传登录定制结果**/
    Observable<FlagModel> setLoginCustom(int userId, String lotteryCustoms,int versionID);
}

package com.cp2y.cube.network.provider;

import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.LoginModel;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by js on 2016/11/29.
 */
public interface UserProvider {

    @GET("/user/login")
    Observable<?> login(@FieldMap Map<String, Object> map);
    @GET("/analyseUser/weixinLogin")
    Observable<LoginModel> WeChartLogin(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/sendAppCode")
    Observable<FlagModel> getLoginCode(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/mobileLogin")
    Observable<LoginModel> PhoneLogin(@QueryMap Map<String,Object> map);
    @GET("/custom/getLotteryCustom")
    Observable<CustomModel> getLoginCustom(@QueryMap Map<String,Object> map);
    @GET("/custom/setLotteryCustom")
    Observable<FlagModel> setLoginCustom(@QueryMap Map<String,Object> map);
}

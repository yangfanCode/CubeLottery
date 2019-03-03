package com.cp2y.cube.network.api.Impl;

import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.LoginModel;
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.api.UserApi;
import com.cp2y.cube.network.provider.UserProvider;

import rx.Observable;

/**
 * Created by js on 2016/11/29.
 */
public class UserApiImpl extends ApiHelper implements UserApi {

    @Override
    public Observable<?> login(String username, String password) {
        return getApiService(UserProvider.class).login(getBasicTransBody()).compose(applySchedulers());
    }

    @Override
    public Observable<LoginModel> WeChartLogin(String openid, String unionid, String pin,String headUrl,String nikeName,int platform) {
        return getApiService(UserProvider.class).WeChartLogin(getBasicTransBody().put("openid",openid).put("unionid",unionid).put("pin",pin).put("headUrl",headUrl).put("nikeName",nikeName).put("platform",platform));
    }

    @Override
    public Observable<FlagModel> getLoginCode(String mobile, int boundFlag) {
        return getApiService(UserProvider.class).getLoginCode(getBasicTransBody().put("mobile",mobile).put("boundFlag",boundFlag));
    }

    @Override
    public Observable<LoginModel> PhoneLogin(String mobile, String code, String pin,int platform) {
        return getApiService(UserProvider.class).PhoneLogin(getBasicTransBody().put("mobile",mobile).put("code",code).put("pin",pin).put("platform",platform));
    }

    @Override
    public Observable<CustomModel> getLoginCustom(int userId) {
        return getApiService(UserProvider.class).getLoginCustom(getBasicTransBody().put("userId",userId));
    }

    @Override
    public Observable<FlagModel> setLoginCustom(int userId, String lotteryCustoms,int versionID) {
        return getApiService(UserProvider.class).setLoginCustom(getBasicTransBody().put("userId",userId).put("lotteryCustoms",lotteryCustoms).put("versionID",versionID));
    }

}

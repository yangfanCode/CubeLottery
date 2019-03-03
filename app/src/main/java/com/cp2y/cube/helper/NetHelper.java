package com.cp2y.cube.helper;

import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.api.Impl.LotteryApiImpl;
import com.cp2y.cube.network.api.Impl.UserApiImpl;
import com.cp2y.cube.network.api.LotteryApi;
import com.cp2y.cube.network.api.UserApi;

/**
 * Created by js on 2017/1/3.
 */
public class NetHelper {

    /**彩票相关接口**/
    public static final LotteryApi LOTTERY_API = new LotteryApiImpl();
    /**登录相关接口**/
    public static final UserApi USER_API = new UserApiImpl();
    /**获得绝对路径**/
    public static String getAbsoluteUrl(String path, String... querys) {
        StringBuilder sb = new StringBuilder();
        sb.append(NetConst.dynamicBaseUrl()).append(path);
        for (int i = 0; i < querys.length - 1; i+=2) {
            String key = querys[i];
            String val = querys[i+1];
            if (sb.indexOf("?") > 0) {
                sb.append("&").append(key).append("=").append(val);
            } else {
                sb.append("?").append(key).append("=").append(val);
            }
        }
        return sb.toString();
    }

}

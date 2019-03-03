package com.cp2y.cube.activity;

import android.os.Bundle;

import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LogUtil;
import com.cp2y.cube.utils.LoginSPUtils;

import java.util.Iterator;
import java.util.Map;

public class CustomUploadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //打包上传定制到服务器
    public void upLoadCustom() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Integer,String> Map= CustomLotteryList.getMap();
        if(Map.size()>0){
            int i=0;
            for(Iterator<Map.Entry<Integer,String>> it = Map.entrySet().iterator(); it.hasNext(); i++){
                Map.Entry<Integer,String> entry=it.next();
                int key=entry.getKey();
                stringBuilder.append(i == 0 ? String.valueOf(key) : String.valueOf("," + String.valueOf(key)));
            }
            customLottery = stringBuilder.toString();
        }
        NetHelper.USER_API.setLoginCustom(LoginSPUtils.getInt("id",0), customLottery, NetConst.VERSION_CONTROL_ID).subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if (args.getFlag() == 1) {
                    if(CustomUploadActivity.this instanceof CustomProvinceDetailActivity){//省份定制
                        //如果是省份彩种定制页面点击完成时
                        ((CustomProvinceDetailActivity)CustomUploadActivity.this).setLoginUpLoadEnd();
                    }
                } else {
                    TipsToast.showTips("上传定制结果失败");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("上传定制结果失败");
            }
        });
        updatePushCustom();//更新推送设置
    }

    private void updatePushCustom(){
        NetHelper.LOTTERY_API.updateCustomPush(CommonUtil.getUserId(),0).subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if(args.getFlag()==1) LogUtil.LogE(CustomUploadActivity.class,"updatePushCustom");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
}

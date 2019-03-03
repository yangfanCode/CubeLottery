package com.cp2y.cube.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.PushSetAdpater;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.PushListModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class PushSettingActivity extends BaseActivity implements MyInterface.PushSetClick{

    private ListView lv;
    private PushSetAdpater adpater;
    private List<PushListModel.Detail> list=new ArrayList<>();
    private AVLoadingIndicatorView AVLoading;
    private ImageView netOff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_setting);
        setMainTitle("开奖推送设置");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        initView();
        initNetOff();
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips(getString(R.string.netOff));
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            netOff.setVisibility(View.GONE);
        }
        initData();
    }

    private void initData() {
        NetHelper.LOTTERY_API.pushLotteryList(CommonUtil.getUserId()).subscribe(new SafeOnlyNextSubscriber<PushListModel>(){
            @Override
            public void onNext(PushListModel args) {
                super.onNext(args);
                if(args.flag==1){
                    AVLoading.setVisibility(View.GONE);
                    if(args.list!=null&&args.list.size()>0){
                        list.clear();
                        list.addAll(args.list);
                        adpater.loadData(list);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips(getString(R.string.netOff));
            }
        });
    }

    private void initView() {
        netOff = (ImageView) findViewById(R.id.netOff);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        lv = (ListView) findViewById(R.id.push_setting_lv);
        adpater=new PushSetAdpater(PushSettingActivity.this);
        lv.setAdapter(adpater);
        adpater.setPushSetClick(this);
    }
    private void updateSingle(int position,int type) {
        int pos=position+1;//头布局
        /**第一个可见的位置**/
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lv.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (pos >= firstVisiblePosition && pos <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lv.getChildAt(pos - firstVisiblePosition);
            SwitchButton switchButton= (SwitchButton) view.findViewById(R.id.pushset_switch);
            if (switchButton != null){

            }
                //修改状态
        }
    }
    @Override
    public void pushSetClick(int position,boolean isChecked) {

        NetHelper.LOTTERY_API.setCustomPush(CommonUtil.getUserId(),list.get(position).lotteryId,isChecked?1:0)
                .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                    @Override
                    public void onNext(FlagModel args) {
                        super.onNext(args);
                        if(args.getFlag()==1){
                            TipsToast.showTips(isChecked?"开启开奖推送成功":"关闭开奖推送成功");
                        }
                    }
                });
    }
}

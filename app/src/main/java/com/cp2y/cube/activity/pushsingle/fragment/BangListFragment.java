package com.cp2y.cube.activity.pushsingle.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.pushsingle.PersonalActivity;
import com.cp2y.cube.activity.pushsingle.adapter.RankAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.SubscribeCancleDialog;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.RankModel;
import com.cp2y.cube.network.subscriber.CommonSubscriber;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Collections;
import java.util.List;

/**
 * 榜单
 */
public class BangListFragment extends Fragment {
    private SparseArray<String> lotteryName = new SparseArray<String>() {{
        put(10002, "双色球中奖回报率");
        put(10088, "大乐透中奖回报率");
        put(10001, "福彩3D中奖回报率");
        put(10003, "排列三中奖回报率");
        put(10004, "排列五中奖回报率");
    }};
    private LinearLayout linearLayout;
    private SubscribeCancleDialog dialog = null;
    private AlertDialog alertDialog = null;
    private AVLoadingIndicatorView AVLoading;
    private int[] imgs = {R.mipmap.bd_no2, R.mipmap.bd_no1, R.mipmap.bd_no3};
    private ImageView netOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bang_list, container, false);
        initView(rootView);
        initNetOff();
        initListener();
        return rootView;
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            netOff.setVisibility(View.GONE);
        }
        initData(0);
    }

    private void initListener() {
    }
    //defaultPos 默认展开条目
    public void initData(int defaultPos) {
        NetHelper.LOTTERY_API.rankList(LoginSPUtils.getInt("id", 0), 0).subscribe(new CommonSubscriber<RankModel>() {
            @Override
            public void onNext(RankModel rankModel) {
                super.onNext(rankModel);
                if (rankModel.flag == 1) {
                    linearLayout.removeAllViews();
                    List<Integer> list = rankModel.lotteryList;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) == 10002) {
                            setView(i, list.get(i), rankModel.doubleball,defaultPos);
                        } else if (list.get(i) == 10088) {
                            setView(i, list.get(i), rankModel.lottoball,defaultPos);
                        } else if (list.get(i) == 10001) {
                            setView(i, list.get(i), rankModel.d3ball,defaultPos);
                        } else if (list.get(i) == 10003) {
                            setView(i, list.get(i), rankModel.p3ball,defaultPos);
                        } else if (list.get(i) == 10004) {
                            setView(i, list.get(i), rankModel.p5ball,defaultPos);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                AVLoading.setVisibility(View.GONE);
            }
        });
    }

    private void initView(View rootView) {
        netOff = (ImageView) rootView.findViewById(R.id.netOff);
        AVLoading = (AVLoadingIndicatorView) rootView.findViewById(R.id.AVLoadingIndicator);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.rank_ll);

    }

    //defaultPos 默认展开条目
    private void setView(int i, int lotteryId, List<RankModel.Detail> data,int defaultPos) {
        View child = LayoutInflater.from(getActivity()).inflate(R.layout.item_rank_list, linearLayout, false);
        RelativeLayout titleLayout = (RelativeLayout) child.findViewById(R.id.item_rank_title_layout);
        LinearLayout ll_detail = (LinearLayout) child.findViewById(R.id.rank_detail_ll);//列表布局
        TextView tvTitle = (TextView) child.findViewById(R.id.rank_tvName);
        ImageView ivArrow = (ImageView) child.findViewById(R.id.rank_iv);
        MyListView lv = (MyListView) child.findViewById(R.id.rank_lv);
        tvTitle.setText(lotteryName.get(lotteryId));//标题
        List<RankModel.Detail> head = data.subList(0, 3);
        Collections.swap(head, 0, 1);//交换第一第二位置
        List<RankModel.Detail> body = data.subList(3, data.size());
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.rank_head, lv, false);
        setHead(head, headView);//头部数据
        RankAdapter adapter = new RankAdapter(getActivity(), body);
        adapter.setSubscribePersonal(new MyInterface.SubscribePersonal() {
            @Override
            public void subscribePersonal(int position, int type) {
                if(!CommonUtil.isNetworkConnected(getActivity())){
                    TipsToast.showTips(getString(R.string.netOff));
                    return;
                }
                if (CommonUtil.isLogin()) {
                    if(type==0){//没有互相关注
                        NetHelper.LOTTERY_API.doSubscribeUser(LoginSPUtils.getInt("id", 0),adapter.getItem(position).userId)
                                .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                                    @Override
                                    public void onNext(FlagModel args) {
                                        super.onNext(args);
                                        if(args.getFlag()==1){
                                            RankModel.Detail detail= adapter.getList().get(position);
                                            detail.subscribeType=1-type;//变更状态
                                            adapter.getList().set(position,detail);
                                            updateSingle(position,1-type,lv);
                                            TipsToast.showTips(getString(R.string.sub_success));
                                        }
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                        TipsToast.showTips(getString(R.string.serviceOff));
                                    }
                                });
                    }else{
                        if(dialog==null){
                            dialog=new SubscribeCancleDialog(getActivity());
                        }
                        if(alertDialog==null){
                            alertDialog=dialog.getDialog();
                        }
                        alertDialog.show();
                        dialog.getTvCancle().setOnClickListener((v -> alertDialog.dismiss()));
                        dialog.getTvSubmit().setOnClickListener((v -> {
                            alertDialog.dismiss();
                            NetHelper.LOTTERY_API.cancleSubscribeUser(LoginSPUtils.getInt("id", 0),adapter.getItem(position).userId,adapter.getItem(position).id)
                                    .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                                        @Override
                                        public void onNext(FlagModel args) {
                                            super.onNext(args);
                                            if(args.getFlag()==1){
                                                RankModel.Detail detail= adapter.getList().get(position);
                                                detail.subscribeType=1-type;//变更状态
                                                adapter.getList().set(position,detail);
                                                updateSingle(position,1-type,lv);
                                                TipsToast.showTips(getString(R.string.sub_cancle));
                                            }
                                        }
                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            TipsToast.showTips(getString(R.string.serviceOff));
                                        }
                                    });
                        }));
                    }
                }else{
                    ((BaseActivity)getActivity()).intentLogin();//登录页面
                }

            }
        });
        lv.setAdapter(adapter);
        if (lv.getHeaderViewsCount() == 0) lv.addHeaderView(headView);
        /**默认状态**/
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
        if (i == defaultPos) {
            setTitleMid(tvTitle, layoutParams);
            ll_detail.setVisibility(View.VISIBLE);//默认展示第一条
            ivArrow.setImageResource(R.mipmap.td_shangla);//上下箭头
        } else {
            setTitleLeft(tvTitle, layoutParams);
            ll_detail.setVisibility(View.GONE);//默认展示第一条
            ivArrow.setImageResource(R.mipmap.td_xiala);//上下箭头
        }
        titleLayout.setOnClickListener((v -> {
            boolean isShow = ll_detail.getVisibility() == View.VISIBLE;
            setOtherViewVisible(i, isShow);//展开与收起
        }));
        linearLayout.addView(child);
    }

    //标题居中
    private void setTitleMid(TextView tvTitle, RelativeLayout.LayoutParams layoutParams) {
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setTextColor(ColorUtils.MID_BLUE);//蓝色
    }

    //标题靠左
    private void setTitleLeft(TextView tvTitle, RelativeLayout.LayoutParams layoutParams) {
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setTextColor(ColorUtils.COLOR_555555);//灰色
    }

    private void setOtherViewVisible(int pos, boolean isShow) {
        for (int i = 0, size = linearLayout.getChildCount(); i < size; i++) {
            View child = linearLayout.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) child;
                LinearLayout ll_detail = (LinearLayout) ll.findViewById(R.id.rank_detail_ll);
                TextView tvTitle = (TextView) ll.findViewById(R.id.rank_tvName);
                ImageView ivArrow = (ImageView) ll.findViewById(R.id.rank_iv);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
                if (pos == i && !isShow) {//展开与收起
                    initData(i);//每次展开刷新数据 刷新最新状态
                    ll_detail.setVisibility(View.VISIBLE);//展开
                    setTitleMid(tvTitle, layoutParams);//居中
                    ivArrow.setImageResource(R.mipmap.td_shangla);
                } else {
                    ll_detail.setVisibility(View.GONE);//收起
                    setTitleLeft(tvTitle, layoutParams);//居左
                    ivArrow.setImageResource(R.mipmap.td_xiala);
                }
            }
        }
    }

    //头部数据
    private void setHead(List<RankModel.Detail> data, View headView) {
        LinearLayout rank_head_ll = (LinearLayout) headView.findViewById(R.id.rank_head_ll);
        for (int i = 0; i < rank_head_ll.getChildCount(); i++) {
            View child = rank_head_ll.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) child;
                RankModel.Detail detail = data.get(i);
                SimpleDraweeView ivIcon = (SimpleDraweeView) ll.findViewById(R.id.item_rank_iv);//头像
                ImageView ivIconBg = (ImageView) ll.findViewById(R.id.item_rank_ivBg);//头像背景皇冠
                TextView tvName = (TextView) ll.findViewById(R.id.item_rank_tvName);//名字
                TextView tvRate = (TextView) ll.findViewById(R.id.item_rank_tvRate);//回报率
                TextView tvAttention = (TextView) ll.findViewById(R.id.item_rank_tvAttention);//关注状态
                ivIconBg.setImageResource(imgs[i]);
                ivIcon.setImageURI(detail.headUrl);
                tvName.setText(detail.nickName);
                ivIconBg.setOnClickListener(iconOnClick);
                ivIconBg.setTag(R.id.id,detail.userId);
                ivIcon.setOnClickListener(iconOnClick);
                ivIcon.setTag(R.id.id,detail.userId);
                tvName.setOnClickListener(iconOnClick);
                tvName.setTag(R.id.id,detail.userId);
                tvRate.setText("回报率: "+detail.returnRate);
                tvAttention.setBackgroundResource(detail.subscribeType == 0 ? R.mipmap.xq_guanzhu : R.mipmap.xq_yiguanzhu);
                tvAttention.setVisibility(LoginSPUtils.getInt("id", 0) == detail.userId ? View.GONE : View.VISIBLE);
                tvAttention.setOnClickListener((v -> {
                    if(!CommonUtil.isNetworkConnected(getActivity())){
                        TipsToast.showTips(getString(R.string.netOff));
                        return;
                    }
                    if (CommonUtil.isLogin()) {
                        int type = detail.subscribeType;
                        if (type == 0) {//没有互相关注
                            NetHelper.LOTTERY_API.doSubscribeUser(LoginSPUtils.getInt("id", 0), detail.userId)
                                    .subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
                                        @Override
                                        public void onNext(FlagModel args) {
                                            super.onNext(args);
                                            if (args.getFlag() == 1) {
                                                TipsToast.showTips(getString(R.string.sub_success));
                                                detail.subscribeType = (1 - type);//变更状态
                                                tvAttention.setBackgroundResource(1 - type == 0 ? R.mipmap.xq_guanzhu : R.mipmap.xq_yiguanzhu);
                                            }
                                        }
                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            TipsToast.showTips(getString(R.string.serviceOff));
                                        }
                                    });
                        } else {
                            if (dialog == null) {
                                dialog = new SubscribeCancleDialog(getActivity());
                            }
                            if (alertDialog == null) {
                                alertDialog = dialog.getDialog();
                            }
                            alertDialog.show();
                            dialog.getTvCancle().setOnClickListener((v1 -> alertDialog.dismiss()));
                            dialog.getTvSubmit().setOnClickListener((v1 -> {
                                alertDialog.dismiss();
                                NetHelper.LOTTERY_API.cancleSubscribeUser(LoginSPUtils.getInt("id", 0), detail.userId, 0)
                                        .subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
                                            @Override
                                            public void onNext(FlagModel args) {
                                                super.onNext(args);
                                                if (args.getFlag() == 1) {
                                                    TipsToast.showTips(getString(R.string.sub_cancle));
                                                    detail.subscribeType = (1 - type);//变更状态
                                                    tvAttention.setBackgroundResource(1 - type == 0 ? R.mipmap.xq_guanzhu : R.mipmap.xq_yiguanzhu);
                                                }
                                            }
                                            @Override
                                            public void onError(Throwable e) {
                                                super.onError(e);
                                                TipsToast.showTips(getString(R.string.serviceOff));
                                            }
                                        });
                            }));

                        }
                    }else{
                        ((BaseActivity)getActivity()).intentLogin();//登录页面
                    }
                }));
            }

        }
    }
    private void updateSingle(int position, int type, ListView lv_child) {
        int pos=position+1;//头布局
        /**第一个可见的位置**/
        int firstVisiblePosition = lv_child.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lv_child.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (pos >= firstVisiblePosition && pos <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lv_child.getChildAt(pos - firstVisiblePosition);
            TextView tvAttention= (TextView) view.findViewById(R.id.item_rank_tvAttention);//关注状态
            //头布局占以为 -1
            if (tvAttention != null)
                tvAttention.setBackgroundResource(type==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);
        }
    }

    View.OnClickListener iconOnClick=v -> {
        int userId= (int) v.getTag(R.id.id);
        Intent intent=new Intent(getActivity(), PersonalActivity.class);
        intent.putExtra("otherUserId",userId);
        startActivity(intent);
    };
}

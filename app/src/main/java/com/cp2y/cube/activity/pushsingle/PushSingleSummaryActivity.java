package com.cp2y.cube.activity.pushsingle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.pushsingle.adapter.PushSingleSummaryAdapter;
import com.cp2y.cube.activity.pushsingle.adapter.PushSingleSummaryNumberAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.CommentInputPopWindow;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.custom.NoDoubleClickListener;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.SubscribeCancleDialog;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.CommentModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.PushSingleSummaryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 推单详情
 */
public class PushSingleSummaryActivity extends BaseActivity implements MyInterface.CommentReply {
    private boolean islMaxCount = false;
    private int currentPage = 1, totalPage = 0;
    private ImageView iv_zhuanfa, netOff;
    private ListView lv_child;
    private PullToRefreshListView lv;
    private int pushSinleId = 0;
    private PushSingleSummaryAdapter adapter;
    private AVLoadingIndicatorView AVLoading;
    private TextView tv_count;
    private RelativeLayout count_layout;
    private CommentInputPopWindow popWindow = null;
    private List<CommentModel.Detail> list;//总数据源
    private EditText editText;
    private int position = 0, floorPos = 0;//回复pos
    private SubscribeCancleDialog dialog = null;
    private AlertDialog alertDialog = null;
    private View footView;
    private LinearLayout ll_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_single_summary);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        pushSinleId = getIntent().getIntExtra("pushSingleID", 0);
        initView();
        initNetOff();
        initListener();
    }

    private void initNetOff() {
        if (!CommonUtil.isNetworkConnected(this)) {//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        } else {
            netOff.setVisibility(View.GONE);
        }
        initData();
    }

    private void initListener() {
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (currentPage < totalPage) {
                    reLoadCommentsData();
                } else {
                    ContextHelper.getApplication().runDelay(lv::onRefreshComplete, 200);
//                    if (lv.getFooterViewsCount() == 0) {
//                        lv.addFooterView(LayoutInflater.from(PushSingleSummaryActivity.this).inflate(R.layout.numlibrary_foot, null));
//                    }
                }
            }
        });
        ll_input.setOnClickListener((v -> {
            if(CommonUtil.isLogin()){
                popInputWindow("说说你的见解…", 0, null);
            }else{
                intentLogin();
            }
        }));
    }

    private void popInputWindow(String textHint, int type, PushSingleSummaryModel args) {
        popWindow=null;
        popWindow = new CommentInputPopWindow(PushSingleSummaryActivity.this);
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//输入
        popWindow.setFocusable(true);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        editText = popWindow.getEditText();//输入框
        TextView tvSend = popWindow.getTextViewSend();//发布
        TextView tvCount = popWindow.getTextViewCount();//输入个数
        popWindow.setOnDismissListener(() -> {
            CommonUtil.setBackgroundAlpha(PushSingleSummaryActivity.this, 1.0f);
        });
        editText.setHint(textHint);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int detailLength = s.length();
                tvCount.setText(detailLength + "/150");
                if (detailLength == 149) {
                    islMaxCount = true;
                }
                if (detailLength == 150 && islMaxCount) {
                    TipsToast.showTips("达到输入上限");
                    islMaxCount = false;
                }
            }
        });
        tvSend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if(!CommonUtil.isNetworkConnected(PushSingleSummaryActivity.this)){//断网
                    TipsToast.showTips(getString(R.string.netOff));
                    return;
                }
                if (type == 0) {//回复推单
                    sendComment();
                } else if (type == 1) {//回复楼主
                    sendCommentFloor(1, list.get(position).criticsID, list.get(position).iD);
                } else if (type == 2) {//互相回复
                    sendCommentFloor(2, list.get(position).replyList.get(floorPos).criticsID, list.get(position).replyList.get(floorPos).commentsID);
                } else {//转发推单
                    changeSingle(args);
                }
            }
        });
        CommonUtil.setBackgroundAlpha(PushSingleSummaryActivity.this, 0.5f);
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        popWindow.showAtLocation(lv, Gravity.BOTTOM, 0, 0);
    }

    //关闭输入框
    private void popDismiss() {
        popWindow.dismiss();
        popWindow = null;
    }

    //回复推单 type=0
    private void sendComment() {
        //发布评论
        String content = editText.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            TipsToast.showTips("评论不能为空");
            return;
        }
        AVLoading.setVisibility(View.VISIBLE);
        NetHelper.LOTTERY_API.putComment(pushSinleId, LoginSPUtils.getInt("id", 0), 0, content, 0, 0)
                .subscribe(new SafeOnlyNextSubscriber<CommentModel>() {
                    @Override
                    public void onNext(CommentModel args) {
                        super.onNext(args);
                        tv_count.setText(args.commentsNum);//评论数
                        if (args.flag == 1) {
                            if (args.list != null && args.list.size() > 0) {
                                TipsToast.showTips("评论成功");
                                currentPage = 1;
                                if(lv_child.getFooterViewsCount()>1)lv_child.removeFooterView(footView);
                                lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//解决头布局过长 直接上拉加载问题
                                totalPage = args.pageSize;
                                list.clear();
                                list.addAll(args.list);
                                adapter.loadData(list);
                                editText.setText("");//置空
                            }
                        }else{
                            if(lv_child.getFooterViewsCount()==1){
                                lv_child.addFooterView(footView);//空数据
                                lv.setMode(PullToRefreshBase.Mode.DISABLED);//解决头布局过长 直接上拉加载问题
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);
                        TipsToast.showTips(getString(R.string.serviceOff));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        AVLoading.setVisibility(View.GONE);
                        popDismiss();//关闭输入框
                    }
                });
    }

    //转发推单
    private void changeSingle(PushSingleSummaryModel args) {
        if(!CommonUtil.isNetworkConnected(PushSingleSummaryActivity.this)){
            TipsToast.showTips(getString(R.string.netOff));
            return;
        }
        PushSingleSummaryModel.Detail detail = args.getPushSingle();
        int lotteryId = detail.getLotteryID();
        String issue = detail.getIssue();
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) content = "转发推单";
        AVLoading.setVisibility(View.VISIBLE);
        NetHelper.LOTTERY_API.pushSingle(LoginSPUtils.getInt("id", 0), issue, lotteryId, "", "", content, pushSinleId, 1)
                .subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
                    @Override
                    public void onNext(FlagModel args) {
                        super.onNext(args);
                        AVLoading.setVisibility(View.GONE);
                        TipsToast.showTips(args.getFlag()==1?"转发成功":args.getMessage());
                        if(args.getFlag()==1)ContextHelper.getApplication().runDelay(PushSingleSummaryActivity.this::finish,2000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);
                        TipsToast.showTips(getString(R.string.serviceOff));
                    }
                });
    }

    private void initData() {
        NetHelper.LOTTERY_API.getPushSingleSummary(LoginSPUtils.getInt("id", 0), pushSinleId).subscribe(new SafeOnlyNextSubscriber<PushSingleSummaryModel>() {
            @Override
            public void onNext(PushSingleSummaryModel args) {
                super.onNext(args);
                if (args.getFlag() == 1) {
                    View head = LayoutInflater.from(PushSingleSummaryActivity.this).inflate(R.layout.pushsingle_summary_head, lv, false);
                    initHeadView(args, head);
                    loadCommentsData();//请求评论数据
                    AVLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }

    private void initHeadView(PushSingleSummaryModel args, View head) {
        if (lv_child.getHeaderViewsCount() > 1) lv_child.removeHeaderView(head);//刷新头布局
        SimpleDraweeView iv_user = (SimpleDraweeView) head.findViewById(R.id.pushsingle_summary_icon);
        TextView tv_name = (TextView) head.findViewById(R.id.pushsingle_summary_name);
        TextView tv_fans = (TextView) head.findViewById(R.id.pushsingle_summary_fans);
        TextView tv_attention = (TextView) head.findViewById(R.id.pushsingle_summary_tvAttention);
        LinearLayout ll_rate = (LinearLayout) head.findViewById(R.id.pushsingle_summary_ratell);//回报率布局
        LinearLayout ll_price = (LinearLayout) head.findViewById(R.id.price_layout);//奖金布局
        TextView tv_title = (TextView) head.findViewById(R.id.pushsingle_summary_title);//推单详情内容
        SimpleDraweeView iv_lotteryIcon = (SimpleDraweeView) head.findViewById(R.id.pushsingle_summary_lotteryicon);//彩种icon
        TextView tv_year = (TextView) head.findViewById(R.id.pushsingle_summary_tv_year);//奖期
        TextView tv_time = (TextView) head.findViewById(R.id.pushsingle_summary_tv_time);//时间
        TextView tv_ding = (TextView) head.findViewById(R.id.pushsingle_dummsary_tvDing);//顶字
        TextView tv_dingNum = (TextView) head.findViewById(R.id.pushsingle_dummsary_tvDingNum);//顶个数
        LinearLayout ll_lotteryNum = (LinearLayout) head.findViewById(R.id.pushsingle_summary_ll);//开奖号码布局
        TextView tv_price = (TextView) head.findViewById(R.id.price_text);
        MyListView lv_num = (MyListView) head.findViewById(R.id.pushsingle_summary_lv);//号码
        LinearLayout ll_ding = (LinearLayout) head.findViewById(R.id.pushsingle_dummsary_dingll);//顶
        /**处理数据**/
        PushSingleSummaryModel.User user = args.getUser();
        iv_user.setImageURI(user.getUrl());//头像
        iv_user.setOnClickListener((v -> goToPersonal(user.getiD())));
        tv_name.setText(user.getName());
        tv_name.setOnClickListener((v -> goToPersonal(user.getiD())));
        tv_fans.setText("粉丝" + user.getFansNum() + "人");
        tv_attention.setBackgroundResource(user.isAttention() ? R.mipmap.xq_yiguanzhu : R.mipmap.xq_guanzhu);
        tv_attention.setVisibility(user.getiD() == LoginSPUtils.getInt("id", 0) ? View.GONE : View.VISIBLE);//自己隐藏
        PushSingleSummaryModel.Detail detail = args.getPushSingle();
        tv_attention.setOnClickListener((v -> {
            if(!CommonUtil.isNetworkConnected(this)){
                TipsToast.showTips(getString(R.string.netOff));
                return;
            }
            if (CommonUtil.isLogin()) {
                boolean isAttention = user.isAttention();
                if (!isAttention) {//没有互相关注
                        NetHelper.LOTTERY_API.doSubscribeUser(LoginSPUtils.getInt("id", 0), user.getiD())
                                .subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
                                    @Override
                                    public void onNext(FlagModel args) {
                                        super.onNext(args);
                                        if (args.getFlag() == 1) {
                                            TipsToast.showTips(getString(R.string.sub_success));
                                            tv_fans.setText("粉丝" + (Integer.valueOf(user.getFansNum()) + 1) + "人");
                                            tv_attention.setBackgroundResource(!user.isAttention() ? R.mipmap.xq_yiguanzhu : R.mipmap.xq_guanzhu);
                                            user.setAttention(!user.isAttention());
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
                        dialog = new SubscribeCancleDialog(PushSingleSummaryActivity.this);
                    }
                    if (alertDialog == null) {
                        alertDialog = dialog.getDialog();
                    }
                    alertDialog.show();
                    dialog.getTvCancle().setOnClickListener((v1 -> alertDialog.dismiss()));
                    dialog.getTvSubmit().setOnClickListener((v1 -> {
                        alertDialog.dismiss();
                        NetHelper.LOTTERY_API.cancleSubscribeUser(LoginSPUtils.getInt("id", 0), user.getiD(), 0)
                                .subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
                                    @Override
                                    public void onNext(FlagModel args) {
                                        super.onNext(args);
                                        if (args.getFlag() == 1) {
                                            TipsToast.showTips(getString(R.string.sub_cancle));
                                            tv_fans.setText("粉丝" + (Integer.valueOf(user.getFansNum()) - 1) + "人");
                                            tv_attention.setBackgroundResource(!user.isAttention() ? R.mipmap.xq_yiguanzhu : R.mipmap.xq_guanzhu);
                                            user.setAttention(!user.isAttention());
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
            } else {
                intentLogin();
            }
        }));
        ll_rate.setVisibility("0.0%".equals(args.getRate()) ? View.GONE : View.VISIBLE);
        if (!"0.0%".equals(args.getRate())) {
            TextView tv_rate = (TextView) head.findViewById(R.id.pushsingle_summary_rate);
            TextView tv_rateText = (TextView) head.findViewById(R.id.pushsingle_summary_tvRate);
            tv_rate.setText(args.getRate());
            tv_rateText.setText(detail.getStatus()==0?"预计回报率":"回报率");
        }
        PushSingleSummaryNumberAdapter numberAdapter = new PushSingleSummaryNumberAdapter(PushSingleSummaryActivity.this, args.getNumberLisbrarylist());
        lv_num.setAdapter(numberAdapter);//号码库显示
        tv_title.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(detail.getTitle())) {//解决textview 空数据占位置
            tv_title.setText(detail.getTitle());
        } else {
            tv_title.setVisibility(View.GONE);
        }
        int lotteryId = detail.getLotteryID();
        iv_lotteryIcon.setImageURI(CommonUtil.concatImgUrl(lotteryId));//彩种图片
        tv_year.setText(detail.getIssue().concat("期"));//奖期
        tv_time.setText(TextUtils.isEmpty(detail.getTime())?"":detail.getTime().concat(" 截止"));//时间
        String price = detail.getPrize();
        head.findViewById(R.id.price_sign).setVisibility(detail.getStatus() != 2?View.GONE:View.VISIBLE); //"￥"隐藏
        tv_price.setText(((detail.getDrawNumber().contains(",")&&detail.getStatus()==0)||"-1".equals(price)) ? "- -" : ("0".equals(price) ? "未中" : String.valueOf(price)));
        //未中时字色变灰改变字号 -1 =>"- -" 0 未中
        tv_price.setTextColor("0".equals(price) ? ColorUtils.GRAY : ColorUtils.NORMAL_RED);
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, price.length()>7?getResources().getDimension(R.dimen.app_tvNomal_size):getResources().getDimension(R.dimen.textSize_16));
        //tv_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, "0".equals(price) ? getResources().getDimension(R.dimen.app_tvNomal_size) : getResources().getDimension(R.dimen.app_tvBig_size));
        String[] nums;//号码数组
        //ll_price.setVisibility(detail.getStatus() == 0 ? View.GONE : View.VISIBLE);//0 未开 1 未中 2中奖
        if (!detail.getDrawNumber().contains(",")) {
            nums = new String[Integer.parseInt(detail.getDrawNumber())];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = "-";
            }
        } else {
            nums = detail.getDrawNumber().split(",");
        }
        int blueIndex = 0;//蓝球位置
        if (lotteryId == 10002) {
            blueIndex = 6;
        } else if (lotteryId == 10088) {
            blueIndex = 5;
        } else{
            blueIndex = 5;//3D 排列3 排列5 5个球红色
        }
        for (int i = 0; i < nums.length; i++) {//开奖号
            TextView tv_ball = (TextView) LayoutInflater.from(PushSingleSummaryActivity.this).inflate(R.layout.openlottery_ball, null).findViewById(R.id.openlottery_ball_tv);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, DisplayUtil.dip2px(3f), 0);//4个参数按顺序分别是左上右下
            tv_ball.setLayoutParams(layoutParams);
            tv_ball.setText(nums[i]);
            tv_ball.setTextColor(i >= blueIndex ? ColorUtils.MID_BLUE : ColorUtils.NORMAL_RED);
            ll_lotteryNum.addView(tv_ball);
        }
        tv_dingNum.setText(args.getPushSingle().getChangeNum());//点赞个数
        if (args.isChange()) {//转发过
            ll_ding.setBackgroundResource(R.mipmap.xq_ding);
            tv_dingNum.setTextColor(ColorUtils.WHITE);
            tv_ding.setTextColor(ColorUtils.WHITE);
        } else {//未转发过
            ll_ding.setBackgroundResource(R.mipmap.xq_weiding);
            tv_dingNum.setTextColor(ColorUtils.COLOR_BEBDBD);
            tv_ding.setTextColor(ColorUtils.NORMAL_RED);
        }
        ll_ding.setOnClickListener((v -> {
            if(CommonUtil.isLogin()){
                popInputWindow("顶！为你转发", 3, args);
            }else{
                intentLogin();
            }
        }));
        if (lv_child.getHeaderViewsCount() == 1) lv_child.addHeaderView(head);
    }

    private void goToPersonal(int id) {
        Intent intent=new Intent(PushSingleSummaryActivity.this, PersonalActivity.class);
        intent.putExtra("otherUserId",id);
        startActivity(intent);
    }


    private void loadCommentsData() {
        currentPage = 1;
        NetHelper.LOTTERY_API.getCommentList(pushSinleId, currentPage, 5)
                .doOnTerminate(() -> lv.onRefreshComplete())
                .subscribe(new SafeOnlyNextSubscriber<CommentModel>() {
                    @Override
                    public void onNext(CommentModel args) {
                        super.onNext(args);
                        tv_count.setText(args.commentsNum);//评论数
                        if (args.flag == 1) {
                            if (args.list != null && args.list.size() > 0) {//有评论
                                if(lv_child.getFooterViewsCount()>1)lv_child.removeFooterView(footView);
                                lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//解决头布局过长 直接上拉加载问题
                                list.clear();
                                list.addAll(args.list);
                                totalPage = args.pageSize;
                                adapter.loadData(list);
                            }
                        }else{
                            if(lv_child.getFooterViewsCount()==1){
                                lv_child.addFooterView(footView);//空数据
                                lv.setMode(PullToRefreshBase.Mode.DISABLED);//解决头布局过长 直接上拉加载问题
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void reLoadCommentsData() {
        NetHelper.LOTTERY_API.getCommentList(pushSinleId, ++currentPage, 5)
                .doOnTerminate(() -> lv.onRefreshComplete())
                .subscribe(new SafeOnlyNextSubscriber<CommentModel>() {
                    @Override
                    public void onNext(CommentModel args) {
                        super.onNext(args);
                        if (args.flag == 1) {
                            if (args.list != null && args.list.size() > 0) {//有评论
                                list.addAll(args.list);
                                adapter.reLoadData(args.list);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void initView() {
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        //iv_zhuanfa = (ImageView) findViewById(R.id.push_single_zhuanfa_iv);
        netOff = (ImageView) findViewById(R.id.netOff);
        lv = (PullToRefreshListView) findViewById(R.id.my_push_single_summary_lv);
        footView=LayoutInflater.from(PushSingleSummaryActivity.this).inflate(R.layout.comment_empty_foot,null);
        count_layout = (RelativeLayout) findViewById(R.id.comment_count_ll);
        ll_input = (LinearLayout) findViewById(R.id.my_push_single_comment_ll);
        tv_count = (TextView) findViewById(R.id.comment_tvCount);
        list = new ArrayList<>();
        lv_child=lv.getRefreshableView();
        adapter = new PushSingleSummaryAdapter(PushSingleSummaryActivity.this);
        lv_child.setAdapter(adapter);
        adapter.setCommentReply(this);//初始化接口
    }

    //回复楼主
    @Override
    public void commentReply(int position) {
        if(CommonUtil.isLogin()){
            this.position = position;
            String nameFloor = list.get(position).name;//楼主名字
            popInputWindow("回复@".concat(nameFloor), 1, null);
        }else{
            intentLogin();
        }
    }

    //回复楼中楼
    @Override
    public void commentFloorReply(int position, int floorPos) {
        if(CommonUtil.isLogin()){
            this.position = position;
            this.floorPos = floorPos;
            String nameFloor = list.get(position).replyList.get(floorPos).criticsName;//回复人名字
            popInputWindow("回复@".concat(nameFloor), 2, null);
        }else{
            intentLogin();
        }
    }

    //回复楼主 互相回复 type=1  2
    private void sendCommentFloor(int type, int byCriticsID, int commentsID) {
        //发布评论
        String content = editText.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            TipsToast.showTips("评论不能为空");
            return;
        }
        AVLoading.setVisibility(View.VISIBLE);
        NetHelper.LOTTERY_API.putComment(pushSinleId, LoginSPUtils.getInt("id", 0), byCriticsID, content, type, commentsID)
                .subscribe(new SafeOnlyNextSubscriber<CommentModel>() {
                    @Override
                    public void onNext(CommentModel args) {
                        super.onNext(args);
                        if (args.flag == 1) {
                            if (args.crList != null && args.crList.size() > 0) {
                                TipsToast.showTips("回复成功");
                                updateSingle(args.crList);//刷新评论
                                editText.setText("");//置空
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);
                        TipsToast.showTips(getString(R.string.serviceOff));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        AVLoading.setVisibility(View.GONE);
                        popDismiss();//关闭输入框
                    }
                });
    }

    /**
     * 局部刷新评论
     */
    private void updateSingle(List<CommentModel.Item> replyList) {
//        /**第一个可见的位置**/
        int firstVisiblePosition = lv_child.getFirstVisiblePosition();
//        /**最后一个可见的位置**/
//        int lastVisiblePosition = lv.getLastVisiblePosition();
//
//        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
//        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
        /**获取指定位置view对象**/
        List<CommentModel.Detail> detail = adapter.getList();//数据源
        detail.get(position).replyList = replyList;//修改数据源
//        }
    }
}

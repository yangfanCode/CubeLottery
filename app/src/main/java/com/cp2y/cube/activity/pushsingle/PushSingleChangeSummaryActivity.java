package com.cp2y.cube.activity.pushsingle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.CommentInputPopWindow;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.SubscribeCancleDialog;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.CommentModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.PushSingleSummaryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wang.avi.AVLoadingIndicatorView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 转发推单详情
 */
public class PushSingleChangeSummaryActivity extends BaseActivity implements MyInterface.CommentReply {
    private boolean islMaxCount = false;
    private int currentPage = 1, totalPage = 0;
    private ImageView iv_zhuanfa, netOff;
    private ListView lv_child;
    private PullToRefreshListView lv;
    private View footView;
    private int pushSinleId = 0;
    private PushSingleSummaryAdapter adapter;
    private AVLoadingIndicatorView AVLoading;
    private TextView  tv_count;
    private RelativeLayout ll_count;
    private LinearLayout ll_input;
    private CommentInputPopWindow popWindow = null;
    private List<CommentModel.Detail> list;//总数据源
    private EditText editText;
    private int position = 0, floorPos = 0;//回复pos
    private SubscribeCancleDialog dialog = null;
    private AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_single_change_summary);
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
                    //refreshLayout.setLoadMore(false);
//                    if (lv.getFooterViewsCount() == 0) {
//                        lv.addFooterView(LayoutInflater.from(PushSingleChangeSummaryActivity.this).inflate(R.layout.numlibrary_foot, null));
//                    }
                }
            }
        });
        ll_input.setOnClickListener((v -> {
            if(CommonUtil.isLogin()){
                popInputWindow("说说你的见解…", 0);
            }else{
                intentLogin();
            }
        }));
    }

    private void popInputWindow(String textHint, int type) {
        popWindow = new CommentInputPopWindow(PushSingleChangeSummaryActivity.this);
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//输入
        popWindow.setFocusable(true);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        editText = popWindow.getEditText();//输入框
        TextView tvSend = popWindow.getTextViewSend();//发布
        TextView tvCount = popWindow.getTextViewCount();//输入个数
        popWindow.setOnDismissListener(() -> {
            CommonUtil.setBackgroundAlpha(PushSingleChangeSummaryActivity.this, 1.0f);
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
        tvSend.setOnClickListener((v -> {
            if(!CommonUtil.isNetworkConnected(PushSingleChangeSummaryActivity.this)){//断网
                TipsToast.showTips(getString(R.string.netOff));
                return;
            }
            if (type == 0) {//回复推单
                sendComment();
            } else if (type == 1) {//回复楼主
                sendCommentFloor(1, list.get(position).criticsID, list.get(position).iD);
            } else {//互相回复
                sendCommentFloor(2, list.get(position).replyList.get(floorPos).criticsID, list.get(position).replyList.get(floorPos).commentsID);
            }
        }));

        CommonUtil.setBackgroundAlpha(PushSingleChangeSummaryActivity.this, 0.5f);
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

    private void initData() {
        NetHelper.LOTTERY_API.getPushSingleSummary(LoginSPUtils.getInt("id", 0), pushSinleId).subscribe(new SafeOnlyNextSubscriber<PushSingleSummaryModel>() {
            @Override
            public void onNext(PushSingleSummaryModel args) {
                super.onNext(args);
                if (args.getFlag() == 1) {
                    int type = 0;//类型
                    List<PushSingleSummaryModel.ChangeDetail> changePushSingle = args.getChangePushSingle();
                    if (changePushSingle != null && changePushSingle.size() > 0) {
                        String rate = args.getChangePushSingle().get(0).getReturnRate();//回报率
                        int status = args.getChangePushSingle().get(0).getStatus();//开奖状态
                        View head = null;
                        if ("0.0%".equals(rate)) {
                            if (status > 0) {//未中
                                head = LayoutInflater.from(PushSingleChangeSummaryActivity.this).inflate(R.layout.pushsingle_change_summary_head2, lv, false);
                            } else {//彩种icon
                                type = 1;
                                head = LayoutInflater.from(PushSingleChangeSummaryActivity.this).inflate(R.layout.pushsingle_change_summary_head, lv, false);
                            }
                        } else {//回报率
                            type = 2;
                            head = LayoutInflater.from(PushSingleChangeSummaryActivity.this).inflate(R.layout.pushsingle_change_summary_head1, lv, false);
                        }
                        if (head != null)
                            initHeadView(args, changePushSingle, head, type, status, rate);
                    }
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

    private void initHeadView(PushSingleSummaryModel args, List<PushSingleSummaryModel.ChangeDetail> changePushSingle, View head, int type, int status, String rate) {
        if (lv_child.getHeaderViewsCount() > 1) lv_child.removeHeaderView(head);//刷新头布局
        SimpleDraweeView iv_user = (SimpleDraweeView) head.findViewById(R.id.pushsingle_summary_icon);
        LinearLayout ll_content= (LinearLayout) head.findViewById(R.id.my_push_single_layout);//灰色内容区
        TextView tv_name = (TextView) head.findViewById(R.id.pushsingle_summary_name);
        TextView tv_fans = (TextView) head.findViewById(R.id.pushsingle_summary_fans);
        TextView tv_attention = (TextView) head.findViewById(R.id.pushsingle_summary_tvAttention);
        TextView tv_title = (TextView) head.findViewById(R.id.pushsingle_change_summary_tvTitle);//推单详情内容
        TextView tv_lotteryName = (TextView) head.findViewById(R.id.my_push_single_lotteryName);
        TextView tv_date = (TextView) head.findViewById(R.id.my_push_single_lotteryDate);
        FlowLayout layout = (FlowLayout) head.findViewById(R.id.my_push_single_flowLayout);
        /**处理数据**/
        PushSingleSummaryModel.User user = args.getUser();
        iv_user.setImageURI(user.getUrl());//头像
        iv_user.setOnClickListener((v -> goToPersonal(user.getiD())));
        tv_name.setText(user.getName());
        tv_name.setOnClickListener((v -> goToPersonal(user.getiD())));
        tv_fans.setText("粉丝" + user.getFansNum() + "人");
        tv_attention.setBackgroundResource(user.isAttention() ? R.mipmap.xq_yiguanzhu : R.mipmap.xq_guanzhu);
        PushSingleSummaryModel.ChangeDetail changeDetail = changePushSingle.get(0);
        ll_content.setOnClickListener((v -> {//跳转详情
            Bundle bundle=new Bundle();
            bundle.putInt("pushSingleID",changeDetail.getForwardingID());
            CommonUtil.openActicity(PushSingleChangeSummaryActivity.this,PushSingleSummaryActivity.class,bundle);
        }));
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
                        dialog = new SubscribeCancleDialog(PushSingleChangeSummaryActivity.this);
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
        if (type == 1) {
            SimpleDraweeView icon = (SimpleDraweeView) head.findViewById(R.id.my_push_single_icon);
            icon.setImageURI(CommonUtil.concatImgUrl(changeDetail.getLotteryID()));//icon
        } else if (type == 2) {
            TextView tv_rate = (TextView) head.findViewById(R.id.my_push_single_rate);
            TextView tv_rateTitle = (TextView) head.findViewById(R.id.my_push_single_type);
            if (status > 0) {//开奖
                //回报率
                tv_rateTitle.setText("回报率");
            } else {//未开奖
                //预计回报率
                tv_rateTitle.setText("预计回报率");
            }
            tv_rate.setText(rate);
        } else if (type == 0) {
            TextView tv_rateTitle = (TextView) head.findViewById(R.id.my_push_single_rate);
            tv_rateTitle.setText("未中");
        }
        tv_title.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(changeDetail.getTitle())) {//解决textview 空数据占位置
            tv_title.setText(changeDetail.getTitle());
        } else {
            tv_title.setVisibility(View.GONE);
        }
        TextView tv_changeName = (TextView) head.findViewById(R.id.push_single_change_tvName);
        TextView tv_changeTitle = (TextView) head.findViewById(R.id.push_single_change_tvTitle);
        tv_changeName.setText(changeDetail.getByUserName());//转发名字
        tv_lotteryName.setText(changeDetail.getLotteryName());//彩种名
        tv_date.setText(changeDetail.getIssue().concat("期"));//奖期
        tv_changeTitle.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(changeDetail.getByTitle())) {
            tv_changeTitle.setText(changeDetail.getByTitle());//转发内容
        } else {
            tv_changeTitle.setVisibility(View.GONE);
        }
        addFlowLayout(layout, position, changePushSingle);//注数子项
        if (lv_child.getHeaderViewsCount() == 1) lv_child.addHeaderView(head);
    }

    private void addFlowLayout(FlowLayout layout, int position, List<PushSingleSummaryModel.ChangeDetail> changePushSingle) {
        layout.removeAllViews();//清空数据
        List<PushSingleSummaryModel.Item> items = changePushSingle.get(0).getNnList();//子项
        for (int i = 0; i < items.size(); i++) {
            TextView item = (TextView) LayoutInflater.from(PushSingleChangeSummaryActivity.this).inflate(R.layout.my_pushsingle_tv, layout, false);
            item.setText(items.get(i).getName().concat(items.get(i).getNote().concat("注")));
            setFlowLayoutMargin(item);//边距
            layout.addView(item);
        }
    }

    /**
     * 设置流式布局右下边距
     *
     * @param layout
     */
    public void setFlowLayoutMargin(View layout) {
        FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) layout.getLayoutParams();
        lp.bottomMargin = DisplayUtil.dip2px(10f);
        lp.rightMargin = DisplayUtil.dip2px(5f);
        layout.setLayoutParams(lp);//设置边距
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
        lv.setMode(PullToRefreshBase.Mode.BOTH);
        footView=LayoutInflater.from(PushSingleChangeSummaryActivity.this).inflate(R.layout.comment_empty_foot,null);
        ll_input = (LinearLayout) findViewById(R.id.my_push_single_comment_ll);
        ll_count = (RelativeLayout) findViewById(R.id.comment_count_ll);
        tv_count = (TextView) findViewById(R.id.comment_tvCount);
        list = new ArrayList<>();
        lv_child=lv.getRefreshableView();
        adapter = new PushSingleSummaryAdapter(PushSingleChangeSummaryActivity.this);
        lv_child.setAdapter(adapter);
        adapter.setCommentReply(this);//初始化接口
    }

    //回复楼主
    @Override
    public void commentReply(int position) {
        if(CommonUtil.isLogin()){
            this.position = position;
            String nameFloor = list.get(position).name;//楼主名字
            popInputWindow("回复@".concat(nameFloor), 1);
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
            popInputWindow("回复@".concat(nameFloor), 2);
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

    private void goToPersonal(int id) {
        Intent intent=new Intent(PushSingleChangeSummaryActivity.this, PersonalActivity.class);
        intent.putExtra("otherUserId",id);
        startActivity(intent);
    }
}

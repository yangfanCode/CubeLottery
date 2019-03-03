package com.cp2y.cube.activity.recognize;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.trends.TrendActivity;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.recognize.adapter.CashDoubleMoneyAdapter;
import com.cp2y.cube.activity.recognize.adapter.CashLottoMoneyAdapter;
import com.cp2y.cube.activity.recognize.adapter.ResultPart0Adapter;
import com.cp2y.cube.activity.recognize.adapter.ResultPart1Adapter;
import com.cp2y.cube.activity.recognize.checklottery.CheckDouble;
import com.cp2y.cube.activity.recognize.checklottery.CheckLotto;
import com.cp2y.cube.activity.recognize.keyword.LotteryKeyWords;
import com.cp2y.cube.activity.recognize.keyword.RegexCheck;
import com.cp2y.cube.activity.recognize.utis.ScanFormatUtils;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.BaiduCheckLotteryModel;
import com.cp2y.cube.model.IssueModel;
import com.cp2y.cube.model.LotteryUploadModel;
import com.cp2y.cube.model.ScanCashNumModel;
import com.cp2y.cube.model.UpLoadScanModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.LotteryService;
import com.cp2y.cube.utils.CloneUtil;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.ReadBase64;
import com.cp2y.cube.utils.ViewUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckLotteryActivity extends CheckNumActivity implements MyInterface.ScanNumEdit,MyInterface.isSavaNumSuccess{
    private int Issue=0,DrawIssue=0;
    private MyListView lv_lotto,lv_double;
    private boolean isDouble=false;
    private boolean isLotto=false;
    private String resul_str="";
    private List<BaiduCheckLotteryModel.lotteryData> list=new ArrayList<>();
    private RelativeLayout switch_layout;
    private Button btn_back_scan;
    private ResultPart0Adapter adapter_double;
    private ResultPart1Adapter adapter_lotto;
    private EditText et_period,et_multiple;
    private TextView tv_pullType;
    private String[] lotteryType={"单式","复式","胆拖","胆拖"};
    private boolean isEnable=true;//是否可点击
    private List<ScanCashNumModel.prize> list_cash=new ArrayList<>();//计算奖金集合
    private MyListView lv_money;//下方奖金listview
    private TextView tv_drawNumber,tv_gotoTrend,tv_drawMoney;//下方开奖号
    private LinearLayout draw_ll,nodraw_ll,cash_money;//下方中奖结果布局
    private CheckLotto check_Lotto;//大乐透校验
    private CheckDouble check_double;//双色球校验
    private CashLottoMoneyAdapter cashadapter;
    private CashDoubleMoneyAdapter cashaDoubledapter;
    private AVLoadingIndicatorView AVLoading;
    private Button cash_or_save;//计算奖金和保存号码库
    private boolean isNewIssue;//是否最新奖期
    private boolean isDrawIssue;//是否最新已开奖奖期
    private long drawTime=0;
    private List<byte[]> checkSame=new ArrayList<>();//检测重复保存集合
    private String image;
    private ScrollView scrollView;
    private List<byte[]> tickets_check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lottery);
        setMainTitle("彩票扫一扫");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        getService(LotteryService.class).isSaveNumSuccess(this);//初始化保存接口
        initView();//初始化布局
        //接收base64
        image=getIntent().getStringExtra("image");
        //initData();//测试数据
        //请求百度接口
        NetHelper.LOTTERY_API.checkLottery("android", "10.10.10.0", "LocateRecognize", "CHN_ENG", "1", image).subscribe(new SafeOnlyNextSubscriber<BaiduCheckLotteryModel>() {
            @Override
            public void onNext(BaiduCheckLotteryModel args) {
                super.onNext(args);
                list=args.getRetData();
                initData();//扫描成功先处理数据
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);//扫描失败显示失败界面
                setFailedView();
            }
        });
    }
    public void initCashMoneyNet(){
        String lotteryId="";
        if(isDouble==true&&isLotto==false){
            lotteryId="10002";
        }else if(isDouble==false&&isLotto==true){
            lotteryId="10088";
        }
        //请求已开奖奖期和最新售卖奖期
        final String finalLotteryId = lotteryId;
        NetHelper.LOTTERY_API.getNewDrawIssue(lotteryId).flatMap(IssueModel->{
            DrawIssue=Integer.valueOf(IssueModel.getIssue());
            return NetHelper.LOTTERY_API.getNewIssue(finalLotteryId);
        }).subscribe(new SafeOnlyNextSubscriber<IssueModel>(){
            @Override
            public void onNext(IssueModel args) {
                super.onNext(args);
                Issue=Integer.valueOf(args.getIssue());
                drawTime=args.getDrawTime();
                //处理双色球大乐透数据
                initResultData();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                setFailedView();
                TipsToast.showNewTips("系统异常，请反馈400-666-7575及时处理");
                startActivity(new Intent(CheckLotteryActivity.this, MainActivity.class));
            }
        });
    }
    //引入失败布局
    private void setFailedView() {
        AVLoading.setVisibility(View.GONE);
        switch_layout.removeAllViews();
        View view_failed= LayoutInflater.from(this).inflate(R.layout.scan_failed,null);
        btn_back_scan = (Button)view_failed. findViewById(R.id.back_scan_btn);
        btn_back_scan.setOnClickListener((v -> finish()));
        //重新设置宽高
        LayoutParams params= new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view_failed.setLayoutParams(params);
        switch_layout.addView(view_failed);
    }

    private void initView() {
        switch_layout = (RelativeLayout) findViewById(R.id.checkNum_layout);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
    }

    //处理数据
    private void initData() {
        try {
            StringBuilder stringBuilder=new StringBuilder();
            for(BaiduCheckLotteryModel.lotteryData data:list){
                stringBuilder.append(data.getWord());
            }
            //彩种识别
            String str=stringBuilder.toString();
            //双色球单式
            //String str="山国福利彩票 CHINA WELFARE LOTTERY83557075689b9be特让码B97AA3A6 DEE 2 8689随机妈销售明2016036奖期2流水号100132站号倍数1倍倍金额10元A:051320283031+O6B:081017223132+16C:O81016182632+01D:O6O912132632+02E:051315172328+14 I I I I I I玩法双色球投注方式单式开奖时间2016-03-31销售时间2016-03-31Thu19:23:03浙江省福利彩票发行中心承销";
            //String str="利彩票 CHINA WELFARE LOTTERY46EE41857542D4068FA2B05F站号3160022017,04.10-18:42:40操作员:1双色球期号:2017041序号:00118单式蓝球A07152025290102B071520252C>020412141722-05开奖日:2017-04-111倍数:001金额:6元站址:新桥路689弄6号NB除出7966F5E0A9DD78A5上海市福利彩票发行中心承销";
            //带号码单式
            //String str="微(福建、贵州、湖南、西藏、吉林)双色球取国福彩票 CHINA WELFARE LOT350105308265-82A9-086-532C5169036/5033R.040722232433-16(1B.040718212932-03(1)B.0C.04千期2014938140408体-040812:41地址福普安区官尾新村B1座5号店DA奖池余额:64542096.00元,037期开奖号妈24-14-2530-061301福建省福利彩票发行中心承销■篇题题三、罐、5题口福中国福彩票 CHINA WELFARE LOTTERY流水";
            //双色球复式
            //String str="国福利彩票 CHINA WELFARE LOTTERY03DE25-094700-74BE59-12020E-96站号33015165特征码EC811BCAD9F26EC7119746流水号100263销售期2016143兑奖期2016143金额8元红单010203040506蓝复0102030415玩法双色球复式倍数2倍开奖时间2016-12-06销售时间2016-12-0619:06:58双色球浙江省福利彩票发行中心承销";
            //双色球胆拖
            //String str="国福利彩票 CHINA WELFARE LOTTERY 7450CF-2EBF9C-8950BA-DF3BFB-2A站号33015165特征码C2B607404A8E1210471779流水号100255销售期2016144兑奖期2016144金额8元蓝球O2 红胆202831红拖O7141627(k数N的玩法双色球胆拖倍数1倍开奖时间2016-12-08 销售时间2016-12-0818:00:23期开奖010203040506+07哈哈哈";

            //大乐透复式
            //String str="体彩·超级大乐透10339-578700-050432-977808911025PM5JLw第17072期共l期03/20开奖1-005130-1010012117/03/2019:29:39复式票1倍合计42元前区07121320252730后区0610德乙推荐03月21日03:15柏林联合S纽伦堡大乐透第17030期奖池:35.90亿嘉绿苑西36幢-06";
            //大乐透胆拖
            //String str="体级大乐透110339537100-017668-9637097603111Jg4Saw第16149期共1期12-042138-1010007717/04/10开奖19:37:15胆拖票1倍合计20元前区胆O3O7前区拖11193135后区拖O10410恭贺31026英超赛事推荐12月20日04:00埃弗顿S利物浦大乐透第l6148期奖池:38.06亿嘉绿苑西36幢-06升t";
            //大乐透单式
            //String str="超级大乐110339-579300-051155-388804992895/yp4W第17032期共」期17/03/22开奖01-005130-1010028317/03/2218:13:381倍合计8元前104+09+10+14+2107+122)06+09+23+28+3408+12③01+11+13+24+3105+08④06+11+21+22+3104+10每日推荐国际赛03月23日03:45德国VS英格兰大乐透第17031期奖池:35.82亿嘉绿苑西36幢-06";
            //String str="超级大乐110339-579300-051155-388804992895/yp4W第17032期共」期17/03/22开奖01-005130-1010028317/03/2218:13:381倍合计8元10+14+17+19+2302+0510+14+17+19+2302+05每日推荐国际赛03月23日03:45德国VS英格兰大乐透第17031期奖池:35.82亿嘉绿苑西36幢-06";
            //新大乐透单式
            //String str="体彩/级第17041期2017年04月12日开奖11033-590500-059463-324106089773 mhd fq单式票追加投注1倍合计12元①08152125340106②0815212534普0210③05082125280106④05082125280210欧冠赛事推荐04月13日02:45拜仁W9皇马大乐透第17040期奖池:36.82亿高技街48-201-005057-1010024017/04/1218:29:18";
            //String str="体彩磁级大第17043期2017年04月17日开奖110339-593500-061327-749101972423329uXA单式票1倍合计10元①03092228320210②1621272832普0211③15242632330811④02051626320311⑤0607142831普0709英超赛事推荐04月18日03:00米堡VS阿森纳大乐透第17042期奖池:36.99亿高技街48-201-005057-1010034017/04/1717:56:02";
            for(String double_data: LotteryKeyWords.DOUBLE_KEYWORDS){
                if(str.contains(double_data)){
                    isDouble=true;
                    disposeDoubleStr(str);//处理字符串格式
                    //TipsToast.showTips("双色球");
                    break;
                }
            }
            if(isDouble==false&&isLotto==false){
                for(String lotto_data:LotteryKeyWords.LOTTO_KEYWORDS){
                    if(str.contains(lotto_data)){
                        isLotto=true;
                        disposeLottoStr(str);//处理字符串格式
                       // TipsToast.showTips("大乐透");
                        break;
                    }
                }
            }
            if(isDouble==false&&isLotto==false){//彩种判断后 非支持彩种情况
                setFailedView();//显示失败界面
                return;
            }
            initCashMoneyNet();//确定彩种后请求期数接口
        } catch (Exception e) {
            e.printStackTrace();
            setFailedView();//显示失败界面
        }
    }
    private void initResultData() {//展示数据
        if(isDouble==true&&isLotto==false){//双色球
            check_double=new CheckDouble(resul_str);
            List<byte[]> check_Double=check_double.getNumber();
            int type=check_double.getType();
            if(!CheckDoubleResult(check_Double,type)){//检查校验结果是否构成一注
                setFailedView();//显示失败界面
                UpLoadPic(0,"10002",image);//上传图片
                return;
            };
            if(check_double.getDate()==Issue){
                TipsToast.showNewTips("您的彩票是双色球"+Issue+"期，将于"+CommonUtil.getDateToString(drawTime)+"21:15分开奖");
            }
            initDoubleView();
            UpLoadPic(1,"10002",image);//上传成功图片
        }else if(isLotto==true&&isDouble==false){//大乐透
            check_Lotto=new CheckLotto(resul_str);
            List<byte[]> check_lotto=check_Lotto.getNumber();
            int type=check_Lotto.getType();
            if(!CheckLottoResult(check_lotto,type)){//检查校验结果是否构成一注
                setFailedView();//显示失败界面
                UpLoadPic(0,"10088",image);//上传图片
                return;
            };
            if(check_Lotto.getDate()==Issue){
                TipsToast.showNewTips("您的彩票是大乐透"+Issue+"期，将于"+CommonUtil.getDateToString(drawTime)+"20:30分开奖");
            }
            initLottoView();
            UpLoadPic(1,"10088",image);//上传成功图片
        }
    }
    //大乐透布局数据
    private void initLottoView() {
        isDrawIssue=check_Lotto.getDate()==0||check_Lotto.getDate()>Issue;//是否展示最新已开奖奖期
        isNewIssue=check_Lotto.getDate()==Issue;//读到的奖期是否为最新
        View view_lotto=LayoutInflater.from(this).inflate(R.layout.scanresult_lotto,null);
        scrollView= (ScrollView) view_lotto.findViewById(R.id.scan_scrollView);
        cash_money= (LinearLayout) view_lotto.findViewById(R.id.scan_cashMoney_ll);
        draw_ll= (LinearLayout) view_lotto.findViewById(R.id.scan_money_Draw_ll);
        nodraw_ll= (LinearLayout) view_lotto.findViewById(R.id.scan_money_noDraw_ll);
        tv_drawNumber= (TextView) view_lotto.findViewById(R.id.scan_result_drawNumber);
        tv_gotoTrend= (TextView) view_lotto.findViewById(R.id.scan_gotoTrend);//去看看走势遗漏
        tv_drawMoney= (TextView) view_lotto.findViewById(R.id.scan_draw_money);//中奖奖金
        lv_money= (MyListView) view_lotto.findViewById(R.id.scan_prize_lv);//奖金listview
        cash_or_save= (Button) view_lotto.findViewById(R.id.lottery_calc_submit);
        initBtnCashSaveListener(false);//计奖按钮监听
        cash_or_save.setText(isNewIssue?"保存号码库":"计算奖金");
        setDataView(view_lotto);//加载布局
        TextView tv_edit= (TextView) view_lotto.findViewById(R.id.lottery_modify_number);
        lv_lotto= (MyListView) view_lotto.findViewById(R.id.scanResult_lv);
        initHeadView(view_lotto);
        String date=isDrawIssue?String.valueOf(DrawIssue):String.valueOf(check_Lotto.getDate());
        et_period.setText(date);//奖期判断
        et_multiple.setText(String.valueOf(check_Lotto.getMultiple()));//倍数
        initHeadListener(false);//奖期倍数监听
        adapter_lotto=new ResultPart1Adapter(this,check_Lotto.getNumber(),check_Lotto.getType());
        adapter_lotto.setScanLottoNumEdit(this);//初始化接口
        tv_pullType.setText(lotteryType[check_Lotto.getType()]+(check_Lotto.isAttach()?"追加":""));//类型
        lv_lotto.setAdapter(adapter_lotto);
        AVLoading.setVisibility(View.GONE);
        cashadapter=new CashLottoMoneyAdapter(CheckLotteryActivity.this);//下方奖期adapter
        lv_money.setAdapter(cashadapter);
        tv_edit.setOnClickListener((v -> {//修改操作
            if(isEnable==true){//修改操作
                adapter_lotto.setEditEndable(isEnable);
                ViewUtils.setViewEnable(isEnable,et_period,et_multiple);
                tv_edit.setText("完成");
            }else{//完成操作
                String periodStr=et_period.getText().toString().trim();
                String multipleStr=et_multiple.getText().toString().trim();
                //数据校验
                if (!AllLastCheck(periodStr, multipleStr)) return;
                //倍数去0
                et_multiple.setText(String.valueOf(Integer.valueOf(multipleStr)));
                //修改完成后切换可点击状态刷新修改数据
                adapter_lotto.setEditEndable(isEnable);
                ViewUtils.setViewEnable(isEnable,et_period,et_multiple);
                tv_edit.setText("修改");
                hintKbOne();//关闭软键盘
            }
            isEnable=!isEnable;
        }));
    }
    //lotto完成按钮和计算奖金按钮校验
    private boolean AllLastCheck(String periodStr, String multipleStr) {
        if((!RegexCheck.isLottoDateCorrect(periodStr))||(!RegexCheck.isLottoDate154(periodStr,Issue))){//最后的奖期校验
            AVLoading.setVisibility(View.GONE);
            TipsToast.showTips("该奖期不存在");
            return false;
        }
        if(!RegexCheck.isLottoMultipleCorrect(multipleStr)|| TextUtils.isEmpty(multipleStr)){
            AVLoading.setVisibility(View.GONE);
            TipsToast.showTips("请输入正确的倍数");//最后的倍数校验
            return false;
        }
        if(RegexCheck.isNewDate(periodStr,Issue)){//是最新奖期
            cash_or_save.setText("保存号码库");//最新奖期变成保存号码库
            cash_money.setVisibility(View.GONE);//隐藏计奖布局
            isNewIssue=true;
        }else{
            cash_or_save.setText("计算奖金");//最新奖期变成保存号码库
            isNewIssue=false;
        }
        List<byte[]> tictks=adapter_lotto.getData();//最后的号码数据校验
        if(!RegexCheck.isLottoNumCorrect(tictks,check_Lotto.getType())){
            AVLoading.setVisibility(View.GONE);
            return false;
        }
        return true;
    }
    //Double完成按钮和计算奖金按钮校验
    private boolean AllDoubleLastCheck(String periodStr, String multipleStr) {
        if((!RegexCheck.isDoubleDateCorrect(periodStr))||(!RegexCheck.isDoubleDate154(periodStr,Issue))){//最后的奖期校验
            AVLoading.setVisibility(View.GONE);
            TipsToast.showTips("该奖期不存在");
            return false;
        }
        if(!RegexCheck.isDoubleMultipleCorrect(multipleStr)|| TextUtils.isEmpty(multipleStr)){
            AVLoading.setVisibility(View.GONE);
            TipsToast.showTips("请输入正确的倍数");//最后的倍数校验
            return false;
        }
        if(RegexCheck.isNewDate(periodStr,Issue)){//是最新奖期
            cash_or_save.setText("保存号码库");//最新奖期变成保存号码库
            cash_money.setVisibility(View.GONE);//隐藏计奖布局
            isNewIssue=true;
        }else{
            cash_or_save.setText("计算奖金");//最新奖期变成保存号码库
            isNewIssue=false;
        }
        List<byte[]> tictks=adapter_double.getData();//最后的号码数据校验
        if(!RegexCheck.isDoubleNumCorrect(tictks,check_double.getType())){
            AVLoading.setVisibility(View.GONE);
            return false;
        }
        return true;
    }
    //处理号码成服务器格式
    private String initNum(List<byte[]> list_lotto,int type) {
        String num="";
        if(type==0){//单式
            num= ScanFormatUtils.bytetoStringSingle(list_lotto);
        }else if(type==1||type==2){//复式
            num= ScanFormatUtils.bytetoStringMutil(list_lotto);
        }else{//胆拖缺
            num= ScanFormatUtils.LottoDantuoQue(list_lotto);
        }
        Log.e("yangfan", "initLottoNum: "+num);
        return num;
    }
    //计算奖金接口
    private void initMoneyNet(String lotteryId,String isuue,int multi,boolean addtional,String numbers,boolean isDouble) {
        NetHelper.LOTTERY_API.scanCashNumber(lotteryId, isuue, multi, addtional, numbers).subscribe(new SafeOnlyNextSubscriber<ScanCashNumModel>(){
            @Override
            public void onNext(ScanCashNumModel args) {
                super.onNext(args);
                AVLoading.setVisibility(View.GONE);//转圈圈消失
                if(args.getFlag()==0){//不存在奖期
                    //如奖期不存在隐藏布局
                    cash_money.setVisibility(View.GONE);
                    TipsToast.showTips("该奖期不存在");
                }else if(args.getFlag()==1){
                    list_cash.clear();
                    list_cash.addAll(args.getDrawDetail().getItems());//获得数据
                    String drawRedNumber=args.getDrawDetail().getDrawRedNumber().replace(","," ");
                    String drawBlueNumber=args.getDrawDetail().getDrawBlueNumber().replace(","," ");
                    tv_drawNumber.setText("开奖号码: ".concat(drawRedNumber).concat(" + ").concat(drawBlueNumber));//展示开奖号
                    if("0.00".equals(args.getDrawDetail().getTotal())){//为未中
                        draw_ll.setVisibility(View.GONE);
                        nodraw_ll.setVisibility(View.VISIBLE);
                        tv_gotoTrend.setOnClickListener((v -> startActivity(new Intent(CheckLotteryActivity.this, TrendActivity.class))));
                    }else{
                        draw_ll.setVisibility(View.VISIBLE);
                        nodraw_ll.setVisibility(View.GONE);
                        tv_drawMoney.setText(args.getDrawDetail().getTotal());
                    }
                    if(!isDouble){//大乐透
                        cashadapter.LoadData(list_cash);//刷新数据
                    }else{//双色球
                        cashaDoubledapter.LoadData(list_cash);//刷新数据
                    }
                    cash_money.setVisibility(View.VISIBLE);//奖期存在显示布局
                    scrollView.fullScroll(View.FOCUS_DOWN);//定位在底部
                }else{
                    TipsToast.showNewTips("您的彩票是"+(isDouble?"双色球":"大乐透")+Issue+"期，将于"+CommonUtil.getDateToString(drawTime)+(isDouble?"21:15分":"20:30")+"开奖");
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);//转圈圈消失
                TipsToast.showTips("系统异常，请反馈400-666-7575及时处理");
            }
        });
    }

    /**
     *计算奖金和保存号码库
     */
    private void initBtnCashSaveListener(boolean isDouble) {
        cash_or_save.setOnClickListener((v -> {
            hintKbOne();//关闭软键盘
            if(!CommonUtil.isNetworkConnected(CheckLotteryActivity.this)){
                TipsToast.showTips("请检查网络设置");
                return;
            }
            if(isNewIssue){//保存号码库操作
                NetHelper.LOTTERY_API.upScanCount(LoginSPUtils.getInt("id",0),isDouble?"10002":"10088","保存号码库").subscribe(new SafeOnlyNextSubscriber<Object>(){
                    @Override
                    public void onNext(Object args) {
                        super.onNext(args);
                    }
                });
                if(isDouble){
                    tickets_check=adapter_double.getData();//获得数据
                }else{
                    tickets_check=adapter_lotto.getData();//获得数据
                }
                if(isSameSave(checkSame,tickets_check)){
                    TipsToast.showTips("已存在号码库");
                    return;
                }
                if(isDouble){//双色球保存
                    if (!AllDoubleLastCheck(et_period.getText().toString().trim(), et_multiple.getText().toString().trim())) return;
                    ScanFormatUtils.ListByteToString(tickets_check,check_double.getType(),0);
                }else{//大乐透保存
                    if (!AllLastCheck(et_period.getText().toString().trim(), et_multiple.getText().toString().trim())) return;
                    ScanFormatUtils.ListByteToString(tickets_check,check_Lotto.getType(),1);
                }
            }else{//计算奖金操作
                AVLoading.setVisibility(View.VISIBLE);
                String periodStr=et_period.getText().toString().trim();
                String multipleStr=et_multiple.getText().toString().trim();
                NetHelper.LOTTERY_API.upScanCount(LoginSPUtils.getInt("id",0),isDouble?"10002":"10088","计算奖金").subscribe(new SafeOnlyNextSubscriber<UpLoadScanModel>(){
                    @Override
                    public void onNext(UpLoadScanModel args) {
                        super.onNext(args);
                    }
                });
                if(isDouble){
                    String number=initNum(adapter_double.getData(),check_double.getType());//处理修改乐透号码
                    //数据校验
                    if (!AllDoubleLastCheck(periodStr, multipleStr)) return;
                    initMoneyNet("10002",periodStr,Integer.valueOf(multipleStr),false,number,true);//请求获取奖金网络处理数据
                    adapter_double.notifyDataSetChanged();
                }else{
                    String number=initNum(adapter_lotto.getData(),check_Lotto.getType());//处理修改乐透号码
                    //数据校验
                    if (!AllLastCheck(periodStr, multipleStr)) return;
                    initMoneyNet("10088",periodStr,Integer.valueOf(multipleStr),check_Lotto.isAttach(),number,false);//请求获取奖金网络处理数据
                    adapter_lotto.notifyDataSetChanged();
                }
                et_multiple.setText(String.valueOf(Integer.valueOf(multipleStr)));//号码去0
            }
        }));
    }

    //奖期倍数监听
    private void initHeadListener(boolean isDouble) {
        et_period.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String etStr=et_period.getText().toString().trim();
                if(isDouble){
                    if((!RegexCheck.isDoubleDateCorrect(etStr)||!RegexCheck.isDoubleDate154(etStr,Issue))&&etStr.length()==7){
                        TipsToast.showTips("该奖期不存在");
                    }
                    if(RegexCheck.isNewDate(etStr,Issue)){//是最新奖期
                        cash_or_save.setText("保存号码库");//最新奖期变成保存号码库
                        cash_money.setVisibility(View.GONE);//隐藏计奖布局
                        isNewIssue=true;
                        TipsToast.showNewTips("您的彩票是双色球"+Issue+"期，将于"+CommonUtil.getDateToString(drawTime)+"21:15分开奖");
                    }else{
                        cash_or_save.setText("计算奖金");//最新奖期变成保存号码库
                        isNewIssue=false;
                    }
                }else{
                    if((!RegexCheck.isLottoDateCorrect(etStr)||!RegexCheck.isLottoDate154(etStr,Issue))&&etStr.length()==5){
                        TipsToast.showTips("该奖期不存在");
                    }
                    if(RegexCheck.isNewDate(etStr,Issue)){//是最新奖期
                        cash_or_save.setText("保存号码库");//最新奖期变成保存号码库
                        cash_money.setVisibility(View.GONE);//隐藏计奖布局
                        isNewIssue=true;
                        TipsToast.showNewTips("您的彩票是大乐透"+Issue+"期，将于"+CommonUtil.getDateToString(drawTime)+"20:30开奖");
                    }else{
                        cash_or_save.setText("计算奖金");//最新奖期变成保存号码库
                        isNewIssue=false;
                    }
                }
            }
        });
        et_multiple.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String etStr=et_multiple.getText().toString().trim();
                if(isDouble){
                    if(!TextUtils.isEmpty(etStr)&&!RegexCheck.isDoubleMultipleCorrect(etStr)){
                        TipsToast.showTips("请输入正确的倍数");
                    }
                }else{
                    if(!TextUtils.isEmpty(etStr)&&!RegexCheck.isLottoMultipleCorrect(etStr)){
                        TipsToast.showTips("请输入正确的倍数");
                    }
                }
            }
        });
    }

    //双色球布局数据
    private void initDoubleView() {
        isDrawIssue=check_double.getDate()==0||check_double.getDate()>Issue;//是否展示最新已开奖奖期
        isNewIssue=check_double.getDate()==Issue;//读到的奖期是否为最新
        View view_double=LayoutInflater.from(this).inflate(R.layout.scanresult_double,null);;
        scrollView= (ScrollView) view_double.findViewById(R.id.scan_scrollView);
        cash_money= (LinearLayout) view_double.findViewById(R.id.scan_cashMoney_ll);
        draw_ll= (LinearLayout) view_double.findViewById(R.id.scan_money_Draw_ll);
        nodraw_ll= (LinearLayout) view_double.findViewById(R.id.scan_money_noDraw_ll);
        tv_drawNumber= (TextView) view_double.findViewById(R.id.scan_result_drawNumber);
        tv_gotoTrend= (TextView) view_double.findViewById(R.id.scan_gotoTrend);//去看看走势遗漏
        tv_drawMoney= (TextView) view_double.findViewById(R.id.scan_draw_money);//中奖奖金
        lv_money= (MyListView) view_double.findViewById(R.id.scan_prize_lv);//奖金listview
        cash_or_save= (Button) view_double.findViewById(R.id.lottery_calc_submit);
        initBtnCashSaveListener(true);//计奖按钮监听
        cash_or_save.setText(isNewIssue?"保存号码库":"计算奖金");
        setDataView(view_double);//加载布局
        TextView tv_edit= (TextView) view_double.findViewById(R.id.lottery_modify_number);
        lv_double= (MyListView) view_double.findViewById(R.id.scanResult_lv);
        initHeadView(view_double);
        et_period.setText(isDrawIssue?String.valueOf(DrawIssue):String.valueOf(check_double.getDate()));//奖期判断
        et_multiple.setText(String.valueOf(check_double.getMultiple()));//倍数
        initHeadListener(true);//奖期倍数监听
        adapter_double=new ResultPart0Adapter(this,check_double.getNumber(),check_double.getType());
        adapter_double.setScanLottoNumEdit(this);//初始化接口
        tv_pullType.setText(lotteryType[check_double.getType()]);//类型
        lv_double.setAdapter(adapter_double);
        AVLoading.setVisibility(View.GONE);
        cashaDoubledapter=new CashDoubleMoneyAdapter(CheckLotteryActivity.this);//下方奖期adapter
        lv_money.setAdapter(cashaDoubledapter);
        tv_edit.setOnClickListener((v -> {//修改操作
            if(isEnable==true){//完成操作
                adapter_double.setEditEndable(isEnable);
                ViewUtils.setViewEnable(isEnable,et_period,et_multiple);
                tv_edit.setText("完成");
            }else{
                String periodStr=et_period.getText().toString().trim();
                String multipleStr=et_multiple.getText().toString().trim();
                //数据校验
                if (!AllDoubleLastCheck(periodStr, multipleStr)) return;
                //倍数去0
                et_multiple.setText(String.valueOf(Integer.valueOf(multipleStr)));
                //修改完成后切换可点击状态刷新修改数据
                adapter_double.setEditEndable(isEnable);
                ViewUtils.setViewEnable(isEnable,et_period,et_multiple);
                tv_edit.setText("修改");
                hintKbOne();//关闭软键盘
            }
            isEnable=!isEnable;
        }));
    }
    //设置奖期,倍数方式数据
    public void initHeadView(View view){
       et_period= (EditText) view.findViewById(R.id.lottery_period);
       et_multiple= (EditText) view.findViewById(R.id.lottery_multiple);
       tv_pullType= (TextView) view.findViewById(R.id.lottery_type);
    }
    //设置请求成功界面布局
    public void setDataView(View view){
        //重新设置宽高
        LayoutParams params= new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        switch_layout.removeAllViews();
        switch_layout.addView(view);
    }
    public void disposeDoubleStr(String str){
        resul_str = str.replace("o", "0").replace("n","0").replace("'", "").replace("D", "0")
                .replace("O", "0").replace("U", "0").replace("l", "1").replace("I","1").replace("[", "")
                .replace("na", "04").replace("{","").replace("[","").replace("}","").replace("]","").replace("【","").replace("】","")
                .replace("米","*").replace("率","*").replace("片","开").replace("并","开")
                .replace("a", "4").replace(";", ":").replace("日", "0")
                .replace("as", "09").replace("(","").replace("口","0").replace("g","9").replace("t","+")
                .replace(")","").replace("益","蓝").replace("皿","蓝").replace("平","*").replace("篮","蓝")
                .replace("湛","蓝").replace("监","蓝").replace("明","期").trim();
        //部分双色球票带开奖号码,,截取掉
        if(cutStr("期开奖号")<0){
            if(cutStr("期开奖")<0){
                cutStr("期奖号");
            }
        }
    }
    public void disposeLottoStr(String str){
        resul_str = str.replace("o", "0").replace("n","0").replace("a","0").replace("D","0").replaceAll("[f\\-开]","+")
                .replaceAll("\\.", "").replace("<","区").replace("日", "0").replace("目", "0").replace("自", "0").replace("u", "0").replace("G", "3").replace("号","+").replace("量","+").replace("鲁","+").replace("'", "")
                .replace("O", "0").replace("口","0").replace("U", "0").replace("l", "1").replace("I","1").replace("[", "")
                .replace("T","7").replace("na", "04").replace("as", "09").trim();
    }
    //号码修改监听单个校验
    @Override
    public void scanLottoNumEdit(int pos, int type,boolean isSingelRed, EditText et) {
        String num_str=et.getText().toString().trim();
        if (TextUtils.isEmpty(num_str)) return;//空数据
        int num=Integer.valueOf(num_str);//去0
        if(type==0){//单式
            checkLottoSingel(num,isSingelRed);
        }else if(type==1){//复式
            checkLottoMuti(pos,num);
        }else{//胆拖
            checkLottoDantuo(pos,num);
        }
    }
    //双色球修改号码监听
    @Override
    public void scanDoubleNumEdit(int pos, int type,boolean isSingelRed, EditText et) {
        String num_str=et.getText().toString().trim();
        if (TextUtils.isEmpty(num_str)) return;//空数据
        int num=Integer.valueOf(num_str);//去0
        if(type==0){//单式
            checkDoubleSingel(num,isSingelRed);
        }else if(type==1){//复式
            checkDoubleMuti(pos,num);
        }else{//胆拖
            checkDoubleDantuo(pos,num);
        }
    }
    //上传图片到服务器
    public void UpLoadPic(int states,String LotteryId,String Base64){
        ReadBase64.base64ToBitmap(Base64);//转成图片
        File f=new File(ReadBase64.CACHE_PATH+"upload.jpg");
        if(f!=null){
            NetHelper.LOTTERY_API.scanCashPicture(states,LoginSPUtils.getInt("id",0),f,0,LotteryId,CommonUtil.getPhoneBrand()).subscribe(new SafeOnlyNextSubscriber<LotteryUploadModel>(){
                @Override
                public void onNext(LotteryUploadModel args) {
                    super.onNext(args);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        }
    }
    //关闭软键盘
    public void  hintKbOne() {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);

        }
    }
    //保存成功接口回调
    @Override
    public void Success(boolean success) {
        if(success){
            checkSame.clear();
            checkSame.addAll(CloneUtil.clone(tickets_check));//重复保存集合
        }

    }
    //部分双色球票带开奖号码,,截取掉
    public int cutStr(String str){
        int index=resul_str.indexOf(str);//没有开奖号 返回-1
        if(index>0){
            resul_str=resul_str.substring(0,index);//index
        }
        return index;
    }
}

package com.cp2y.cube.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyFilterDoubleModeBlueAdapter;
import com.cp2y.cube.adapter.MyFilterDoubleModeRedAdapter;
import com.cp2y.cube.adapter.MyNumLibrary3DAdapter;
import com.cp2y.cube.adapter.MyNumLibraryCQSSCAdapter;
import com.cp2y.cube.model.NewHomeNumberModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.widgets.TouchLessGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 首页号码库
 */
public class MainBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //双色球首页号码库入口
    public void DoubleMainView(LinearLayout ll, NewHomeNumberModel args, View holdView) {
        NewHomeNumberModel.Drawer drawer = null;
        ll.removeAllViews();
        drawer = args.getList_doubleBall().getTlist();
        //ViewUtils.showViewsVisible(false, view_Single, view_double, view_dantuo);
        LinearLayout layout = (LinearLayout) holdView.findViewById(R.id.library_issue_layout);
        TextView title_lotto = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_lotteryname);
        title_lotto.setText("双色球");
        String[] nums = drawer.getDrawNumber().split(",");
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(nums[i]);
            }
        }
        setTextType(drawer, holdView);
        DoubleData(args.getList_doubleBall(), ll);//数据处理
    }
    //大乐透首页号码库入口
    public void LottoMainView(LinearLayout ll, NewHomeNumberModel args, View holdView) {
        NewHomeNumberModel.Drawer drawer = null;
        ll.removeAllViews();
        drawer = args.getList_lotto().getTlist();
        //ll_Singel_num.removeAllViews();
        //ViewUtils.showViewsVisible(false, view_lotto_Single, view_lotto_double, view_lotto_dantuo);
        LinearLayout layout = (LinearLayout) holdView.findViewById(R.id.library_issue_layout);
        TextView title_lotto = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_lotteryname);
        title_lotto.setText("大乐透");
        String[] nums = drawer.getDrawNumber().split(",");
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(nums[i]);
                if (i >= 5) {//大乐透蓝球
                    tv.setBackgroundResource(R.drawable.lottery_ball_blue_big_all);
                }
            }
        }
        setTextType(drawer, holdView);
        LottoData(args.getList_lotto(), ll);
    }
    //福彩3D  排列3 首页号码库入口
    public void D3MainView(LinearLayout ll, NewHomeNumberModel args, View holdView,int flag) {
        NewHomeNumberModel.Drawer drawer = null;
        ll.removeAllViews();
        drawer = (flag==2?args.getList_d3().getTlist():args.getList_p3().getTlist());
        //ll_Singel_num.removeAllViews();
        //ViewUtils.showViewsVisible(false, view_lotto_Single, view_lotto_double, view_lotto_dantuo);
        LinearLayout layout = (LinearLayout) holdView.findViewById(R.id.library_issue_layout);
        TextView title_lotto = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_lotteryname);
        title_lotto.setText(flag==2?"福彩3D":"排列3");
        String[] nums = drawer.getDrawNumber().split(",");
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(nums[i]);
            }
        }
        setTextType(drawer, holdView);
        D3Data(args, ll,flag);
    }

    // 排列5 首页号码库入口
    public void P5MainView(LinearLayout ll, NewHomeNumberModel args, View holdView,int flag) {
        NewHomeNumberModel.Drawer drawer = null;
        ll.removeAllViews();
        drawer = (flag==4?args.getList_p5().getTlist():args.getList_cqssc().getTlist());
        //ll_Singel_num.removeAllViews();
        //ViewUtils.showViewsVisible(false, view_lotto_Single, view_lotto_double, view_lotto_dantuo);
        LinearLayout layout = (LinearLayout) holdView.findViewById(R.id.library_issue_layout);
        TextView title = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_lotteryname);
        title.setText(flag==4?"排列5":"重庆时时彩");
        String[] nums = drawer.getDrawNumber().split(",");
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(nums[i]);
            }
        }
        setTextType(drawer, holdView);
        if(flag==4){
            P5Data(args, ll);
        }else{
            CQSSCData(args, ll);
        }
    }

    /**
     * 设置开奖信息文字
     *
     * @param drawer
     * @param holdView
     */
    public void setTextType(NewHomeNumberModel.Drawer drawer, View holdView) {
        String price = drawer.getPrize();
        TextView issue = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_lotteryyear);
        issue.setText(drawer.getIssue().concat("期开奖"));
        //holdView.findViewById(R.id.library_price).setVisibility(price == 0? View.GONE:View.VISIBLE);
        TextView textView = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_money);
        TextView textRMB = (TextView) holdView.findViewById(R.id.app_main_horizantal_tv_RMB);
        TextView textDate=(TextView) holdView.findViewById(R.id.app_main_horizantal_tv_lotterydate);
        textDate.setText(CommonUtil.getDateToString2(drawer.getCreateTime()));//开奖时间
        textView.setText("-1".equals(price) ? "--" : ("0".equals(price) ? "未中" : String.valueOf(price)));
        //未中时字色变灰
        textView.setTextColor("0".equals(price) ? ColorUtils.GRAY : ColorUtils.NORMAL_RED);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, "0".equals(price) ? getResources().getDimension(R.dimen.app_tvNomal_size) : getResources().getDimension(R.dimen.app_tvBig_size));
        //隐藏￥
        textRMB.setVisibility("0".equals(price) ? View.GONE : View.VISIBLE);
    }


    public void DoubleData(NewHomeNumberModel.DoubleBall list_doubleBall, LinearLayout ll) {
        for (NewHomeNumberModel.NumberData number : list_doubleBall.getList()) {
            //读本地
//                            List<String> list = FileUtils.readMaxLine(FileUtils.getLibraryFile(number.getIssue(), number.getId()), 5);
//                            if (list.size() == 0) {
//                                app_vp.setVisibility(View.GONE);
//                                return;
//                            }
            if (!number.getFiveNumber().contains("#") || number.getFiveNumber().contains(";")) {
                //没有#号为单式票,有分号为单式
                //单式布局
                View view_Single = LayoutInflater.from(this).inflate(R.layout.numlibrary_singel, null);//单式布局
                //View view_lotto_Single = LayoutInflater.from(this).inflate(R.layout.numlibrary_singel, null);//单式布局
                if (!number.getFiveNumber().contains(";") && !number.getFiveNumber().contains("#")) {//一注单式
                    LinearLayout ll_Singel_num = (LinearLayout) view_Single.findViewById(R.id.numlibrary_single_ll_myNum);
                    String[] data = number.getFiveNumber().split(" ");
                    LinearLayout view_Single_add = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.numlibrary_singel_add, null);
                    for (int i = 0; i < view_Single_add.getChildCount(); i++) {
                        TextView tv = (TextView) view_Single_add.getChildAt(i);
                        String dataStr = CommonUtil.preZeroForBall(data[i]);
                        tv.setText(dataStr);
                    }
                    ll_Singel_num.addView(view_Single_add);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view_Single_add.getLayoutParams();
                    lp.bottomMargin = DisplayUtil.dip2px(10f);
                    view_Single_add.setLayoutParams(lp);
                } else {//多注单式
                    String[] data = number.getFiveNumber().split(";");
                    LinearLayout ll_Singel_num = (LinearLayout) view_Single.findViewById(R.id.numlibrary_single_ll_myNum);
                    for (String singel : data) {
                        if (!singel.equals("greate")) {//大于5注标记
                            String[] data_Singel = singel.split(" ");
                            LinearLayout view_Single_add = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.numlibrary_singel_add, null);
                            for (int i = 0; i < view_Single_add.getChildCount(); i++) {
                                TextView tv = (TextView) view_Single_add.getChildAt(i);
                                String dataStr = CommonUtil.preZeroForBall(data_Singel[i]);
                                tv.setText(dataStr);
                            }
                            ll_Singel_num.addView(view_Single_add);
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view_Single_add.getLayoutParams();
                            lp.bottomMargin = DisplayUtil.dip2px(10f);
                            view_Single_add.setLayoutParams(lp);
                        }
                    }
                }
                //view_Single.setVisibility(View.VISIBLE);
                ll.addView(view_Single);
            } else {
                String[] data = number.getFiveNumber().split("#");
                if (data.length == 2) {//复式
                    String[] red_data = data[0].split(" ");
                    String[] blue_data = data[1].split(" ");
                    //双色球复式布局
                    View view_double = LayoutInflater.from(this).inflate(R.layout.main_viewpager_double, null);
                    TouchLessGridView gv_red = (TouchLessGridView) view_double.findViewById(R.id.numlibrary_double_gv_red);
                    TouchLessGridView gv_blue = (TouchLessGridView) view_double.findViewById(R.id.numlibrary_double_gv_blue);
                    MyFilterDoubleModeRedAdapter adapter_red = new MyFilterDoubleModeRedAdapter(Arrays.asList(red_data), this, R.layout.item_filter_double_redball);
                    MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blue_data), this, R.layout.item_filter_double_blueball, 0);
                    gv_red.setAdapter(adapter_red);
                    gv_blue.setAdapter(adapter_blue);
                    view_double.setVisibility(View.VISIBLE);
                    ll.addView(view_double);
                } else {//胆拖
                    //双色球胆拖布局
                    String[] red_dan = data[0].split(" ");
                    String[] red_tuo = data[1].split(" ");
                    String[] blue = data[2].split(" ");
                    View view_dantuo = LayoutInflater.from(this).inflate(R.layout.main_viewpager_dantuo, null);
                    TouchLessGridView gv_dan = (TouchLessGridView) view_dantuo.findViewById(R.id.numlibrary_dantuo_dan_gv);
                    TouchLessGridView gv_tuo = (TouchLessGridView) view_dantuo.findViewById(R.id.numlibrary_dantuo_tuo_gv);
                    TouchLessGridView gv_lan = (TouchLessGridView) view_dantuo.findViewById(R.id.numlibrary_dantuo_lan_gv);
                    MyFilterDoubleModeRedAdapter adapter_red_dan = new MyFilterDoubleModeRedAdapter(Arrays.asList(red_dan), this, R.layout.item_filter_double_redball);
                    MyFilterDoubleModeRedAdapter adapter_red_tuo = new MyFilterDoubleModeRedAdapter(Arrays.asList(red_tuo), this, R.layout.item_filter_double_redball);
                    MyFilterDoubleModeBlueAdapter adapter_blue2 = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blue), this, R.layout.item_filter_double_blueball,1);
                    gv_dan.setAdapter(adapter_red_dan);
                    gv_tuo.setAdapter(adapter_red_tuo);
                    gv_lan.setAdapter(adapter_blue2);
                    view_dantuo.setVisibility(View.VISIBLE);
                    ll.addView(view_dantuo);
                }
            }
        }
    }

    public void LottoData(NewHomeNumberModel.Lotto list_LottoBall, LinearLayout ll) {
        for (NewHomeNumberModel.NumberData number : list_LottoBall.getList()) {
            //读本地
//                            List<String> list = FileUtils.readMaxLine(FileUtils.getLibraryFile(number.getIssue(), number.getId()), 5);
//                            if (list.size() == 0) {
//                                app_vp.setVisibility(View.GONE);
//                                return;
//                            }
            if (!number.getFiveNumber().contains("#") || number.getFiveNumber().contains(";")) {
                //没有#号为单式票,有分号为单式
                //单式布局
                View view_Single = LayoutInflater.from(this).inflate(R.layout.numlibrary_singel, null);//单式布局
                //View view_lotto_Single = LayoutInflater.from(this).inflate(R.layout.numlibrary_singel, null);//单式布局
                if (!number.getFiveNumber().contains(";") && !number.getFiveNumber().contains("#")) {//一注单式
                    LinearLayout ll_Singel_num = (LinearLayout) view_Single.findViewById(R.id.numlibrary_single_ll_myNum);
                    String[] data = number.getFiveNumber().split(" ");
                    LinearLayout view_Single_add = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.numlibrary_singel_add, null);
                    for (int i = 0; i < view_Single_add.getChildCount(); i++) {
                        TextView tv = (TextView) view_Single_add.getChildAt(i);
                        String dataStr = CommonUtil.preZeroForBall(data[i]);
                        tv.setText(dataStr);
                        if (i >= 5) {
                            tv.setTextColor(ColorUtils.MID_BLUE);
                        }
                    }
                    ll_Singel_num.addView(view_Single_add);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view_Single_add.getLayoutParams();
                    lp.bottomMargin = DisplayUtil.dip2px(10f);
                    view_Single_add.setLayoutParams(lp);
                } else {//多注单式
                    String[] data = number.getFiveNumber().split(";");
                    LinearLayout ll_Singel_num = (LinearLayout) view_Single.findViewById(R.id.numlibrary_single_ll_myNum);
                    for (String singel : data) {
                        if (!singel.equals("greate")) {//大于5注标记
                            String[] data_Singel = singel.split(" ");
                            LinearLayout view_Single_add = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.numlibrary_singel_add, null);
                            for (int i = 0; i < view_Single_add.getChildCount(); i++) {
                                TextView tv = (TextView) view_Single_add.getChildAt(i);
                                String dataStr = CommonUtil.preZeroForBall(data_Singel[i]);
                                tv.setText(dataStr);
                                if (i >= 5) {
                                    tv.setTextColor(ColorUtils.MID_BLUE);
                                }
                            }
                            ll_Singel_num.addView(view_Single_add);
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view_Single_add.getLayoutParams();
                            lp.bottomMargin = DisplayUtil.dip2px(10f);
                            view_Single_add.setLayoutParams(lp);
                        }
                    }
                }
                //view_Single.setVisibility(View.VISIBLE);
                ll.addView(view_Single);
            } else {
                String[] data = number.getFiveNumber().split("#");
                if (data.length == 2) {//复式
                    String[] red_data = data[0].split(" ");
                    String[] blue_data = data[1].split(" ");
                    //大乐透复式布局
                    View view_lotto_double = LayoutInflater.from(this).inflate(R.layout.main_viewpager_double, null);
                    TouchLessGridView lotto_gv_red = (TouchLessGridView) view_lotto_double.findViewById(R.id.numlibrary_double_gv_red);
                    TouchLessGridView lotto_gv_blue = (TouchLessGridView) view_lotto_double.findViewById(R.id.numlibrary_double_gv_blue);
                    MyFilterDoubleModeRedAdapter lotto_adapter_red = new MyFilterDoubleModeRedAdapter(Arrays.asList(red_data), this, R.layout.item_filter_double_redball);
                    MyFilterDoubleModeBlueAdapter lotto_adapter_blue = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blue_data), this, R.layout.item_filter_double_blueball,0);
                    lotto_gv_red.setAdapter(lotto_adapter_red);
                    lotto_gv_blue.setAdapter(lotto_adapter_blue);
                    view_lotto_double.setVisibility(View.VISIBLE);
                    ll.addView(view_lotto_double);
                } else {//胆拖
                    String[] red_dan = data[0].split(" ");
                    String[] red_tuo = data[1].split(" ");
                    String[] blue = data[2].split(" ");
                    String[] blue_tuo = data[3].split(" ");
                    View view_lotto_dantuo = LayoutInflater.from(this).inflate(R.layout.numlibrary_lotto_dantuo, null);
                    TouchLessGridView lotto_gv_dan = (TouchLessGridView) view_lotto_dantuo.findViewById(R.id.numlibrary_dantuo_dan_gv);
                    TouchLessGridView lotto_gv_tuo = (TouchLessGridView) view_lotto_dantuo.findViewById(R.id.numlibrary_dantuo_tuo_gv);
                    TouchLessGridView lotto_gv_lan = (TouchLessGridView) view_lotto_dantuo.findViewById(R.id.numlibrary_dantuo_lan_gv);
                    TouchLessGridView lotto_gv_houTuo = (TouchLessGridView) view_lotto_dantuo.findViewById(R.id.numlibrary_dantuo_houTuo_gv);
                    MyFilterDoubleModeRedAdapter adapter_lotto_red_dan = new MyFilterDoubleModeRedAdapter(Arrays.asList(red_dan), this, R.layout.item_filter_double_redball);
                    MyFilterDoubleModeRedAdapter adapter_lotto_red_tuo = new MyFilterDoubleModeRedAdapter(Arrays.asList(red_tuo), this, R.layout.item_filter_double_redball);
                    MyFilterDoubleModeBlueAdapter adapter_lotto_blue2 = new MyFilterDoubleModeBlueAdapter(new ArrayList<String>(), this, R.layout.item_filter_double_blueball,1);
                    MyFilterDoubleModeBlueAdapter adapter_lotto_blue_tuo = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blue_tuo), this, R.layout.item_filter_double_blueball,1);
                    lotto_gv_dan.setAdapter(adapter_lotto_red_dan);
                    lotto_gv_tuo.setAdapter(adapter_lotto_red_tuo);
                    lotto_gv_lan.setAdapter(adapter_lotto_blue2);
                    lotto_gv_houTuo.setAdapter(adapter_lotto_blue_tuo);
                    if (blue.length == 0) {//后胆为空
                        adapter_lotto_blue2.reloadData(Arrays.asList(new String[]{"100"}));
                    } else {//后胆不为空
                        adapter_lotto_blue2.reloadData(Arrays.asList(blue));
                    }
                    view_lotto_dantuo.setVisibility(View.VISIBLE);
                    ll.addView(view_lotto_dantuo);
                }
            }
        }
    }

    //3D号码库数据
    public void D3Data(NewHomeNumberModel args, LinearLayout d3_ll,int flag) {
        for (NewHomeNumberModel.NumberData number : flag==2?args.getList_d3().getList():args.getList_p3().getList()) {
            String fiveNumber = number.getFiveNumber();
            int type = number.getPlayType();//类型
            if (fiveNumber.contains(";")) {//多注的结果 组选单式 和直选单式
                //组选单式和直选单式多注
                View zuxuan = LayoutInflater.from(this).inflate(R.layout.item_3d_number_zuxuansingle, null);
                LinearLayout layout = (LinearLayout) zuxuan.findViewById(R.id.simple_single_layout);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
                }//隐藏箭头
                zuxuan.findViewById(R.id.next_image).setVisibility(View.GONE);
                zuxuan.findViewById(R.id.app_num_library_view).setVisibility(View.GONE);
                zuxuan.findViewById(R.id.library_root).setBackground(null);
                zuxuan.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
                String[] filter_singel = fiveNumber.split(";");
                if (filter_singel.length == 5 || filter_singel.length == 6) {//5注过滤好吗 或者大于5注过滤号码
                    int max = 5;//最大注数
                    for (int i = 0; i < max; i++) {
                        addD3SingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, new HashSet<>(), type);
                    }
                } else {//小于5注过滤号码
                    int max = filter_singel.length;//最大注数
                    for (int i = 0; i < max; i++) {
                        addD3SingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, new HashSet<>(), type);
                    }
                }
                d3_ll.addView(zuxuan);
            } else {//单注保存结果
                if (type == 13||type==1||type==94) {//直选单式
                    View zhixuan_Single = LayoutInflater.from(this).inflate(R.layout.item_library_zhixuan_single, null);
                    String[] numArray = CommonUtil.stringToArray(fiveNumber);//号码
                    ((TextView) zhixuan_Single.findViewById(R.id.zhixuan_single_bai)).setText(numArray[0]);
                    ((TextView) zhixuan_Single.findViewById(R.id.zhixuan_single_shi)).setText(numArray[1]);
                    ((TextView) zhixuan_Single.findViewById(R.id.zhixuan_single_ge)).setText(numArray[2]);
                    zhixuan_Single.findViewById(R.id.library_root).setBackground(null);
                    d3_ll.addView(zhixuan_Single);
                } else if (type == 12||type==0||type==93) {//直选定位
                    View zhixuan_Location = LayoutInflater.from(this).inflate(R.layout.item_library_zhixuan_location, null);
                    zhixuan_Location.findViewById(R.id.library_root).setBackground(null);
                    String[] numArray = fiveNumber.split("-");
                    String[] bai = CommonUtil.stringToArray(numArray[0]);//百位数据
                    String[] shi = CommonUtil.stringToArray(numArray[1]);//十位数据
                    String[] ge = CommonUtil.stringToArray(numArray[2]);//个位数据
                    TouchLessGridView gv_bai = (TouchLessGridView) zhixuan_Location.findViewById(R.id.item_zhixuan_location_bai_gv);
                    TouchLessGridView gv_shi = (TouchLessGridView) zhixuan_Location.findViewById(R.id.item_zhixuan_location_shi_gv);
                    TouchLessGridView gv_ge = (TouchLessGridView) zhixuan_Location.findViewById(R.id.item_zhixuan_location_ge_gv);
                    MyNumLibrary3DAdapter adapter_bai = new MyNumLibrary3DAdapter(Arrays.asList(bai), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_shi = new MyNumLibrary3DAdapter(Arrays.asList(shi), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_ge = new MyNumLibrary3DAdapter(Arrays.asList(ge), this, new ArrayList<>(), 0, false);//不需要高亮显示传空集合
                    gv_bai.setAdapter(adapter_bai);
                    gv_shi.setAdapter(adapter_shi);
                    gv_ge.setAdapter(adapter_ge);
                    d3_ll.addView(zhixuan_Location);
                } else if (type == 16 || type == 17||type == 6 || type == 7||type==126||type==127) {//组3复式
                    View muti = null;
                    if (type == 16||type == 6||type==126) {
                        muti = LayoutInflater.from(this).inflate(R.layout.item_library_zu3_muti, null);
                    } else {
                        muti = LayoutInflater.from(this).inflate(R.layout.item_library_zu6_muti, null);
                    }
                    muti.findViewById(R.id.library_root).setBackground(null);
                    String[] numArray = CommonUtil.stringToArray(fiveNumber);//号码
                    TouchLessGridView gv_zu3 = (TouchLessGridView) muti.findViewById(R.id.item_zuxuan_muti_gv);
                    MyNumLibrary3DAdapter adapter_zu3 = new MyNumLibrary3DAdapter(Arrays.asList(numArray), this, new ArrayList<>(), 0, false);
                    gv_zu3.setAdapter(adapter_zu3);
                    d3_ll.addView(muti);
                } else if (type == 172||type==173||type==168||type==169||type==522||type==521) {//组3胆拖
                    View danTuo = null;
                    if (type == 172||type == 168||type == 522) {
                        danTuo = LayoutInflater.from(this).inflate(R.layout.item_library_zu3_dantuo, null);
                    } else {
                        danTuo = LayoutInflater.from(this).inflate(R.layout.item_library_zu6_dantuo, null);
                    }
                    danTuo.findViewById(R.id.library_root).setBackground(null);
                    String[] numArray = fiveNumber.split(",");
                    String[] dan = CommonUtil.stringToArray(numArray[0]);//胆数据
                    String[] tuo = CommonUtil.stringToArray(numArray[1]);//拖数据
                    TouchLessGridView gv_dan = (TouchLessGridView) danTuo.findViewById(R.id.item_zuxuan_dan_gv);
                    TouchLessGridView gv_tuo = (TouchLessGridView) danTuo.findViewById(R.id.item_zuxuan_tuo_gv);
                    MyNumLibrary3DAdapter adapter_dan = new MyNumLibrary3DAdapter(Arrays.asList(dan), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_tuo = new MyNumLibrary3DAdapter(Arrays.asList(tuo), this, new ArrayList<>(), 0, false);
                    gv_dan.setAdapter(adapter_dan);
                    gv_tuo.setAdapter(adapter_tuo);
                    d3_ll.addView(danTuo);
                } else if (type == 18||type==5||type==129) {//(组选单式)1注  组6单式1注 过滤后组选单式1注
                    View zuxuan = LayoutInflater.from(this).inflate(R.layout.item_3d_number_zuxuansingle, null);
                    zuxuan.findViewById(R.id.library_root).setBackground(null);
                    LinearLayout layout = (LinearLayout) zuxuan.findViewById(R.id.simple_single_layout);
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
                    }//隐藏箭头
                    zuxuan.findViewById(R.id.next_image).setVisibility(View.GONE);
                    addD3SingleData((LinearLayout) layout.getChildAt(0), fiveNumber, 0, new HashSet<>(), type);
                    d3_ll.addView(zuxuan);
                }
            }
        }
    }
    //排列5号码库数据
    public void P5Data(NewHomeNumberModel args, LinearLayout p5_ll) {
        for (NewHomeNumberModel.NumberData number : args.getList_p5().getList()) {
            String fiveNumber = number.getFiveNumber();
            int type = number.getPlayType();//类型
            if (fiveNumber.contains(";")||type == 9||type==92) {//多注的结果 单式
                View singles = LayoutInflater.from(this).inflate(R.layout.item_p5_singles, null);
                LinearLayout layout = (LinearLayout) singles.findViewById(R.id.simple_single_layout);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
                }//隐藏箭头
                singles.findViewById(R.id.next_image).setVisibility(View.GONE);
                singles.findViewById(R.id.app_num_library_view).setVisibility(View.GONE);
                singles.findViewById(R.id.library_root).setBackground(null);
                singles.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
                if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                    String[] filter_singel = fiveNumber.split(";");
                    if (filter_singel.length == 5 || filter_singel.length == 6) {//5注过滤好吗 或者大于5注过滤号码
                        int max = 5;//最大注数
                        for (int i = 0; i < max; i++) {
                            addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i);
                        }
                    } else {//小于5注过滤号码
                        int max = filter_singel.length;//最大注数
                        for (int i = 0; i < max; i++) {
                            addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i);
                        }
                    }
                }else{
                    addSingleData((LinearLayout) layout.getChildAt(0), fiveNumber, 0);
                }
                p5_ll.addView(singles);
            } else {//复式
                    View muti = LayoutInflater.from(this).inflate(R.layout.item_library_p5_muti, null);
                    muti.findViewById(R.id.library_root).setBackground(null);
                    String[] numArray = fiveNumber.split("-");
                    String[] wan = CommonUtil.stringToArray(numArray[0]);//万位数据
                    String[] qian = CommonUtil.stringToArray(numArray[1]);//千位数据
                    String[] bai = CommonUtil.stringToArray(numArray[2]);//百位数据
                    String[] shi = CommonUtil.stringToArray(numArray[3]);//十位数据
                    String[] ge = CommonUtil.stringToArray(numArray[4]);//个位数据
                    TouchLessGridView gv_wan = (TouchLessGridView) muti.findViewById(R.id.item_zhixuan_location_wan_gv);
                    TouchLessGridView gv_qian = (TouchLessGridView) muti.findViewById(R.id.item_zhixuan_location_qian_gv);
                    TouchLessGridView gv_bai = (TouchLessGridView) muti.findViewById(R.id.item_zhixuan_location_bai_gv);
                    TouchLessGridView gv_shi = (TouchLessGridView) muti.findViewById(R.id.item_zhixuan_location_shi_gv);
                    TouchLessGridView gv_ge = (TouchLessGridView) muti.findViewById(R.id.item_zhixuan_location_ge_gv);
                    MyNumLibrary3DAdapter adapter_wan = new MyNumLibrary3DAdapter(Arrays.asList(wan), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_qian = new MyNumLibrary3DAdapter(Arrays.asList(qian), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_bai = new MyNumLibrary3DAdapter(Arrays.asList(bai), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_shi = new MyNumLibrary3DAdapter(Arrays.asList(shi), this, new ArrayList<>(), 0, false);
                    MyNumLibrary3DAdapter adapter_ge = new MyNumLibrary3DAdapter(Arrays.asList(ge), this, new ArrayList<>(), 0, false);//不需要高亮显示传空集合
                    gv_wan.setAdapter(adapter_wan);
                    gv_qian.setAdapter(adapter_qian);
                    gv_bai.setAdapter(adapter_bai);
                    gv_shi.setAdapter(adapter_shi);
                    gv_ge.setAdapter(adapter_ge);
                    p5_ll.addView(muti);
            }
        }
    }

    //重庆时时彩号码库数据
    public void CQSSCData(NewHomeNumberModel args, LinearLayout cqssc_ll) {
        for (NewHomeNumberModel.NumberData number : args.getList_cqssc().getList()) {
            String fiveNumber = number.getFiveNumber();
            int type = MapUtils.CQSSC_LIBRARY_TYPE.get(number.getPlayType());//类型
            View convertView=null;
            if (type == 1) {//五星单式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_cq5_singles, cqssc_ll, false);
            } else if (type == 2) {//五星复式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq5_muti, cqssc_ll, false);
            } else if (type == 3) {//三星直选单式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_cq3_number_zhixuansingle, cqssc_ll, false);
            } else if (type == 4) {//三星直选定位
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq3_zhixuan_location, cqssc_ll, false);
            } else if (type == 5) {//三星组3复式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq3_zu3_muti, cqssc_ll, false);
            } else if (type == 6) {//三星组3胆拖
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq3_zu3_dantuo, cqssc_ll, false);
            } else if (type == 7) {//三星组选单式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_cq3_number_zuxuansingle, cqssc_ll, false);
            } else if (type == 8) {//三星组6复式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq3_zu6_muti, cqssc_ll, false);
            } else if (type == 9) {//三星组6胆拖
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq3_zu6_dantuo, cqssc_ll, false);
            } else if (type == 10) {//二星直选单式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_cq2_number_zhixuansingle, cqssc_ll, false);
            } else if (type == 11) {//二星直选定位
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq2_zhixuan_location, cqssc_ll, false);
            } else if (type == 12) {//二星组选单式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_cq2_number_zuxuansingle, cqssc_ll, false);
            } else if (type == 13) {//二星组选复式
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq2_zuxuan_muti, cqssc_ll, false);
            } else {//二星组选胆拖
                convertView = LayoutInflater.from(this).inflate(R.layout.item_library_cq2_zuxuan_dantuo, cqssc_ll, false);
            }
            convertView.findViewById(R.id.library_root).setBackground(null);
            if (type == 1 || type == 3 || type == 7 || type == 10 || type == 12) {//多注的结果 单式
                LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
                }//隐藏箭头
                convertView.findViewById(R.id.next_image).setVisibility(View.GONE);
                convertView.findViewById(R.id.app_num_library_view).setVisibility(View.GONE);
                convertView.findViewById(R.id.library_root).setBackground(null);
                convertView.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
                if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                    String[] filter_singel = fiveNumber.split(";");
                    if (filter_singel.length == 5 || filter_singel.length == 6) {//5注过滤好吗 或者大于5注过滤号码
                        int max = 5;//最大注数
                        for (int i = 0; i < max; i++) {
                            addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i);
                        }
                    } else {//小于5注过滤号码
                        int max = filter_singel.length;//最大注数
                        for (int i = 0; i < max; i++) {
                            addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i);
                        }
                    }
                }else{
                    addSingleData((LinearLayout) layout.getChildAt(0), fiveNumber, 0);
                }
            } else if (type == 2) {//五星复式
                String[] numArray = fiveNumber.split("-");
                String[] wan = CommonUtil.stringToArray(numArray[0]);//万位数据
                String[] qian = CommonUtil.stringToArray(numArray[1]);//千位数据
                String[] bai = CommonUtil.stringToArray(numArray[2]);//百位数据
                String[] shi = CommonUtil.stringToArray(numArray[3]);//十位数据
                String[] ge = CommonUtil.stringToArray(numArray[4]);//个位数据
                TouchLessGridView gv_wan = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_wan_gv);
                TouchLessGridView gv_qian = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_qian_gv);
                TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
                TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
                TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
                MyNumLibraryCQSSCAdapter adapter_wan = new MyNumLibraryCQSSCAdapter(Arrays.asList(wan), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_qian = new MyNumLibraryCQSSCAdapter(Arrays.asList(qian), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_bai = new MyNumLibraryCQSSCAdapter(Arrays.asList(bai), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_shi = new MyNumLibraryCQSSCAdapter(Arrays.asList(shi), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_ge = new MyNumLibraryCQSSCAdapter(Arrays.asList(ge), this, new ArrayList<>(), 0, false);
                adapter_wan.setLocationCount(0);//百位标记
                adapter_qian.setLocationCount(1);//百位标记
                adapter_bai.setLocationCount(2);//百位标记
                adapter_shi.setLocationCount(3);//十位标记
                adapter_ge.setLocationCount(4);//个位标记
                gv_wan.setAdapter(adapter_wan);
                gv_qian.setAdapter(adapter_qian);
                gv_bai.setAdapter(adapter_bai);
                gv_shi.setAdapter(adapter_shi);
                gv_ge.setAdapter(adapter_ge);
            } else if (type == 4) {//三星直选定位
                String[] numArray = fiveNumber.split("-");
                String[] bai = CommonUtil.stringToArray(numArray[0]);//百位数据
                String[] shi = CommonUtil.stringToArray(numArray[1]);//十位数据
                String[] ge = CommonUtil.stringToArray(numArray[2]);//个位数据
                TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
                TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
                TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
                MyNumLibraryCQSSCAdapter adapter_bai = new MyNumLibraryCQSSCAdapter(Arrays.asList(bai), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_shi = new MyNumLibraryCQSSCAdapter(Arrays.asList(shi), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_ge = new MyNumLibraryCQSSCAdapter(Arrays.asList(ge), this, new ArrayList<>(), 0, false);
                adapter_bai.setLocationCount(0);//百位标记
                adapter_shi.setLocationCount(1);//十位标记
                adapter_ge.setLocationCount(2);//个位标记
                gv_bai.setAdapter(adapter_bai);
                gv_shi.setAdapter(adapter_shi);
                gv_ge.setAdapter(adapter_ge);
            } else if (type == 5 || type == 8 || type == 13) {//三星组3组6复式 二星组选复式
                String[] numArray = CommonUtil.stringToArray(fiveNumber);//号码
                TouchLessGridView gv_zu3 = (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_muti_gv);
                MyNumLibraryCQSSCAdapter adapter_zu3 = new MyNumLibraryCQSSCAdapter(Arrays.asList(numArray), this, new ArrayList<>(), 0, false);
                gv_zu3.setAdapter(adapter_zu3);
            } else if (type == 6 || type == 9 || type == 14) {//三星组3组6胆拖 三星组选胆拖
                String[] numArray = fiveNumber.split(",");
                String[] dan = CommonUtil.stringToArray(numArray[0]);//胆数据
                String[] tuo = CommonUtil.stringToArray(numArray[1]);//拖数据
                TouchLessGridView gv_dan = (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_dan_gv);
                TouchLessGridView gv_tuo = (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_tuo_gv);
                MyNumLibraryCQSSCAdapter adapter_dan = new MyNumLibraryCQSSCAdapter(Arrays.asList(dan), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_tuo = new MyNumLibraryCQSSCAdapter(Arrays.asList(tuo), this, new ArrayList<>(), 0, false);
                gv_dan.setAdapter(adapter_dan);
                gv_tuo.setAdapter(adapter_tuo);
            } else if (type == 11) {//二星直选定位
                String[] numArray = fiveNumber.split("-");
                String[] shi = CommonUtil.stringToArray(numArray[0]);//十位数据
                String[] ge = CommonUtil.stringToArray(numArray[1]);//个位数据
                TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
                TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
                MyNumLibraryCQSSCAdapter adapter_shi = new MyNumLibraryCQSSCAdapter(Arrays.asList(shi), this, new ArrayList<>(), 0, false);
                MyNumLibraryCQSSCAdapter adapter_ge = new MyNumLibraryCQSSCAdapter(Arrays.asList(ge), this, new ArrayList<>(), 0, false);
                adapter_shi.setLocationCount(0);//百位标记
                adapter_ge.setLocationCount(1);//十位标记
                adapter_ge.setLocationCount(2);//个位标记
                gv_shi.setAdapter(adapter_shi);
                gv_ge.setAdapter(adapter_ge);
            }
            cqssc_ll.addView(convertView);
        }
    }

    /**
     * 添加3D单式布局
     *
     * @param layout
     * @param numberStr
     * @param position
     */
    public void addD3SingleData(LinearLayout layout, String numberStr, int position, Set<String> awardNums, int playType) {
        if (position == 0) {
            TextView title = (TextView) layout.findViewById(R.id.single_flag_text);
            title.setVisibility(View.VISIBLE);
            title.setText((playType == 13||playType == 1) ? "直选单式" : "组选单式");
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr = CommonUtil.stringToArray(numberStr);
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = numberArr[i - 1];
            text.setText(dataStr);
//            if (awardNums.contains(dataStr)) {//红球中奖号
//                text.setBackgroundResource(R.drawable.lottery_ball_red);
//            }
        }
    }
    /**
     * 添加单式布局
     *
     * @param layout
     * @param numberStr
     * @param position
     */
    public void addSingleData(LinearLayout layout, String numberStr, int position) {
        if (position == 0) {
            TextView title = (TextView) layout.findViewById(R.id.single_flag_text);
            title.setVisibility(View.VISIBLE);
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr = CommonUtil.stringToArray(numberStr);
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = numberArr[i - 1];
            text.setText(dataStr);
//            if (awardNums.contains(dataStr)) {//红球中奖号
//                text.setBackgroundResource(R.drawable.lottery_ball_red);
//            }
        }
    }
}

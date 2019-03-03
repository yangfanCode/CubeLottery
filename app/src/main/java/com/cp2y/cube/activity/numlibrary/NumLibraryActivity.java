package com.cp2y.cube.activity.numlibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.numlibrary.numfragment.CQSSCLibraryFragment;
import com.cp2y.cube.activity.numlibrary.numfragment.D3LibraryFragment;
import com.cp2y.cube.activity.numlibrary.numfragment.DoubleLibraryFragment;
import com.cp2y.cube.activity.numlibrary.numfragment.LottoLibraryFragment;
import com.cp2y.cube.activity.numlibrary.numfragment.P3LibraryFragment;
import com.cp2y.cube.activity.numlibrary.numfragment.P5LibraryFragment;
import com.cp2y.cube.custom.ClosePop;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.NumLibraryExitModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NumLibraryActivity extends BaseActivity implements ClosePop{
    private boolean DOUBLE_NUMLIBRARY=false;//是否有双色球号码库
    private boolean LOTTO_NUMLIBRARY=false;//是否有大乐透号码库
    private boolean D3_NUMLIBRARY=false;//是否有福彩3D号码库
    private boolean P3_NUMLIBRARY=false;//是否有排列3号码库
    private boolean P5_NUMLIBRARY=false;//是否有排列5号码库
    private boolean CQ3_NUMLIBRARY=false;//是否有重庆时时彩3星号码库
    private boolean CQ5_NUMLIBRARY=false;//是否有重庆时时彩5星号码库
    private boolean CQSSC_NUMLIBRARY=false;//是否有重庆时时彩号码库
    private AVLoadingIndicatorView AVLoading;
    private PopupWindow popupWindow = null;
    private FragmentManager manager=getSupportFragmentManager();
    private List<Class> Library=new ArrayList<>();//fragment页面
    //改版后 换成map key为lotteryId value 为 fragment 点击根据ID来找 首页改点击
    private Class LIBRARY[]={DoubleLibraryFragment.class,LottoLibraryFragment.class,D3LibraryFragment.class, P3LibraryFragment.class,P5LibraryFragment.class, CQSSCLibraryFragment.class};
    private RelativeLayout title_layout;
    private TextView title,numLibrary_tip;
    private ImageView icon,netOff;
    private Toolbar toolBar;
    private int flag=-1;
    private List<Integer> data=new ArrayList<>();
    //根据标题拿fragment位置
    private Map<String,Integer> fragmentPos=new HashMap<String,Integer>(){{put("双色球",0);put("大乐透",1);put("福彩3D",2);put("排列3",3);put("排列5",4);put("重庆时时彩",5);}};
    private TreeMap<Integer,String> titleMap=new TreeMap<>(new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            String var1=String.valueOf(o1);
            String var2=String.valueOf(o2);
            return Integer.parseInt(var1)-Integer.parseInt(var2);//按照Integer的key排序
        }
    });
    private List<String> titles=new ArrayList<>();//title集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_library);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setVisibility(View.GONE);
        flag=getIntent().getIntExtra("flag",-1);
        setClosePop(this);
        initView();//初始化
        addFragment();//添加数据
        initNetOff();//断网控制
        initListener();//监听
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            AVLoading.setVisibility(View.GONE);
            toolBar.setVisibility(View.VISIBLE);
            //没号码库
            title_layout.setEnabled(false);
            icon.setVisibility(View.GONE);
            title.setText("号码库");
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
            title_layout.setEnabled(true);
        }
        initNet();
    }
    
    //请求接口
    private void initNet() {
        NetHelper.LOTTERY_API.getNumberExist(LoginSPUtils.getInt("id",0),1).subscribe(new SafeOnlyNextSubscriber<NumLibraryExitModel>(){
            @Override
            public void onNext(NumLibraryExitModel args) {
                super.onNext(args);
                data=args.getExistNumber();
                AVLoading.setVisibility(View.GONE);
                toolBar.setVisibility(View.VISIBLE);
                if(data!=null&&data.size()>0){//有号码库
                        //flag==-1侧拉菜单进入,其他为制定猜中进入
                        titleSort();//排序标题
                        //侧拉菜单不传flag默认-1  0双色球 开启双色球页面
                        if((flag==0&&DOUBLE_NUMLIBRARY)||(flag==-1&&DOUBLE_NUMLIBRARY)){//默认双色球
                            DoubleTitle();
                        }else if((flag==1&&LOTTO_NUMLIBRARY)||(flag==-1&&LOTTO_NUMLIBRARY)){//开启大乐透页面
                            LottoTitle();
                        }else if((flag==2&&D3_NUMLIBRARY)||(flag==-1&&D3_NUMLIBRARY)){
                            D3Title();//开启福彩3D页面
                        }else if((flag==3&&P3_NUMLIBRARY)||(flag==-1&&P3_NUMLIBRARY)){
                            P3Title();//开启排列3页面
                        }else if((flag==4&&P5_NUMLIBRARY)||(flag==-1&&P5_NUMLIBRARY)){
                            P5Title();//开启排列5页面
                        }else if((flag==5&&CQSSC_NUMLIBRARY)||(flag==-1&&CQSSC_NUMLIBRARY)){
                            CQSSCTitle();//开启重庆时时彩页面
                        }else{//有号码库但没有指定彩种的号码库时
                            if(DOUBLE_NUMLIBRARY){
                                DoubleTitle();
                            }else if(LOTTO_NUMLIBRARY){
                                LottoTitle();
                            }else if(D3_NUMLIBRARY){
                                D3Title();
                            }else if(P3_NUMLIBRARY){
                                P3Title();
                            }else if(P5_NUMLIBRARY){
                                P5Title();
                            }else if(CQSSC_NUMLIBRARY){
                                CQSSCTitle();
                            }
                        }
                }else{
                    //没号码库
                    setTitleControl(0,"号码库");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                toolBar.setVisibility(View.VISIBLE);
                //没号码库
                setTitleControl(0,"号码库");
            }
        });
    }

    private void LottoTitle() {
        initChange(1);
        setTitleControl(data.size(),"大乐透-号码库");
    }

    private void D3Title() {
        initChange(2);
        setTitleControl(data.size(),"福彩3D-号码库");
    }

    private void DoubleTitle() {
        initChange(0);
        setTitleControl(data.size(),"双色球-号码库");
    }

    private void P3Title() {
        initChange(3);
        setTitleControl(data.size(),"排列3-号码库");
    }

    private void P5Title() {
        initChange(4);
        setTitleControl(data.size(),"排列5-号码库");
    }

    private void CQSSCTitle() {
        initChange(5);
        setTitleControl(data.size(),"重庆时时彩-号码库");
    }

    private void initView() {
        title_layout = (RelativeLayout) findViewById(R.id.toolbar_numlibrary_layout);
        icon=(ImageView) findViewById(R.id.toolbar_icon);
        title=(TextView) findViewById(R.id.toolbar_title);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        numLibrary_tip = (TextView) findViewById(R.id.numLibrary_tip);
        netOff = (ImageView) findViewById(R.id.netOff);//断网
    }

    private void initListener() {
        title_layout.setOnClickListener((v -> {
            int size=data.size();
            if(popupWindow==null){
                showSubTitles(size,flag<=0?0:flag);
                icon.setImageResource(R.mipmap.icon_xiala_top);
            }else{
                if(popupWindow.isShowing()){
                    icon.setImageResource(R.mipmap.icon_xiaola);
                    popupWindow.dismiss();
                }else{
                    icon.setImageResource(R.mipmap.icon_xiala_top);
                    showSubTitles(size,flag<=0?0:flag);
                }
            }
        }));
    }

    //切换号码库
    private void initChange(int position) {
        try {
            manager.beginTransaction().replace(R.id.library_container,(Fragment)Library.get(position).newInstance(),"LibraryTrend").commit();
            title.setText(titleMap.get(Integer.valueOf(position))+"-号码库");
            if(popupWindow!=null){
                popupWindow.dismiss();
                icon.setImageResource(R.mipmap.icon_xiaola);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSubTitles(int size, int pos) {
        if(popupWindow==null){
            View contentView = LayoutInflater.from(NumLibraryActivity.this)
                    .inflate(R.layout.numlibrary_pop, null);
            FlowLayout ll= (FlowLayout) contentView.findViewById(R.id.num_library_pop_ll);
            View view=contentView.findViewById(R.id.half_tanslate_view);
            ll.removeAllViews();
            RadioButton[] buttons = new RadioButton[size];
            for(int i=0;i<size;i++){
                RadioButton button= (RadioButton) LayoutInflater.from(this).inflate(R.layout.numlibrary_radiobutton,ll,false);
                button.setText(titles.get(i));
                buttons[i]=button;
                int width=getResources().getDisplayMetrics().widthPixels;
                if(size<4){//前3个数据动态设置宽度
                    FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams((width/size),DisplayUtil.dip2px(55f));
                    button.setLayoutParams(lp);
                }else{//大于等于个宽度固定
                    FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams((width/3),DisplayUtil.dip2px(55f));
                    button.setLayoutParams(lp);
                }
                ll.addView(button);
            }
            //默认选择的button 根据标题拿titles里的位置
            String s=title.getText().toString();
            String t=s.substring(0,s.indexOf("-"));
            int index=titles.indexOf(t);
            buttons[index].setChecked(true);
            CompoundButton.OnCheckedChangeListener listener = (compoundButton, checked) -> {
                if (checked) {
                    for (int i = 0; i < buttons.length; i++) {
                        RadioButton button = buttons[i];
                        if (compoundButton == button) {
                            //切换显示转圈
                            initChange(fragmentPos.get(button.getText().toString()));
                            //showSubTitles(icon,i);
                            continue;
                        }
                        button.setChecked(false);
                    }
                }
            };
            view.setOnClickListener((v -> {
                icon.setImageResource(R.mipmap.icon_xiaola);
                popupWindow.dismiss();
            }));
            for (RadioButton button : buttons) {
                button.setOnCheckedChangeListener(listener);
            }
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            //popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setContentView(contentView);
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
        }else{
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
        }
    }
    //动态添加页面数据
    public void addFragment(){
        for(Class c:LIBRARY){
            Library.add(c);
        }
    }
    //设置标题 显示
    public void setTitleControl(int libraryCount,String text){
        if(libraryCount==0){//无号码库
            title_layout.setEnabled(false);
            numLibrary_tip.setVisibility(View.VISIBLE);
            icon.setVisibility(View.GONE);
        }else if(libraryCount==1){//1个 不能点击
            title_layout.setEnabled(false);
            icon.setVisibility(View.GONE);
        }else{
            title_layout.setEnabled(true);
            icon.setVisibility(View.VISIBLE);
        }
        title.setText(text);
    }
    //打开侧拉关闭pop
    @Override
    public void closePop() {
        popupWindow.dismiss();
    }

    public void titleSort(){
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i)==10002){
                titleMap.put(0,"双色球");
                DOUBLE_NUMLIBRARY=true;//有双色球号码库
            }else if(data.get(i)==10088){
                LOTTO_NUMLIBRARY=true;//有大乐透号码库
                titleMap.put(1,"大乐透");
            }else if(data.get(i)==10001){
                D3_NUMLIBRARY=true;//有福彩3D号码库
                titleMap.put(2,"福彩3D");
            }else if(data.get(i)==10003){
                P3_NUMLIBRARY=true;//有排列3号码库
                titleMap.put(3,"排列3");
            }else if(data.get(i)==10004){
                P5_NUMLIBRARY=true;//有排列5号码库
                titleMap.put(4,"排列5");
            }else if(data.get(i)==10089){
                CQSSC_NUMLIBRARY=true;//有重庆时时彩号码库
                titleMap.put(5,"重庆时时彩");
            }
        }
        for(Iterator<Map.Entry<Integer,String>> it=titleMap.entrySet().iterator();it.hasNext();){
            Map.Entry<Integer,String> entry=it.next();
            int key=entry.getKey();
            String value=entry.getValue();
            titles.add(value);
        }
    }
}

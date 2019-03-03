package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.NumLibraryDetailActivity;
import com.cp2y.cube.adapter.MyFilterDoubleModeBlueAdapter;
import com.cp2y.cube.adapter.MyFilterDoubleModeRedAdapter;
import com.cp2y.cube.adapter.MyNumLibrary3DAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.model.PushSingleSummaryModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.widgets.TouchLessGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yangfan on 2018/1/15.
 */
public class PushSingleSummaryNumberAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<PushSingleSummaryModel.NumberDetail>list;
    private SparseIntArray sparseIntArray=new SparseIntArray(){{put(10002,0);put(10088,1);put(10001,2);put(10003,3);put(10004,5);}};
    public PushSingleSummaryNumberAdapter(Context context,List<PushSingleSummaryModel.NumberDetail>data){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.list=data;
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public PushSingleSummaryModel.NumberDetail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        PushSingleSummaryModel.NumberDetail numberDetail=list.get(position);
        int playType=numberDetail.getPlayType();
        int lotteryId=numberDetail.getLotteryId();
        String fiveNumber = numberDetail.getFiveNumber();
        if(lotteryId==10002||lotteryId==10088){//双色球 大乐透
            boolean isDouble=lotteryId==10002?true:false;
            if(fiveNumber.contains(";")){
                return isDouble?0:3;
            }else{
                if(fiveNumber.split("#").length==2){
                    return 1;//只有一个#号为复式
                }else if(!fiveNumber.contains("#")){
                    return isDouble?0:3;//没有#为单式
                }else{//胆拖
                    return isDouble?2:4;
                }
            }
        }else{//福彩3D 排列3 排列5
            if (fiveNumber.contains(";")) {//多注
                if(lotteryId==10001||lotteryId==10003){
                    return 9;//福彩3D排列3多注单式
                }else{
                    return 12;//排列5多注单式
                }
            }else{
                return MapUtils.PUSH_SINGLE_TYPE.get(playType);//单独一注
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 14;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == 0) {//双色球单式
                convertView = inflater.inflate(R.layout.item_pushsingle_single, parent, false);
            } else if (type == 1) {//双色球大乐透复式
                convertView = inflater.inflate(R.layout.item_pushsingle_muti, parent, false);
            } else if (type == 2) {//双色球胆拖
                convertView = inflater.inflate(R.layout.item_pushsingle_dantuo, parent, false);
            } else if (type == 3) {//大乐透单式
                convertView = inflater.inflate(R.layout.item_pushsingle_lotto_single, parent, false);
            } else if (type == 4) {//大乐透胆拖
                convertView = inflater.inflate(R.layout.item_pushsingle_lotto_dantuo, parent, false);
            } else if (type == 5) {//福彩3D排列3直选单式
                convertView = inflater.inflate(R.layout.item_library_zhixuan_single, parent, false);
            } else if (type == 6) {//福彩3D排列3直选定位
                convertView = inflater.inflate(R.layout.item_library_zhixuan_location, parent, false);
            } else if (type == 7) {//福彩3D排列3组3复式
                convertView = inflater.inflate(R.layout.item_library_zu3_muti, parent, false);
            } else if (type == 8) {//福彩3D排列3组3胆拖
                convertView = inflater.inflate(R.layout.item_library_zu3_dantuo, parent, false);
            } else if (type == 9) {//福彩3D排列3组选单式
                convertView = inflater.inflate(R.layout.item_3d_number_zuxuansingle, parent, false);
            } else if (type == 10) {//福彩3D排列3组6复式
                convertView = inflater.inflate(R.layout.item_library_zu6_muti, parent, false);
            } else if (type == 11) {//福彩3D排列3组6胆拖
                convertView = inflater.inflate(R.layout.item_library_zu6_dantuo, parent, false);
            } else if (type == 12) {//排列5直选单式
                convertView = inflater.inflate(R.layout.item_p5_singles, parent, false);
            } else if (type == 13) {//排列5直选定位
                convertView = inflater.inflate(R.layout.item_library_p5_muti, parent, false);
            }
        }
        PushSingleSummaryModel.NumberDetail numberDetail=list.get(position);
        String issue = numberDetail.getIssue();
//        if (position < getCount() - 1) {
//            PushSingleSummaryModel.NumberDetail nextNumber = list.get(position + 1);
//            convertView.findViewById(R.id.seperate_line1).setVisibility(nextNumber.getIssue() .equals(issue)? View.GONE:View.VISIBLE);
//        } else {
//            convertView.findViewById(R.id.seperate_line1).setVisibility(View.VISIBLE);
//        }
        //convertView.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
        String fiveNumber=numberDetail.getFiveNumber();
        int lotteryId=numberDetail.getLotteryId();
        boolean isShowNext = (fiveNumber.contains(";") && fiveNumber.split(";").length == 6);//是否展示箭头图标
        convertView.setOnClickListener(null);
        if(isShowNext){//点击跳转号码详情
            convertView.setOnClickListener((v -> {
                Intent it = new Intent(context, NumLibraryDetailActivity.class);
                it.putExtra("id", numberDetail.getId());
                it.putExtra("issue", numberDetail.getIssue());
                it.putExtra("flag",sparseIntArray.get(numberDetail.getLotteryId()));
                context.startActivity(it);
            }));
        }
        if (type == 0||type == 3||type == 12) {//双色球 大乐透 排列5 单式
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }
            convertView.findViewById(R.id.next_image).setVisibility(isShowNext?View.VISIBLE:View.GONE);
            if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                String[] filter_singel=fiveNumber.split(";");
                if(filter_singel.length==5||filter_singel.length==6){//5注过滤好吗 或者大于5注过滤号码
                    int max = 5 ;//最大注数
                    for (int i=0;i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i,type);
                    }
                }else{//小于5注过滤号码
                    int max = filter_singel.length ;//最大注数
                    for (int i=0;i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i,type);
                    }
                }
            } else if (!fiveNumber.contains(";")){//单单一注单式
                addSingleData((LinearLayout) layout.getChildAt(0), fiveNumber, 0,type);
            }
        }else if(type == 1){//双色球 大乐透复式
            String[] arr = fiveNumber.split("#");
            String[] redArr = arr[0].split(" ");
            String[] blueArr = arr[1].split(" ");
            MyGridView gv_red = (MyGridView) convertView.findViewById(R.id.filter_double_gv_red);
            MyGridView gv_blue = (MyGridView) convertView.findViewById(R.id.filter_double_gv_blue);
            MyFilterDoubleModeRedAdapter adapter_red = new MyFilterDoubleModeRedAdapter(Arrays.asList(redArr), context, R.layout.item_filter_double_redball);
            MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blueArr), context, R.layout.item_filter_double_blueball,0);
            gv_red.setAdapter(adapter_red);
            gv_blue.setAdapter(adapter_blue);
        }else if(type==2||type==4){//双色球 大乐透胆拖
            MyGridView gv_dan= (MyGridView) convertView.findViewById(R.id.filter_dantuo_dan_gv);
            MyGridView gv_tuo= (MyGridView) convertView.findViewById(R.id.filter_dantuo_tuo_gv);
            MyGridView gv_lan= (MyGridView) convertView.findViewById(R.id.filter_dantuo_lan_gv);
            String[] arr = fiveNumber.split("#");
            String[] red1Arr = arr[0].split(" ");
            String[] red2Arr = arr[1].split(" ");
            String[] blueArr = arr[2].split(" ");
            MyFilterDoubleModeRedAdapter adapter_red_dan = new MyFilterDoubleModeRedAdapter(Arrays.asList(red1Arr), context, R.layout.item_filter_double_redball);
            MyFilterDoubleModeRedAdapter adapter_red_tuo = new MyFilterDoubleModeRedAdapter(Arrays.asList(red2Arr), context, R.layout.item_filter_double_redball);
            gv_dan.setAdapter(adapter_red_dan);
            gv_tuo.setAdapter(adapter_red_tuo);
            if(type==2){//双色球
                MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blueArr), context, R.layout.item_filter_double_blueball,1);
                gv_lan.setAdapter(adapter_blue);
            }else{//大乐透
                String[] blue2Arr = arr[3].split(" ");
                MyGridView gv_lan_houTuo= (MyGridView) convertView.findViewById(R.id.filter_dantuo_lan_houTuo_gv);
                MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(blueArr.length==0?Arrays.asList(new String[]{"100"}):Arrays.asList(blueArr), context, R.layout.item_filter_double_blueball,1);
                MyFilterDoubleModeBlueAdapter adapter_blue_houTuo = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blue2Arr), context, R.layout.item_filter_double_blueball,2);
                gv_lan.setAdapter(adapter_blue);
                gv_lan_houTuo.setAdapter(adapter_blue_houTuo);
            }
        }else  if(type == 5){//福彩3D排列3直选单式
            String[] numArray=CommonUtil.stringToArray(fiveNumber);//号码
            TextView num1=((TextView)convertView.findViewById(R.id.zhixuan_single_bai));
            TextView num2=((TextView)convertView.findViewById(R.id.zhixuan_single_shi));
            TextView num3=((TextView)convertView.findViewById(R.id.zhixuan_single_ge));
            num1.setText(numArray[0]);
            num2.setText(numArray[1]);
            num3.setText(numArray[2]);
            num1.setBackgroundResource(R.drawable.lottery_ball_big);
            num2.setBackgroundResource(R.drawable.lottery_ball_big);
            num3.setBackgroundResource(R.drawable.lottery_ball_big);
        } else if(type==6){//福彩3D排列3直选定位
            String[] numArray=fiveNumber.split("-");
            String[] bai=CommonUtil.stringToArray(numArray[0]);//百位数据
            String[] shi=CommonUtil.stringToArray(numArray[1]);//十位数据
            String[] ge=CommonUtil.stringToArray(numArray[2]);//个位数据
            TouchLessGridView gv_bai= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
            TouchLessGridView gv_shi= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
            TouchLessGridView gv_ge= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
            MyNumLibrary3DAdapter adapter_bai = new MyNumLibrary3DAdapter(Arrays.asList(bai), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_shi = new MyNumLibrary3DAdapter(Arrays.asList(shi), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_ge = new MyNumLibrary3DAdapter(Arrays.asList(ge), context,new ArrayList<String>(),type,false);
            adapter_bai.setLocationCount(0);//百位标记
            adapter_shi.setLocationCount(1);//十位标记
            adapter_ge.setLocationCount(2);//个位标记
            gv_bai.setAdapter(adapter_bai);
            gv_shi.setAdapter(adapter_shi);
            gv_ge.setAdapter(adapter_ge);
        }else if(type==7||type==10){//福彩3D排列3组3组6复式
            String[] numArray=CommonUtil.stringToArray(fiveNumber);//号码
            TouchLessGridView gv_zu3= (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_muti_gv);
            MyNumLibrary3DAdapter adapter_zu3 = new MyNumLibrary3DAdapter(Arrays.asList(numArray), context,new ArrayList<String>(),type,false);
            gv_zu3.setAdapter(adapter_zu3);
        }else if(type==8||type==11){//福彩3D排列3组3组6胆拖
            String[] numArray=fiveNumber.split(",");
            String[] dan=CommonUtil.stringToArray(numArray[0]);//胆数据
            String[] tuo=CommonUtil.stringToArray(numArray[1]);//拖数据
            TouchLessGridView gv_dan= (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_dan_gv);
            TouchLessGridView gv_tuo= (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_tuo_gv);
            MyNumLibrary3DAdapter adapter_dan = new MyNumLibrary3DAdapter(Arrays.asList(dan), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_tuo = new MyNumLibrary3DAdapter(Arrays.asList(tuo), context,new ArrayList<String>(),type,false);
            gv_dan.setAdapter(adapter_dan);
            gv_tuo.setAdapter(adapter_tuo);
        }else if(type==9){//福彩3D排列3组选单式
            int playType=numberDetail.getPlayType();//区分组选单式和直选单式13或18
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }//隐藏箭头
            convertView.findViewById(R.id.next_image).setVisibility(isShowNext? View.VISIBLE : View.GONE);
            if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                String[] filter_singel = fiveNumber.split(";");
                if (filter_singel.length == 5 || filter_singel.length == 6) {//5注过滤好吗 或者大于5注过滤号码
                    int max = 5;//最大注数
                    for (int i = 0; i < max; i++) {
                        addD3SingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i,playType,lotteryId);
                    }
                } else {//小于5注过滤号码
                    int max = filter_singel.length;//最大注数
                    for (int i = 0; i < max; i++) {
                        addD3SingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, playType,lotteryId);
                    }
                }
            } else if (!fiveNumber.contains(";")) {//单单一注单式
                addD3SingleData((LinearLayout) layout.getChildAt(0), fiveNumber, 0, playType,lotteryId);
            }
        } else if(type==13){//排列5直选定位
            String[] numArray=fiveNumber.split("-");
            String[] wan=CommonUtil.stringToArray(numArray[0]);//万位数据
            String[] qian=CommonUtil.stringToArray(numArray[1]);//千位数据
            String[] bai=CommonUtil.stringToArray(numArray[2]);//百位数据
            String[] shi=CommonUtil.stringToArray(numArray[3]);//十位数据
            String[] ge=CommonUtil.stringToArray(numArray[4]);//个位数据
            TouchLessGridView gv_wan= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_wan_gv);
            TouchLessGridView gv_qian= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_qian_gv);
            TouchLessGridView gv_bai= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
            TouchLessGridView gv_shi= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
            TouchLessGridView gv_ge= (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
            MyNumLibrary3DAdapter adapter_wan = new MyNumLibrary3DAdapter(Arrays.asList(wan), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_qian = new MyNumLibrary3DAdapter(Arrays.asList(qian), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_bai = new MyNumLibrary3DAdapter(Arrays.asList(bai), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_shi = new MyNumLibrary3DAdapter(Arrays.asList(shi), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_ge = new MyNumLibrary3DAdapter(Arrays.asList(ge), context,new ArrayList<String>(),type,false);
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
        }
        return convertView;
    }
    /**
     * 添加单式布局
     * @param layout
     * @param numberStr
     * @param position
     */
    private void addSingleData(LinearLayout layout, String numberStr, int position,int type) {
        if (position == 0) {
            layout.findViewById(R.id.single_flag_text).setVisibility(View.VISIBLE);
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr;
        numberArr=type==12?CommonUtil.stringToArray(numberStr):numberStr.split(" ");//排列5和双色球大乐透格式不一致
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = type==12?numberArr[i-1]:CommonUtil.preZeroForBall(numberArr[i-1]);
            text.setText(dataStr);
        }
    }

    /**
     * 添加单式布局
     *
     * @param layout
     * @param numberStr
     * @param position
     */
    private void addD3SingleData(LinearLayout layout, String numberStr, int position, int playType,int lotteryId) {
        if (position == 0) {
            TextView title= (TextView) layout.findViewById(R.id.single_flag_text);
            title.setVisibility(View.VISIBLE);
            title.setText(lotteryId==10001?(playType==13?"直选单式":"组选单式"):(playType==1?"直选单式":"组选单式"));
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr = CommonUtil.stringToArray(numberStr);
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = numberArr[i - 1];
            text.setText(dataStr);
        }
    }

}

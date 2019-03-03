package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyNumLibrary3DAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.SingletonSelectSingleCheck;
import com.cp2y.cube.model.SelectSingleNumModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.widgets.TouchLessGridView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by js on 2017/1/9.
 */
public class D3SelectAdapter extends BaseAdapter{
    private MyInterface.partNotify notify;
    private int flag=-1;//2,福彩3D  3,排列3
    private boolean isChecked=true;
    private Context context;
    private List<SelectSingleNumModel.Detail> list = new ArrayList<>();
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener=((buttonView, isChecked) -> {
        int pos= (int) buttonView.getTag(R.id.position);
        checkdSave(pos,isChecked);//单独勾选
    });


    public D3SelectAdapter(Context context,int flag) {
        this.context = context;
        this.flag=flag;
        inflater = LayoutInflater.from(context);
    }
    public void setNotify(MyInterface.partNotify notify){
        this.notify=notify;
    }
    public void loadData(List<SelectSingleNumModel.Detail> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void reLoadData(List<SelectSingleNumModel.Detail> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SelectSingleNumModel.Detail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(list.get(position).getFiveNumber())) return 0;//返回头部,显示头部
        SelectSingleNumModel.Detail number = list.get(position);
        String fiveNumber = number.getFiveNumber();
        int playType=number.getPlayType();//编码
        if (fiveNumber.contains(";")) {
            if (fiveNumber.split(";").length == 5) {
                //过滤产生等于5注单式票
                return 5;
            } else if (fiveNumber.split(";").length == 6) {
                //过滤产生大于5注单式票
                return 8;
            } else {
                return 5;//过滤产生不到5注
            }
        } else {//选号保存类型
            return getFlagType(playType);
        }
    }

    public int getFlagType(int playType){
        if(flag==2){
            return MapUtils.D3_LIBRARY_TYPE.get(playType);
        }else{
            return MapUtils.P3_LIBRARY_TYPE.get(playType);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    //全选 全不选
    private void checkedAll(boolean isCheckedAll){
        if(flag==2){
            SingletonSelectSingleCheck.clearD3Data();//清空数据
            if(isCheckedAll){//全选
                for(int i=0,size=list.size();i<size;i++){
                    SingletonSelectSingleCheck.registerD3Service(i);
                }
            }
        }else{
            SingletonSelectSingleCheck.clearP3Data();//清空数据
            if(isCheckedAll){//全选
                for(int i=0,size=list.size();i<size;i++){
                    SingletonSelectSingleCheck.registerP3Service(i);
                }
            }
        }
        notify.notifyListView();
    }
    //单勾选
    private void checkdSave(int position,boolean isChecked){
        if(flag==2){
            SingletonSelectSingleCheck.removeD3Map(0);//先删除全选
            if(isChecked){
                SingletonSelectSingleCheck.registerD3Service(position);
            }else{
                SingletonSelectSingleCheck.removeD3Map(position);
            }
            if (list.size() - SingletonSelectSingleCheck.getD3Obj().size() <= 2) {
                if (SingletonSelectSingleCheck.getD3Obj().size() == list.size() - 1) {
                    SingletonSelectSingleCheck.registerD3Service(0);
                } else if(SingletonSelectSingleCheck.getD3Obj().size() == list.size() - 2){
                    SingletonSelectSingleCheck.removeD3Map(0);
                }
                notify.notifyListView();
            }
        }else{
            SingletonSelectSingleCheck.removeP3Map(0);//先删除全选
            if(isChecked){
                SingletonSelectSingleCheck.registerP3Service(position);
            }else{
                SingletonSelectSingleCheck.removeP3Map(position);
            }
            if (list.size() - SingletonSelectSingleCheck.getP3Obj().size() <= 2) {
                if (SingletonSelectSingleCheck.getP3Obj().size() == list.size() - 1) {
                    SingletonSelectSingleCheck.registerP3Service(0);
                } else if(SingletonSelectSingleCheck.getP3Obj().size() == list.size() - 2){
                    SingletonSelectSingleCheck.removeP3Map(0);
                }
                notify.notifyListView();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int orgType=getItemViewType(position);
        int type = orgType==8?5:orgType;//orgType=8为大于5注单式
        if (convertView == null) {
            if(type == 0){//头布局
                convertView = inflater.inflate(R.layout.item_select_single_head, null);
            }else if (type == 1) {//直选单式
                convertView = inflater.inflate(R.layout.item_selectsingle_zhixuan_single, viewGroup, false);
            } else if (type == 2) {//直选定位
                convertView = inflater.inflate(R.layout.item_selectsingle_zhixuan_location, viewGroup, false);
            } else if (type == 3) {//组3复式
                convertView = inflater.inflate(R.layout.item_selectsingle_zu3_muti, viewGroup, false);
            } else if (type == 4) {//组3胆拖
                convertView = inflater.inflate(R.layout.item_selectsingle_zu3_dantuo, viewGroup, false);
            } else if (type == 5) {//组选单式
                convertView = inflater.inflate(R.layout.item_selectsingle_zuxuansingle, viewGroup, false);
            } else if (type == 6) {//组6复式
                convertView = inflater.inflate(R.layout.item_selectsingle_zu6_muti, viewGroup, false);
            } else if (type == 7) {//组6胆拖
                convertView = inflater.inflate(R.layout.item_selectsingle_zu6_dantuo, viewGroup, false);
            }
        }
        if (orgType == 4) return convertView;
        SelectSingleNumModel.Detail number = list.get(position);
        String issue = number.getIssue();
        if (position < getCount() - 1) {
            SelectSingleNumModel.Detail nextNumber = list.get(position + 1);
            convertView.findViewById(R.id.seperate_line1).setVisibility(nextNumber.getIssue() .equals(issue)? View.GONE:View.VISIBLE);
        } else {
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.VISIBLE);
        }
        CheckBox check= (CheckBox) convertView.findViewById(R.id.select_single_check);
        check.setTag(R.id.position,position);
        if (type == 0) {
            convertView.findViewById(R.id.seperate_line2).setVisibility(position == 0 ? View.GONE:View.VISIBLE);
            TextView lotteryName= (TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryType);
            TextView date= (TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryYear);
            lotteryName.setText(flag==2?"福彩3D":"排列3");
            date.setText(list.get(1).getIssue()+"期");
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
            check.setOnClickListener((v -> {
                checkedAll(check.isChecked());//全选
            }));
            boolean checked=flag==2?SingletonSelectSingleCheck.isD3Contains(position):SingletonSelectSingleCheck.isP3Contains(position);
            check.setChecked(checked);//是否选择
            return convertView;
        }
        //List<String> list = number.getList();//反序列化文件
        String fiveNumber=number.getFiveNumber();
        if (fiveNumber == null || fiveNumber.length() == 0) {
            convertView.findViewById(R.id.library_root).setVisibility(View.GONE);
            return convertView;//没有读取到内容
        }
        convertView.findViewById(R.id.library_root).setVisibility(View.VISIBLE);
        String numberStr = fiveNumber;
        boolean checked=flag==2?SingletonSelectSingleCheck.isD3Contains(position):SingletonSelectSingleCheck.isP3Contains(position);
        check.setChecked(checked);//是否选择
        check.setOnCheckedChangeListener(checkedChangeListener);
        if(type == 1){//直选单式
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
        } else if(type==2){//直选定位
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
        }else if(type==3||type==6){//组3组6复式
            String[] numArray=CommonUtil.stringToArray(fiveNumber);//号码
            TouchLessGridView gv_zu3= (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_muti_gv);
            MyNumLibrary3DAdapter adapter_zu3 = new MyNumLibrary3DAdapter(Arrays.asList(numArray), context,new ArrayList<String>(),type,false);
            gv_zu3.setAdapter(adapter_zu3);
        }else if(type==4||type==7){//组3组6胆拖
            String[] numArray=fiveNumber.split(",");
            String[] dan=CommonUtil.stringToArray(numArray[0]);//胆数据
            String[] tuo=CommonUtil.stringToArray(numArray[1]);//拖数据
            TouchLessGridView gv_dan= (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_dan_gv);
            TouchLessGridView gv_tuo= (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_tuo_gv);
            MyNumLibrary3DAdapter adapter_dan = new MyNumLibrary3DAdapter(Arrays.asList(dan), context,new ArrayList<String>(),type,false);
            MyNumLibrary3DAdapter adapter_tuo = new MyNumLibrary3DAdapter(Arrays.asList(tuo), context,new ArrayList<String>(),type,false);
            gv_dan.setAdapter(adapter_dan);
            gv_tuo.setAdapter(adapter_tuo);
        }else if(type==5){//组选单式
            int playType=number.getPlayType();//区分组选单式和直选单式13或18
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }//隐藏箭头
            convertView.findViewById(R.id.next_image).setVisibility(orgType == 8 ? View.VISIBLE : View.GONE);
            if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                String[] filter_singel = numberStr.split(";");
                if (filter_singel.length == 5 || filter_singel.length == 6) {//5注过滤好吗 或者大于5注过滤号码
                    int max = 5;//最大注数
                    for (int i = 0; i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i,playType);
                    }
                } else {//小于5注过滤号码
                    int max = filter_singel.length;//最大注数
                    for (int i = 0; i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, playType);
                    }
                }
            } else if (!fiveNumber.contains(";")) {//单单一注单式
                addSingleData((LinearLayout) layout.getChildAt(0), numberStr, 0, playType);
            }
        }
        return convertView;
    }

    /**
     * 添加单式布局
     *
     * @param layout
     * @param numberStr
     * @param position
     */
    private void addSingleData(LinearLayout layout, String numberStr, int position, int playType) {
        if (position == 0) {
            TextView title= (TextView) layout.findViewById(R.id.single_flag_text);
            title.setVisibility(View.VISIBLE);
            title.setText(flag==2?(playType==13?"直选单式":"组选单式"):(flag==3?(playType==1?"直选单式":"组选单式"):(playType==94?"直选单式":"组选单式")));
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

    /**
     * 添加复式和胆拖布局
     */
    private void addMutiData(FlowLayout mutiLayout, String[] numArr, boolean isRed) {
        mutiLayout.removeAllViews();
        for (String num : numArr) {
            TextView ball = (TextView) inflater.inflate(isRed?R.layout.red_ball_text:R.layout.blue_ball_text, null);
            String val = CommonUtil.preZeroForBall(num);
            ball.setText(val);
            //设置初始背景,修复复用错乱
            ball.setBackgroundResource(R.drawable.lottery_ball);
            mutiLayout.addView(ball);
            FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) ball.getLayoutParams();
            lp.rightMargin = DisplayUtil.dip2px(4f);
            lp.bottomMargin = DisplayUtil.dip2px(10f);
            ball.setLayoutParams(lp);
        }
    }

}

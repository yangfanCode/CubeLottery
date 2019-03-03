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
public class P5SelectAdapter extends BaseAdapter{
    private MyInterface.partNotify notify;
    private Context context;
    private List<SelectSingleNumModel.Detail> list = new ArrayList<>();
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener=((buttonView, isChecked) -> {
        int pos= (int) buttonView.getTag(R.id.position);
        checkdSave(pos,isChecked);//单独勾选
    });


    public P5SelectAdapter(Context context) {
        this.context = context;
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
            if (fiveNumber.split(";").length == 6) {
                //过滤产生大于5注单式票
                return 3;
            } else {
                return 1;//过滤产生小于等于5注
            }
        } else {//选号保存类型
            return MapUtils.P5_LIBRARY_TYPE.get(playType);
        }
    }


    @Override
    public int getViewTypeCount() {
        return 4;
    }

    //全选 全不选
    private void checkedAll(boolean isCheckedAll){
        if(isCheckedAll){//全选
            SingletonSelectSingleCheck.clearP5Data();//清空数据
            for(int i=0,size=list.size();i<size;i++){
                SingletonSelectSingleCheck.registerP5Service(i);
            }
        }else{//全不选
            SingletonSelectSingleCheck.clearP5Data();
        }
        notify.notifyListView();
    }
    //单勾选
    private void checkdSave(int position,boolean isChecked){
        SingletonSelectSingleCheck.removeP5Map(0);//先删除全选
        if(isChecked){
            SingletonSelectSingleCheck.registerP5Service(position);
        }else{
            SingletonSelectSingleCheck.removeP5Map(position);
        }
        if (list.size() - SingletonSelectSingleCheck.getP5Obj().size() <= 2) {
            if (SingletonSelectSingleCheck.getP5Obj().size() == list.size() - 1) {
                SingletonSelectSingleCheck.registerP5Service(0);
            } else if(SingletonSelectSingleCheck.getP5Obj().size() == list.size() - 2){
                SingletonSelectSingleCheck.removeP5Map(0);
            }
            notify.notifyListView();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int orgType=getItemViewType(position);
        int type = orgType==3?1:orgType;//orgType=3为大于5注单式
        if (convertView == null) {
            if(type == 0){//头布局
                convertView = inflater.inflate(R.layout.item_select_single_head, null);
            }else if (type == 1) {//直选单式
                convertView = inflater.inflate(R.layout.item_p5_selectsingle_singles, viewGroup, false);
            } else if (type == 2) {//直选定位
                convertView = inflater.inflate(R.layout.item_selectsingle_p5_muti, viewGroup, false);
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
            lotteryName.setText("排列5");
            date.setText(list.get(1).getIssue()+"期");
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
            check.setOnClickListener((v -> {
                checkedAll(check.isChecked());//全选
            }));
            check.setChecked(SingletonSelectSingleCheck.isP5Contains(position));//是否选择
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
        check.setChecked(SingletonSelectSingleCheck.isP5Contains(position));//修改选择状态
        check.setOnCheckedChangeListener(checkedChangeListener);
        if(type == 1){//单式
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }//隐藏箭头
            convertView.findViewById(R.id.next_image).setVisibility(orgType == 3 ? View.VISIBLE : View.GONE);
            if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                String[] filter_singel = numberStr.split(";");
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
            } else if (!fiveNumber.contains(";")) {//单单一注单式
                addSingleData((LinearLayout) layout.getChildAt(0), numberStr, 0);
            }
        } else if(type==2){//直选定位
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
     *
     * @param layout
     * @param numberStr
     * @param position
     */
    private void addSingleData(LinearLayout layout, String numberStr, int position) {
        if (position == 0) {
            TextView title= (TextView) layout.findViewById(R.id.single_flag_text);
            title.setVisibility(View.VISIBLE);
            title.setText("单式");
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

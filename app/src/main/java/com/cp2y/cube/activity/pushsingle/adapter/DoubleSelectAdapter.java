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
import com.cp2y.cube.adapter.MyFilterDoubleModeBlueAdapter;
import com.cp2y.cube.adapter.MyFilterDoubleModeRedAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonSelectSingleCheck;
import com.cp2y.cube.model.SelectSingleNumModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by js on 2017/1/9.
 */
public class DoubleSelectAdapter extends BaseAdapter {
    private MyInterface.partNotify notify;
    private Context context;
    private List<SelectSingleNumModel.Detail> list = new ArrayList<>();
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = ((buttonView, isChecked) -> {
        int pos = (int) buttonView.getTag(R.id.position);
        checkdSave(pos, isChecked);//单独勾选
    });


    public DoubleSelectAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setNotify(MyInterface.partNotify notify) {
        this.notify = notify;
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
        if (fiveNumber.contains(";")) {
            if (fiveNumber.split(";").length == 5) {
                //过滤产生等于5注单式票
                return 1;
            } else if (fiveNumber.split(";").length == 6) {
                //过滤产生大于5注单式票
                return 5;
            } else {
                return 1;//过滤产生不到5注
            }
        } else {
            if (fiveNumber.split("#").length == 2) {
                return 2;//只有一个#号为复式
            } else if (!fiveNumber.contains("#")) {
                return 1;//没有#为单式
            } else {//胆拖
                return 3;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    //全选 全不选
    private void checkedAll(boolean isCheckedAll) {
        if (isCheckedAll) {//全选
            SingletonSelectSingleCheck.clearDoubleData();//清空数据
            for (int i = 0, size = list.size(); i < size; i++) {
                SingletonSelectSingleCheck.registerDoubleService(i);
            }
        } else {//全不选
            SingletonSelectSingleCheck.clearDoubleData();
        }
        notify.notifyListView();
    }

    //单勾选
    private void checkdSave(int position, boolean isChecked) {
        SingletonSelectSingleCheck.removeDoubleMap(0);//先删除全选
        if (isChecked) {
            SingletonSelectSingleCheck.registerDoubleService(position);
        } else {
            SingletonSelectSingleCheck.removeDoubleMap(position);
        }
        if (list.size() - SingletonSelectSingleCheck.getDoubleObj().size() <= 2) {
            if (SingletonSelectSingleCheck.getDoubleObj().size() == list.size() - 1) {
                SingletonSelectSingleCheck.registerDoubleService(0);
            } else if(SingletonSelectSingleCheck.getDoubleObj().size() == list.size() - 2){
                SingletonSelectSingleCheck.removeDoubleMap(0);
            }
            notify.notifyListView();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int orgType = getItemViewType(position);
        int type = orgType % 4;
        if (convertView == null) {
            if (type == 0) {
                convertView = inflater.inflate(R.layout.item_select_single_head, null);
            } else if (type == 1) {
                convertView = inflater.inflate(R.layout.item_selectsingle_single, null);
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.item_selectsingle_muti, null);
            } else if (type == 3) {
                convertView = inflater.inflate(R.layout.item_selectsingle_dan, null);
            }
        }
        if (orgType == 4) return convertView;
        SelectSingleNumModel.Detail number = list.get(position);
        String issue = number.getIssue();
        if (position < getCount() - 1) {
            SelectSingleNumModel.Detail nextNumber = list.get(position + 1);
            convertView.findViewById(R.id.seperate_line1).setVisibility(nextNumber.getIssue().equals(issue) ? View.GONE : View.VISIBLE);
        } else {
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.VISIBLE);
        }
        CheckBox check = (CheckBox) convertView.findViewById(R.id.select_single_check);
        check.setTag(R.id.position, position);
        if (type == 0) {
            convertView.findViewById(R.id.seperate_line2).setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            TextView lotteryName = (TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryType);
            TextView date = (TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryYear);
            lotteryName.setText("双色球");
            date.setText(list.get(1).getIssue() + "期");
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.GONE);
            check.setOnClickListener((v -> {
                checkedAll(check.isChecked());//全选
            }));
            check.setChecked(SingletonSelectSingleCheck.isDoubleContains(position));//是否选择
            return convertView;
        }
        //List<String> list = number.getList();//反序列化文件
        String fiveNumber = number.getFiveNumber();
        if (fiveNumber == null || fiveNumber.length() == 0) {
            convertView.findViewById(R.id.library_root).setVisibility(View.GONE);
            return convertView;//没有读取到内容
        }
        convertView.findViewById(R.id.library_root).setVisibility(View.VISIBLE);
        String numberStr = fiveNumber;
        if (type == 1) {
            check.setChecked(SingletonSelectSingleCheck.isDoubleContains(position));//修改选择状态
            check.setOnCheckedChangeListener(checkedChangeListener);
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }
            convertView.findViewById(R.id.next_image).setVisibility(orgType == 5 ? View.VISIBLE : View.GONE);
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
        } else if (type == 2) {
            check.setChecked(SingletonSelectSingleCheck.isDoubleContains(position));//修改选择状态
            check.setOnCheckedChangeListener(checkedChangeListener);
            String[] arr = numberStr.split("#");
            String[] redArr = arr[0].split(" ");
            String[] blueArr = arr[1].split(" ");
            MyGridView gv_red = (MyGridView) convertView.findViewById(R.id.filter_double_gv_red);
            MyGridView gv_blue = (MyGridView) convertView.findViewById(R.id.filter_double_gv_blue);
            MyFilterDoubleModeRedAdapter adapter_red = new MyFilterDoubleModeRedAdapter(Arrays.asList(redArr), context, R.layout.item_filter_double_redball);
            MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blueArr), context, R.layout.item_filter_double_blueball, 0);
            gv_red.setAdapter(adapter_red);
            gv_blue.setAdapter(adapter_blue);
        } else if (type == 3) {
            check.setChecked(SingletonSelectSingleCheck.isDoubleContains(position));//修改选择状态
            check.setOnCheckedChangeListener(checkedChangeListener);
            String[] arr = numberStr.split("#");
            String[] red1Arr = arr[0].split(" ");
            String[] red2Arr = arr[1].split(" ");
            String[] blueArr = arr[2].split(" ");
            MyGridView gv_dan = (MyGridView) convertView.findViewById(R.id.filter_dantuo_dan_gv);
            MyGridView gv_tuo = (MyGridView) convertView.findViewById(R.id.filter_dantuo_tuo_gv);
            MyGridView gv_lan = (MyGridView) convertView.findViewById(R.id.filter_dantuo_lan_gv);
            MyFilterDoubleModeRedAdapter adapter_red_dan = new MyFilterDoubleModeRedAdapter(Arrays.asList(red1Arr), context, R.layout.item_filter_double_redball);
            MyFilterDoubleModeRedAdapter adapter_red_tuo = new MyFilterDoubleModeRedAdapter(Arrays.asList(red2Arr), context, R.layout.item_filter_double_redball);
            MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(Arrays.asList(blueArr), context, R.layout.item_filter_double_blueball, 1);
            gv_dan.setAdapter(adapter_red_dan);
            gv_tuo.setAdapter(adapter_red_tuo);
            gv_lan.setAdapter(adapter_blue);
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
            layout.findViewById(R.id.single_flag_text).setVisibility(View.VISIBLE);
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr = numberStr.split(" ");
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = CommonUtil.preZeroForBall(numberArr[i - 1]);
            text.setText(dataStr);
        }
    }

    /**
     * 添加复式和胆拖布局
     */
    private void addMutiData(FlowLayout mutiLayout, String[] numArr, boolean isRed) {
        mutiLayout.removeAllViews();
        for (String num : numArr) {
            TextView ball = (TextView) inflater.inflate(isRed ? R.layout.red_ball_text : R.layout.blue_ball_text, null);
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

package com.cp2y.cube.activity.numlibrary.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.NewNumberModel;
import com.cp2y.cube.utils.CloneUtil;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.FileUtils;
import com.cp2y.cube.utils.MapUtils;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by js on 2017/1/9.
 */
public class NumberAdapter extends BaseAdapter{

    private Context context;
    private List<NewNumberModel.NumberData> data = new ArrayList<>();
    private Map<String, Integer> issues = new HashMap<>();
    private Map<String, NewNumberModel.Drawer> drawers = new HashMap<>();
    private LayoutInflater inflater;

    public NumberAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void reloadData(Map<String,List<NewNumberModel.NumberData>> data, List<NewNumberModel.Drawer> drawers) {
        this.data.clear();
        this.issues.clear();
        this.drawers.clear();
        addData(data, drawers);
    }

    public void removeData(int position) {
        NewNumberModel.NumberData number = data.get(position);
        String issue = number.getIssue();
        int count = issues.get(issue) - 1;
        data.remove(position);//删除当前记录
        FileUtils.removeLibraryFile(issue, number.getId(),0);//删除本地文件
        if (count > 0) {
            issues.put(issue, count);
            NewNumberModel.Drawer drawer = drawers.get(issue);
            if (drawer != null) {
               // long price = drawer.getPrize() - drawer.getPrize();
                drawer.setPrize("0");
            }
        } else {
            issues.remove(issue);
            drawers.remove(issue);
            //移除头信息
            int index = position - 1;
            if (index >= 0) data.remove(index);
        }
        notifyDataSetChanged();
    }

    public void addData(Map<String,List<NewNumberModel.NumberData>> data, List<NewNumberModel.Drawer> drawers) {
        for (Iterator<Map.Entry<String, List<NewNumberModel.NumberData>>> it = data.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<NewNumberModel.NumberData>> entry = it.next();
            List<NewNumberModel.NumberData> list = entry.getValue();
            for (NewNumberModel.NumberData number: list) {
                String issue = number.getIssue();
                if (!issues.containsKey(issue)) {
                    issues.put(issue , 0);
                    NewNumberModel.NumberData numberHead = new NewNumberModel.NumberData();
                    CloneUtil.cloneObject(number, numberHead);
                    numberHead.setHead(true);
                    this.data.add(numberHead);//添加头部
                }
                issues.put(issue , issues.get(issue) + 1);//每次都加1
                this.data.add(number);
            }
            this.drawers.clear();
            for (NewNumberModel.Drawer drawer: drawers) {//开奖信息
                this.drawers.put(drawer.getIssue(), drawer);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public NewNumberModel.NumberData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        NewNumberModel.NumberData number = data.get(position);
        if (number.isHead()) return 0;//返回头部,显示头部
        String fiveNumber = number.getFiveNumber();
//        if (list == null) {
//            File libFile = FileUtils.getLibraryFile(number.getIssue(), number.getId());
//            list = FileUtils.readMaxLine(libFile, 6);//反序列化文件,读取6条
//            number.setList(list);
//        }
//        if (list.size() == 0) {//使用单式票结构
//            return 1;
//        } else if (list.size() > 5){//使用单式票结构
//            return 5;
//        } else if (list.size() > 1) {//使用单式票结构
//            return 1;
//        } else {
//            String numberStr = list.get(0);
//            if (!numberStr.contains("#")) {//没有#号为单式票
//                return 1;
//            } else if (numberStr.indexOf("#") == numberStr.lastIndexOf("#")) {//只有一个#号为复式
//                return 2;
//            } else {
//                return 3;
//            }
//        }
            if(fiveNumber.contains(";")){
                if(fiveNumber.split(";").length==5){
                    //过滤产生等于5注单式票
                    return 1;
                }else if(fiveNumber.split(";").length==6){
                    //过滤产生大于5注单式票
                    return 5;
                }else{
                    return 1;//过滤产生不到5注
                }
            }else{
                if(fiveNumber.split("#").length==2){
                    return 2;//只有一个#号为复式
                }else if(!fiveNumber.contains("#")){
                    return 1;//没有#为单式
                }else{//胆拖
                    return 3;
                }
            }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int orgType = getItemViewType(position);
        int type = orgType % 4;
        if (convertView == null) {
            if (type == 0) {
                convertView = inflater.inflate(R.layout.item_number_head, null);
            } else if (type == 1) {
                convertView = inflater.inflate(R.layout.item_number_single, null);
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.item_number_muti, null);
            } else if (type == 3) {
                convertView = inflater.inflate(R.layout.item_number_dan, null);
            }
        }
        if (orgType == 4) return convertView;
        NewNumberModel.NumberData number = data.get(position);
        String issue = number.getIssue();
        if (type == 0) {
            convertView.findViewById(R.id.seperate_line2).setVisibility(position == 0 ? View.GONE:View.VISIBLE);
        }
        if (position < getCount() - 1) {
            NewNumberModel.NumberData nextNumber = data.get(position + 1);
            convertView.findViewById(R.id.seperate_line1).setVisibility(nextNumber.getIssue() .equals(issue)? View.GONE:View.VISIBLE);
        } else {
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.VISIBLE);
        }
        String[] nums = drawers.containsKey(issue) ? drawers.get(issue).getDrawNumber().split(","): MapUtils.DEF_DOUBLE_BALL;//开奖号
        if (type == 0) {
            LinearLayout openNumbers = (LinearLayout) convertView.findViewById(R.id.app_num_library_ll_myNum);
            for (int i = 0; i < openNumbers.getChildCount(); i++) {
                TextView textView = (TextView) openNumbers.getChildAt(i);
                textView.setText(nums[i]);
            }
            TextView issueText = (TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryYear);
            issueText.setText(String.valueOf(issue).concat("期开奖"));
            String price = drawers.containsKey(issue) ? drawers.get(issue).getPrize():"-1";
            //convertView.findViewById(R.id.price_layout).setVisibility(drawers.containsKey(issue) ? View.VISIBLE:View.GONE);
            convertView.findViewById(R.id.price_sign).setVisibility("0".equals(price)||"-1".equals(price)?View.GONE:View.VISIBLE); //"￥"隐藏
            convertView.findViewById(R.id.price_text).setVisibility(nums[0].equals("-")?View.GONE:View.VISIBLE); //"--"隐藏
            TextView textView = (TextView) convertView.findViewById(R.id.price_text);
            textView.setText("-1".equals(price)?"- -":("0".equals(price)?"未中":String.valueOf(price)));
            //未中时字色变灰改变字号
            textView.setTextColor("0".equals(price)? ColorUtils.GRAY: ColorUtils.NORMAL_RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,"0".equals(price)? context.getResources().getDimension(R.dimen.app_tvNomal_size):context.getResources().getDimension(R.dimen.app_tvBig_size));
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
        Set<String> numReds = new HashSet<>();
        List<String> numList = Arrays.asList(nums);
        numReds.addAll(numList.subList(0,6));//截取前6个是为红球
        Set<String> numBlues = new HashSet<>();
        numBlues.addAll(numList.subList(6,7));
        if (type == 1) {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }
            convertView.findViewById(R.id.next_image).setVisibility(orgType == 5?View.VISIBLE:View.GONE);
            if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                String[] filter_singel=numberStr.split(";");
                if(filter_singel.length==5||filter_singel.length==6){//5注过滤好吗 或者大于5注过滤号码
                    int max = 5 ;//最大注数
                    for (int i=0;i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, numReds, numBlues);
                    }
                }else{//小于5注过滤号码
                    int max = filter_singel.length ;//最大注数
                    for (int i=0;i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, numReds, numBlues);
                    }
                }
            } else if (!fiveNumber.contains(";")){//单单一注单式
                addSingleData((LinearLayout) layout.getChildAt(0), numberStr, 0, numReds, numBlues);
            }
        } else if (type == 2) {
            String[] arr = numberStr.split("#");
            String[] redArr = arr[0].split(" ");
            String[] blueArr = arr[1].split(" ");
            FlowLayout redLayout = (FlowLayout) convertView.findViewById(R.id.library_muti_red_layout);
            FlowLayout blueLayout = (FlowLayout) convertView.findViewById(R.id.library_muti_blue_layout);
            addMutiData(redLayout, redArr, true, numReds);
            addMutiData(blueLayout, blueArr, false, numBlues);
        } else if (type == 3) {
            String[] arr = numberStr.split("#");
            String[] red1Arr = arr[0].split(" ");
            String[] red2Arr = arr[1].split(" ");
            String[] blueArr = arr[2].split(" ");
            FlowLayout red1Layout = (FlowLayout) convertView.findViewById(R.id.library_dan_red1_layout);
            FlowLayout red2Layout = (FlowLayout) convertView.findViewById(R.id.library_dan_red2_layout);
            FlowLayout blueLayout = (FlowLayout) convertView.findViewById(R.id.library_dan_blue_layout);
            addMutiData(red1Layout, red1Arr, true, numReds);
            addMutiData(red2Layout, red2Arr, true, numReds);
            addMutiData(blueLayout, blueArr, false, numBlues);
        }
        return convertView;
    }

    /**
     * 添加单式布局
     * @param layout
     * @param numberStr
     * @param position
     */
    private void addSingleData(LinearLayout layout, String numberStr, int position, Set<String> numReds, Set<String> numBlues) {
        if (position == 0) {
            layout.findViewById(R.id.single_flag_text).setVisibility(View.VISIBLE);
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr = numberStr.split(" ");
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = CommonUtil.preZeroForBall(numberArr[i-1]);
            text.setText(dataStr);
            if (i < 7 && numReds.contains(dataStr)) {//红球中奖号
                text.setBackgroundResource(R.drawable.lottery_ball_red);
            } else if (i == 7 && numBlues.contains(dataStr)) {//蓝球中奖号
                text.setBackgroundResource(R.drawable.lottery_ball_blue);
            }
        }
    }

    /**
     * 添加复式和胆拖布局
     */
    private void addMutiData(FlowLayout mutiLayout, String[] numArr, boolean isRed, Set<String> numBalls) {
        mutiLayout.removeAllViews();
        for (String num : numArr) {
            TextView ball = (TextView) inflater.inflate(isRed?R.layout.red_ball_text:R.layout.blue_ball_text, null);
            String val = CommonUtil.preZeroForBall(num);
            ball.setText(val);
            //设置初始背景,修复复用错乱
            ball.setBackgroundResource(R.drawable.lottery_ball_big);
            mutiLayout.addView(ball);
            if (isRed) {
                ball.setBackgroundResource(numBalls.contains(val)?R.drawable.lottery_ball_red:R.drawable.lottery_ball);
            } else {
                ball.setBackgroundResource(numBalls.contains(val)?R.drawable.lottery_ball_blue:R.drawable.lottery_ball);
            }
            FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) ball.getLayoutParams();
            lp.rightMargin = DisplayUtil.dip2px(4f);
            lp.bottomMargin = DisplayUtil.dip2px(10f);
            ball.setLayoutParams(lp);
        }
    }

}

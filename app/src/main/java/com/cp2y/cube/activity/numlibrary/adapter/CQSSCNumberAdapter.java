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
import com.cp2y.cube.adapter.MyNumLibraryCQSSCAdapter;
import com.cp2y.cube.model.NewNumberModel;
import com.cp2y.cube.utils.CloneUtil;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.FileUtils;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.widgets.TouchLessGridView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by js on 2017/1/9.
 */
public class CQSSCNumberAdapter extends BaseAdapter {
    private Context context;
    private List<NewNumberModel.NumberData> data = new ArrayList<>();
    private Map<String, Integer> issues = new HashMap<>();
    private Map<String, NewNumberModel.Drawer> drawers = new HashMap<>();
    private LayoutInflater inflater;

    public CQSSCNumberAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void reloadData(Map<String, List<NewNumberModel.NumberData>> data, List<NewNumberModel.Drawer> drawers) {
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
        FileUtils.removeLibraryFile(issue, number.getId(), 0);//删除本地文件
        if (count > 0) {
            issues.put(issue, count);
            NewNumberModel.Drawer drawer = drawers.get(issue);
            if (drawer != null) {
                //long price = drawer.getPrize() - drawer.getPrize();
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

    public void addData(Map<String, List<NewNumberModel.NumberData>> data, List<NewNumberModel.Drawer> drawers) {
        for (Iterator<Map.Entry<String, List<NewNumberModel.NumberData>>> it = data.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<NewNumberModel.NumberData>> entry = it.next();
            List<NewNumberModel.NumberData> list = entry.getValue();
            for (NewNumberModel.NumberData number : list) {
                String issue = number.getIssue();
                if (!issues.containsKey(issue)) {
                    issues.put(issue, 0);
                    NewNumberModel.NumberData numberHead = new NewNumberModel.NumberData();
                    CloneUtil.cloneObject(number, numberHead);
                    numberHead.setHead(true);
                    this.data.add(numberHead);//添加头部
                }
                issues.put(issue, issues.get(issue) + 1);//每次都加1
                this.data.add(number);
            }
            this.drawers.clear();
            for (NewNumberModel.Drawer drawer : drawers) {//开奖信息
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
        int playType = number.getPlayType();//编码
//        if (fiveNumber.contains(";")) {
//            if (fiveNumber.split(";").length == 5) {
//                //过滤产生等于5注单式票
//                return 5;
//            } else if (fiveNumber.split(";").length == 6) {
//                //过滤产生大于5注单式票
//                return 8;
//            } else {
//                return 5;//过滤产生不到5注
//            }
//        } else {//选号保存类型
//        }
        return MapUtils.CQSSC_LIBRARY_TYPE.get(playType);
    }

    @Override
    public int getViewTypeCount() {
        return 15;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        //int orgType=getItemViewType(position);
        int type = getItemViewType(position);//orgType=8为大于5注单式
        if (convertView == null) {
            if (type == 0) {//头布局
                convertView = inflater.inflate(R.layout.item_p5_head, null);
            } else if (type == 1) {//五星单式
                convertView = inflater.inflate(R.layout.item_cq5_singles, viewGroup, false);
            } else if (type == 2) {//五星复式
                convertView = inflater.inflate(R.layout.item_library_cq5_muti, viewGroup, false);
            } else if (type == 3) {//三星直选单式
                convertView = inflater.inflate(R.layout.item_cq3_number_zhixuansingle, viewGroup, false);
            } else if (type == 4) {//三星直选定位
                convertView = inflater.inflate(R.layout.item_library_cq3_zhixuan_location, viewGroup, false);
            } else if (type == 5) {//三星组3复式
                convertView = inflater.inflate(R.layout.item_library_cq3_zu3_muti, viewGroup, false);
            } else if (type == 6) {//三星组3胆拖
                convertView = inflater.inflate(R.layout.item_library_cq3_zu3_dantuo, viewGroup, false);
            } else if (type == 7) {//三星组选单式
                convertView = inflater.inflate(R.layout.item_cq3_number_zuxuansingle, viewGroup, false);
            } else if (type == 8) {//三星组6复式
                convertView = inflater.inflate(R.layout.item_library_cq3_zu6_muti, viewGroup, false);
            } else if (type == 9) {//三星组6胆拖
                convertView = inflater.inflate(R.layout.item_library_cq3_zu6_dantuo, viewGroup, false);
            } else if (type == 10) {//二星直选单式
                convertView = inflater.inflate(R.layout.item_cq2_number_zhixuansingle, viewGroup, false);
            } else if (type == 11) {//二星直选定位
                convertView = inflater.inflate(R.layout.item_library_cq2_zhixuan_location, viewGroup, false);
            } else if (type == 12) {//二星组选单式
                convertView = inflater.inflate(R.layout.item_cq2_number_zuxuansingle, viewGroup, false);
            } else if (type == 13) {//二星组选复式
                convertView = inflater.inflate(R.layout.item_library_cq2_zuxuan_muti, viewGroup, false);
            } else if (type == 14) {//二星组选胆拖
                convertView = inflater.inflate(R.layout.item_library_cq2_zuxuan_dantuo, viewGroup, false);
            }
        }
        NewNumberModel.NumberData number = data.get(position);
        String issue = number.getIssue();
        if (type == 0) {
            convertView.findViewById(R.id.seperate_line2).setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        }
        if (position < getCount() - 1) {
            NewNumberModel.NumberData nextNumber = data.get(position + 1);
            convertView.findViewById(R.id.seperate_line1).setVisibility(nextNumber.getIssue().equals(issue) ? View.GONE : View.VISIBLE);
        } else {
            convertView.findViewById(R.id.seperate_line1).setVisibility(View.VISIBLE);
        }
        String[] nums = drawers.containsKey(issue) ? drawers.get(issue).getDrawNumber().split(",") : MapUtils.DEF_P5_BALL;//开奖号
        if (type == 0) {
            LinearLayout openNumbers = (LinearLayout) convertView.findViewById(R.id.app_num_library_ll_myNum);
            for (int i = 0; i < openNumbers.getChildCount(); i++) {
                TextView textView = (TextView) openNumbers.getChildAt(i);
                textView.setText(nums[i]);
            }
            TextView issueText = (TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryYear);
            issueText.setText(String.valueOf(issue).concat("期开奖"));
            String price = drawers.containsKey(issue) ? drawers.get(issue).getPrize() : "-1";
            //convertView.findViewById(R.id.price_layout).setVisibility(drawers.containsKey(issue) ? View.VISIBLE:View.GONE);
            convertView.findViewById(R.id.price_sign).setVisibility("0".equals(price) || "-1".equals(price) ? View.GONE : View.VISIBLE); //"￥"隐藏
            convertView.findViewById(R.id.price_text).setVisibility(nums[0].equals("-") ? View.GONE : View.VISIBLE); //"--"隐藏
            TextView textView = (TextView) convertView.findViewById(R.id.price_text);
            textView.setText("-1".equals(price) ? "- -" : ("0".equals(price) ? "未中" : String.valueOf(price)));
            //未中时字色变灰改变字号
            textView.setTextColor("0".equals(price) ? ColorUtils.GRAY : ColorUtils.NORMAL_RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, "0".equals(price) ? context.getResources().getDimension(R.dimen.app_tvNomal_size) : context.getResources().getDimension(R.dimen.app_tvBig_size));
            ((TextView) convertView.findViewById(R.id.app_num_library_tv_lotteryType)).setText("重庆时时彩");
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
        List<String> awardNums = new ArrayList<>();
        List<String> numList = number.getDrawNumber()!=null?Arrays.asList(number.getDrawNumber().split(",")):Arrays.asList(MapUtils.DEF_P5_BALL);//每个号码的开奖号
        awardNums.addAll(numList);//截取前6个是为红球
        boolean isShowNext = (fiveNumber.contains(";") && fiveNumber.split(";").length == 6);//是否展示箭头图标
        int playType = number.getPlayType();//把3星2星直选单式布局改成组选单式样式
        if (type == 1 || type == 3 || type == 7 || type == 10 || type == 12) {//五星单式 三星组选单式  二星直选单式  二星组选单式
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.simple_single_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                layout.getChildAt(i).setVisibility(View.GONE);//单式票先全部隐藏
            }//隐藏箭头
            convertView.findViewById(R.id.next_image).setVisibility(isShowNext ? View.VISIBLE : View.GONE);
            if (fiveNumber.contains(";")) {//单式票(通过过滤产生的结果)
                String[] filter_singel = numberStr.split(";");
                if (filter_singel.length == 5 || filter_singel.length == 6) {//5注过滤好吗 或者大于5注过滤号码
                    int max = 5;//最大注数
                    for (int i = 0; i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, awardNums, playType);
                    }
                } else {//小于5注过滤号码
                    int max = filter_singel.length;//最大注数
                    for (int i = 0; i < max; i++) {
                        addSingleData((LinearLayout) layout.getChildAt(i), filter_singel[i], i, awardNums, playType);
                    }
                }
            } else if (!fiveNumber.contains(";")) {//单单一注单式
                addSingleData((LinearLayout) layout.getChildAt(0), numberStr, 0, awardNums, playType);
            }
        } else if (type == 2) {//五星复式
            String[] numArray = fiveNumber.split("-");
            String[] wan = CommonUtil.stringToArray(numArray[0]);//万位数据
            String[] qian = CommonUtil.stringToArray(numArray[1]);//千位数据
            String[] bai = CommonUtil.stringToArray(numArray[2]);//百位数据
            String[] shi = CommonUtil.stringToArray(numArray[3]);//十位数据
            String[] ge = CommonUtil.stringToArray(numArray[4]);//个位数据
            boolean isAward = CQ5ZhixuanLocationDrawNum(awardNums, Arrays.asList(wan), Arrays.asList(qian), Arrays.asList(bai), Arrays.asList(shi), Arrays.asList(ge));
            TouchLessGridView gv_wan = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_wan_gv);
            TouchLessGridView gv_qian = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_qian_gv);
            TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
            TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
            TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
            MyNumLibraryCQSSCAdapter adapter_wan = new MyNumLibraryCQSSCAdapter(Arrays.asList(wan), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_qian = new MyNumLibraryCQSSCAdapter(Arrays.asList(qian), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_bai = new MyNumLibraryCQSSCAdapter(Arrays.asList(bai), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_shi = new MyNumLibraryCQSSCAdapter(Arrays.asList(shi), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_ge = new MyNumLibraryCQSSCAdapter(Arrays.asList(ge), context, awardNums, type, isAward);
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
            boolean isAward = ZhixuanLocationDrawNum(awardNums, Arrays.asList(bai), Arrays.asList(shi), Arrays.asList(ge));
            TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
            TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
            TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
            MyNumLibraryCQSSCAdapter adapter_bai = new MyNumLibraryCQSSCAdapter(Arrays.asList(bai), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_shi = new MyNumLibraryCQSSCAdapter(Arrays.asList(shi), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_ge = new MyNumLibraryCQSSCAdapter(Arrays.asList(ge), context, awardNums, type, isAward);
            adapter_bai.setLocationCount(0);//百位标记
            adapter_shi.setLocationCount(1);//十位标记
            adapter_ge.setLocationCount(2);//个位标记
            gv_bai.setAdapter(adapter_bai);
            gv_shi.setAdapter(adapter_shi);
            gv_ge.setAdapter(adapter_ge);
        } else if (type == 5 || type == 8 || type == 13) {//三星组3组6复式 二星组选复式
            boolean isAward = false;
            if (type == 5) {
                isAward = Zu3MutiDrawNum(awardNums, fiveNumber);
            } else if (type == 8) {
                isAward = Zu6MutiDrawNum(awardNums, fiveNumber);
            } else {
                isAward = CQ2ZuXuanMutiDrawNum(awardNums, fiveNumber);
            }
            String[] numArray = CommonUtil.stringToArray(fiveNumber);//号码
            TouchLessGridView gv_zu3 = (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_muti_gv);
            MyNumLibraryCQSSCAdapter adapter_zu3 = new MyNumLibraryCQSSCAdapter(Arrays.asList(numArray), context, awardNums, type, isAward);
            gv_zu3.setAdapter(adapter_zu3);
        } else if (type == 6 || type == 9 || type == 14) {//三星组3组6胆拖 三星组选胆拖
            boolean isAward = false;
            String[] numArray = fiveNumber.split(",");
            String[] dan = CommonUtil.stringToArray(numArray[0]);//胆数据
            String[] tuo = CommonUtil.stringToArray(numArray[1]);//拖数据
            if (type == 6) {
                isAward = Zu3DanTuoDrawNum(awardNums, Arrays.asList(dan), Arrays.asList(tuo));
            } else if (type == 9) {
                isAward = Zu6DanTuoDrawNum(awardNums, Arrays.asList(dan), Arrays.asList(tuo));
            } else {
                isAward = CQ2ZuXuanDanTuoDrawNum(awardNums, Arrays.asList(dan), Arrays.asList(tuo));
            }
            TouchLessGridView gv_dan = (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_dan_gv);
            TouchLessGridView gv_tuo = (TouchLessGridView) convertView.findViewById(R.id.item_zuxuan_tuo_gv);
            MyNumLibraryCQSSCAdapter adapter_dan = new MyNumLibraryCQSSCAdapter(Arrays.asList(dan), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_tuo = new MyNumLibraryCQSSCAdapter(Arrays.asList(tuo), context, awardNums, type, isAward);
            gv_dan.setAdapter(adapter_dan);
            gv_tuo.setAdapter(adapter_tuo);
        } else if (type == 11) {//二星直选定位
            String[] numArray = fiveNumber.split("-");
            String[] shi = CommonUtil.stringToArray(numArray[0]);//十位数据
            String[] ge = CommonUtil.stringToArray(numArray[1]);//个位数据
            boolean isAward = CQ2ZhixuanLocationDrawNum(awardNums, Arrays.asList(shi), Arrays.asList(ge));
            TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
            TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
            MyNumLibraryCQSSCAdapter adapter_shi = new MyNumLibraryCQSSCAdapter(Arrays.asList(shi), context, awardNums, type, isAward);
            MyNumLibraryCQSSCAdapter adapter_ge = new MyNumLibraryCQSSCAdapter(Arrays.asList(ge), context, awardNums, type, isAward);
            adapter_shi.setLocationCount(0);//十位标记
            adapter_ge.setLocationCount(1);//个位标记
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
    private void addSingleData(LinearLayout layout, String numberStr, int position, List<String> awardNums, int playType) {
        if (position == 0) {
            TextView title = (TextView) layout.findViewById(R.id.single_flag_text);
            title.setVisibility(View.VISIBLE);
            //title.setText(flag==2?(playType==13?"直选单式":"组选单式"):(flag==3?(playType==1?"直选单式":"组选单式"):(playType==94?"直选单式":"组选单式")));
        }
        layout.setVisibility(View.VISIBLE);
        String[] numberArr = CommonUtil.stringToArray(numberStr);
        boolean isAward = false;
        if (!awardNums.contains("-")) {//已开奖
            if (playType == 94 || playType == 92 || playType == 98) {//直选单式
                isAward = ZhiXuanMoreDrawNum(awardNums, numberStr);//判断是否中奖
            } else {//组选单式
                isAward = zuXuanDanDrawNum(awardNums, numberStr);//判断是否中奖
            }
        }
        for (int i = 1; i < layout.getChildCount(); i++) {
            TextView text = (TextView) layout.getChildAt(i);
            //设置初始背景,修复复用错乱
            text.setBackgroundResource(R.drawable.lottery_ball_big);
            String dataStr = numberArr[i - 1];
            text.setText(dataStr);
            if (isAward) {//中奖号
                text.setBackgroundResource(R.drawable.lottery_ball_red);
            }
        }
    }

    /**
     * 添加复式和胆拖布局
     */
    private void addMutiData(FlowLayout mutiLayout, String[] numArr, boolean isRed, Set<String> numBalls) {
        mutiLayout.removeAllViews();
        for (String num : numArr) {
            TextView ball = (TextView) inflater.inflate(isRed ? R.layout.red_ball_text : R.layout.blue_ball_text, null);
            String val = CommonUtil.preZeroForBall(num);
            ball.setText(val);
            //设置初始背景,修复复用错乱
            ball.setBackgroundResource(R.drawable.lottery_ball_big);
            mutiLayout.addView(ball);
            if (isRed) {
                ball.setBackgroundResource(numBalls.contains(val) ? R.drawable.lottery_ball_red : R.drawable.lottery_ball);
            } else {
                ball.setBackgroundResource(numBalls.contains(val) ? R.drawable.lottery_ball_blue : R.drawable.lottery_ball);
            }
            FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) ball.getLayoutParams();
            lp.rightMargin = DisplayUtil.dip2px(4f);
            lp.bottomMargin = DisplayUtil.dip2px(10f);
            ball.setLayoutParams(lp);
        }
    }

    /**
     * 组选单式是否中奖判断
     */
    public boolean zuXuanDanDrawNum(List<String> numBalls, String nums) {
        List<String> numLibrary = new ArrayList<>();
        CommonUtil.SortCollection(numBalls);//排序
        String draw="";
        for(String num:numBalls){
            draw=draw.concat(num);
        }
         //= numBalls.get(0).concat(numBalls.get(1).concat(numBalls.get(2)));
        for (int i = 0; i < nums.length(); i++) {//截取号码排序
            String n = nums.substring(i, i + 1);
            numLibrary.add(n);
        }
        CommonUtil.SortCollection(numLibrary);//排序
        String num="";
        for(String n:numLibrary){
            num=num.concat(n);
        }
        //String num = numLibrary.get(0).concat(numLibrary.get(1).concat(numLibrary.get(2)));
        return draw.equals(num);
    }

    /**
     * 直选单式是否中奖判断
     *
     * @param numBalls
     */
    public boolean ZhixuanSingleDrawNum(List<String> numBalls, String[] numArray) {
        return (numBalls.get(0).equals(numArray[0]) && numBalls.get(1).equals(numArray[1]) && numBalls.get(2).equals(numArray[2]));
    }


    /**
     * 多注直选单式是否中奖判断
     */
    public boolean ZhiXuanMoreDrawNum(List<String> numBalls, String nums) {
        String draw="";
        for(String num:numBalls){
            draw=draw.concat(num);
        }
        return draw.equals(nums);
    }

    /**
     * 直选定位是否中奖判断
     *
     * @param numBalls
     * @return
     */
    public boolean ZhixuanLocationDrawNum(List<String> numBalls, List<String> bai, List<String> shi, List<String> ge) {
        return (bai.contains(numBalls.get(0)) && shi.contains(numBalls.get(1)) && ge.contains(numBalls.get(2)));
    }

    /**
     * 2星直选定位是否中奖判断
     *
     * @param numBalls
     * @return
     */
    public boolean CQ2ZhixuanLocationDrawNum(List<String> numBalls, List<String> shi, List<String> ge) {
        return (shi.contains(numBalls.get(0)) && ge.contains(numBalls.get(1)));
    }

    /**
     * 5星直选定位是否中奖判断
     *
     * @param numBalls
     * @return
     */
    public boolean CQ5ZhixuanLocationDrawNum(List<String> numBalls, List<String> wan, List<String> qian, List<String> bai, List<String> shi, List<String> ge) {
        return (wan.contains(numBalls.get(0)) && qian.contains(numBalls.get(1)) && bai.contains(numBalls.get(2)) && shi.contains(numBalls.get(3)) && ge.contains(numBalls.get(4)));
    }

    /**
     * 三星组3复式是否中奖判断
     *
     * @param numBalls
     * @param num
     * @return
     */
    public boolean Zu3MutiDrawNum(List<String> numBalls, String num) {
        String s = numBalls.get(0).concat(numBalls.get(1).concat(numBalls.get(2)));
        if (CommonUtil.check3DNumType(s) == 2) {//两个号码相同组3判断
            return (num.contains(numBalls.get(0)) && num.contains(numBalls.get(1)) && num.contains(numBalls.get(2)));
        } else {
            return false;
        }
    }

    /**
     * 三星组3胆拖是否中奖判断
     *
     * @param numBalls
     * @param dan
     * @param tuo
     * @return
     */
    public boolean Zu3DanTuoDrawNum(List<String> numBalls, List<String> dan, List<String> tuo) {
        String s = numBalls.get(0).concat(numBalls.get(1).concat(numBalls.get(2)));
        if (CommonUtil.check3DNumType(s) == 2) {//两个号码相同组3判断
            String num1 = numBalls.get(0);
            String num2 = "";
            if (num1.equals(numBalls.get(1))) {
                num2 = numBalls.get(2);
            } else {
                num2 = numBalls.get(1);
            }
            List<String> total = new ArrayList<>();//总数据集合
            total.addAll(dan);
            total.addAll(tuo);
            return total.contains(num1) && total.contains(num2);
        } else {
            return false;
        }
    }

    /**
     * 三星组6复式是否中奖判断
     *
     * @param numBalls
     * @param num
     * @return
     */
    public boolean Zu6MutiDrawNum(List<String> numBalls, String num) {
        String s = numBalls.get(0).concat(numBalls.get(1).concat(numBalls.get(2)));
        if (CommonUtil.check3DNumType(s) == 0) {//两个号码相同组3判断
            return (num.contains(numBalls.get(0)) && num.contains(numBalls.get(1)) && num.contains(numBalls.get(2)));
        } else {
            return false;
        }
    }

    /**
     * 二星组选复式是否中奖判断
     *
     * @param numBalls
     * @param num
     * @return
     */
    public boolean CQ2ZuXuanMutiDrawNum(List<String> numBalls, String num) {
        return (num.contains(numBalls.get(0)) && num.contains(numBalls.get(1)));
    }


    /**
     * 三星组6胆拖是否中奖判断
     *
     * @param numBalls
     * @param dan
     * @param tuo
     * @return
     */
    public boolean Zu6DanTuoDrawNum(List<String> numBalls, List<String> dan, List<String> tuo) {
        String s = numBalls.get(0).concat(numBalls.get(1).concat(numBalls.get(2)));
        if (CommonUtil.check3DNumType(s) == 0) {//3个号码不相同组6判断
            String num1 = numBalls.get(0);
            String num2 = numBalls.get(1);
            String num3 = numBalls.get(2);
            List<String> total = new ArrayList<>();//总数据集合
            total.addAll(dan);
            total.addAll(tuo);
            return total.contains(num1) && total.contains(num2) && total.contains(num3);
        } else {
            return false;
        }
    }

    /**
     * 二星组选胆拖是否中奖判断
     *
     * @param numBalls
     * @param dan
     * @param tuo
     * @return
     */
    public boolean CQ2ZuXuanDanTuoDrawNum(List<String> numBalls, List<String> dan, List<String> tuo) {
        String num1 = numBalls.get(0);
        String num2 = numBalls.get(1);
        List<String> total = new ArrayList<>();//总数据集合
        total.addAll(dan);
        total.addAll(tuo);
        return total.contains(num1) && total.contains(num2);
    }
}

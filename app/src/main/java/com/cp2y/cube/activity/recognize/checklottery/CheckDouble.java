package com.cp2y.cube.activity.recognize.checklottery;

import android.util.SparseArray;

import com.cp2y.cube.activity.recognize.keyword.RegexCheck;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**双色球解析
 * Created by yangfan on 2017/3/30.
 */

public class CheckDouble {
    private int ticketType = 1;//默认是复式票
    private Pattern p;
    private Matcher m;
    private String data=null;
    public CheckDouble(String data){
        this.data=data;
    }
    //获得双色球奖期
    public int getDate(){//20开头  销售期开头  兑奖期开头  2027018(1223)期
        int date_str = 0;
        p = Pattern.compile("(20[12]\\d{4}[\\t ]+)|[销售]{1,2}期[:\\t ]*([\\d\\t ]+)|[兑开奖]{1,2}[\\t ]*期[:\\t ]*([\\d\\t ]+)|(20[12]\\d{4}期+)|(期[\\t ]*号[:\\t ]*([\\d\\t ]+))");
        m = p.matcher(data);
        if (m.find()) {//读取奖期
            String s = m.group(0);
            s = s == null ? m.group(1) : s;
            s = s == null ? m.group(2) : s;
            s = s == null ? m.group(3) : s;
            int start = s.indexOf("20");//截取奖期销[\\t ]*售[\\t ]*期[:\\t ]*([\\d\\t ]+)
            if (start > -1) {
                s = s.substring(start);
            }
            if (s.length() > 7) {
                s = s.substring(0, 7);
            }
            date_str = Integer.valueOf(s.replaceAll("\\s", ""));//去任何空白字符
        } else {
            date_str = 0;
        }
        return date_str;
    }
    //获得双色球倍数
    public int getMultiple(){
        int multiples = 1;
        p = Pattern.compile("倍[\\t ]*数[:\\t ]*([\\d\\t ]+)");
        m = p.matcher(data);
        if (m.find()) {
            try {
                int multiple = Integer.valueOf(m.group(1).replaceAll("\\s", ""));//去任何空白字符
                multiples=multiple;
            } catch (NumberFormatException e) {
            }
        }else{
            multiples=1;
        }
        return multiples;
    }
    //获得双色球号码 单式复式 两个集合红篮球   胆拖三个集合胆拖蓝
    public List<byte[]> getNumber(){
        //匹配票面
        SparseArray<byte[]> tickets = new SparseArray<byte[]>();
        p = Pattern.compile("[\\t ]*([蓝球红胆拖单复]{2})(([:\\t ]{0,1}\\d[\\t ]*\\d\\s*)+)");
        m = p.matcher(data);
        boolean isMuti = false;
        ticketType = 1;//默认是复式票
        while(m.find()){//多次匹配 复式或者胆拖票 有文字
            isMuti = true;
            String type=m.group(1);
            String num=m.group(2);
            num = num.replaceAll("[\\s:]", "");
            int length = num.length();
            if (length > 0 && length % 2 == 0) {
                byte[] nums = new byte[num.length() / 2];
                List<Byte> list = new ArrayList<Byte>();
                boolean skipFlag = false;
                for (int i = 0; i < nums.length; i++) {
                    String s = num.substring(i*2, (i+1)*2);//截取号码
                    nums[i] = Byte.valueOf(s);
                    byte preNum = i > 0 ? nums[i-1] : 0;//中间值
                    if (nums[i] > 33) {//错误数据直接跳过
                        skipFlag = true;
                        break;
                    } else if (nums[i] < preNum) {//如果后面的数字小于前面的数字跳过
                        break;
                    }
                    list.add(nums[i]);
                }
                if (skipFlag) {//跳过本条数据
                    continue;
                }
                nums = new byte[list.size()];
                for (int i = 0; i < nums.length; i++) {
                    nums[i] = list.get(i);
                }//单复式 红蓝   胆拖 红蓝拖
                if ((type.contains("拖") && tickets.get(2)==null) || type.contains("红拖")||type.contains("球拖")) {//红拖
                    ticketType = 2;
                    tickets.put(2, nums);
                } else if (((type.contains("蓝复")) && tickets.get(1)==null) || type.contains("蓝球") || type.contains("蓝单")||type.contains("蓝")) {//蓝球
                    tickets.put(1, nums);
                } else if(tickets.get(0)==null || type.contains("红球") ||type.contains("红单")|| type.contains("红胆") || type.contains("红复")||type.contains("球胆")) {//红胆或者红球
                    tickets.put(0, nums);
                }
            }
        }
        //校验下数据，如果
        boolean clearFlag = false;
        for (int i = 0; i < tickets.size(); i++) {//如果数据不完整的话就抛弃
            if (tickets.get(i) == null) {
                clearFlag = true;
                break;
            }
        }
        int red_ball_count = 0;
        if (tickets.get(0)!=null) {//红球个数
            red_ball_count += tickets.get(0).length;
        }
        if (tickets.get(2)!=null) {
            red_ball_count += tickets.get(2).length;
        }
        if (clearFlag || tickets.size() < 2 || red_ball_count < 6) {//红球个数小于6够不成一注
            tickets.clear();
        }
        if (tickets.size() == 3) {//胆拖交换下蓝球和红拖的位置
            byte[] blue_balls = tickets.get(1);
            byte[] red_balls = tickets.get(2);
            tickets.put(1, red_balls);
            tickets.put(2, blue_balls);
        }
        if(ticketType==1&&tickets.size()==2&&tickets.get(0).length+tickets.get(1).length==7){//单独针对北京单式和复式相同处理
            //复式格式处理成单式格式
            byte[]red=tickets.get(0);
            byte[]blue=tickets.get(1);
            byte[]single= CommonUtil.ArrayConcat(red,blue);
            tickets.clear();//匹配单式
            tickets.put(0,single);
            ticketType=0;
        }
        if (!isMuti) {//单式
            ticketType = 0;
            p = Pattern.compile("(\\d[\\t ]*\\d[\\t ]*){6}[\\+\\-\\*]{0,1}\\d{2}");//E:051315172328+14
            m = p.matcher(data);
            int k = 0;
            while (m.find()) {
                String num = m.group(0);
                num = num.replaceAll("[\\s\\+\\-\\*]", "");//去特殊符号
                if(!RegexCheck.DoubleDelete(num)){
                    continue;//错误超过1个排除数据
                }
                int length = num.length();
                if (length > 0 && length % 2 == 0) {
                    byte[] nums = new byte[num.length() / 2];
                    boolean isDiscard = false;
                    for (int i = 0; i < nums.length; i++) {
                        String s = num.substring(i*2, (i+1)*2);
                        byte curNum = Byte.valueOf(s);
                        byte preNum = i > 0 ? nums[i-1] : 0;
                        if (preNum / 10 == curNum / 10 && curNum < preNum && curNum % 10 == 0) {//如果10位相等并且个位为0,则替换个位为8
                            curNum = (byte) ((curNum/10)*10 + 8);
                        } else if (curNum == 0) {//两个重复号码
                            curNum = 8;
                        }
                        nums[i] = curNum;
                        if(i<6){//红球
                            if ( nums[i] >33) {//第二次判断超过范围去掉这条数据
                                isDiscard = true;//数据错误
                                break;
                            }
                        }else{//蓝球
                            if ( nums[i] >16) {//第二次判断超过范围去掉这条数据
                                isDiscard = true;//数据错误
                                break;
                            }
                        }
                    }
                    if (!isDiscard) {//数据无误
                        tickets.put(k, nums);
                        k++;
                    }
                }
            }
        }
        List<byte[]> ticketList = new ArrayList<byte[]>();
        for (int i = 0; i < tickets.size(); i++) {
            ticketList.add(tickets.get(i));
        }//size为12345单式,,size为2复式  size为3胆拖
        return ticketList;
    }
    //ticketType 0单式  1复式  2胆拖
    public int getType(){
        return ticketType;//默认是复式票
    }
}

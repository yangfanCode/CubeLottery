package com.cp2y.cube.activity.recognize.checklottery;

import android.util.SparseArray;

import com.cp2y.cube.activity.recognize.keyword.RegexCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**大乐透解析
 * Created by yangfan on 2017/3/30.
 */
public class CheckLotto {
    private int ticketType = 1;//默认复式票
    private Pattern p;
    private Matcher m;
    private String data=null;
    public CheckLotto(String data){
        this.data=data;
    }
    //获得大乐透奖期 17021期
    public int getDate(){
        p = Pattern.compile("([1,2]\\d{4})期");
        m = p.matcher(data);
        if (m.find()) {
            int date = Integer.valueOf(m.group(1));
            return date;
        }else{
            return 0;
        }
    }
    //获得大乐透倍数
    public int getMultiple(){
        //读1位数字
        p = Pattern.compile("(\\d{1})倍");
        m = p.matcher(data);
        if(m.find()){
            int muti=Integer.valueOf(m.group(1));
            if(muti==0){
                return 1;
            }else{
                return muti;
            }
        }else{
            return 1;
        }
    }
    //获得是否追加
    public boolean isAttach(){
        return data.contains("追加");
    }
    //获得大乐透号码数据
    public List<byte[]> getNumber(){//4个00+  一个0000 一个+00
        ticketType = 1;//默认复式票
        List<byte[]> tickets = new ArrayList<byte[]>();
        if(!data.contains("复")&&!data.contains("胆")&&!data.contains("拖")){
            //单式票
            p = Pattern.compile("(([\\t ]*\\d[\\t ]*\\d[\\t ]*[\\·\\+014]{0,1}){4}([\\t ]*\\d[\\t ]*\\d[\\t ]*){2}[\\·\\+014]{0,1}[\\t ]*\\d[\\t ]*\\d)|((\\d\\d){5}([\\+ ]|[\\u4e00-\\u9fa5]){0,1}(\\d\\d){2})");
            m = p.matcher(data);
            //result.setTickets(tickets);
            while (m.find()) {//符合加号单式票
                ticketType = 0;
                String num = m.group();
                num=num.replaceAll("[\\u4e00-\\u9fa5]","");
                num = num.replaceAll("\\s", "");//先替换掉空格
//            char[] chars = num.toCharArray();
//            chars[2] = chars[5] = chars[8] = chars[11] = chars[16] = '+';//替换掉中间的分隔符
                num = num.replaceAll("\\+", "");
                num = num.replaceAll("\\·", "");
                if(!RegexCheck.LottoDelete(num)){
                    continue;//错误超过1个排除数据
                }
                byte[] nums = new byte[num.length() / 2];
                boolean isDiscard = false;//失败
                for (int i = 0; i < nums.length; i++) {
                    String s = num.substring(i*2, (i+1)*2);
                    byte curNum = Byte.valueOf(s);
                    byte preNum = i > 0 ? nums[i-1] : 0;
                    if (preNum / 10 == curNum / 10 && curNum < preNum && curNum % 10 == 0) {//如果10位相等并且个位为0,则替换个位为8
                        curNum = (byte) ((curNum/10)*10 + 8);
                    } else if (curNum == 0) {
                        curNum = 8;
                    }
                    nums[i] = curNum;
                    if(i<5){//红球
                        if ( nums[i] >35) {//第二次判断超过范围去掉这条数据
                            isDiscard = true;//数据错误
                            break;
                        }
                    }else{//蓝球
                        if ( nums[i] >12) {//第二次判断超过范围去掉这条数据
                            isDiscard = true;//数据错误
                            break;
                        }
                    }
                }
                if (!isDiscard) {
                    tickets.add(nums);
                }
            }
        }
        //非单式
        if (ticketType > 0) {//[\u4e00-\u9fa5]至少匹配一个汉字 继续匹配 00 00多次匹配
            p = Pattern.compile("([前后区胆拖]{1,3})[\\t ]*(\\d{2}[\\t ]*(\\d{2}\\s*)*)");
            m = p.matcher(data);
            SparseArray<byte[]> cacheTickets = new SparseArray<byte[]>();
            while (m.find()) {
                String type = m.group(1);
                String num = m.group(2).replaceAll("\\s", "");
                byte[] nums = new byte[num.length() / 2];
                boolean isDiscard = false;
                for (int i = 0; i < nums.length; i++) {
                    String s = num.substring(i*2, (i+1)*2);
                    byte curNum = Byte.valueOf(s);
                    byte preNum = i > 0 ? nums[i-1] : 0;
                    if (preNum / 10 == curNum / 10 && curNum < preNum && curNum % 10 == 0) {//如果10位相等并且个位为0,则替换个位为8
                        curNum = (byte) ((curNum/10)*10 + 8);
                    } else if (curNum == 0) {
                        curNum = 8;
                    }
                    nums[i] = curNum;
                    if(nums[i] >35){//超过35的数据 至判断红球标准 蓝球展示数据处理
                        isDiscard=true;
                        break;
                    }
//                    if (nums[i] < preNum) {//如果后面的数字小于前面的数字跳过
//                        isDiscard = true;
//                        break;//去掉匹配失败功能
//                    }
                }
                if (isDiscard) {//丢掉不合适的数据
                    continue;//读取下一条据
                }
                //前区胆
                if (type.length() == 3 && ticketType >0) {//胆拖模式
                    if (type.contains("前")&&type.contains("拖")) {
                        cacheTickets.put(1, nums);
                    } else if (type.contains("前")&&type.contains("胆")) {
                        cacheTickets.put(0, nums);
                    } else if (type.contains("后")&&type.contains("拖")) {
                        cacheTickets.put(3, nums);
                    } else if (type.contains("后")&&type.contains("胆")) {
                        cacheTickets.put(2, nums);
                    } else {
                        continue;
                    }
                    ticketType = 2;
                } else {//前区 后区 复式
                    if (type.contains("前")) {
                        cacheTickets.put(0, nums);
                    } else if (type.contains("后")) {
                        cacheTickets.put(1, nums);
                    } else {
                        continue;
                    }
                }
            }
            //校验下数据
            if (cacheTickets.size() < 2) {//无效数据
                cacheTickets.clear();
            } else if (cacheTickets.size() > 2) {//胆拖
            } else if (cacheTickets.get(0)==null || cacheTickets.get(1)==null){//无效数据复式
                cacheTickets.clear();
            }
            if (ticketType == 2) {//校验胆拖数据
                if (cacheTickets.get(0) == null) {//前区胆一定要有
                    cacheTickets.clear();
                } else if (cacheTickets.get(1) == null) {//前区拖一定要有
                    cacheTickets.clear();
                } else if (cacheTickets.get(2) == null) {//前胆前拖后拖
                    ticketType = 3;//后胆可无
                } else if (cacheTickets.get(3) == null) {//后区拖一定要有
                    cacheTickets.clear();
                }
            }
            //result.setType(ticketType);
            int size = ticketType == 1 ? 2 : 4;//复式或胆拖
            for (int i = 0; i < size; i++) {
                byte[] ticket = cacheTickets.get(i);
                if (ticket != null) {
                    tickets.add(ticket);
                }else{
                    tickets.add(new byte[]{100});//无后胆传100标记
                }
            }
        }
        return tickets;//size 1234 单  2 复式  4胆拖
    }
    //ticketType 0 单式 1 复式 2胆拖全  3胆拖缺
    //0单式   1复式 2胆拖全  3 胆拖缺
    public int  getType(){
        return ticketType;
    }
}

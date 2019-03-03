package com.cp2y.cube.activity.news.fragment;


import android.support.v4.app.Fragment;

import com.cp2y.cube.utils.CommonUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsBaseFragment extends Fragment {
    //双色球大乐透 注数和金钱单位
    public String setLargeMoney(long money){
        if(money%100000000==0){//如果被1亿整除
            return String.valueOf((money/100000000));//返回几亿
        }else{
            if(money%10000000==0){//如果被1千万整除
                return String.valueOf(CommonUtil.changeDouble((double)money/100000000));//返回几.几亿
            }else if(money%1000000==0){//如果被1百万整除
                return CommonUtil.changeDoubleTwo((double)money/100000000);//返回几.几几亿
            }else if(money%100000==0){//如果被10万整除
                return CommonUtil.changeDoubleThree((double)money/100000000);//返回几.几几几亿
            }else{
                return CommonUtil.changeDoubleFour((double)money/100000000);//返回几.几几几几亿
            }
        }
    }
    public String setLargeCount(int count){
        if(count%10000==0){//如果被10000整除
            return String.valueOf((count/10000));//返回几万
        }else{
            if(count%1000==0){//如果被1000整除
                return String.valueOf(CommonUtil.changeDouble((double)count/10000));//返回几.几万
            }else {
                return CommonUtil.changeDoubleTwo((double)count/10000);//返回几.几几万
            }
        }
    }

}

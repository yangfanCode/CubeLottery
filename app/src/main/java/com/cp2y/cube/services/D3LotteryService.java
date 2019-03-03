package com.cp2y.cube.services;

import android.util.Log;
import android.util.SparseIntArray;

import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.LotteryHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.FileUtils;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.LotterySaveUtils;
import com.cp2y.cube.utils.SPUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by js on 2017/1/9.
 */
public class D3LotteryService extends Service {
    private MyInterface.isSavaNumSuccess isSavaNumSuccess;//是否保存成功
    public void isSaveNumSuccess(MyInterface.isSavaNumSuccess isSavaNumSuccess){
        this.isSavaNumSuccess=isSavaNumSuccess;
    }
    private boolean isZhiXuan=false;
    //过滤结果直选组选区分
    public void setIsZhiXuan(boolean isZhiXuan){
        this.isZhiXuan=isZhiXuan;
    }

    private Map<Integer,String> lotteryId=new HashMap<Integer,String>(){{
        put(2,"10001");
        put(3,"10003");
        put(5,"10093");
    }};
    /**playType**/
    private SparseIntArray save3dNum=new SparseIntArray(){{put(2,13);put(3,1);put(5,94);}};//直选单式
    private SparseIntArray saveD3LocationNum=new SparseIntArray(){{put(2,12);put(3,0);put(5,93);}};//直选定位
    private SparseIntArray save3dMutiNum=new SparseIntArray(){{put(2,16);put(3,6);put(5,126);}};//组3复式
    private SparseIntArray saveZu3DantuoNum=new SparseIntArray(){{put(2,172);put(3,168);put(5,522);}};//组3胆拖
    private SparseIntArray saveZu6Num=new SparseIntArray(){{put(2,18);put(3,5);put(5,129);}};//组6单式
    private SparseIntArray saveZu6MutiNum=new SparseIntArray(){{put(2,17);put(3,7);put(5,127);}};//组6复式
    private SparseIntArray saveZu6DantuoNum=new SparseIntArray(){{put(2,173);put(3,169);put(5,521);}};//组6胆拖
    /**
     * 保存本地文件并上传服务器
     * @param data
     */
    public void saveLotteryNumber(List data, int type,int flag,int noteNumber) {
        Log.e("yangfan", "saveLotteryNumber: "+CommonUtil.getIMEI());
        NetHelper.LOTTERY_API.getNewIssue(lotteryId.get(flag)).flatMap((issueModel -> {
            String issue = issueModel.getIssue();
            //修改彩种保存路径
            File dir = new File(flag==2?FileUtils.D3_PATH:FileUtils.P3_PATH, issue);
            FileUtils.makeDirs(dir);
            int id = SPUtil.getAndIncrease("libraryId");
            File file = new File(dir, String.valueOf(id).concat(".tmp"));
            if (type == 0) {//直选单式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag), LoginSPUtils.getInt("id",0), issue,save3dNum.get(flag),noteNumber, true);
            } else if (type == 1) {//直选定位
                LotterySaveUtils.saveD3LocationNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveD3LocationNum.get(flag),noteNumber, true);
            } else if (type == 2){//组3复式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,save3dMutiNum.get(flag),noteNumber, true);
            } else if (type == 3){//组3胆拖
                LotterySaveUtils.saveZu3DantuoNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZu3DantuoNum.get(flag),noteNumber, true);
            } else if (type == 4){//组6单式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZu6Num.get(flag),noteNumber, true);
            } else if (type == 5){//组6复式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZu6MutiNum.get(flag),noteNumber, true);
            } else if(type == 6) {//组6胆拖
                LotterySaveUtils.saveZu3DantuoNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZu6DantuoNum.get(flag),noteNumber, true);
            }else{//过滤结果保存
                LotteryHelper.serialize3DLotteryRecords(file, (List<byte[]>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,isZhiXuan?save3dNum.get(flag):saveZu6Num.get(flag),noteNumber, true);
            }

        })).subscribe(new SafeOnlyNextSubscriber<Integer>(){
            @Override
            public void onNext(Integer args) {
                super.onNext(args);
                //删除掉之前的压缩文件
                TipsToast.showTips("已成功保存至号码库");
                GuideFlag.isClick_Save=true;
                if(isSavaNumSuccess!=null){
                    isSavaNumSuccess.Success(true);//成功
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("保存失败，请检查网络设置");
                GuideFlag.isClick_Save=true;
                if(isSavaNumSuccess!=null){
                    isSavaNumSuccess.Success(false);//失败
                }
            }
        });
    }
}

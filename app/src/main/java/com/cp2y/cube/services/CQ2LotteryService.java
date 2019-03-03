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
public class CQ2LotteryService extends Service {
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
        put(7,"10095");
    }};
    /**playType**/
    private SparseIntArray saveNum=new SparseIntArray(){{put(7,98);}};//直选单式
    private SparseIntArray saveLocationNum=new SparseIntArray(){{put(7,97);}};//直选定位
    private SparseIntArray saveZuXuanNum=new SparseIntArray(){{put(7,101);}};//组选单式
    private SparseIntArray saveZuXuanMutiNum=new SparseIntArray(){{put(7,100);}};//组选复式
    private SparseIntArray saveZuXuanDanTuoNum=new SparseIntArray(){{put(7,103);}};//组选胆拖
    /**
     * 保存本地文件并上传服务器
     * @param data
     */
    public void saveLotteryNumber(List data, int type,int flag,int noteNumber) {
        Log.e("yangfan", "saveLotteryNumber: "+CommonUtil.getIMEI());
        NetHelper.LOTTERY_API.getNewIssue(lotteryId.get(flag)).flatMap((issueModel -> {
            String issue = issueModel.getIssue();
            //修改彩种保存路径
            File dir = new File(FileUtils.CQSSC_PATH, issue);
            FileUtils.makeDirs(dir);
            int id = SPUtil.getAndIncrease("libraryId");
            File file = new File(dir, String.valueOf(id).concat(".tmp"));
            if (type == 0) {//直选单式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag), LoginSPUtils.getInt("id",0), issue,saveNum.get(flag),noteNumber, true);
            } else if (type == 1) {//直选定位
                LotterySaveUtils.saveCQ2LocationNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveLocationNum.get(flag),noteNumber, true);
            } else if (type == 2){//组选单式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZuXuanNum.get(flag),noteNumber, true);
            } else if (type == 3){//组选复式
                LotterySaveUtils.save3dNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZuXuanMutiNum.get(flag),noteNumber, true);
            } else if (type == 4){//组选胆拖
                LotterySaveUtils.saveZu3DantuoNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,saveZuXuanDanTuoNum.get(flag),noteNumber, true);
            } else{//过滤结果保存
                LotteryHelper.serialize3DLotteryRecords(file, (List<byte[]>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag),LoginSPUtils.getInt("id",0), issue,isZhiXuan?saveNum.get(flag):saveZuXuanNum.get(flag),noteNumber, true);
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

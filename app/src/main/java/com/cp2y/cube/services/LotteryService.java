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
public class LotteryService extends Service {
    private MyInterface.isSavaNumSuccess isSavaNumSuccess;//是否保存成功
    public void isSaveNumSuccess(MyInterface.isSavaNumSuccess isSavaNumSuccess){
        this.isSavaNumSuccess=isSavaNumSuccess;
    }

    private Map<Integer,String> lotteryId=new HashMap<Integer,String>(){{
        put(0,"10002");
        put(1,"10088");
    }};
    /**playType**/
    private SparseIntArray saveSingle=new SparseIntArray(){{put(0,1001);put(1,1004);}};//单式
    private SparseIntArray saveMuti=new SparseIntArray(){{put(0,1002);put(1,1005);}};//复式
    private SparseIntArray saveDantuo=new SparseIntArray(){{put(0,1003);put(1,1006);}};//胆拖
    /**
     * 保存本地文件并上传服务器
     * @param data
     * @param flag 0,双色球1,大乐透
     */
    public void saveLotteryNumber(List data, int type,int flag,int noteNumber) {
        Log.e("yangfan", "saveLotteryNumber: "+CommonUtil.getIMEI());
        NetHelper.LOTTERY_API.getNewIssue(lotteryId.get(flag)).flatMap((issueModel -> {
            String issue = issueModel.getIssue();
            //修改彩种保存路径
            File dir = new File(flag==0?FileUtils.DOUBLE_PATH:(flag==1?FileUtils.LOTTO_PATH:FileUtils.PATH), issue);
            FileUtils.makeDirs(dir);
            int id = SPUtil.getAndIncrease("libraryId");
            File file = new File(dir, String.valueOf(id).concat(".tmp"));
            if (type == 0) {///单式
                LotterySaveUtils.saveSelectNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag), LoginSPUtils.getInt("id",0), issue,saveSingle.get(flag),noteNumber, true);
            } else if (type == 1) {///复式
                LotterySaveUtils.saveSelectDoubleNum(file, (List<String>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag), LoginSPUtils.getInt("id",0), issue,saveMuti.get(flag),noteNumber, true);
            } else if (type == 2){//胆拖
                if(flag==0){//双色球胆拖
                    LotterySaveUtils.saveSelectDanTuoNum(file, (List<String>) data);
                }else if(flag==1){//大乐透胆拖
                    LotterySaveUtils.saveLottoDanTuoNum(file, (List<String>) data);
                }
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag), LoginSPUtils.getInt("id",0), issue,saveDantuo.get(flag),noteNumber, true);
            } else {//保存过滤结果
                LotteryHelper.serializeLotteryRecords(file, (List<byte[]>) data);
                return NetHelper.LOTTERY_API.uploadLotteryFilePin(file,lotteryId.get(flag), LoginSPUtils.getInt("id",0), issue,saveSingle.get(flag),noteNumber, true);
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

package com.cp2y.cube.callback;

import android.view.View;
import android.widget.EditText;

/**
 * Created by admin on 2016/12/21.
 */
public class MyInterface {
    public interface SelectNum {
        void NomalToDanTuo(int flag);

        void DanTuoToNomal(int flag);
    }

    public interface closeAVLoading {
        void closeAVLOAding();
    }

    public interface TrendItemClick {
        void click(View v, int flag);
    }

    public interface IgnoreDateChange {//遗漏走势期数

        void IgnoreChange(int position, int type);
    }

    public interface IgnoreIsBlue {//遗漏走势是否含篮球

        void isCalcBlue(boolean calcBlue, int position);
    }

    public interface ScanNumEdit {//扫一扫号码修改监听 pos条目 type彩票类型格式 edittextPos单式的号码位置

        void scanLottoNumEdit(int pos, int type, boolean isSingelRed, EditText et);

        void scanDoubleNumEdit(int pos, int type, boolean isSingelRed, EditText et);
    }

    public interface isSavaNumSuccess {
        void Success(boolean success);
    }

    public interface setSelectNumPopUps {
        void setSelectNumPopUp(int x, int y, int postion, boolean isShow);//普通选号
    }

    /**
     * x,y坐标 position 号码,isshow 显示与隐藏 isRed红篮球
     **/
    public interface setSelectNumD3PopUps {
        void setSelectNumD3PopUp(int x, int y, int postion, boolean isShow, boolean isRed);//3D组选带胆选号
    }

    public interface customCount {
        boolean customCounts(int lotteryId);
    }

    /**
     * 推单选单全选局部刷新
     **/
    public interface partNotify {
        void notifyListView();
    }

    /**
     * 评论回复
     **/
    public interface CommentReply {
        void commentReply(int position);//回复楼主

        void commentFloorReply(int position, int floorPos);//楼中楼
    }

    /**
     * 关注未关注
     **/
    public interface SubscribePersonal {
        void subscribePersonal(int position,int type);//关注
    }

    /**
     * 打开他人中心页面
     */
    public interface OpenPersonalAct{
        void subscribePersonal(int userId);
    }

    public interface PushSetClick{
        void pushSetClick(int position,boolean isChecked);
    }

    /**
     * 计奖器奖期
     */
    public interface SetCalcIssue{
        void setCalcIssue(String issue);
    }
}

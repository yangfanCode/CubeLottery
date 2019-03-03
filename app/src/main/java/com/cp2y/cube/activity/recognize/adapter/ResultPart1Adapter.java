package com.cp2y.cube.activity.recognize.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Arrays;
import java.util.List;

public class ResultPart1Adapter extends BaseAdapter {
	private boolean isEnable=false;
	private Context mContext;
	private List<byte[]> mTickets;
	private int mTicketType;
	private static final String[] LETTERS = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26"};
	//号码修改监听
    private MyInterface.ScanNumEdit scanNumEdit;
    public void setScanLottoNumEdit(MyInterface.ScanNumEdit scanNumEdit){//初始化
        this.scanNumEdit=scanNumEdit;
    }
	public ResultPart1Adapter(Context mContext, List<byte[]> tickets, int ticketType) {
		super();
		this.mContext = mContext;
		this.mTickets = tickets;
		this.mTicketType = ticketType;
	}
    //设置edittext是否可点击刷新数据
    public void setEditEndable(boolean isEnable){
        this.isEnable=isEnable;
        notifyDataSetChanged();
    }
    //得到数据源
    public List<byte[]>  getData(){
        return mTickets;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTickets.size();
	}

	@Override
	public byte[] getItem(int position) {
		// TODO Auto-generated method stub
		return mTickets.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (convertView == null) {//根据类型来区别需要inflate出来的资源
			if (mTicketType == 0) {//单式票
				convertView = inflater.inflate(R.layout.item_lottery_result1, null);
			} else {//复式票 胆拖票
				convertView = inflater.inflate(R.layout.item_lottery_result11, null);
			}
		}
		byte[] ticket = mTickets.get(position);
		if (mTicketType == 0) {//单式票 布局改为动态添加 固定布局修改号码有bug
			TextView index = (TextView) convertView.findViewById(R.id.result_index);
            View view_line=convertView.findViewById(R.id.scan_result_singleLine);
            view_line.setVisibility(position==mTickets.size()-1?View.GONE:View.VISIBLE);//分割线
			index.setText(LETTERS[position]);//序号
			//数据
			LinearLayout red_ball_layout = (LinearLayout) convertView.findViewById(R.id.result_red_ball_layout);
			LinearLayout blue_ball_layout = (LinearLayout) convertView.findViewById(R.id.result_blue_ball_layout);
            red_ball_layout.removeAllViews();//remove the old views
            blue_ball_layout.removeAllViews();//remove the old views
            for(int i=0;i<ticket.length;i++){
                if(i<5){
                    View view=LayoutInflater.from(mContext).inflate(R.layout.red_ball_layout,parent,false);
                    red_ball_layout.addView(view);
                    setLinerLayoutMargin(view);//设置边距
                }else{
                    View view=LayoutInflater.from(mContext).inflate(R.layout.blue_ball_layout,parent,false);
                    blue_ball_layout.addView(view);
                    setLinerLayoutMargin(view);//设置边距
                }
            }
            initBalls(red_ball_layout, Arrays.copyOfRange(ticket, 0, 5), R.id.result_red_ball_edit,position);
            initBalls(blue_ball_layout, Arrays.copyOfRange(ticket, 5, 7), R.id.result_blue_ball_edit,position);
        } else {//复式票
			FlowLayout ball_layout = (FlowLayout) convertView.findViewById(R.id.result_ticket_layout);
            boolean isRed= mTicketType==1?position < 1:position<2;//复式胆拖红球
			ball_layout.removeAllViews();//remove the old views
			while (ball_layout.getChildCount() < ticket.length) {
				if (isRed) {
                    View view=LayoutInflater.from(mContext).inflate(R.layout.red_ball_layout,parent,false);
					ball_layout.addView(view);
                    setFlowLayoutMargin(view);//设置边距
				} else {
                    if(ticket.length==1&&ticket[0]==100&&mTicketType==3){
                        View view=LayoutInflater.from(mContext).inflate(R.layout.none_houtuo_wu,parent,false);
                        ball_layout.addView(view);//后胆无
                        setFlowLayoutMargin(view);//设置边距
                    }else{
                        View view=LayoutInflater.from(mContext).inflate(R.layout.blue_ball_layout,parent,false);
                        ball_layout.addView(view);
                        setFlowLayoutMargin(view);//设置边距
                    }
                }
			}
			TextView title = (TextView) convertView.findViewById(R.id.result_title);
			String[] titles = mTickets.size() == 2 ? new String[] {"前区:", "后区:"} : new String[]{"前区胆:", "前区拖:", "后区胆:","后区拖:"};
			if (position < titles.length) {
				title.setText(titles[position]);
			}
			if (isRed) {
				initBalls(ball_layout, ticket, R.id.result_red_ball_edit,position);//设置数据
			} else {
				initBalls(ball_layout, ticket, R.id.result_blue_ball_edit,position);
			}
		}
		return convertView;
	}
	
	/**
	 * 初始化球号
	 * @param layout
	 * @param ticket
	 * @param editId
	 */
	private void initBalls(ViewGroup layout, byte[] ticket, int editId,int pos) {
		int k = 0;
        boolean isSingelRed=ticket.length==5;//判断单式红篮球
		for (int i = 0; i < layout.getChildCount(); i++) {
			View view = layout.getChildAt(i);
			if (view instanceof LinearLayout) {
				EditText ball_edit = (EditText) view.findViewById(editId);
				if (k < ticket.length) {
					String num = CommonUtil.preZeroForBall(ticket[k]);//自动补0
					ball_edit.setText(num);
                    ball_edit.setEnabled(isEnable);
                    final int finalI = i;
                    ball_edit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                        @Override
                        public void afterTextChanged(Editable s) {
                            String editTextStr=ball_edit.getText().toString().trim();
                            if (editTextStr.length() == 2) {
                                scanNumEdit.scanLottoNumEdit(pos, mTicketType, isSingelRed, ball_edit);//监听校验执行方法
                            }
                                if (!TextUtils.isEmpty(ball_edit.getText().toString().trim())) {
                                    byte editNum = Byte.parseByte(ball_edit.getText().toString().trim());
                                    if (mTicketType == 0) {
                                        //单式重复判断
                                        if(editTextStr.length() == 2){//两位号码再判断
                                            if (CommonUtil.SingleSame(mTickets.get(pos), editNum, isSingelRed, finalI, 0, 5, 7)) {
                                                TipsToast.showTips("号码不可重复");
                                            }
                                        }
                                        if (isSingelRed) {
                                            mTickets.get(pos)[finalI] = editNum;//修改红球数据源
                                        } else {
                                            mTickets.get(pos)[finalI + 5] = editNum;//修改蓝球数据源
                                        }
                                    } else if (mTicketType == 1) {//复式重复判断
                                        if(editTextStr.length() == 2) {
                                            if (CommonUtil.byteContains(mTickets.get(pos), editNum, finalI)) {//拿修改后的数据判断
                                                TipsToast.showTips("号码不可重复");
                                            }
                                        }
                                        mTickets.get(pos)[finalI] = editNum;//修改数据源
                                    } else {//胆拖重复判断
                                        if(editTextStr.length() == 2) {
                                            if (CommonUtil.lottoDanTuoSame(mTickets.get(pos), editNum, finalI, pos, mTickets)) {//拿修改后的数据判断
                                                TipsToast.showTips("号码不可重复");
                                            }
                                        }
                                        mTickets.get(pos)[finalI] = editNum;//修改数据源
                                    }
                                } else {//edittext为空时默认-1
                                    if (mTicketType == 0 && isSingelRed == false) {//单式蓝球特殊处理
                                        mTickets.get(pos)[finalI + 5] = -1;
                                    } else {
                                        mTickets.get(pos)[finalI] = -1;
                                    }
                                }
                            }
                    });//内容监听
				}
				k++;
			}
		}
	}

    /**
     * 设置流式布局右下边距
     * @param layout
     */
    public void setFlowLayoutMargin(View layout){
        FlowLayout.LayoutParams lp= (FlowLayout.LayoutParams) layout.getLayoutParams();
        lp.bottomMargin= DisplayUtil.dip2px(10f);
        lp.rightMargin=DisplayUtil.dip2px(5f);
        layout.setLayoutParams(lp);//设置边距
    }
    /**
     * 设置线性布局右下边距
     * @param layout
     */
    public void setLinerLayoutMargin(View layout){
        LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) layout.getLayoutParams();
        lp.rightMargin=DisplayUtil.dip2px(5f);
        layout.setLayoutParams(lp);//设置边距
    }
}

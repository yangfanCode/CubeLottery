package com.cp2y.cube.custom;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.CustomProvinceActivity;
import com.cp2y.cube.adapter.FilterCustomAdapter;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.MapUtils;

import java.util.ArrayList;
import java.util.List;

//点击加号弹出动画菜单
public class MoreWindow extends PopupWindow implements OnClickListener {

	private String TAG = MoreWindow.class.getSimpleName();
	Activity mContext;
	private int mWidth;
	private int mHeight;
	private int statusBarHeight;
	private Bitmap mBitmap = null;
	private Bitmap overlay = null;
	private boolean isShowAnimal = true;//是否展示动画
	private Handler mHandler = new Handler();

	public MoreWindow(Activity context) {
		mContext = context;
	}

	public void init() {
//		Rect frame = new Rect();
//		mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//		statusBarHeight = frame.top;
//		DisplayMetrics metrics = new DisplayMetrics();
//		mContext.getWindowManager().getDefaultDisplay()
//				.getMetrics(metrics);
//		mWidth = metrics.widthPixels;
//		mHeight = metrics.heightPixels;
//设置popwindow的宽高
		setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
	}

	private Bitmap blur() {
		if (null != overlay) {
			return overlay;
		}
		long startMs = System.currentTimeMillis();

		View view = mContext.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		mBitmap = view.getDrawingCache();

		float scaleFactor = 8;
		float radius = 10;
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();

		overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(mBitmap, 0, 0, paint);

		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		Log.i(TAG, "blur time is:" + (System.currentTimeMillis() - startMs));
		return overlay;
	}

	private Animation showAnimation1(final View view, int fromY, int toY) {
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
		go.setDuration(300);
		TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
		go1.setDuration(100);
		go1.setStartOffset(250);
		set.addAnimation(go1);
		set.addAnimation(go);

		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		});
		return set;
	}


	public void showMoreWindow(View anchor) {
		final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.center_add_filter, null);
		setContentView(layout);
		List<CustomModel.Detail> list = new ArrayList<>();//数据
		ImageView close = (ImageView) layout.findViewById(R.id.center_add_filter_close);
		TextView title = (TextView) layout.findViewById(R.id.toolbar_title);
		RelativeLayout noDevelop_layout = (RelativeLayout) layout.findViewById(R.id.noDevelop_layout);
		MyGridView gv = (MyGridView) layout.findViewById(R.id.filter_custom_gv);
		FilterCustomAdapter adapter = new FilterCustomAdapter(mContext);
		gv.setAdapter(adapter);
		title.setText("选号过滤");
		boolean loginState = LoginSPUtils.isLogin();//获取登录状态
		if (!loginState) {//未登录
			list.addAll(((BaseActivity) mContext).initCustomData(MapUtils.FILTER_DEVELOP,false));//排序数据
			if (list.size() == 0) {
				showNoDevelop(noDevelop_layout);
			} else {
				isShowAnimal = true;
				adapter.loadData(list);
			}
			showViewLayout(anchor, layout, close);//展示
		} else {//已登录
			NetHelper.LOTTERY_API.getFilterHome(LoginSPUtils.getInt("id", 0), NetConst.VERSION_CONTROL_ID).subscribe(new SafeOnlyNextSubscriber<CustomModel>() {
				@Override
				public void onNext(CustomModel args) {
					super.onNext(args);
					if (args.getLotteryCustom() != null && args.getLotteryCustom().size() > 0) {
						//CustomLotteryList.synchronizedLoginData(args.getLotteryCustom());//同步登陆数据
						//不用排序了 在判断敬请期待为空拿数据放后面
						//List<CustomModel.Detail> sort = ((BaseActivity) mContext).initLoginCustomData(MapUtils.FILTER_DEVELOP, args.getLotteryCustom());//排序数据
						//有已开发
						isShowAnimal = true;
						list.addAll(args.getLotteryCustom());
						if(args.getLotteryCustomAZ()!=null){
							list.addAll(args.getLotteryCustomAZ());
						}
						adapter.loadData(list);
					} else {
						showNoDevelop(noDevelop_layout);
					}
					showViewLayout(anchor, layout, close);//展示
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					TipsToast.showTips("请检查网络设置");
				}
			});
		}
	}

	private void showViewLayout(View anchor, final RelativeLayout layout, ImageView close) {
		close.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.center_add_anim));
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowing()) {
					if (isShowAnimal) {
						closeAnimation(layout);
					} else {
						dismiss();
					}
				}
			}

		});
		if (isShowAnimal) showAnimation(layout);
		//setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
		setOutsideTouchable(true);
		setFocusable(true);
		showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
	}

	//展示未开发定制布局
	private void showNoDevelop(RelativeLayout noDevelop_layout) {
		isShowAnimal = false;
		View view = LayoutInflater.from(mContext).inflate(R.layout.nodevelop_layout, null);
		noDevelop_layout.addView(view);
		noDevelop_layout.setVisibility(View.VISIBLE);
		((Button) view.findViewById(R.id.go_custom_btn)).setOnClickListener((v -> mContext.startActivity(new Intent(mContext, CustomProvinceActivity.class))));
	}

	private void showAnimation(ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			final View child = layout.getChildAt(i);
			if (child.getId() == R.id.center_add_layout || child.getId() == R.id.app_title) {
				continue;
			}
			//child.setOnClickListener(this);
			child.setVisibility(View.INVISIBLE);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					child.setVisibility(View.VISIBLE);
					ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
					fadeAnim.setDuration(300);
					KickBackAnimator kickAnimator = new KickBackAnimator();
					kickAnimator.setDuration(150);
					fadeAnim.setEvaluator(kickAnimator);
					fadeAnim.start();
				}
			}, i * 50);
		}

	}

	private void closeAnimation(ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			final View child = layout.getChildAt(i);
			if (child.getId() == R.id.center_add_layout || child.getId() == R.id.app_title) {
				continue;
			}
			//child.setOnClickListener(this);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					child.setVisibility(View.VISIBLE);
					ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
					fadeAnim.setDuration(200);
					KickBackAnimator kickAnimator = new KickBackAnimator();
					kickAnimator.setDuration(100);
					fadeAnim.setEvaluator(kickAnimator);
					fadeAnim.start();
					fadeAnim.addListener(new AnimatorListener() {

						@Override
						public void onAnimationStart(Animator animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animator animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animator animation) {
							child.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onAnimationCancel(Animator animation) {
							// TODO Auto-generated method stub

						}
					});
				}
			}, (layout.getChildCount() - i - 1) * 30);

			if (child.getId() == R.id.filter_custom_gv) {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						dismiss();
					}
				}, (layout.getChildCount() - i) * 30 + 80);
			}
		}

	}

	@Override
	public void onClick(View v) {

	}

	public void destroy() {
		if (null != overlay) {
			overlay.recycle();
			overlay = null;
			System.gc();
		}
		if (null != mBitmap) {
			mBitmap.recycle();
			mBitmap = null;
			System.gc();
		}
	}

}

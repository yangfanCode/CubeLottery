package com.cp2y.cube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.numlibrary.NumLibraryActivity;
import com.cp2y.cube.model.jpush.JPushExtra;
import com.cp2y.cube.model.jpush.JpushExtrasType;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LogUtil;
import com.cp2y.cube.utils.MapUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
//			LogUtil.LogE(MyReceiver.class,"onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                LogUtil.LogE(MyReceiver.class, "JPush用户注册成功 接收Registration Id : " + regId);
                //send the Registration Id to your server...
                //SharedPreferencesUtil.getInstance().putString(SharedPreferencesUtil.KEY_JPUSH_REGISTRATION_ID, regId);
                //CommonUtil2.bindJiGuangToken();
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                // TODO 目前暂不支持接收自定义消息
                LogUtil.LogE(MyReceiver.class, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                LogUtil.LogE(MyReceiver.class, "接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                LogUtil.LogE(MyReceiver.class, "接收到推送下来的通知的ID: " + notifactionId);
                receivingNotification(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                LogUtil.LogE(MyReceiver.class, "用户点击打开了通知");
                openNotification(context, bundle);

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                LogUtil.LogE(MyReceiver.class, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                LogUtil.LogE(MyReceiver.class, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                LogUtil.LogE(MyReceiver.class, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtil.LogE(MyReceiver.class, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
                    }
                } catch (JSONException e) {
                    LogUtil.LogE(MyReceiver.class, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        LogUtil.LogE(MyReceiver.class, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        LogUtil.LogE(MyReceiver.class, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        LogUtil.LogE(MyReceiver.class, "extras : " + extras);
    }

    //	private void openNotification(Context context, Bundle bundle) {
//		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//		LogUtil.LogE(MyReceiver.class, "extras : " + extras);
//
//		if (!TextUtils.isEmpty(extras)) {
//			Gson gson = new Gson();
//			JPushExtraParent jPushExtraParent = gson.fromJson(extras, JPushExtraParent.class);
//			if (jPushExtraParent != null && !TextUtils.isEmpty(jPushExtraParent.extras)) {
//				JPushExtra jPushExtra = gson.fromJson(jPushExtraParent.extras, JPushExtra.class);
//				if (jPushExtra != null && !TextUtils.isEmpty(jPushExtra.type)) {
//					Bundle bundle2;
//					JpushExtrasType jpushExtrasType = JpushExtrasType.createWithCode(jPushExtra.type);
//					if(jpushExtrasType != null){
//						switch (jpushExtrasType) {
//							case type1://开奖详情
//								CommonUtil.openActicity(context, MainActivity.class,null);
//								break;
//							case type2://号码库
//								Intent intent=new Intent(context, NumLibraryActivity.class);
//								intent.putExtra("flag", MapUtils.LOTTERY_FLAG.get(Integer.valueOf(jPushExtra.lotteryId)));
//								context.startActivity(intent);
//								break;
//						}
//					}
//				}
//			}
//		}
//	}
    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        LogUtil.LogE(MyReceiver.class, "extras : " + extras);

        if (!TextUtils.isEmpty(extras)) {
            Gson gson = new Gson();
            JPushExtra jPushExtra = gson.fromJson(extras, JPushExtra.class);
            if (jPushExtra != null && !TextUtils.isEmpty(jPushExtra.type)) {
                Bundle bundle2;
                JpushExtrasType jpushExtrasType = JpushExtrasType.createWithCode(jPushExtra.type);
                if (jpushExtrasType != null) {
                    switch (jpushExtrasType) {
                        case type0://开奖详情
                            Intent intent = new Intent(context, NumLibraryActivity.class);
                            intent.putExtra("flag", MapUtils.LOTTERY_FLAG.get(Integer.valueOf(jPushExtra.lotteryId)));
                            context.startActivity(intent);
                            break;
                        case type1://号码库
                            CommonUtil.openActicity(context, MainActivity.class, null);
                            break;
                    }
                }
            }
        }
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//		}
    }

//	private void getUserInfo(final Context context, final String userId) {
//		Map<String, Object> map = new HashMap<>();
//		if (!TextUtils.isEmpty(userId))
//			map.put("userId", userId);
//		HttpUtils.getInstance().getPost("", false, null).getUserInfo(map).subscribeOn(Schedulers.io()).
//				observeOn(AndroidSchedulers.mainThread())
//				.subscribe(new HttpUtils.RxObserver<ResultBean<Login>>() {
//					@Override
//					public void onSuccess(ResultBean<Login> resultBean) {
//						if (resultBean == null) return;
//						if (resultBean.success && resultBean.data != null) {
//							Login login = resultBean.data;
//							DemoHelper.getInstance().createEaseUserAndSave(userId, login.userName, CommonUtil2.getPictureFullAddress(login.userAvatar, null));
//							Bundle bundle2 = new Bundle();
//							bundle2.putString(ImConstant.EXTRA_USER_ID, CommonUtil2.getHxImUserIdByAppUserId(userId));
//							bundle2.putString(ImConstant.EXTRA_RECEIVE_USER_NICK_NAME, login.userName);
//							bundle2.putString(ImConstant.EXTRA_RECEIVE_USER_AVATAR, CommonUtil2.getPictureFullAddress(login.userAvatar, null));
//							CommonUtil2.openActicity(context, ChatActivity.class, bundle2, Intent.FLAG_ACTIVITY_NEW_TASK);
//						}
//					}
//				});
//	}

}

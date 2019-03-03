package com.cp2y.cube.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 处理tagalias相关的逻辑
 */
public class TagAliasOperatorHelper {
    private static final String TAG = "JIGUANG-TagAliasHelper";
    public static int sequence = 1;
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;

    public static final int ACTION_CHECK = 6;

    public static final int DELAY_SEND_ACTION = 1;
    private Context context;

    private static TagAliasOperatorHelper mInstance;

    private TagAliasOperatorHelper() {
    }

    public static TagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context != null && context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private HashMap<Integer, TagAliasBean> tagAliasActionCache = new HashMap<Integer, TagAliasBean>();

    public TagAliasBean get(int sequence) {
        return tagAliasActionCache.get(sequence);
    }

    public TagAliasBean remove(int sequence) {
        return tagAliasActionCache.get(sequence);
    }

    public void put(int sequence, TagAliasBean tagAliasBean) {
        tagAliasActionCache.put(sequence, tagAliasBean);
    }

    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasBean) {
                        LogUtil.LogE(TagAliasOperatorHelper.class, "on delay time");
                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        tagAliasActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            handleAction(context, sequence, tagAliasBean);
                        } else {
                            LogUtil.LogE(TagAliasOperatorHelper.class, "#unexcepted - context was null");
                        }
                    } else {
                        LogUtil.LogE(TagAliasOperatorHelper.class, "#unexcepted - msg obj was incorrect");
                    }
                    break;
            }
        }
    };

    /**
     * 处理设置tag
     */
    public void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            LogUtil.LogE(TagAliasOperatorHelper.class, "tagAliasBean was null");
            return;
        }
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {
                case ACTION_GET:
                    JPushInterface.getAlias(context, sequence);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
//                    JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                    JPushInterface.setAlias(context, tagAliasBean.alias, mAliasCallback);
                    break;
                default:
                    LogUtil.LogE(TagAliasOperatorHelper.class, "unsupport alias action type");
            }
        } else {
            switch (tagAliasBean.action) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_CHECK:
                    //一次只能check一个tag
                    String tag = (String) tagAliasBean.tags.toArray()[0];
                    JPushInterface.checkTagBindState(context, sequence, tag);
                    break;
                case ACTION_GET:
                    JPushInterface.getAllTags(context, sequence);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    LogUtil.LogE(TagAliasOperatorHelper.class, "unsupport tag action type");
                    break;
            }
        }
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
//                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
            }
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
        }
    };

    private boolean RetryActionIfNeeded(int errorCode, TagAliasBean tagAliasBean) {
        if (!CommonUtil.isNetworkConnected(context)) {
            LogUtil.LogE(TagAliasOperatorHelper.class, "no network");
            return false;
        }
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            LogUtil.LogE(TagAliasOperatorHelper.class, "need retry");
            if (tagAliasBean != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasBean;
                delaySendHandler.sendMessageDelayed(message, 1000 * 60);
//                String logs = getRetryStr(tagAliasBean != null ? tagAliasBean.isAliasAction : false, tagAliasBean != null ? tagAliasBean.action : -1, errorCode);
//                ExampleUtil.showToast(logs, context);
                return true;
            }
        }
        return false;
    }

    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
        String str = "Failed to %s %s due to %s. Try again after 60s.";
        str = String.format(Locale.ENGLISH, str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"), (errorCode == 6002 ? "timeout" : "server too busy"));
        return str;
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_ADD:
                return "add";
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
            case ACTION_GET:
                return "get";
            case ACTION_CLEAN:
                return "clean";
            case ACTION_CHECK:
                return "check";
        }
        return "unkonw operation";
    }

    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LogUtil.LogE(TagAliasOperatorHelper.class, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        LogUtil.LogE(TagAliasOperatorHelper.class, "tags size:" + jPushMessage.getTags().size());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = tagAliasActionCache.get(sequence);
        if (tagAliasBean == null) {
//            ExampleUtil.showToast("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LogUtil.LogE(TagAliasOperatorHelper.class, "action - modify tag Success,sequence:" + sequence);
            tagAliasActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tags success";
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
//            ExampleUtil.showToast(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
//                ExampleUtil.showToast(logs, context);
            }
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LogUtil.LogE(TagAliasOperatorHelper.class, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = tagAliasActionCache.get(sequence);
        if (tagAliasBean == null) {
//            ExampleUtil.showToast("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LogUtil.LogE(TagAliasOperatorHelper.class, "tagBean:" + tagAliasBean);
            tagAliasActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
//            ExampleUtil.showToast(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
//                ExampleUtil.showToast(logs, context);
            }
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        LogUtil.LogE(TagAliasOperatorHelper.class, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = tagAliasActionCache.get(sequence);
        if (tagAliasBean == null) {
//            ExampleUtil.showToast("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            LogUtil.LogE(TagAliasOperatorHelper.class, "action - modify alias Success,sequence:" + sequence);
            tagAliasActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " alias success";
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
//            ExampleUtil.showToast(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
            LogUtil.LogE(TagAliasOperatorHelper.class, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
//                ExampleUtil.showToast(logs, context);
            }
        }
    }

    public static class TagAliasBean {
        public int action;
        public Set<String> tags;
        public String alias;
        public boolean isAliasAction;

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }


}

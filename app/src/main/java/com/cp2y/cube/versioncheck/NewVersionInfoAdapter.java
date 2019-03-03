package com.cp2y.cube.versioncheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 新版本内容
 * Created by yangfan on 2017/9/23.
 * nrainyseason@163.com
 */

public class NewVersionInfoAdapter extends BaseAdapter {

    private String[] mList;
    private Context mContext;

    public NewVersionInfoAdapter(Context context, String[] list) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public Object getItem(int position) {
        return mList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder mHolder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_new_version_info_item, null);
            mHolder = new ViewHolder(view);
            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }
        mHolder.tvContent.setText(mList[position].trim());

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.tv_content)
        TextView tvContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

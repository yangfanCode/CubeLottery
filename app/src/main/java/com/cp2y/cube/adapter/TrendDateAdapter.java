package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendDateAdapter extends BaseAdapter {

    private Context context;
    private List<String> data = new ArrayList<>();

    public TrendDateAdapter(Context context) {
        this.context = context;
    }

    public void reloadData(List<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void reverseData() {
        Collections.reverse(this.data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_date, null);
        }
        TextView date = (TextView) convertView.findViewById(R.id.trend_date);
        date.setText(data.get(position));
        boolean showTopLine = position > 0 && (position % 5 == 0);
        boolean showBottomLine = position % 5 == 4;
        convertView.findViewById(R.id.line_top).setVisibility(showTopLine?View.VISIBLE:View.GONE);
        convertView.findViewById(R.id.line_bottom).setVisibility(showBottomLine?View.VISIBLE:View.GONE);
        return convertView;
    }
}

package com.cp2y.cube.activity.multicalc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.multicalc.fragment.adapter.MultiCalcResultAdapter;
import com.cp2y.cube.model.MultiCalcResultModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiResultFragment extends Fragment {
    private MultiCalcResultAdapter adapter;
    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_multi_result, container, false);
        initView(rootView);
        return rootView;
    }

    public void initData(List<MultiCalcResultModel> list) {
        if(list==null||list.size()==0)return;
        adapter.loadData(list);
    }

    private void initView(View rootView) {
        lv = (ListView) rootView.findViewById(R.id.multi_calc_rlvResult);
        adapter=new MultiCalcResultAdapter(getActivity());
        lv.setAdapter(adapter);
    }

}

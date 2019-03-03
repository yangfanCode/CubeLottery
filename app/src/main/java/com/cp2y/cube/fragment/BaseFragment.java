package com.cp2y.cube.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.cp2y.cube.services.Service;

/**
 * Created by js on 2017/1/9.
 */
public class BaseFragment extends Fragment {

    public <T extends Service> T getService(Class<T> tClass) {
        return Service.getService(tClass);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

}

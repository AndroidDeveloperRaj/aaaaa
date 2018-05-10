package com.merrichat.net.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merrichat.net.R;

/**
 * Created by amssy on 17/11/4.
 * 美遇
 */

public class MerriMeetFragment extends BaseFragment {

    private View view;

    /**
     * Fragment 的构造函数。
     */
    public MerriMeetFragment() {
    }

    public static MerriMeetFragment newInstance() {
        return new MerriMeetFragment();
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_merri_meet, container, false);
        initView();
        return view;
    }

    private void initView(){

    }
}

package com.merrichat.net.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.model.WaiteGroupBuyModel;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by amssy on 17/11/4.
 * fragment的基类
 */

public abstract class BaseFragment extends RxFragment {
    public Context cnt;
    private TextView mTvTitle;
    private View mFakeStatusBar;
    private LinearLayout llContent;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cnt = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_base, container, false);
        llContent = (LinearLayout) view.findViewById(R.id.ll_content);
        //获取到子类中的View布局
        View contentView = setContentViewResId(inflater,container,savedInstanceState);
        //添加到内容控件中
        llContent.addView(contentView);
        return view;
    }
    public abstract View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    @Override
    public void onResume() {
        super.onResume();
        // 统计页面
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
//        //统计时长
        MobclickAgent.onResume(cnt);
    }

    @Override
    public void onPause() {
        super.onPause();
        //保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(cnt);
    }
}

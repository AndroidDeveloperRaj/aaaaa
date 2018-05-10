package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.CircleVideoAdapter;
import com.merrichat.net.adapter.DashVideoAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.DashModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频打赏
 * Created by amssy on 18/4/16.
 */

public class VideoDashDialog extends DialogFragment implements OnLoadmoreListener, OnRefreshListener{
    /**
     * 关闭按钮
     */
    @BindView(R.id.iv_close_comment)
    ImageView ivCloseComment;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
//    @BindView(R.id.refreshLayout)
//    SmartRefreshLayout refreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rel_title)
    RelativeLayout relTitle;
    @BindView(R.id.tv_dash_title)
    TextView tvDashTitle;
    @BindView(R.id.btn_dash)
    Button btnDash;
    @BindView(R.id.rel_bottom)
    RelativeLayout relBottom;


    private int REFRESHORLOADMORE = 1;
    private int currentPage = 1;
    private String contentId;
    private String toMemberId;
    private String relName;
    private List<DashModel.DataBean> list_dash = new ArrayList<>();
    private DashVideoAdapter videoAdapter;

    public static VideoDashDialog getInstance(Context mContext, FragmentManager fm) {
        String tag = VideoDashDialog.class.getName();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, tag);
            VideoDashDialog dialogFragment = (VideoDashDialog) fragment;
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);//设置取消标题栏
            dialogFragment.setCancelable(true);//外围点击 dismiss
            return dialogFragment;
        } else {
            return (VideoDashDialog) fragment;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        //将对话框内部的背景设为透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setWindowAnimations(R.style.AnimBottom);
        window.setGravity(Gravity.BOTTOM);//居中显示
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        //将对话框外部的背景设为透明
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        windowParams.height = (int) (StringUtil.getHeight(getActivity()) * 0.5);
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(windowParams);

        // 去掉对话框默认标题栏
        View view = inflater.inflate(R.layout.video_dash_dialog, container, false);
        ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            contentId = arguments.getString("contentId");
            toMemberId = arguments.getString("toMemberId");
            relName = arguments.getString("relName");
        }

        if (UserModel.getUserModel().getMemberId().equals(toMemberId)) {
            relBottom.setVisibility(View.GONE);
        }else {
            relBottom.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        videoAdapter = new DashVideoAdapter(R.layout.item_dash_video, list_dash);
        recyclerView.setAdapter(videoAdapter);

        queryDash();
        return view;
    }


    /**
     * 判断弹窗是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    /**
     * 显示DialogFragment(注此方法会衍生出状态值问题(本人在正常使用时并未出现过))
     *
     * @param manager
     * @param tag
     * @param isResume 在Fragment中使用可直接传入isResumed()
     *                 在FragmentActivity中使用可自定义全局变量 boolean isResumed 在onResume()和onPause()中分别传人判断为true和false
     */
    public void show(FragmentManager manager, String tag, boolean isResume) {
        if (!isShowing()) {
            if (isResume) {
                //正常显示
                if (!isAdded()) {
                    show(manager, tag);
                } else {
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.show(this);
                    ft.commit();
                }
            } else {
                //注 此方法会衍生出一些状态问题,慎用（在原代码中 需要设置  mDismissed = false 和 mShownByMe = true 并未在此引用到,如果需要用到相关判断值,此方法不可用）
                FragmentTransaction ft = manager.beginTransaction();
                if (!isAdded()) {
                    ft.add(this, tag);
                } else {
                    ft.show(this);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    /**
     * 关闭DialogFragment
     *
     * @param isResume 在Fragment中使用可直接传入isResumed()
     *                 在FragmentActivity中使用可自定义全局变量 boolean isResumed 在onResume()和onPause()中分别传人判断为true和false
     */
    public void dismiss(boolean isResume) {
        if (isResume) {
            dismiss();
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (isShowing()) {
            super.dismissAllowingStateLoss();
        }
    }

    @OnClick({R.id.iv_close_comment, R.id.btn_dash})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_comment:
                dismiss();
                break;
            case R.id.btn_dash:
                if (StringUtil.isLogin(getActivity())) {
                    if (!UserModel.getUserModel().getMemberId().equals(toMemberId)) {
                        RedPacketDialog dialog = RedPacketDialog.getInstance(MerriApp.mContext, getActivity().getSupportFragmentManager());
                        Bundle bundle = new Bundle();
                        bundle.putString("contentId", "" + contentId);
                        bundle.putString("toMemberId", "" + toMemberId);
                        bundle.putString("relName", "" + relName);
                        bundle.putString("flag", "1");
                        dialog.setArguments(bundle);
                        dialog.show(getActivity().getSupportFragmentManager(), "", true);
                    }
                }else {
                    //RxToast.showToast("");
                }
                break;
        }
    }

    /**
     * 上拉加载
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        currentPage++;
    }

    /**
     * 下拉刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 1;
        currentPage = 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 查询打赏记录
     */
    private void queryDash() {
        OkGo.<String>get(Urls.GET_REWARD_LOG)
                .tag(this)
                .params("tieId", contentId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    DashModel dashModel = JSON.parseObject(response.body(), DashModel.class);
                                    list_dash.clear();
                                    list_dash.addAll(dashModel.getData());
                                    if (list_dash.size() > 0) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    videoAdapter.notifyDataSetChanged();
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        if (tvEmpty != null) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        //打赏成功
        if (myEventBusModel.VIDEO_DASH_SUCCESS) {
            RxToast.showToast("打赏成功");
            queryDash();
        }
    }
}

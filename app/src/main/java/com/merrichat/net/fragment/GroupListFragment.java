package com.merrichat.net.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.adapter.GroupListAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupListModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;

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
 * Created by xly on 2018/4/20.
 */
public class GroupListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    public static final int activityId = MiscUtil.getActivityId();
    /**
     * 群组列表
     */
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    /**
     * 群组个数
     */
    TextView tvGroupNum;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private ArrayList<GroupListModel.DataBean.ListBean> groupList = new ArrayList();
    private GroupListAdapter groupListAdapter;
    /**
     * foot布局
     */
    private View viewFooter;

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        communityList();
        return view;
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        groupListAdapter = new GroupListAdapter(R.layout.item_group_list, groupList);
        rlRecyclerview.setAdapter(groupListAdapter);
        groupListAdapter.setOnItemClickListener(this);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_GROUP_LIST) {//刷新数据
            communityList();
        }
    }

    /**
     * 群组列表
     */
    private void communityList() {
        OkGo.<String>post(Urls.communityList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data != null && data.optBoolean("success")) {

                                    GroupListModel groupListModel = new Gson().fromJson(response.body(), GroupListModel.class);
                                    List<GroupListModel.DataBean.ListBean> listBeans = groupListModel.getData().getList();
                                    if (listBeans != null && listBeans.size() > 0) {
                                        groupList.clear();
                                        groupList.addAll(listBeans);
                                        tvEmpty.setVisibility(View.GONE);
                                        rlRecyclerview.setVisibility(View.VISIBLE);
                                        addFooter();
                                        groupListAdapter.notifyDataSetChanged();
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        rlRecyclerview.setVisibility(View.GONE);
                                    }

                                }
                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                                tvEmpty.setVisibility(View.VISIBLE);
                                rlRecyclerview.setVisibility(View.GONE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvEmpty.setVisibility(View.VISIBLE);
                            rlRecyclerview.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tvEmpty.setVisibility(View.VISIBLE);
                        rlRecyclerview.setVisibility(View.GONE);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    private void addFooter() {
        viewFooter = getActivity().getLayoutInflater().inflate(R.layout.layout_group_list_foot, (ViewGroup) rlRecyclerview.getParent(), false);
        tvGroupNum = (TextView) viewFooter.findViewById(R.id.tv_group_num);
        tvGroupNum.setText(groupList.size() + "个群组");
        groupListAdapter.addFooterView(viewFooter, 0);

    }

    @OnClick({R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty:
                communityList();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        Intent intent = new Intent(cnt, GroupChattingAty.class);
        intent.putExtra("groupId", groupList.get(position).getCommunityId());
        intent.putExtra("group", groupList.get(position).getCommunityName());
        intent.putExtra("groupLogoUrl", groupList.get(position).getCommunityImgUrl());
        intent.addFlags(activityId);
        startActivity(intent);
    }
}

package com.merrichat.net.activity.groupmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.BuyMarketAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMarketModel;
import com.merrichat.net.utils.RxTools.RxToast;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我要买
 * Created by amssy on 18/1/18.
 */
public class BuyMarketFragment extends BaseFragment implements BuyMarketAdapter.onItemClick,OnLoadmoreListener, OnRefreshListener {
    @BindView(R.id.recycler_market)
    RecyclerView recyclerMarket;
    Unbinder unbinder1;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private View view;
    private BuyMarketAdapter adapter;
    private String groupId = "";//群ID
    private int pageSize = 10;
    private int pageNum = 1;
    private ArrayList<GroupMarketModel.DataBean> listMarket;
    private int REFRESHORLOADMORE = 1;

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy_market, container, false);
        unbinder1 = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    private void initView() {
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);

        //注册广播
        EventBus.getDefault().register(this);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra("groupId");
        }

        listMarket = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMarket.setLayoutManager(linearLayoutManager);
        adapter = new BuyMarketAdapter(getActivity(), listMarket);
        recyclerMarket.setAdapter(adapter);
        adapter.setOnBuyMarketItemClickLinster(this);
        //查询群市场上屏列表
        findMarketList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBuyMarketItemClickLinster(int position) {
        //跳转详情 传入交易ID
        startActivity(new Intent(getActivity(), MarketShopDetailAty.class).putExtra("id", "" + listMarket.get(position).getId()));
    }

    /**
     * 查询群市场商品列表
     */
    private void findMarketList() {
        OkGo.<String>get(Urls.findTransInfoByGroupId)
                .tag(this)
                .params("groupId", groupId)
                .params("pageSize", pageSize)
                .params("pageNum", pageNum)
                .execute(new StringDialogCallback(getActivity()) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                if (refreshLayout != null) {
                                    if (REFRESHORLOADMORE == 1) {
                                        listMarket.clear();
                                        refreshLayout.finishRefresh();
                                    } else {
                                        refreshLayout.finishLoadmore();
                                    }
                                }
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    GroupMarketModel groupMarketModel = JSON.parseObject(response.body(), GroupMarketModel.class);
                                    listMarket.addAll(groupMarketModel.getData());
                                    if (groupMarketModel.getData() == null || groupMarketModel.getData().size() == 0) {
                                        refreshLayout.setLoadmoreFinished(true);
                                    }
                                    if (listMarket.size() == 0){
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tvEmpty.setText("暂无数据哦～");
                                        }
                                    }else {
                                        adapter.notifyDataSetChanged();
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvEmpty.setText("暂无数据哦～");
                                    }
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
                            tvEmpty.setText("网络请求失败，请重试");
                        }
                        if (refreshLayout != null) {
                            if (REFRESHORLOADMORE == 1) {
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadmore();
                            }
                        }
                    }
                });
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.SHOP_SOLD_OUT) {//商品下架成功刷新界面
            findMarketList();
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        pageNum++;
        findMarketList();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 1;
        pageNum = 1;
        findMarketList();
    }
}

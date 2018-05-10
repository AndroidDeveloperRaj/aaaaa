package com.merrichat.net.activity.grouporder;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.PendingRegimentAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.QueryOrderJsonModel;
import com.merrichat.net.model.SellOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by AMSSY1 on 2018/1/19.
 * <p>
 * 销售订单--待成团
 */

public class PendingRegimentFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, BaseQuickAdapter.OnItemChildClickListener {
    private final int orderStatus = 0;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
    private final String key = "sale";//{sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    @BindView(R.id.swipe_refresh_layout)
    SmartRefreshLayout swipeRefreshLayout;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    Unbinder unbinder;
    private PendingRegimentAdapter pendingRegimentAdapter;
    private List<SellOrderModel.DataBean> pendingRegimentList = new ArrayList();
    private int refreshOrLoadMore = -1;//5 刷新，6 加载更多
    private CommomDialog dialog;
    private int pageSize = 10;//每页的条数
    private int currentPage = 1;//当前页数

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_regiment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        EventBus.getDefault().register(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        pendingRegimentAdapter = new PendingRegimentAdapter(R.layout.item_pending_regiment, pendingRegimentList);
        rlRecyclerview.setAdapter(pendingRegimentAdapter);
        pendingRegimentAdapter.setOnItemChildClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
        if (pendingRegimentList.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //查询销售订单
            pendingRegimentList.clear();
            currentPage = 1;
            queryOrder();
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_PENDING_REGIMENT) {
            //查询销售订单
            pendingRegimentList.clear();
            currentPage = 1;
            queryOrder();
        }

    }

    /**
     * 查询销售订单
     */
    private void queryOrder() {
        QueryOrderJsonModel queryOrderJsonModel = new QueryOrderJsonModel();
        String memberId = UserModel.getUserModel().getMemberId();
        if (RxDataTool.isNullString(memberId)) {
            RxToast.showToast("请登录后查看！");
            return;
        }
        queryOrderJsonModel.setOrderStatus(orderStatus);
        queryOrderJsonModel.setKey(key);
        queryOrderJsonModel.setPageSize(pageSize);
        queryOrderJsonModel.setCurrentPage(this.currentPage);
        queryOrderJsonModel.setMemberId(memberId);
        String jsonStr = JSON.toJSONString(queryOrderJsonModel);
        OkGo.<String>post(Urls.queryOrder)//
                .tag(this)//
                .params("jsObject", jsonStr)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                if (swipeRefreshLayout != null) {
                                    if (refreshOrLoadMore == 5) {
                                        pendingRegimentList.clear();
                                        swipeRefreshLayout.finishRefresh();
                                    } else if (refreshOrLoadMore == 6) {
                                        swipeRefreshLayout.finishLoadmore();
                                    }
                                }
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    SellOrderModel sellOrderModel = JSON.parseObject(response.body(), SellOrderModel.class);
                                    List<SellOrderModel.DataBean> dataBeans = sellOrderModel.getData();
                                    if (dataBeans != null && dataBeans.size() > 0) {
                                        pendingRegimentList.addAll(dataBeans);
                                    }
                                    if (tvEmpty != null) {
                                        if (pendingRegimentList.size() > 0) {
                                            tvEmpty.setVisibility(View.GONE);
                                        } else {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    pendingRegimentAdapter.notifyDataSetChanged();

                                } else {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvEmpty.setVisibility(View.VISIBLE);
                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tvEmpty.setVisibility(View.VISIBLE);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        if (swipeRefreshLayout != null) {
                            if (refreshOrLoadMore == 5) {
                                pendingRegimentList.clear();
                                swipeRefreshLayout.finishRefresh();
                            } else if (refreshOrLoadMore == 6) {
                                swipeRefreshLayout.finishLoadmore();
                            }
                        }
                    }
                });

    }

    /**
     * 刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshOrLoadMore = 5;
        currentPage = 1;
        queryOrder();
    }

    /**
     * 加载更多
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        refreshOrLoadMore = 6;
        currentPage++;
        queryOrder();

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        SellOrderModel.DataBean dataBean = pendingRegimentList.get(position);
        final String serialNumber = dataBean.getSerialNumber();
        switch (view.getId()) {
            case R.id.tv_contact_buyer://取消成团  0取消，1同意
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要取消成团吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                agreeRegiment(serialNumber, 0, position);
                            }
                        }
                    }).setTitle("取消成团");
                    dialog.show();
                }
                break;
            case R.id.tv_contact_seller://同意成团 0取消，1同意
                agreeRegiment(serialNumber, 1, position);
                break;
        }
    }

    /**
     * 取消成团 同意成团
     *
     * @param serialNumber 拼团号
     * @param type         0取消，1同意
     * @param position     位置
     */
    private void agreeRegiment(String serialNumber, final int type, final int position) {

        OkGo.<String>post(Urls.agreeRegiment)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("serialNumber", serialNumber)
                .params("type", type)//0取消，1同意
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    pendingRegimentList.remove(position);
                                    pendingRegimentAdapter.notifyDataSetChanged();
                                    if (type == 0) {
                                        RxToast.showToast("已取消成团！");

                                    } else if (type == 1) {
                                        RxToast.showToast("已成团！");
                                    }
                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
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

                    }
                });

    }


    @OnClick({R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty://空白页
                //查询销售订单
                pendingRegimentList.clear();
                currentPage = 1;
                queryOrder();
                break;

        }
    }
}

package com.merrichat.net.activity.grouporder;

import android.app.Dialog;
import android.content.Intent;
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
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.adapter.PendingDeliveryAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.PendingDeliveryModel;
import com.merrichat.net.model.QueryOrderJsonModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxActivityTool;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by AMSSY1 on 2018/1/22.
 * <p>
 * 销售订单--待发货
 */

public class PendingDeliveryFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener, OnLoadmoreListener {


    private final int orderStatus = 2;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
    private final String key = "sale";//{sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
    private final int orderFlag = 1;//0:买家 1:表示是卖家
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
    private ArrayList<PendingDeliveryModel.DataBean> pendingDeliveryList = new ArrayList<PendingDeliveryModel.DataBean>();
    private PendingDeliveryAdapter pendingDeliveryAdapter;
    private int pageSize = 10;//每页的条数
    private int currentPage = 1;//当前页数
    private int refreshOrLoadMore = -1;//5 刷新，6 加载更多
    private CommomDialog dialog;

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
        pendingDeliveryAdapter = new PendingDeliveryAdapter(R.layout.item_pending_delivery, pendingDeliveryList);
        rlRecyclerview.setAdapter(pendingDeliveryAdapter);
        pendingDeliveryAdapter.setOnItemChildClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_PENDING_DELIVERY) {
            //查询销售订单
            pendingDeliveryList.clear();
            currentPage = 1;
            queryOrder();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //查询销售订单
            pendingDeliveryList.clear();
            currentPage = 1;
            queryOrder();
        }
    }
    @OnClick({R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty://空白页
                //查询销售订单
                pendingDeliveryList.clear();
                currentPage = 1;
                queryOrder();
                break;

        }
    }
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        PendingDeliveryModel.DataBean dataBean = pendingDeliveryList.get(position);
        switch (view.getId()) {
            case R.id.ll_product_info://详情
                Intent intent = new Intent(getActivity(), SellOrderDetailAty.class);
                intent.putExtra("serialNumber", dataBean.getOrderId());
                intent.putExtra("fragmentId", "待发货");
                intent.putExtra("hisMemberId", dataBean.getMemberId());
                startActivity(intent);
                break;
            case R.id.tv_contact_seller://确认发货
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderBeanInfo", dataBean);
                RxActivityTool.skipActivity(getActivity(), ConfirmDeliveryGoodAty.class, bundle);
                break;
            case R.id.tv_contact_cancle://取消订单
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要取消订单吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                deleteOrder(position);

                            }
                        }
                    }).setTitle("取消订单");
                    dialog.show();
                }
                break;
            case R.id.tv_contact_buyer://联系买家
                Intent intent1 = new Intent(getActivity(), SingleChatActivity.class);
                intent1.putExtra("receiverMemberId", dataBean.getMemberId());
                intent1.putExtra("receiverHeadUrl", dataBean.getMemberUrl());
                intent1.putExtra("receiverName", dataBean.getMemberName());
                startActivity(intent1);
                break;
        }
    }

    /**
     * 取消订单
     *
     * @param position
     */
    private void deleteOrder(final int position) {
        PendingDeliveryModel.DataBean dataBean = pendingDeliveryList.get(position);
        String orderId = dataBean.getOrderId();
        OkGo.<String>post(Urls.deleteOrder)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("orderId", orderId)
                .params("flag", orderFlag)
                .params("orderStatus", orderStatus)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        pendingDeliveryList.remove(position);
                                        RxToast.showToast(data.optString("message"));
                                        pendingDeliveryAdapter.notifyDataSetChanged();
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
        queryOrderJsonModel.setCurrentPage(currentPage);
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
                                        pendingDeliveryList.clear();
                                        swipeRefreshLayout.finishRefresh();
                                    } else if (refreshOrLoadMore == 6) {
                                        swipeRefreshLayout.finishLoadmore();
                                    }
                                }
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    PendingDeliveryModel pendingDeliveryModel = JSON.parseObject(response.body(), PendingDeliveryModel.class);
                                    List<PendingDeliveryModel.DataBean> dataBeans = pendingDeliveryModel.getData();
                                    if (dataBeans != null && dataBeans.size() > 0) {
                                        pendingDeliveryList.addAll(dataBeans);
                                    }
                                    if (tvEmpty!=null) {
                                        if (pendingDeliveryList.size() > 0) {
                                            tvEmpty.setVisibility(View.GONE);
                                        } else {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    pendingDeliveryAdapter.notifyDataSetChanged();

                                } else {
                                    tvEmpty.setVisibility(View.GONE);
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvEmpty.setVisibility(View.GONE);
                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tvEmpty.setVisibility(View.GONE);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        if (swipeRefreshLayout != null) {
                            if (refreshOrLoadMore == 5) {
                                pendingDeliveryList.clear();
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


}

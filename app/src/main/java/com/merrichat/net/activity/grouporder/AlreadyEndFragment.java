package com.merrichat.net.activity.grouporder;

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
import com.merrichat.net.adapter.AlreadyEndAdapter;
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
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by AMSSY1 on 2018/1/22.
 * <p>
 * 销售订单--已结束
 */

public class AlreadyEndFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener, OnLoadmoreListener {
    private final int orderStatus = 4;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
    private final String key = "sale";//{sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
    private final int orderFlag = 2;//1退货退款，2直接退款，3拼团失败直接退款, 4申请仲裁
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
    private ArrayList<PendingDeliveryModel.DataBean> pendingDeliveryList = new ArrayList<>();
    private AlreadyEndAdapter alreadyEndAdapter;
    private int pageSize = 10;//每页的条数
    private int currentPage = 1;//当前页数
    private int refreshOrLoadMore = -1;//5 刷新，6 加载更多

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_regiment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        alreadyEndAdapter = new AlreadyEndAdapter(R.layout.item_pending_delivery, pendingDeliveryList);

        rlRecyclerview.setAdapter(alreadyEndAdapter);
        alreadyEndAdapter.setOnItemChildClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
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
                                    if (tvEmpty != null) {
                                        if (pendingDeliveryList.size() > 0) {
                                            tvEmpty.setVisibility(View.GONE);
                                        } else {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    alreadyEndAdapter.notifyDataSetChanged();

                                } else {
                                    tvEmpty.setVisibility(View.VISIBLE);

                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                            } catch (JSONException e) {
                                tvEmpty.setVisibility(View.VISIBLE);

                                e.printStackTrace();

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
                                pendingDeliveryList.clear();
                                swipeRefreshLayout.finishRefresh();
                            } else if (refreshOrLoadMore == 6) {
                                swipeRefreshLayout.finishLoadmore();
                            }
                        }
                    }
                });

    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        PendingDeliveryModel.DataBean dataBean = pendingDeliveryList.get(position);
        String orderId = dataBean.getOrderId();
        switch (view.getId()) {
            case R.id.tv_contact_seller://查看物流
                switch (dataBean.getSendType()) {
                    case "1":
                        Bundle bundle = new Bundle();
                        bundle.putString("orderId", orderId);
                        RxActivityTool.skipActivity(getActivity(), ChaKanWuLiuActivity.class, bundle);
                        break;
                    case "2":
                        RxToast.showToast("此订单自取！");
                        break;
                    case "3":
                        RxToast.showToast("此订单送货上门！");
                        break;
                    default:
                        break;
                }

                break;
            case R.id.ll_product_info://详情
                Bundle bundle1 = new Bundle();
                bundle1.putString("serialNumber", orderId);
                bundle1.putString("fragmentId", "已结束");
                bundle1.putString("hisMemberId", dataBean.getMemberId());
                RxActivityTool.skipActivity(getActivity(), SellOrderDetailAty.class, bundle1);

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

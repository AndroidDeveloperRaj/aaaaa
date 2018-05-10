package com.merrichat.net.activity.message.setting;

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
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.adapter.ToBeDeliveredAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.QueryOrderJsonModel;
import com.merrichat.net.model.SellOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
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
 * Created by amssy on 18/2/5.
 * 群订单管理--仲裁退款
 */

public class ArbitrationRefundsFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener {
    /**
     * 订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
     */
    private final int orderStatus = 7;
    /**
     * {sale:销售订单,buy:购买订单} 群查询的时候传sale,因为群订单只有销售订单
     */
    private final String key = "sale";
    Unbinder unbinder;
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    @BindView(R.id.swipe_refresh_layout)
    SmartRefreshLayout swipeRefreshLayout;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    private ToBeDeliveredAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<SellOrderModel.DataBean> pendingRegimentList;
    /**
     * 下拉刷新
     */
    private int PullDownRefresh = 1;
    /**
     * 上拉加载更多
     */
    private int PullupLoading = 2;
    /**
     * 下拉刷新或是上拉加载数据
     */
    private int REFESH_TYPE;
    /**
     * 每页的条数
     */
    private int pageSize = 10;

    /**
     * 当前页数
     */
    private int currentPage = 1;


    /**
     * 群id
     */
    private String groupId = "";

    /**
     * 待发货 已发货  已结束   仲裁退款  共用一个adaptr
     * source 代表来源
     * 待发货  2
     * 已发货  3
     * 已结束  4
     * 仲裁退款  5
     */
    private int source = 5;
    private int checkTransMapPosition = 0;//拒绝仲裁的位置

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_be_grouped, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_GROUP_ORDER_LIST) {//刷新页面
            currentPage = 1;
            REFESH_TYPE = PullDownRefresh;
            queryOrder(REFESH_TYPE);
        }
        if (myEventBusModel.FINISH_GROUP_ORDER_DETAILS) {
            if (pendingRegimentList != null && pendingRegimentList.size() > 0) {
                SellOrderModel.DataBean dataBean = pendingRegimentList.get(checkTransMapPosition);
                dataBean.setReStatus("6");
                pendingRegimentList.set(checkTransMapPosition, dataBean);
                adapter.notifyItemChanged(checkTransMapPosition);
            }

        }
    }

    private void initView() {
        groupId = getActivity().getIntent().getStringExtra("groupId");
        pendingRegimentList = new ArrayList<>();
        rlRecyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlRecyclerview.setLayoutManager(layoutManager);
        adapter = new ToBeDeliveredAdapter(cnt, pendingRegimentList, source);
        rlRecyclerview.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
        REFESH_TYPE = PullDownRefresh;
        queryOrder(REFESH_TYPE);


        adapter.setOnItemClickListener(new ToBeDeliveredAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(cnt, GroupOrderDetailActivity.class);
                intent.putExtra("source", source);
                intent.putExtra("groupId", groupId);
                intent.putExtra("serialNumber", pendingRegimentList.get(position).getOrderId());
                startActivity(intent);
            }

            @Override
            public void contactSellerOClick(int position) {
                Intent intent = new Intent(cnt, SingleChatActivity.class);
                intent.putExtra("receiverMemberId", pendingRegimentList.get(position).getShopMemberId());
                intent.putExtra("receiverName", pendingRegimentList.get(position).getShopMemberName());
                intent.putExtra("receiverHeadUrl", "");
                startActivity(intent);
            }

            @Override
            public void CallSellerOClick(int position) {
                Intent intent = new Intent(cnt, SingleChatActivity.class);
                intent.putExtra("receiverMemberId", pendingRegimentList.get(position).getMemberId());
                intent.putExtra("receiverName", pendingRegimentList.get(position).getMemberName());
                intent.putExtra("receiverHeadUrl", "");
                startActivity(intent);
            }

            @Override
            public void CheckTransMapOClick(int position) {
                checkTransMapPosition = position;
                SellOrderModel.DataBean dataBean = pendingRegimentList.get(position);
                SellOrderModel.DataBean.OrderItemListBean orderItemListBean = dataBean.getOrderItemList().get(0);
                Intent intent1 = new Intent(cnt, ConfirmArbitrationActivity.class);
                intent1.putExtra("groupId", groupId);
                intent1.putExtra("productName", orderItemListBean.getProductName());
                intent1.putExtra("productPropertySpecValue", orderItemListBean.getProductPropertySpecValue());
                intent1.putExtra("deliveryFee", dataBean.getDeliveryFee() + "");
                intent1.putExtra("productNum", orderItemListBean.getProductNum() + "");
                intent1.putExtra("totalProductAmount", dataBean.getTotalAmount() + "");
                intent1.putExtra("img", orderItemListBean.getImg());
                intent1.putExtra("orderId", dataBean.getOrderId());
                startActivity(intent1);
            }
        });
    }

    @OnClick({R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty:
                //查询群成员
                REFESH_TYPE = PullDownRefresh;
                queryOrder(PullDownRefresh);
                break;
        }
    }

    /**
     * 查询销售订单
     */
    private void queryOrder(final int REFESH_TYPE) {
        QueryOrderJsonModel queryOrderJsonModel = new QueryOrderJsonModel();
        queryOrderJsonModel.setOrderStatus(orderStatus);
        queryOrderJsonModel.setKey(key);
        queryOrderJsonModel.setPageSize(pageSize);
        queryOrderJsonModel.setCurrentPage(currentPage);
        queryOrderJsonModel.setGroupId(groupId);
        queryOrderJsonModel.setMemberId(UserModel.getUserModel().getMemberId());
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
                                    if (REFESH_TYPE == PullDownRefresh) {
                                        pendingRegimentList.clear();
                                        swipeRefreshLayout.finishRefresh();
                                    } else if (REFESH_TYPE == PullupLoading) {
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
                                    if (pendingRegimentList.size() > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                    tvEmpty.setVisibility(View.VISIBLE);
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
                            if (REFESH_TYPE == PullDownRefresh) {
                                pendingRegimentList.clear();
                                swipeRefreshLayout.finishRefresh();
                            } else if (REFESH_TYPE == PullupLoading) {
                                swipeRefreshLayout.finishLoadmore();
                            }
                        }
                    }
                });

    }

    /**
     * 下拉刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        currentPage = 1;
        REFESH_TYPE = PullDownRefresh;
        queryOrder(REFESH_TYPE);

    }

    /**
     * 上拉加载更多
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        currentPage++;
        REFESH_TYPE = PullupLoading;
        queryOrder(REFESH_TYPE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}

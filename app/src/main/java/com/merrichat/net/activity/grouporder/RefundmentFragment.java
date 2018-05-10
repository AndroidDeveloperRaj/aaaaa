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
import com.merrichat.net.adapter.RefundmentAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.PendingDeliveryModel;
import com.merrichat.net.model.QueryOrderJsonModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.Logger;
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
 * 销售订单--退款
 */

public class RefundmentFragment extends BaseFragment implements OnRefreshListener, OnLoadmoreListener, BaseQuickAdapter.OnItemChildClickListener {
    private final int orderStatus = 7;//订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
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
    private ArrayList<PendingDeliveryModel.DataBean> list = new ArrayList<>();
    private RefundmentAdapter refundmentAdapter;
    private int currentPage = 1;//当前页数
    private int pageSize = 10;//每页的条数
    private int refreshOrLoadMore = -1;//5 刷新，6 加载更多
    private CommomDialog dialog;
    private int position = 0;
    private String orderId = "";

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

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_REFUND) {
            //查询销售订单
            list.clear();
            currentPage = 1;
            queryOrder();
        }

    }

    private void initView() {
        EventBus.getDefault().register(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        refundmentAdapter = new RefundmentAdapter(R.layout.item_pending_delivery, list);

        rlRecyclerview.setAdapter(refundmentAdapter);
        refundmentAdapter.setOnItemChildClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
        if (list.size() > 0) {
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
            list.clear();
            currentPage = 1;
            queryOrder();
        }
    }

    @OnClick({R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty://空白页
                //查询销售订单
                list.clear();
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
                                        list.clear();
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
                                        list.addAll(dataBeans);
                                    }
                                    if (tvEmpty != null) {
                                        if (list.size() > 0) {
                                            tvEmpty.setVisibility(View.GONE);
                                        } else {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    refundmentAdapter.notifyDataSetChanged();
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
                                list.clear();
                                swipeRefreshLayout.finishRefresh();
                            } else if (refreshOrLoadMore == 6) {
                                swipeRefreshLayout.finishLoadmore();
                            }
                        }
                    }
                });

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        this.position = position;
        PendingDeliveryModel.DataBean dataBean = list.get(position);
        Logger.e("onItemChildClick……1", position + "");
        orderId = dataBean.getOrderId();
        switch (view.getId()) {
            case R.id.ll_product_info://
                Bundle bundle = new Bundle();
                bundle.putString("orderId", orderId);
                RxActivityTool.skipActivity(getActivity(), RefundMentOrderDetailAty.class, bundle);

                break;
            case R.id.tv_contact_cancle://拒绝退款
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("orderBeanInfo", dataBean);
                RxActivityTool.skipActivity(getActivity(), RefusingRefundmentAty.class, bundle1);
                break;
            case R.id.tv_contact_seller://确认退款
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要退款吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                confirmProduct(true);
                            }
                        }
                    }).setTitle("确认退款");
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
            default:
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

    /**
     * 确认/拒绝退款
     *
     * @param flag true:确认退款,false:取消退款
     */
    private void confirmProduct(boolean flag) {
        Logger.e("onItemChildClick……2", position + "");
        OkGo.<String>post(Urls.confirmProduct)//
                .tag(this)//
                .params("flag", flag)
                .params("orderId", orderId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        list.get(position).setReStatus("3");//0:还没申请退款,1:申请中, 2:申请拒绝, 3:申请通过 4:仲裁中 5:仲裁失败,6:仲裁成功
                                        refundmentAdapter.notifyDataSetChanged();
                                        RxToast.showToast("已退款成功！");
                                    } else {
                                        RxToast.showToast("退款失败，请重试！");
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
}

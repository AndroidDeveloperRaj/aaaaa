package com.merrichat.net.activity.groupmanage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.adapter.BuyOrderAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.model.QueryOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WaiteGroupBuyModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.CheckHttpUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("ValidFragment")
public class BuyOrderFragment extends BaseFragment implements GroupItemClickListener, OnRefreshListener, OnLoadmoreListener {
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
    private List<WaiteGroupBuyModel.Data> pendingDeliveryList = new ArrayList<>();
    private BuyOrderAdapter buyOrderAdapter;
    private int currentPage = 1;//当前页数
    private int refreshOrLoadMore = -1;//5 刷新，6 加载更多

    private int currentPosition;  //当前订单的状态

    /**
     * 当前订单的状态
     *
     * @param position 页面state
     */
    @SuppressLint("ValidFragment")
    public BuyOrderFragment(int position) {
        this.currentPosition = position;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rlRecyclerview.setLayoutManager(layoutManager);
        buyOrderAdapter = new BuyOrderAdapter(R.layout.item_waite_group_buy, pendingDeliveryList, this, currentPosition, this);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
        rlRecyclerview.setAdapter(buyOrderAdapter);
        getOrderStateList(currentPosition);

        buyOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("orderId", buyOrderAdapter.getData().get(position).orderId + "");
                intent.putExtra("current_position", currentPosition);  //当前界面是什么状态
                intent.putExtra("serialNumber", buyOrderAdapter.getData().get(position).serialNumber);
                startActivityForResult(intent, currentPosition);  //以当前界面的mode 作为request
            }
        });
    }


    /**
     * 获取购买订单的状态页面详情
     *
     * @param currentPosition 购买订单页面状态
     */
    public void getOrderStateList(final int currentPosition) {  //当前页面
        QueryOrderModel.QueryOrderRequestParams requestParams = new QueryOrderModel.QueryOrderRequestParams();
        requestParams.key = "buy";
        requestParams.memberId = UserModel.getUserModel().getMemberId();
        requestParams.pageSize = 10 + "";
        requestParams.currentPage = currentPage + "";
        switch (currentPosition) {   //订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
            case 0:
                requestParams.orderStatus = "" + 0;  //待成团
                break;
            case 1:
                requestParams.orderStatus = "" + 2;
                break;
            case 2:
                requestParams.orderStatus = "" + 3;
                break;
            case 3:
                requestParams.orderStatus = "" + 4;
                break;
            case 4:
                requestParams.orderStatus = "" + 7;
                break;
        }

        final Gson gson = new Gson();
        ApiManager.getApiManager().getService(WebApiService.class).queryOrder(gson.toJson(requestParams))
                .compose(this.<WaiteGroupBuyModel>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<WaiteGroupBuyModel>() {
                    @Override
                    public void onNext(WaiteGroupBuyModel waiteGroupBuyModel) {
                        if (waiteGroupBuyModel.success) {
                            if (swipeRefreshLayout != null) {
                                if (refreshOrLoadMore == 5) {
                                    pendingDeliveryList.clear();
                                    swipeRefreshLayout.finishRefresh();
                                } else if (refreshOrLoadMore == 6) {
                                    swipeRefreshLayout.finishLoadmore();
                                }
                            }

                            if (waiteGroupBuyModel.data.size() != 0) {
                                pendingDeliveryList.addAll(waiteGroupBuyModel.data);
                                buyOrderAdapter.notifyDataSetChanged();
                                tvEmpty.setVisibility(View.GONE);
                            } else if (currentPage != 1) {
                                tvEmpty.setVisibility(View.GONE);
                                GetToast.useString(cnt, "没有数据了");
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            if (waiteGroupBuyModel.message != null && waiteGroupBuyModel.message.length() > 0) {
                                Toast.makeText(getContext(), waiteGroupBuyModel.message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (pendingDeliveryList.size() > 0) {
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getContext(), "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });

    }


    @Override
    public void clickListener(int state) {
        notifyDataSetChanged();
    }

    /**
     * 刷新  上拉
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!CheckHttpUtil.isNetworkConnected(getActivity())) {
            if (pendingDeliveryList.size() > 0) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            return;
        }
        currentPage = 1;
        refreshOrLoadMore = 5;
        pendingDeliveryList.clear();
        getOrderStateList(currentPosition);
    }

    /**
     * 加载更多
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        currentPage++;
        refreshOrLoadMore = 6;
        getOrderStateList(currentPosition);
    }

    public void notifyDataSetChanged() {
        if (!CheckHttpUtil.isNetworkConnected(getActivity())) {
            if (pendingDeliveryList.size() > 0) {
                tvEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            return;
        }
        currentPage = 1;
        refreshOrLoadMore = 5;
        if (null != buyOrderAdapter) {
            pendingDeliveryList.clear();
            getOrderStateList(currentPosition);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == currentPosition) {
            notifyDataSetChanged();
        }
    }
}

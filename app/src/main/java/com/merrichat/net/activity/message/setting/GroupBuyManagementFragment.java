package com.merrichat.net.activity.message.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.merrichat.net.R;
import com.merrichat.net.adapter.GroupBuyManagementAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.model.QueryOrderModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WaiteGroupBuyModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.GetToast;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amssy on 18/1/23.
 * 群订单管理fragment
 */

@SuppressLint("ValidFragment")
public class GroupBuyManagementFragment extends BaseFragment {
    private PullToRefreshListView mPullToRefreshListView;//下拉刷新和上拉加载
    private GroupBuyManagementAdapter groupBuyManagementAdapter;
    private int currentPosition;  //当前订单的状态
    private String mGroupId;  //群id

    private int currentPage ;  //每页的条数


    /**
     * 当前订单的状态
     *
     * @param position 页面state
     */
    @SuppressLint("ValidFragment")
    public GroupBuyManagementFragment(int position, String mGroupId) {
        this.mGroupId = mGroupId;
        this.currentPosition = position;
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waite_group, container, false);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.waiting_for_list);
        currentPage = 1;
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        getOrderStateList(currentPosition);
    }

    private void initViews() {
        groupBuyManagementAdapter = new GroupBuyManagementAdapter(getActivity(), this, currentPosition);
        mPullToRefreshListView.setAdapter(groupBuyManagementAdapter);
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), GroupOrderDetailActivity.class);
                intent.putExtra("current_position", currentPosition);  //当前界面是什么状态
                intent.putExtra("orderId", groupBuyManagementAdapter.getData().get(position - 1).orderId + "");
                startActivityForResult(intent, currentPosition);  //以当前界面的mode 作为request

            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                groupBuyManagementAdapter.clearItems();
                getOrderStateList(currentPosition);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getOrderStateList(currentPosition);

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 获取购买订单的状态页面详情
     *
     * @param currentPosition 购买订单页面状态
     */
    public void getOrderStateList(final int currentPosition) {  //当前页面
        QueryOrderModel.QueryOrderRequestParams requestParams = new QueryOrderModel.QueryOrderRequestParams();
        requestParams.key = "sale";
        requestParams.memberId = UserModel.getUserModel().getMemberId();
        requestParams.groupId = mGroupId;
        requestParams.currentPage = currentPage + "";
        requestParams.pageSize = 10 + "";
        switch (currentPosition) {   //订单状态,0待成团,1未付款,2待发货,3送货中/（待收货）,4已收货 ,5已取消,6拒收,7退款 8仲裁
            case 0:
                requestParams.orderStatus = ""+0;  //待成团
                break;
            case 1:
                requestParams.orderStatus = ""+2;
                break;
            case 2:
                requestParams.orderStatus =""+ 3;
                break;
            case 3:
                requestParams.orderStatus =""+ 4;
                break;
            case 4:
                requestParams.orderStatus =""+ 8;
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
                            if (waiteGroupBuyModel.data.size() == 0) {

                            } else {
                                currentPage++;
                                groupBuyManagementAdapter.addItemList(waiteGroupBuyModel.data);
                                groupBuyManagementAdapter.notifyDataSetChanged();
                            }

                        } else {
                            if (waiteGroupBuyModel.message != null && waiteGroupBuyModel.message.length() > 0) {
                                Toast.makeText(getContext(), waiteGroupBuyModel.message, Toast.LENGTH_LONG).show();
                            }
                        }
                        mPullToRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mPullToRefreshListView.onRefreshComplete();
                        GetToast.useString(cnt, "没有网络了，检查一下吧～！");
                    }
                });

    }


}

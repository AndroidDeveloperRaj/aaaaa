package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.AddGroupAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AddGroupModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.LocationService;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 添加群
 * Created by amssy on 18/1/22.
 */
public class AddGroupFragment extends BaseFragment implements AddGroupAdapter.onAddGroupItemClickLinster, OnLoadmoreListener, OnRefreshListener {
    /**
     * 定位
     */
    public LocationService locationService;
    @BindView(R.id.lay_search)
    LinearLayout tvSearch;
    @BindView(R.id.recycler_view_add_group)
    RecyclerView recyclerViewAddGroup;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    private View view;
    private Unbinder unbinder;
    private AddGroupAdapter adapter;
    private int pageSize = 10;
    private int pageNum = 1;
    private int REFRESHORLOADMORE = 1;//0表示加载数据  1表示刷新数据
    private List<AddGroupModel.DataBean.ListBean> listBeans;
    private AddGroupModel addGroupModel;
    private boolean isSearch = true;//为true时数据清空，为false时继续加载数据
    private String mLongitude;
    private String mLatitude;
    private String type;
    //定位回调，根据需求获取所需要的参数，下面是所有参数
    private BDLocationListener merriLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                mLatitude = String.valueOf(location.getLatitude());
                mLongitude = String.valueOf(location.getLongitude());
                if (locationService != null) {
                    locationService.stop();
                }
                //第一次加载附近群组  搜索条件传空
                searchGroup("");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        locationService = ((MerriApp) getActivity().getApplication()).locationService;
        locationService.registerListener(merriLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    /***
     * Stop location service
     */
    @Override
    public void onStop() {
        locationService.unregisterListener(merriLocationListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        listBeans = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewAddGroup.setLayoutManager(linearLayoutManager);
        adapter = new AddGroupAdapter(getActivity(), listBeans);
        recyclerViewAddGroup.setAdapter(adapter);
        adapter.setAddGroupItemClickLinster(this);

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchGroupActivity.class);
                intent.putExtra("mLongitude", mLongitude);
                intent.putExtra("mLatitude", mLatitude);
                startActivity(intent);
            }
        });

        //搜索按钮监听
//        tvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    //点击搜索执行操作
//                    //关闭软键盘
//                    InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//                    mInputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//
//                    isSearch = true;
//                    searchGroup(tvSearch.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });
//        /**
//         * 输入框输入改变监听
//         */
//        tvSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(tvSearch.getText().toString())) {
//                    searchGroup(tvSearch.getText().toString());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.FILTER_NEAR_GROUP) {
            type = myEventBusModel.FILTER_NEAR_GROUP_TYPE;
            listBeans.clear();
            searchGroup("");
        }

    }

    ;

    /**
     * 加入群的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {

        //是否加入过0:没有，1：申请过该群，还没通过，2：已经是该群成员
        AddGroupModel.DataBean.ListBean listBean = listBeans.get(position);
        switch (listBean.getIsJoin()) {
            case 0:
                //申请加入群
                joinCommunity(listBean);
                break;
            case 1:
                //申请加入群
                RxToast.showToast("已申请,等待管理员同意");
                break;
            case 2:
                RxToast.showToast("您已经是该群成员");
                break;
        }
    }

    /**
     * 搜索群
     */
    private void searchGroup(String groupName) {
        OkGo.<String>get(Urls.nearCommunity)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityName", groupName)//群名称 为空则为附近群组 有值模糊搜索
                .params("longitude", mLongitude)
                .params("type", type)
                .params("latitude", mLatitude)
                .params("pageSize", pageSize)
                .params("pageNum", pageNum)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                if (refreshLayout != null) {
                                    if (REFRESHORLOADMORE == 1) {
                                        refreshLayout.finishRefresh();
                                    } else {
                                        refreshLayout.finishLoadmore();
                                    }
                                }
                                if (isSearch) {
                                    listBeans.clear();
                                }
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    addGroupModel = JSON.parseObject(response.body(), AddGroupModel.class);
                                    if (addGroupModel.getData().isSuccess()) {
                                        if (addGroupModel.getData().getList() == null || addGroupModel.getData().getList().size() == 0) {
                                            refreshLayout.setLoadmoreFinished(true);
                                        } else {
                                            listBeans.addAll(addGroupModel.getData().getList());
                                        }

                                        if (listBeans.size() == 0) {
                                            if (tvEmpty != null) {
                                                tvEmpty.setVisibility(View.VISIBLE);
                                                tvEmpty.setText("暂无数据哦～");
                                            }
                                        } else {
                                            if (tvEmpty != null) {
                                                tvEmpty.setVisibility(View.GONE);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        RxToast.showToast(data.optJSONObject("data").optString("message"));
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tvEmpty.setText("暂无数据哦～");
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

    /**
     * 加入群
     *
     * @param listBean
     */
    private void joinCommunity(final AddGroupModel.DataBean.ListBean listBean) {
        OkGo.<String>post(Urls.joinCommunity)
                .tag(this)
                .params("cid", listBean.getCommunityId())
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //是否加入0:加入不成功，1：加入成功，2：申请成功，等待审核
                                    int isJoin = data.optJSONObject("data").optInt("isJoin");
                                    switch (isJoin) {
                                        case 0:
                                            RxToast.showToast(data.optJSONObject("data").optString("message"));
                                            break;
                                        case 1:
                                            RxToast.showToast(data.optJSONObject("data").optString("message"));
                                            listBean.setIsJoin(2);
                                            adapter.notifyDataSetChanged();
//                                            searchGroup(tvSearch.getText().toString());
                                            break;
                                        case 2:
                                            RxToast.showToast(data.optJSONObject("data").optString("message"));
                                            listBean.setIsJoin(1);
                                            adapter.notifyDataSetChanged();
//                                            searchGroup(tvSearch.getText().toString());
                                            break;
                                    }
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
                    }
                });
    }

    /**
     * 上拉加载
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        pageNum++;
        isSearch = false;
        searchGroup("");
    }

    /**
     * 下拉刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 1;
        pageNum = 1;
        isSearch = true;
        searchGroup("");
    }

    @OnClick({R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_empty://空白页
                locationService.start();
                break;
        }
    }
}

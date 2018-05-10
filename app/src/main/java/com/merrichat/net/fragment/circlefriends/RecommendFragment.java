package com.merrichat.net.fragment.circlefriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.activity.circlefriend.TuWenAlbumAty;
import com.merrichat.net.adapter.RecommendAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CircleModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.networklistening.ConnectivityStatus;
import com.merrichat.net.networklistening.ReactiveNetwork;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.DrawableCenterTextView;
import com.merrichat.net.view.MyStaggeredGridLayoutManager;
import com.merrichat.net.view.SpaceItemDecorations;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
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
import butterknife.Unbinder;

/**
 * Created by amssy on 17/11/16.
 * 朋友圈---推荐
 */

public class RecommendFragment extends BaseFragment implements RecommendAdapter.OnItemClickListener, OnLoadmoreListener, OnRefreshListener, RecommendAdapter.DianZanOnCheckListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder1;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private View view;

    private RecommendAdapter recommendAdapter;
    private SpaceItemDecorations spaceItemDecoration;
    private int pageNum = 1;//当前页
    private int pageSize = 10;//每页数量
    private double longitude = 0.0;//经度
    private double latitude = 0.0;//纬度
    private String memberId = "0";

    private List<CircleModel.ListBean> listBeans;
    private int REFRESHORLOADMORE = 1;
    private int clickPosition = -1;
    private boolean isFirstStart = true;
    private CircleModel nearCircleModel;
    private boolean isFinish = false;
    private ReactiveNetwork reactiveNetwork;
    private boolean isNetwork = true;
    private int mPosition = 0;


    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    private void initView() {
        //网络连接状态的监听
        reactiveNetwork = new ReactiveNetwork();
        reactiveNetwork.setNetworkEvent(new ReactiveNetwork.NetworkEvent() {
            @Override
            public void enent(ConnectivityStatus status) {
                //Logger.e("获取网络状态：status.description＝＝＝" + status.description);
                if (status.description.equals("offline") || status.description.equals("unknown")) {
                    RxToast.showToast("请检查网络连接");
                    isNetwork = false;
                } else if (status.description.equals("connected to WiFi network")) {
                    //RxToast.showToast("connected to WiFi");
                    isNetwork = true;
                } else if (status.description.equals("connected to mobile network")) {
                    //RxToast.showToast("connected to mobile");
                    isNetwork = true;
                }
            }
        });
        reactiveNetwork.observeNetworkConnectivity(getActivity());

        EventBus.getDefault().register(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);
        //设置 Header 为 BezierRadar 样式
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()).setPrimaryColorId(R.color.white));
        //设置 Footer 为 球脉冲
        refreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));
//        if (isFirstStart) {
//            refreshLayout.autoRefresh();
//            isFirstStart = false;
//        }

        listBeans = new ArrayList<>();
        final MyStaggeredGridLayoutManager manager = new MyStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//        //防止item 交换位置
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                manager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
//            }
//        });
        recyclerView.setLayoutManager(manager);
        recommendAdapter = new RecommendAdapter(cnt, listBeans);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());// 初始化动画效果
        recyclerView.setAdapter(recommendAdapter);
        spaceItemDecoration = new SpaceItemDecorations(2, 2);
        recyclerView.addItemDecoration(spaceItemDecoration);
        //设置添加,移除item的动画,DefaultItemAnimator为默认的
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recommendAdapter.setOnItemClickListener(this);
        recommendAdapter.setDianZanOnCheckListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstStart) {
                if (UserModel.getUserModel().getIsLogin()) {
                    findCircleNear(UserModel.getUserModel().getMemberId());
                } else {
                    findCircleNear("0");
                }
                isFirstStart = false;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        reactiveNetwork.relese(getActivity());
    }

    /**
     * 查询推荐
     *
     * @param memberId
     */
    private void findCircleNear(String memberId) {
        OkGo.<String>get(Urls.FRIENDS_RECOMEND)
                .tag(this)
                .params("memberId", memberId)
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
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
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    if (REFRESHORLOADMORE == 1) {
                                        listBeans.clear();
                                    }
                                    mPosition = listBeans.size();
                                    nearCircleModel = JSON.parseObject(response.body(), CircleModel.class);
                                    listBeans.addAll(nearCircleModel.data.list);
                                    if (nearCircleModel.data.list == null || nearCircleModel.data.list.size() == 0) {
                                        //RxToast.showToast("暂无更多数据...");
                                        if (refreshLayout != null) {
                                            refreshLayout.setLoadmoreFinished(true);
                                        }
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
                                        if (REFRESHORLOADMORE == 1){
                                            recommendAdapter.clearHeight();
                                            recommendAdapter.notifyDataSetChanged();
                                        }else {
                                            recommendAdapter.notifyItemInserted(mPosition);
                                        }
                                    }
                                } else {
                                    //RxToast.showToast(data.optString("message"));
                                    RxToast.showToast(R.string.connect_to_server_fail2);
                                    if (tvEmpty != null && listBeans.size() == 0) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvEmpty.setText(R.string.connect_to_server_fail2);
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
                        if (tvEmpty != null && listBeans.size() == 0) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText(R.string.connect_to_server_fail);
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

    @Override
    public void onItemClick(final int position) {
        clickPosition = position;
        if (isNetwork) {
            //日志标识 1图文专辑 2视频 3照片 4录像
            int flag = listBeans.get(position).log.flag;
            switch (flag) {
                case 2:
                    Intent intent = new Intent(getActivity(), CircleVideoActivity.class);
                    intent.putExtra("contentId", "" + listBeans.get(position).log.id);
                    intent.putExtra("toMemberId", "" + listBeans.get(position).log.memberId);
                    intent.putExtra("tab_item", 2);
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent1 = new Intent(getActivity(), TuWenAlbumAty.class);
                    intent1.putExtra("toMemberId", listBeans.get(position).log.memberId + "");
                    intent1.putExtra("contentId", listBeans.get(position).log.id + "");
                    intent1.putExtra("tab_item", 2);
                    startActivity(intent1);
                    break;
            }
        } else {
            RxToast.showToast("请检查网络连接");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        pageNum++;
        if (UserModel.getUserModel().getIsLogin()) {
            findCircleNear(UserModel.getUserModel().getMemberId());
        } else {
            findCircleNear("0");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 1;
        pageNum = 1;
        if (refreshLayout != null) {
            refreshLayout.setLoadmoreFinished(false);
        }
        if (UserModel.getUserModel().getIsLogin()) {
            findCircleNear(UserModel.getUserModel().getMemberId());
        } else {
            findCircleNear("0");
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.DELEATE_LOG2) {
            listBeans.remove(clickPosition);
            recommendAdapter.notifyDataSetChanged();
        }
        if (myEventBusModel.FRIEND_RELEASE_SUCCESS) {//发布视频成功广播

        }
        //发布视频和图文成功的时候刷新数据
        if (myEventBusModel.REFRESH_RECOMMENT_FRAGMENT) {
            findCircleNear(UserModel.getUserModel().getMemberId());
        }
        if (myEventBusModel.FRESH_LIST_DATA) {
            if (clickPosition != -1) {
                CircleModel.ListBean listBean = listBeans.get(clickPosition);
                listBean.income = myEventBusModel.income;
                listBean.likes = myEventBusModel.isLike;
                listBean.log.likeCounts = myEventBusModel.likeCounts;
                listBeans.remove(clickPosition);
                listBeans.add(clickPosition, listBean);
                recommendAdapter.notifyItemChanged(clickPosition);
                clickPosition = -1;
            }
        }
    }

    @Override
    public void dianZanOnCheckListener(boolean isChecked, int position) {
        if (StringUtil.isLogin(getActivity())) {
            if (!isChecked) {//是否点赞 0点赞，1为取消点赞
                updateLikes(isChecked, 0, position);
            } else {
                updateLikes(isChecked, 1, position);
            }
        } else {
            RxToast.showToast("请先登录哦");
        }
    }

    /**
     * 点赞、取消点赞
     *
     * @param isChecked
     * @param flag
     * @param position
     */
    private void updateLikes(final boolean isChecked, final int flag, final int position) {
        long id = 0;
        long memberId = 0;
        id = listBeans.get(position).log.id;
        memberId = listBeans.get(position).log.memberId;
//        if (REC_TPYE == YINGJI_FLAG) {
//            id = movieListBeans.get(position).getId();
//            memberId = movieListBeans.get(position).getMemberId();
//
//        } else if (REC_TPYE == COLLECTION_FLAG) {
//            id = collectListBeans.get(position).getId();
//            memberId = collectListBeans.get(position).getMemberId();
//        }
        OkGo.<String>get(Urls.UPDATE_LIKES)//
                .tag(this)//
                .params("logId", id)
                .params("personUrl", UserModel.getUserModel().getImgUrl())
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", memberId)
                .params("flag", flag)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data.optBoolean("result")) {
                                    int counts = data.optInt("count");
                                    if (flag == 0) {
                                        RxToast.showToast("已点赞");
                                        listBeans.get(position).likes = true;

                                    } else {
                                        RxToast.showToast("已取消点赞");
                                        listBeans.get(position).likes = false;
                                    }
                                    listBeans.get(position).log.likeCounts = counts;
                                    //刷新公分
                                    beautyLogIncome(String.valueOf(listBeans.get(position).log.id), position);
                                } else {
                                    RxToast.showToast(data.optString("msg"));
                                }

                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                            }
                            recommendAdapter.notifyItemChanged(position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 查询帖子的预计收入
     */
    private void beautyLogIncome(String id, final int position) {
        OkGo.<String>post(Urls.beautyLogIncome)
                .tag(this)
                .params("id", id)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                String income = jsonObjectEx.optString("data");
                                listBeans.get(position).setIncome(income);
                                recommendAdapter.notifyItemChanged(position);
                            } else {
                                //RxToast.showToast(R.string.connect_to_server_fail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });

    }
}

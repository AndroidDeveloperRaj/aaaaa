package com.merrichat.net.fragment.circlefriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.circlefriend.CircleDetailsVideoAty;
import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.activity.circlefriend.TuWenAlbumAty;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.adapter.AttentionAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AttentionModel;
import com.merrichat.net.model.CircleModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.networklistening.ConnectivityStatus;
import com.merrichat.net.networklistening.ReactiveNetwork;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxToast;
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
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.VISIBLE;

/**
 * Created by amssy on 17/11/16.
 * 朋友圈---关注
 */

public class AttentionFragment extends BaseFragment implements AttentionAdapter.OnItemClickListener, OnLoadmoreListener, OnRefreshListener, AttentionAdapter.DianZanOnCheckListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder1;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.btn_login_friend)
    Button btnLogin;
    @BindView(R.id.lin_toast)
    LinearLayout linToast;
    @BindView(R.id.iv_empty)
    ImageView ivEmpty;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private View view;
    private int pageNum = 1;//当前页
    private int pageSize = 10;//每页数量
    private List<AttentionModel.DataBean.ListBean> listBeans;
    private AttentionAdapter goodFriendsAdapter;
    private SpaceItemDecorations spaceItemDecoration;
    private int REFRESHORLOADMORE = 1;
    private int clickPosition;
    private boolean isFirstStart = true;
    private boolean isNetwork = true;
    private ReactiveNetwork reactiveNetwork;
    private int mPosition = 0;

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attention, container, false);
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
        EventBus.getDefault().register(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableAutoLoadmore(true);

        //设置 Header 为 BezierRadar 样式
        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()).setPrimaryColorId(R.color.white));
        //设置 Footer 为 球脉冲
        refreshLayout.setRefreshFooter(new BallPulseFooter(getActivity()).setSpinnerStyle(SpinnerStyle.Scale));

        listBeans = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
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
        goodFriendsAdapter = new AttentionAdapter(cnt, listBeans);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());// 初始化动画效果
        recyclerView.setAdapter(goodFriendsAdapter);
        spaceItemDecoration = new SpaceItemDecorations(2, 2);
        recyclerView.addItemDecoration(spaceItemDecoration);
        goodFriendsAdapter.setOnItemClickListener(this);
        goodFriendsAdapter.setDianZanOnCheckListener(this);
        recyclerView.setHasFixedSize(true);

        if (!UserModel.getUserModel().getIsLogin()) {
            linToast.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.GONE);
            tvNone.setText("您还未登录请前去登录~");
        }
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
    }

    @OnClick(R.id.btn_login_friend)
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_friend:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstStart && UserModel.getUserModel().getIsLogin()) {
                queryBeautyLogAttentionRelation();
                isFirstStart = false;
            }
        }
    }

    /**
     * 查询关注人的帖子
     */
    private void queryBeautyLogAttentionRelation() {
        OkGo.<String>get(Urls.QUERY_BEAUTY_LOG_ATTENTION)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("pageNum", pageNum)
                .params("pageSize", pageSize)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                if (refreshLayout != null) {
                                    if (REFRESHORLOADMORE == 1) {
                                        listBeans.clear();
                                        refreshLayout.finishRefresh();
                                    } else {
                                        refreshLayout.finishLoadmore();
                                    }
                                }
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    if (REFRESHORLOADMORE == 1){
                                        listBeans.clear();
                                    }
                                    mPosition = listBeans.size();
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.GONE);
                                    }
                                    AttentionModel attentionModel = JSON.parseObject(response.body(), AttentionModel.class);
                                    if (attentionModel.getData().getList() == null || attentionModel.getData().getList().size() == 0) {
                                        if (refreshLayout != null) {
                                            refreshLayout.setLoadmoreFinished(true);
                                        }
                                    } else {
                                        listBeans.addAll(attentionModel.getData().getList());
                                        if (REFRESHORLOADMORE == 1){
                                            goodFriendsAdapter.notifyDataSetChanged();
                                        }else {
                                            goodFriendsAdapter.notifyItemInserted(mPosition);
                                        }
                                    }
                                    if (listBeans.size() > 0) {
                                        if (null != linToast)
                                            linToast.setVisibility(View.GONE);
                                    } else {
                                        if (linToast != null && btnLogin != null && tvNone != null) {
                                            linToast.setVisibility(View.VISIBLE);
                                            btnLogin.setVisibility(View.GONE);
                                            ivEmpty.setVisibility(VISIBLE);
                                            tvNone.setText("您没有关注,去逛逛附近或推荐吧~");
                                        }
                                    }
                                } else {
                                    //RxToast.showToast(data.optString("message"));
                                    RxToast.showToast(R.string.connect_to_server_fail2);
                                    if (tvEmpty != null && listBeans.size() == 0) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvEmpty.setText("服务器已断开链接，请重试");
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
     * ITEM的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        clickPosition = position;
        if (isNetwork) {
            //日志标识 1图文专辑 2视频 3照片 4录像
            int flag = listBeans.get(position).getBeautyLog().getFlag();
            switch (flag) {
                case 2:
                    Intent intent = new Intent(getActivity(), CircleVideoActivity.class);
                    intent.putExtra("contentId", "" + listBeans.get(position).getBeautyLog().getId());
                    intent.putExtra("toMemberId", "" + listBeans.get(position).getBeautyLog().getMemberId());
                    intent.putExtra("tab_item", "3");
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent1 = new Intent(getActivity(), TuWenAlbumAty.class);
                    intent1.putExtra("toMemberId", listBeans.get(position).getBeautyLog().getMemberId() + "");
                    intent1.putExtra("contentId", listBeans.get(position).getBeautyLog().getId() + "");
                    intent1.putExtra("tab_item", 3);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
        EventBus.getDefault().unregister(this);
        reactiveNetwork.relese(getActivity());
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        pageNum++;
        queryBeautyLogAttentionRelation();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (UserModel.getUserModel().getIsLogin()) {
            REFRESHORLOADMORE = 1;
            pageNum = 1;
            if (refreshLayout != null) {
                refreshLayout.setLoadmoreFinished(false);
            }
            queryBeautyLogAttentionRelation();
        } else {
            refreshlayout.finishRefresh();
            RxToast.showToast("您还未登录，请先登录哦~");
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.DELEATE_LOG3) {
            listBeans.remove(clickPosition);
            goodFriendsAdapter.notifyDataSetChanged();
        }
        if (myEventBusModel.FRESH_LIST_DATA){
            if (clickPosition != -1) {
                if (listBeans.size() > 0) {
                    AttentionModel.DataBean.ListBean listBean = listBeans.get(clickPosition);
                    listBean.setIncome(myEventBusModel.income);
                    listBean.setLikes(myEventBusModel.isLike);
                    listBean.getBeautyLog().setLikeCounts(myEventBusModel.likeCounts);
                    listBeans.remove(clickPosition);
                    listBeans.add(clickPosition, listBean);
                    goodFriendsAdapter.notifyItemChanged(clickPosition);
                }
                clickPosition = -1;
            }
        }
    }

    @Override
    public void dianZanOnCheckListener(boolean isChecked, int position) {
        if (!isChecked) {//是否点赞 0点赞，1为取消点赞
            updateLikes(isChecked, 0, position);
        } else {
            updateLikes(isChecked, 1, position);
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
        long id ;
        long memberId;
        id = listBeans.get(position).getBeautyLog().getId();
        memberId = listBeans.get(position).getBeautyLog().getMemberId();
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
                                        listBeans.get(position).setLikes(true);

                                    } else {
                                        RxToast.showToast("已取消点赞");
                                        listBeans.get(position).setLikes(false);
                                    }
                                    listBeans.get(position).getBeautyLog().setLikeCounts(counts);
                                    //刷新公分
                                    beautyLogIncome(String.valueOf(listBeans.get(position).getBeautyLog().getId()), position);
                                } else {
                                    RxToast.showToast(data.optString("msg"));
                                }
                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                            }
                            goodFriendsAdapter.notifyItemChanged(position);

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
        if (!UserModel.getUserModel().getIsLogin()) {
            linToast.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            tvNone.setText("您还未登录请前去登录~");
        } else if (isFirstStart) {
            queryBeautyLogAttentionRelation();
            isFirstStart = false;
        }
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
                                goodFriendsAdapter.notifyItemChanged(position);
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

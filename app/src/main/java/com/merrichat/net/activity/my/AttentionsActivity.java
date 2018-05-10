package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.adapter.FansAndAttentionAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MyFansModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/12/15.
 */

public class AttentionsActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, OnRefreshListener, OnLoadmoreListener, BaseQuickAdapter.OnItemClickListener {
    public final static int activityId = MiscUtil.getActivityId();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.fans_recyclerview)
    RecyclerView attentionRecyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    private FansAndAttentionAdapter attentionAdapter;
    private ArrayList<MyFansModel.DataBean> attentionsList = new ArrayList();
    private int status = 0;//0:关注,1:粉丝 (必填)
    private int currentPage = 1;
    private int pageSize = 20;
    private int REFRESHORLOADMORE = -1;// 5 刷新 6 加载更多
    private int atteStatus = 1;//关注 0 /取消关注 1 (必填)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans_attention);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("我的关注");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        attentionRecyclerview.setLayoutManager(layoutManager);
        attentionAdapter = new FansAndAttentionAdapter(R.layout.item_fans_attention, attentionsList, activityId);
        attentionRecyclerview.setAdapter(attentionAdapter);
        attentionAdapter.setOnItemChildClickListener(this);
        attentionAdapter.setOnItemClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        queryAttentionRelation();
    }

    @OnClick({R.id.iv_back, R.id.tv_title_text, R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_title_text:
                break;
            case R.id.tv_empty:
                queryAttentionRelation();
                break;
        }
    }

    /**
     * 查询我的关注
     */
    private void queryAttentionRelation() {
        OkGo.<String>get(Urls.QUERY_ATTENTIONRELATION)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("status", status)
                .params("currentPage", currentPage)
                .params("pageSize", pageSize)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
//                        refreshLayout.finishRefresh();
                        if (response != null) {
                            if (REFRESHORLOADMORE == 5) {
                                attentionsList.clear();
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadmore();
                            }
                            Gson gson = new Gson();
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    MyFansModel myFansModel = gson.fromJson(response.body(), MyFansModel.class);
                                    List<MyFansModel.DataBean> dataBeans = myFansModel.getData();
                                    if (dataBeans != null && dataBeans.size() > 0) {
                                        attentionsList.addAll(dataBeans);
                                    }
                                    if (attentionsList.size() > 0) {
                                        tvEmpty.setVisibility(View.GONE);
                                    } else {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }
                                    attentionAdapter.notifyDataSetChanged();
                                } else {
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
                        if (REFRESHORLOADMORE == 5) {
                            attentionsList.clear();
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MyFansModel.DataBean dataBean = attentionsList.get(position);
        long id = dataBean.getId();
        long toMemberId = dataBean.getToMemberId();
        String toMemberName = dataBean.getToMemberName();
        switch (view.getId()) {
            case R.id.tv_cancle_attention:
                atteStatus = 1;
                addToAttentionRelation(id, toMemberId, toMemberName, position);
                break;
        }
    }

    private void addToAttentionRelation(long id, long toMemberId, String toMemberName, final int position) {
        OkGo.<String>get(Urls.ADD_ATTENTIONRELATION)//
                .tag(this)//
                .params("id", id)
                .params("fromMemberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", toMemberId)
                .params("toMemberName", toMemberName)
                .params("atteStatus", atteStatus)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
//                        refreshLayout.finishRefresh();
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        if (atteStatus == 1) {
                                            RxToast.showToast("已取消关注！");
                                            attentionsList.remove(position);
                                            if (attentionsList.size() > 0) {
                                                tvEmpty.setVisibility(View.GONE);
                                            } else {
                                                tvEmpty.setVisibility(View.VISIBLE);
                                            }
                                            attentionAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        RxToast.showToast(data.optString("info"));
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
                    }
                });
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        currentPage = 1;
        REFRESHORLOADMORE = 5;
        queryAttentionRelation();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 6;
        currentPage++;
        queryAttentionRelation();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("hisMemberId", attentionsList.get(position).getToMemberId());
        bundle.putString("hisImgUrl", attentionsList.get(position).getHeadUrl());
        bundle.putString("hisNickName", attentionsList.get(position).getToMemberName());
        RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle);
    }
}

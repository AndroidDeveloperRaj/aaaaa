package com.merrichat.net.activity.my.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.groupmarket.CashDepositActivity;
import com.merrichat.net.activity.message.setting.MasterWalletsActivity;
import com.merrichat.net.adapter.CashDepositAdapter;
import com.merrichat.net.adapter.MyGroupWalletAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CashDepositModel;
import com.merrichat.net.model.GroupListModel;
import com.merrichat.net.model.GroupMarketModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的群钱包
 * Created by amssy on 18/3/14.
 */

public class MyGroupWalletAty extends BaseActivity implements MyGroupWalletAdapter.OnGroupItemClick{
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;


    private MyGroupWalletAdapter adapter;
    private List<GroupListModel.DataBean.ListBean> listBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group_wallet);
        ButterKnife.bind(this);
        initView();
        initRecyclerView();
        communityList();
    }

    private void initView() {
        tvTitleText.setText("我的群钱包");
    }

    private void initRecyclerView() {
        listBeans = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyGroupWalletAdapter(this , listBeans);
        recyclerView.setAdapter(adapter);
        adapter.setOnGroupItemClickLister(this);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 群组列表
     */
    private void communityList() {
        OkGo.<String>post(Urls.communityList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data != null && data.optBoolean("success")) {

                                    GroupListModel groupListModel = new Gson().fromJson(response.body(), GroupListModel.class);
                                    List<GroupListModel.DataBean.ListBean> listBean = groupListModel.getData().getList();
                                    if (listBean != null && listBean.size() > 0) {
                                        listBeans.clear();
                                        for (int i = 0; i < listBean.size(); i++) {
                                            GroupListModel.DataBean.ListBean B = listBean.get(i);
                                            //是否是管理员新加 0:成员 1:管理员 2:群主
                                            if ("2".equals(B.getIsMaster())){
                                                listBeans.add(B);
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (listBeans.size() > 0) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                            }
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

    /**
     * Item的点击事件
     * @param position
     */
    @Override
    public void onGroupItemClickLister(int position) {
        startActivity(new Intent(cnt, MasterWalletsActivity.class).putExtra("groupId", listBeans.get(position).getCommunityId()).putExtra("communityAccountId", listBeans.get(position).getAccountId()).putExtra("isMaster", Integer.valueOf(listBeans.get(position).getIsMaster())));
    }
}

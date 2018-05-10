package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.SetMasterWalletsAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MasterWalletsModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/19.
 * 群主钱包设置
 */

public class SetMasterWalletsActivity extends MerriActionBarActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmty;

    private SetMasterWalletsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<MasterWalletsModel> masterWalletlList;

    /**
     * 群id
     */
    private String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_wallets_set);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        setTitle("群主钱包设置");
        setLeftBack();
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        masterWalletlList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SetMasterWalletsAdapter(cnt, masterWalletlList, groupId);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new SetMasterWalletsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(cnt, HisYingJiAty.class);
                intent.putExtra("hisMemberId", Long.parseLong(masterWalletlList.get(position).getMasterId()));
                intent.putExtra("hisImgUrl", masterWalletlList.get(position).getMasterImgUrl());
                intent.putExtra("hisNickName", masterWalletlList.get(position).getMasterName());
                startActivity(intent);
            }
        });


        adapter.setSwitchButtonClickListener(new SetMasterWalletsAdapter.SwitchButtonOnclickListener() {
            @Override
            public void onClickListener(int position) {
                MasterWalletsModel model = masterWalletlList.get(position);
                if (model.getIsShowWallet() == 0) {
                    model.setIsShowWallet(1);
                } else if (model.getIsShowWallet() == 1) {
                    model.setIsShowWallet(0);
                }
                adapter.notifyDataSetChanged();
            }
        });
        communityWalletSetUp();
    }

    /**
     * 群钱包设置页面
     */
    private void communityWalletSetUp() {
        OkGo.<String>post(Urls.communityWalletSetUp)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    Gson gson = new Gson();
                                    JSONArray list = data.optJSONArray("list");
                                    for (int i = 0; i < list.length(); i++) {
                                        MasterWalletsModel model = gson.fromJson(list.get(i).toString(), MasterWalletsModel.class);
                                        masterWalletlList.add(model);
                                    }
                                    if (null == masterWalletlList || masterWalletlList.size() < 1) {
                                        tvEmty.setText("暂无管理员");
                                        tvEmty.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        tvEmty.setVisibility(View.GONE);
                                    }
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}

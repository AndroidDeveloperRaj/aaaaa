package com.merrichat.net.activity.groupmarket;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.CashDepositAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CashDepositModel;
import com.merrichat.net.model.UserModel;
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
 * 我的保证金
 * Created by amssy on 18/1/23.
 */

public class CashDepositActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private CashDepositAdapter adapter;
    private List<CashDepositModel.DataBean.ListBean> listBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_deposit);
        ButterKnife.bind(this);
        initView();
        initRecyclerView();
        myCommunityMargin();
    }

    private void initView() {
        tvTitleText.setText("我的保证金");
    }

    private void initRecyclerView() {
        listBeans = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CashDepositActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CashDepositAdapter(CashDepositActivity.this , listBeans);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 查询我的保证金
     */
    private void myCommunityMargin() {
        OkGo.<String>get(Urls.myCommunityMargin)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //数据不全 实体类需要改动 数据未绑定
                                    CashDepositModel cashDepositModel = JSON.parseObject(response.body(), CashDepositModel.class);
                                    listBeans.clear();
                                    listBeans.addAll(cashDepositModel.getData().getList());
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
}

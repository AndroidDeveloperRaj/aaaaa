package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.BonusRecordAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.BonusRecordModel;
import com.merrichat.net.model.BonusReportModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/8.
 * <p>
 * 奖励记录
 */

public class BonusRecordActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, OnRefreshListener, OnLoadmoreListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    @BindView(R.id.swipeLayout)
    SmartRefreshLayout mSwipeRefreshLayout;
    TextView tvBonusNum;
    TextView tvDate;
    private BonusRecordAdapter bonusRecordAdapter;
    private ArrayList<BonusRecordModel.DataBean.BonusRecordsBean> bonusRecordList;
    private BonusReportModel.DataBean.BonusReportsBean bonusReportModel;
    private int currentPage = 1;
    private boolean ISREFESH = false;// true 刷新 false加载更多
    private String dataValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonusrecord);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("奖励记录");
        bonusRecordList = new ArrayList<>();
        bonusReportModel = (BonusReportModel.DataBean.BonusReportsBean) getIntent().getSerializableExtra("bonusReportModel");

        if (bonusReportModel != null) {
            dataValue = bonusReportModel.getCurrentYear() + "-" + bonusReportModel.getMonth();

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlRecyclerview.setLayoutManager(layoutManager);
        bonusRecordAdapter = new BonusRecordAdapter(R.layout.item_bonusrecord, bonusRecordList);
        rlRecyclerview.setAdapter(bonusRecordAdapter);
        bonusRecordAdapter.setOnItemClickListener(this);
        bonusRecordAdapter.addHeaderView(getHeaderView());
        mSwipeRefreshLayout.setEnableLoadmore(false);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadmoreListener(this);
        getBonusRecordList();
    }

    /**
     * 获取报表记录
     */
    private void getBonusRecordList() {
        OkGo.<String>get(Urls.GET_BONUSRECORDLIST)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("currentMonth", dataValue)
                .params("pageNum", "")
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (ISREFESH) {
                            bonusRecordList.clear();
                            mSwipeRefreshLayout.finishRefresh();
                        } else {
                            mSwipeRefreshLayout.finishLoadmore();
                        }
                        if (response != null) {
                            Gson gson = new Gson();
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    BonusRecordModel bonusRecordModel = gson.fromJson(response.body(), BonusRecordModel.class);
                                    if (bonusRecordModel.isSuccess()) {
                                        BonusRecordModel.DataBean data = bonusRecordModel.getData();
                                        List<BonusRecordModel.DataBean.BonusRecordsBean> bonusRecords = data.getBonusRecords();
                                        bonusRecordList.addAll(bonusRecords);
                                        bonusRecordAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    RxToast.showToast(jsonObjectEx.optString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (ISREFESH) {
                            mSwipeRefreshLayout.finishRefresh();
                        } else {
                            mSwipeRefreshLayout.finishLoadmore();
                        }
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.bonusrecord_head_view, (ViewGroup) rlRecyclerview.getParent(), false);
        tvBonusNum = (TextView) view.findViewById(R.id.tv_bonus_num);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        if (bonusReportModel != null) {
            tvBonusNum.setText(StringUtil.getPrice(bonusReportModel.getAmount() + "") + "讯美币");
            tvDate.setText(bonusReportModel.getCurrentYear() + "年" + bonusReportModel.getMonth() + "月" + "奖励记录");
        }
        return view;
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        currentPage = 1;
        ISREFESH = true;
        getBonusRecordList();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        currentPage++;
        ISREFESH = false;
        getBonusRecordList();
    }
}

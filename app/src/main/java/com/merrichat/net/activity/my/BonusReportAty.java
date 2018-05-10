package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.BonusReportAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.BonusReportModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/7.
 * <p>
 * 奖励报表
 */

public class BonusReportAty extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

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
    /**
     * 头部广告
     */
    @BindView(R.id.iv_ad_header)
    ImageView ivAdHeader;
    /**
     * 无数据 加载页
     */
    @BindView(R.id.tv_no_text)
    TextView tvNoText;
    private BonusReportAdapter bonusReportAdapter;
    private ArrayList<BonusReportModel.DataBean.BonusReportsBean> bonusReportList;
    private ImageView ivAdImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitetomakemoney);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("奖励报表");
        bonusReportList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlRecyclerview.setLayoutManager(layoutManager);
        bonusReportAdapter = new BonusReportAdapter(R.layout.item_bonusreport, bonusReportList);
        rlRecyclerview.setAdapter(bonusReportAdapter);
        bonusReportAdapter.setOnItemClickListener(this);
//        bonusReportAdapter.addHeaderView(getHeaderView(), -1, LinearLayout.VERTICAL);
        bonusReportAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bonusReportModel", bonusReportList.get(position));
                RxActivityTool.skipActivity(BonusReportAty.this, BonusRecordActivity.class, bundle);
            }
        });
        getBonusReportList();

    }

    /**
     * 获取记录
     */
    private void getBonusReportList() {
        OkGo.<String>get(Urls.GET_BONUSREPORTLIST)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("days", "")
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            Gson gson = new Gson();
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    BonusReportModel bonusReportModel = gson.fromJson(response.body(), BonusReportModel.class);
                                    if (bonusReportModel.isSuccess()) {
                                        BonusReportModel.DataBean data = bonusReportModel.getData();
                                        List<BonusReportModel.DataBean.BonusReportsBean> bonusReports = data.getBonusReports();
                                        bonusReportList.addAll(bonusReports);
                                        Glide.with(BonusReportAty.this).load(data.getImg()).into(ivAdHeader);
                                        if (bonusReportList.size() > 0) {
                                            tvNoText.setVisibility(View.GONE);
                                        } else {
                                            tvNoText.setVisibility(View.VISIBLE);
                                        }
                                        bonusReportAdapter.notifyDataSetChanged();
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
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    private View.OnClickListener headerViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
    }


    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.item_shareto_makemoney_header, (ViewGroup) rlRecyclerview.getParent(), false);
        ivAdImage = (ImageView) view.findViewById(R.id.iv_ad_image);
        view.setOnClickListener(headerViewOnClickListener());
        return view;
    }

    @OnClick({R.id.iv_back, R.id.iv_ad_header})
    public void onViewClicked(View view) {
        //判断是否是快速点击
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_ad_header:
                RxActivityTool.skipActivity(BonusReportAty.this, ShareToMakeMoneyStrategyAty.class);
                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}

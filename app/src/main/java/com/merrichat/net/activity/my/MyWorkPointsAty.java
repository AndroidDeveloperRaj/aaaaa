package com.merrichat.net.activity.my;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Allen Cheng on 2018/3/15.
 * <p>
 * 我的——挖矿工分
 */

public class MyWorkPointsAty extends MerriActionBarActivity {
    @BindView(R.id.tv_work_points)
    TextView tvWorkPoints;
    @BindView(R.id.tv_mei_bi)
    TextView tvMeiBi;
    @BindView(R.id.tv_get_paid)
    TextView tvGetPaid;
    /**
     * 最近7天点赞数
     */
    @BindView(R.id.tv_zan_num)
    TextView tvZanNum;
    private CommomDialog dialog;
    private String meiBi = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workpoints);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle("我的金币");
        setLeftBack();
        queryCharmCounts();
    }

    @OnClick({R.id.tv_mei_bi, R.id.tv_get_paid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_mei_bi:
                break;
            case R.id.tv_get_paid://领工资
                if (tvWorkPoints.getText().toString().trim().equals("0")) {
                    RxToast.showToast("暂无工分兑换工资！");
                    return;
                }
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要领取工资吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                exchangeMeiBi();
                            }
                        }
                    }).setTitle("领工资");
                    dialog.show();
                }

                break;
        }
    }

    /**
     * 使用工分兑换美币
     */
    private void exchangeMeiBi() {
        OkGo.<String>post(Urls.exchangeMeiBi)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback(MyWorkPointsAty.this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success") && data != null) {
                                RxToast.showToast(data.optString("msg"));
                                if (data.optBoolean("result")) {
                                    queryCharmCounts();
                                }
                            } else {
                                RxToast.showToast("领取工资失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            RxToast.showToast("请求失败，请重试！");

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
     * 查询个人工分
     */
    private void queryCharmCounts() {
        OkGo.<String>post(Urls.queryCharmCounts)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback(MyWorkPointsAty.this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success") && data != null) {
                                if (data.optBoolean("flag")) {
                                    JSONObject charmCountsData = data.optJSONObject("charmCounts");
                                    if (charmCountsData != null) {
                                        tvWorkPoints.setText(charmCountsData.optString("totalCharmCounts"));
                                    }
                                    meiBi = data.optString("meiBi");
                                    tvMeiBi.setText(meiBi + " 元");
                                } else {
                                    tvMeiBi.setText(meiBi + " 元");
                                    tvWorkPoints.setText("0");
                                }
                            } else {
                                RxToast.showToast("查询工分失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            RxToast.showToast("请求失败，请重试！");
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

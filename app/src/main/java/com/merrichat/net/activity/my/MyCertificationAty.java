package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.setting.IdentityVerificationAty;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDeviceTool;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/12/27.
 * <p>
 * 我的认证
 */

public class MyCertificationAty extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_menwomen_status)
    TextView tvMenwomenStatus;
    @BindView(R.id.rl_menwomen_confirm)
    RelativeLayout rlMenwomenConfirm;
    @BindView(R.id.tv_id_certification_status)
    TextView tvIdCertificationStatus;
    @BindView(R.id.rl_person_confirm)
    RelativeLayout rlPersonConfirm;
    private int status = 0;//0表示的是查看审核状态,1:表示的是提交男/女神的申请
    private File file;
    private int identityStatus = -1;
    private int starStatus = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_certification);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("我的认证");
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyStar();
    }

    /**
     * 男神女神申请状态
     */
    private void applyStar() {

        file = new File(ConstantsPath.pic2videoPath, "1.txt");
        if (!RxFileTool.isFileExists(file)) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        OkGo.<String>get(Urls.APPLY_STAR)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("status", status)
                .params("deviceType", RxDeviceTool.getIMEI(MyCertificationAty.this))
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        String gender = UserModel.getUserModel().getGender();
                                        identityStatus = data.optInt("identityStatus");
                                        starStatus = data.optInt("starStatus");
                                        if (starStatus == 0) {
                                            tvMenwomenStatus.setText("等待审核");
                                        } else if (starStatus == 1) {
                                            tvMenwomenStatus.setText("已审核通过！");
                                        } else if (starStatus == 2) {
                                            tvMenwomenStatus.setText("审核被拒绝！");
                                        } else if (starStatus == 4) {
                                            if (gender.equals("1")) {

                                                tvMenwomenStatus.setText("认证成为男神收益更多");
                                            } else if (gender.equals("2")) {
                                                tvMenwomenStatus.setText("认证成为女神收益更多");

                                            }
                                        }

                                        if (identityStatus == 0) {
                                            tvIdCertificationStatus.setText("等待审核");
                                        } else if (identityStatus == 1) {
                                            tvIdCertificationStatus.setText("已认证");
                                        } else {
                                            tvIdCertificationStatus.setText("身份认证之后才可提现");
                                        }
                                    }
                                } else {
                                    RxToast.showToast("认证状态获取失败！");
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

    @OnClick({R.id.iv_back, R.id.rl_menwomen_confirm, R.id.rl_person_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_menwomen_confirm:
                Bundle bundle = new Bundle();
                bundle.putInt("starStatus", starStatus);
                RxActivityTool.skipActivity(this, MenAndWomenVerificationAty.class, bundle);
                break;
            case R.id.rl_person_confirm:
                RxActivityTool.skipActivity(this, IdentityVerificationAty.class);
                break;
        }
    }
}

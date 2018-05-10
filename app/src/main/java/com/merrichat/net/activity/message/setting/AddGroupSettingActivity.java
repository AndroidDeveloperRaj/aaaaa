package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/20.
 * 加群设置
 */

public class AddGroupSettingActivity extends MerriActionBarActivity {

    /**
     * 允许任何人加群
     */
    @BindView(R.id.rl_allow_all)
    RelativeLayout rlAllowAll;

    @BindView(R.id.tv_allow_all)
    TextView tvAllowAll;

    @BindView(R.id.iv_allow_all)
    ImageView ivAllowAll;


    /**
     * 不允许任何人加群
     */
    @BindView(R.id.rl_not_allowed)
    RelativeLayout rlNotAllowed;

    @BindView(R.id.tv_not_allowed)
    TextView tvNotAllowed;

    @BindView(R.id.iv_not_allowed)
    ImageView ivNotAllowed;

    /**
     * 需要管理员审核
     */
    @BindView(R.id.rl_need_to_review)
    RelativeLayout rlNeedToReview;

    @BindView(R.id.tv_need_to_review)
    TextView tvNeedToReview;

    @BindView(R.id.iv_need_to_review)
    ImageView ivNeedToReview;


    /**
     * 加群设置flag
     * 1： 允许任何人加群
     * 2：不允许任何人加群
     * 3需要管理员审核
     */
    private int AddGroupFlag;

    private String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_settings);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle("加群设置");
        setLeftBack();
        setRightText("完成", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinSetUp();
            }
        });
        groupId = getIntent().getStringExtra("groupId");
        joinOrBannedPage();
    }


    /**
     * 查询加群设置状态
     */
    private void joinOrBannedPage() {
        OkGo.<String>post(Urls.joinOrBannedPage)
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
                                    //加群设置 1：默认允许任何人加群 2：不允许任何人加群 3：需要管理员审核
                                    String joinSetUp = data.optString("joinSetUp");
                                    if ("1".equals(joinSetUp)) {
                                        AddGroupFlag = 1;
                                        setAddGroup();
                                    } else if ("2".equals(joinSetUp)) {
                                        AddGroupFlag = 2;
                                        setAddGroup();
                                    } else if ("3".equals(joinSetUp)) {
                                        AddGroupFlag = 3;
                                        setAddGroup();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 加群设置
     */
    private void joinSetUp() {
        OkGo.<String>post(Urls.joinSetUp)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("cid", groupId)
                .params("joinSetup", AddGroupFlag)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "设置成功");
                                    finish();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick({R.id.rl_allow_all, R.id.rl_not_allowed, R.id.rl_need_to_review})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_allow_all://允许任何人加群
                if (AddGroupFlag == 1) {
                    return;
                }
                AddGroupFlag = 1;
                setAddGroup();
                break;
            case R.id.rl_not_allowed://不允许任何人加群
                if (AddGroupFlag == 2) {
                    return;
                }
                AddGroupFlag = 2;
                setAddGroup();
                break;
            case R.id.rl_need_to_review://需要管理员审核
                if (AddGroupFlag == 3) {
                    return;
                }
                AddGroupFlag = 3;
                setAddGroup();
                break;
        }
    }


    /**
     * 设置加群设置状态
     */
    private void setAddGroup() {
        switch (AddGroupFlag) {
            case 1:
                ivAllowAll.setVisibility(View.VISIBLE);
                ivNotAllowed.setVisibility(View.GONE);
                ivNeedToReview.setVisibility(View.GONE);

                tvAllowAll.setTextColor(getResources().getColor(R.color.FF3D6F));
                tvNotAllowed.setTextColor(getResources().getColor(R.color.black_new_two));
                tvNeedToReview.setTextColor(getResources().getColor(R.color.black_new_two));
                break;
            case 2:
                ivAllowAll.setVisibility(View.GONE);
                ivNotAllowed.setVisibility(View.VISIBLE);
                ivNeedToReview.setVisibility(View.GONE);

                tvAllowAll.setTextColor(getResources().getColor(R.color.black_new_two));
                tvNotAllowed.setTextColor(getResources().getColor(R.color.FF3D6F));
                tvNeedToReview.setTextColor(getResources().getColor(R.color.black_new_two));
                break;

            case 3:
                ivAllowAll.setVisibility(View.GONE);
                ivNotAllowed.setVisibility(View.GONE);
                ivNeedToReview.setVisibility(View.VISIBLE);

                tvAllowAll.setTextColor(getResources().getColor(R.color.black_new_two));
                tvNotAllowed.setTextColor(getResources().getColor(R.color.black_new_two));
                tvNeedToReview.setTextColor(getResources().getColor(R.color.FF3D6F));
                break;
        }
    }
}

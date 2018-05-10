package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
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
 * 设置群禁言
 */

public class SetGroupGossipActivity extends MerriActionBarActivity {

    /**
     * 允许所与人发言
     */
    @BindView(R.id.tv_allow_all)
    TextView tvAllowAll;
    @BindView(R.id.iv_allow_all)
    ImageView ivAllowAll;
    @BindView(R.id.rl_allow_all)
    RelativeLayout rlAllowAll;


    /**
     * 只允许群主和管理员发言
     */
    @BindView(R.id.tv_limit_section)
    TextView tvLimitSection;
    @BindView(R.id.iv_limit_section)
    ImageView ivLimitSection;
    @BindView(R.id.rl_limit_section)
    RelativeLayout rlLimitSection;

    /**
     * 指定禁言
     */
    @BindView(R.id.rl_designate_gag)
    RelativeLayout rlDesignateGag;

    /**
     * 禁言名单
     */
    @BindView(R.id.rl_list_of_gossip)
    RelativeLayout rlListOfGossip;


    /**
     * 禁言flag
     * <p>
     * 1：允许所与人发言
     * 2：只允许群主和管理员发言
     */
    private int GroupGossipFlag;

    /**
     * 群id
     */
    private String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_group_gossip);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle("设置群禁言");
        setLeftBack();
        setRightText("完成", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableSendMsgSetUp();
            }
        });
        groupId = getIntent().getStringExtra("groupId");
        joinOrBannedPage();
    }

    /**
     * 查询群禁言状态
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
//                                    禁言设置 1：允许所有人发言，2：群主和管理员发言 3:全部禁言
                                    String disableSendMsg = data.optString("disableSendMsg");
                                    if ("1".equals(disableSendMsg)) {
                                        GroupGossipFlag = 1;
                                        setGroupGossipStaus();
                                    } else if ("2".equals(disableSendMsg)) {
                                        GroupGossipFlag = 2;
                                        setGroupGossipStaus();
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
     * 群禁言设置
     */
    private void disableSendMsgSetUp() {
        OkGo.<String>post(Urls.disableSendMsgSetUp)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("cid", groupId)
                .params("disableSendMsg", GroupGossipFlag)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                GetToast.useString(cnt, "设置成功");
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick({R.id.rl_allow_all, R.id.rl_limit_section, R.id.rl_designate_gag, R.id.rl_list_of_gossip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_allow_all://允许所与人发言
                if (GroupGossipFlag == 1) {
                    return;
                }
                GroupGossipFlag = 1;
                setGroupGossipStaus();
                break;
            case R.id.rl_limit_section://只允许群主和管理员发言
                if (GroupGossipFlag == 2) {
                    return;
                }
                GroupGossipFlag = 2;
                setGroupGossipStaus();
                break;
            case R.id.rl_designate_gag://指定禁言
                startActivity(new Intent(cnt, DesignateGagActivity.class).putExtra("groupId",groupId));
                break;
            case R.id.rl_list_of_gossip://禁言名单
                startActivity(new Intent(cnt, ListOfGossipActivity.class).putExtra("groupId",groupId));
                break;
        }
    }


    /**
     * 设置群禁言状态
     */
    private void setGroupGossipStaus() {
        switch (GroupGossipFlag) {
            case 1:
                ivAllowAll.setVisibility(View.VISIBLE);
                tvAllowAll.setTextColor(getResources().getColor(R.color.FF3D6F));
                ivLimitSection.setVisibility(View.GONE);
                tvLimitSection.setTextColor(getResources().getColor(R.color.black_new_two));
                break;

            case 2:
                ivLimitSection.setVisibility(View.VISIBLE);
                tvLimitSection.setTextColor(getResources().getColor(R.color.FF3D6F));
                ivAllowAll.setVisibility(View.GONE);
                tvAllowAll.setTextColor(getResources().getColor(R.color.black_new_two));
                break;
        }
    }
}

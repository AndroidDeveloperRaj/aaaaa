package com.merrichat.net.activity.his;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.HisHomeModel;
import com.merrichat.net.model.HisZiLiaoSettingModel;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.SysApp;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/12/4.
 * <p>
 * Ta主页--资料设置
 */

public class HisZiLiaoSettingAty extends BaseActivity {
    private final String NOSEE_MINE = "1";
    private final String NOSEE_HIS = "0";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_jubao)
    TextView tvJubao;
    @BindView(R.id.tv_nosee_mine)
    TextView tvNoseeMine;
    @BindView(R.id.tv_nosee_his)
    TextView tvNoseeHis;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.sb_nosee_mine)
    SwitchButton sbNoseeMine;
    @BindView(R.id.sb_nosee_his)
    SwitchButton sbNoseeHis;
    private String memberId = "";
    private String hisMemberId = "";
    private HisHomeModel.DataBean hisHomeModelData;
    private String SEE_TYPE = "-1";
    private CommomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_ziliaosetting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("资料设置");
        memberId = UserModel.getUserModel().getMemberId();
        hisHomeModelData = (HisHomeModel.DataBean) getIntent().getSerializableExtra("hisHomeModelData");
        if (hisHomeModelData != null) {
            String isFriend = hisHomeModelData.getIsFriend();
            hisMemberId = hisHomeModelData.getHisMemberInfo().getMemberId();
            if (!RxDataTool.isNullString(hisMemberId) && isFriend.equals("2")) {  //2:好友,0/1不是好友(1:好友请求中,0:不是好友)
                tvDelete.setVisibility(View.VISIBLE);
            }
        }
        hideFriendCircle("");
        sbNoseeMine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SEE_TYPE = NOSEE_MINE;
                hideFriendCircle(NOSEE_MINE);
            }
        });
        sbNoseeHis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SEE_TYPE = NOSEE_HIS;

                hideFriendCircle(NOSEE_HIS);

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_jubao, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_jubao:
                Bundle bundle = new Bundle();
                bundle.putSerializable("hisHomeModelData", hisHomeModelData);
                RxActivityTool.skipActivity(this, ComplaintOthersAty.class, bundle);
                break;
            case R.id.tv_delete:
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要删除好友吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                deleteGoodFriend();
                                MessageModel.clearSingleMessageModel(hisMemberId);
                                MessageListModle.clearSingleMessageModelByReceivedId(hisMemberId);
                                MyEventBusModel model = new MyEventBusModel();
                                model.SINGLE_MESSAGE_IS_REFRESH = true;
                                model.MESSAGE_IS_REFRESH = true;
                                EventBus.getDefault().post(model);
                            }
                        }
                    }).setTitle("删除好友");
                    dialog.show();
                }

                break;
        }
    }

    private void deleteGoodFriend() {
        OkGo.<String>get(Urls.deleteGoodFriend)//
                .tag(this)//
                .params("memberId", memberId)
                .params("toMemberId", hisMemberId)
                .execute(new StringDialogCallback(cnt) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.REFRESH_HIS_HOME = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                    RxToast.showToast("已删除好友！");
                                    tvDelete.setVisibility(View.GONE);
                                } else {
                                    RxToast.showToast("删除好友失败！");
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

    private void hideFriendCircle(final String type) {
        OkGo.<String>get(Urls.hideFriendCircle)//
                .tag(this)//
                .params("memberId", memberId)
                .params("toMemberId", hisMemberId)
                .params("status", type)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            Gson gson = new Gson();
                            HisZiLiaoSettingModel hisZiLiaoSettingModel = gson.fromJson(response.body(), HisZiLiaoSettingModel.class);
                            if (hisZiLiaoSettingModel != null && hisZiLiaoSettingModel.isSuccess()) {
                                HisZiLiaoSettingModel.DataBean hisZiLiaoSettingModelData = hisZiLiaoSettingModel.getData();
                                if (RxDataTool.isNullString(type)) {//查询

                                    if (hisZiLiaoSettingModelData.getHeSeeMe() == 0) {//不让他看我的动态 0:让看,1:不让看
                                        sbNoseeMine.setCheckedImmediatelyNoEvent(false);
                                    } else {
                                        sbNoseeMine.setCheckedImmediatelyNoEvent(true);
                                    }
                                    if (hisZiLiaoSettingModelData.getIseeHim() == 0) {//我不让他看我的动态 0:让看,1:不让看
                                        sbNoseeHis.setCheckedImmediatelyNoEvent(false);
                                    } else {
                                        sbNoseeHis.setCheckedImmediatelyNoEvent(true);
                                    }
                                } else if (NOSEE_MINE.equals(type)) {

                                }
                            } else {
                                RxToast.showToast("修改失败！");
                                noChangeStateButtonStatus();
                            }

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        noChangeStateButtonStatus();

                    }
                });
    }

    /**
     * 修改失败时，不改变按钮状态
     */
    private void noChangeStateButtonStatus() {
        if (SEE_TYPE.equals(NOSEE_MINE)) {
            sbNoseeMine.setCheckedImmediatelyNoEvent(!sbNoseeMine.isChecked());
        } else {
            sbNoseeHis.setCheckedImmediatelyNoEvent(!sbNoseeHis.isChecked());
        }
    }
}

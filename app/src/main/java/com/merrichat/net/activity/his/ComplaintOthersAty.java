package com.merrichat.net.activity.his;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.message.setting.GroupSettingActivity;
import com.merrichat.net.activity.message.setting.MemberManagementActivity;
import com.merrichat.net.adapter.ComplaintOthersAdapter;
import com.merrichat.net.adapter.ComplaintOthersReasonAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ComplaintOthersReasonModel;
import com.merrichat.net.model.ComplaintOthersReportInfoModel;
import com.merrichat.net.model.HisHomeModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.InputMethodUtils;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.KeyBoardHelper;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.CommomDialog;
import com.merrichat.net.view.MyScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by AMSSY1 on 2017/12/5.
 * <p>
 * Ta的影集——举报
 */

public class ComplaintOthersAty extends BaseActivity {
    private static final int REQUEST_IMAGE = 2;
    private final int REQUEST_CODE_CHOOSE = 0x0001;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recyclerview_reason)
    RecyclerView recyclerviewReason;
    @BindView(R.id.recyclerview_img)
    RecyclerView recyclerviewImg;
    @BindView(R.id.et_complanit_reason)
    EditText etComplanitReason;
    @BindView(R.id.tv_text_num)
    TextView tvTextNum;
    @BindView(R.id.rl_edit)
    RelativeLayout rlEdit;
    @BindView(R.id.tv_submit_area)
    TextView tvSubmitArea;
    @BindView(R.id.sv_scrollview)
    MyScrollView svScrollview;
    @BindView(R.id.rl_all)
    RelativeLayout rlAll;
    private ArrayList<String> imgUrlList = new ArrayList<>();
    private ComplaintOthersAdapter complaintOthersImgAdapter;
    private String authority;
    private ArrayList<ComplaintOthersReasonModel.DataBean> reasonList;
    private ComplaintOthersReasonAdapter complaintOthersReasonAdapter;
    private String typeName = "";//选择的举报内容
    private HisHomeModel.DataBean hisHomeModelData;
    private UserModel userModel;
    private Gson gson;
    private List<File> fileList;

    private String groupId = "";
    private KeyBoardHelper boardHelper;
    private int bottomHeight;
    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyBoardheight) {

            final int height = keyBoardheight;
            if (bottomHeight > height) {
                //layoutBottom.setVisibility(View.GONE);
            } else {
                int offset = bottomHeight - height;
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) svScrollview
                        .getLayoutParams();
                lp.topMargin = offset;
                svScrollview.setLayoutParams(lp);
            }

        }

        @Override
        public void OnKeyBoardClose(int oldKeyBoardheight) {
            if (View.VISIBLE != tvSubmitArea.getVisibility()) {
                //layoutBottom.setVisibility(View.VISIBLE);
            }
            final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) svScrollview
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                svScrollview.setLayoutParams(lp);
            }

        }
    };
    private String hisMemberId;
    private String hisNickName;
    private String hisPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_others);
        ButterKnife.bind(this);
        authority = MerriApp.getFileProviderName(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("举报");
        if (getIntent().getFlags() == GroupSettingActivity.activityId) {
            groupId = getIntent().getStringExtra("groupId");
        } else if (getIntent().getIntExtra("activityId", -1) == MemberManagementActivity.activityId) {//群成员
            hisMemberId = getIntent().getStringExtra("communityMemberId");
            hisNickName = getIntent().getStringExtra("memberName");
            hisPhone = getIntent().getStringExtra("hisPhone");
        } else {
            hisHomeModelData = (HisHomeModel.DataBean) getIntent().getSerializableExtra("hisHomeModelData");
        }
        userModel = UserModel.getUserModel();
        gson = new Gson();

        imgUrlList = new ArrayList<>();
        reasonList = new ArrayList<>();
        fileList = new ArrayList<>();
        queryReportingTypes();
        initReasonRecyclerView();
        initImgRecyclerView();
        etComplanitReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 200) {
                    RxToast.showToast("投诉原因最多200个字！");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 200) {
                    String etComplanitReasonText = etComplanitReason.getText().toString().trim();
                    etComplanitReason.setText(etComplanitReasonText.substring(0, 200));
                    etComplanitReason.setSelection(etComplanitReason.getText().toString().trim().length());
                    InputMethodUtils.closeSoftKeyboard(ComplaintOthersAty.this);
                }
                tvTextNum.setText(etComplanitReason.getText().toString().trim().length() + "");

            }
        });
        boardHelper = new KeyBoardHelper(this);
        boardHelper.onCreate();
        boardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
        tvSubmitArea.post(new Runnable() {
            @Override
            public void run() {
                bottomHeight = tvSubmitArea.getHeight() - 200;
            }
        });
    }

    /**
     * 查询举报信息列表
     */
    private void queryReportingTypes() {
        OkGo.<String>get(Urls.queryReportingTypes)//
                .tag(this)//
                .params("currentPage", 0)
                .params("pageSize", 30)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    Gson gson = new Gson();
                                    ComplaintOthersReasonModel complaintOthersReasonModel = gson.fromJson(response.body(), ComplaintOthersReasonModel.class);
                                    List<ComplaintOthersReasonModel.DataBean> data = complaintOthersReasonModel.getData();
                                    if (data != null) {
                                        reasonList.addAll(data);
                                    }
                                    complaintOthersReasonAdapter.notifyDataSetChanged();
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

    private void initReasonRecyclerView() {
        LinearLayoutManager reasonLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerviewReason.setLayoutManager(reasonLayoutManager);
        complaintOthersReasonAdapter = new ComplaintOthersReasonAdapter(R.layout.item_complaintothers_reason, reasonList);
        recyclerviewReason.setAdapter(complaintOthersReasonAdapter);
        complaintOthersReasonAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (int i = 0; i < reasonList.size(); i++) {
                    if (i == position) {
                        reasonList.get(i).setChecked(true);
                        typeName = reasonList.get(i).getTypeName();
                    } else {
                        reasonList.get(i).setChecked(false);
                    }
                }
                complaintOthersReasonAdapter.notifyDataSetChanged();
                rlEdit.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initImgRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerviewImg.setLayoutManager(layoutManager);
        complaintOthersImgAdapter = new ComplaintOthersAdapter(R.layout.item_img_80, imgUrlList);
        recyclerviewImg.setAdapter(complaintOthersImgAdapter);
        complaintOthersImgAdapter.addFooterView(getHeaderView(), -1, LinearLayout.HORIZONTAL);
        complaintOthersImgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                //弹出提示框
                new CommomDialog(cnt, R.style.dialog, "您确定要删除该图片吗？", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            imgUrlList.remove(position);
                            complaintOthersImgAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                    }
                }).setTitle("删除图片").show();

            }
        });
    }

    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.item_complaintothers_footer, (ViewGroup) recyclerviewImg.getParent(), false);
        view.setOnClickListener(headerViewOnClickListener());
        return view;
    }

    private View.OnClickListener headerViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUrlList != null && imgUrlList.size() < 9) {

                    MultiImageSelector selector = MultiImageSelector.create(ComplaintOthersAty.this);
                    selector.showCamera(true);
                    selector.count(9);
                    selector.multi();
                    selector.origin(imgUrlList);
                    selector.start(ComplaintOthersAty.this, REQUEST_IMAGE);
                } else {
                    RxToast.showToast("图片证据最多9张喔～");
                }

               /* RxToast.showToast("this is footer!");
                Matisse.from(ComplaintOthersAty.this)
                        .choose(MimeType.ofImage(), false)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, authority))
                        .maxSelectable(9)
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.dp120))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .theme(R.style.Matisse_MerriChat)

                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
*/

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && data != null) {
            ArrayList<String> resultStringList = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);

            if (resultStringList != null) {
                imgUrlList.clear();
                imgUrlList.addAll(resultStringList);
                complaintOthersImgAdapter.notifyDataSetChanged();
            }

        }
    }

    @OnClick({R.id.iv_back, R.id.tv_submit_area})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit_area:
                String complaintReason = etComplanitReason.getText().toString().trim();
                if (RxDataTool.isNullString(complaintReason)) {
                    RxToast.showToast("请输入举报原因！");
                    return;
                }
                if (getIntent().getFlags() == GroupSettingActivity.activityId) {
                    if (TextUtils.isEmpty(groupId)) {
                        GetToast.useString(cnt, "群id不能为空");
                        return;
                    }

                    for (int i = 0; i < imgUrlList.size(); i++) {
                        fileList.add(new File(imgUrlList.get(i)));

                    }

                    communityComplaints(complaintReason);
                } else if (getIntent().getIntExtra("activityId", -1) == MemberManagementActivity.activityId) {//群成员举报
                    if (userModel == null) {
                        RxToast.showToast("提交失败，请重试！");
                        return;
                    }
                    ComplaintOthersReportInfoModel complaintOthersReportInfoModel = new ComplaintOthersReportInfoModel();
                    complaintOthersReportInfoModel.setClientMemberName(userModel.getRealname());
                    complaintOthersReportInfoModel.setClientPhone(userModel.getMobile());
                    complaintOthersReportInfoModel.setClientMemberId(userModel.getMemberId());
                    complaintOthersReportInfoModel.setBeTipOffMemberName(hisNickName);
                    complaintOthersReportInfoModel.setBeTipOffPhone(hisPhone);
                    complaintOthersReportInfoModel.setBeTipOffMemberId(hisMemberId);
                    complaintOthersReportInfoModel.setBeTipOffContent(complaintReason);
                    complaintOthersReportInfoModel.setBeTipOffType(typeName);
                    String toJson = gson.toJson(complaintOthersReportInfoModel);
                    for (int i = 0; i < imgUrlList.size(); i++) {
                        fileList.add(new File(imgUrlList.get(i)));

                    }
                    reportingAudit(toJson);
                } else {
                    if (userModel == null || hisHomeModelData == null) {
                        RxToast.showToast("提交失败，请重试！");
                        return;
                    }
                    HisHomeModel.DataBean.HisMemberInfoBean hisMemberInfo = hisHomeModelData.getHisMemberInfo();
                    ComplaintOthersReportInfoModel complaintOthersReportInfoModel = new ComplaintOthersReportInfoModel();
                    complaintOthersReportInfoModel.setClientMemberName(userModel.getRealname());
                    complaintOthersReportInfoModel.setClientPhone(userModel.getMobile());
                    complaintOthersReportInfoModel.setClientMemberId(userModel.getMemberId());
                    complaintOthersReportInfoModel.setBeTipOffMemberName(hisMemberInfo.getNickName());
                    complaintOthersReportInfoModel.setBeTipOffPhone(hisMemberInfo.getMobile());
                    complaintOthersReportInfoModel.setBeTipOffMemberId(hisMemberInfo.getMemberId());
                    complaintOthersReportInfoModel.setBeTipOffContent(complaintReason);
                    complaintOthersReportInfoModel.setBeTipOffType(typeName);
                    String toJson = gson.toJson(complaintOthersReportInfoModel);
                    for (int i = 0; i < imgUrlList.size(); i++) {
                        fileList.add(new File(imgUrlList.get(i)));

                    }
                    reportingAudit(toJson);
                }
                break;
        }
    }


    /**
     * 群投诉
     */
    private void communityComplaints(String complaintReason) {
        OkGo.<String>post(Urls.communityComplaints)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("cid", groupId)
                .params("complaintType", typeName)
                .params("reason", complaintReason)
                .addFileParams("file", fileList)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "举报成功");
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

    private void reportingAudit(String toJson) {
        OkGo.<String>post(Urls.reportingAudit)//
                .tag(this)//
                .addFileParams("file", fileList)
                .params("jsonReportInfo", toJson)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    RxToast.showToast(jsonObjectEx.optJSONObject("data").optString("info"));
                                } else {
                                    RxToast.showToast(jsonObjectEx.optString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }
}
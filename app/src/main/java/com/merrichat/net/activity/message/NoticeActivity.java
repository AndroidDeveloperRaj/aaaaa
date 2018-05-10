package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.setting.IdentityVerificationAty;
import com.merrichat.net.activity.setting.IdentityVerificationSuccessAty;
import com.merrichat.net.adapter.NoticeAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.NoticeModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.view.PopupList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/14.
 * 消息----通知列表
 */

public class NoticeActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private NoticeAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<NoticeModel> list;

    private float mRawX;
    private float mRawY;
    private List<String> popupMenuItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_NOTICE_ATY) {//刷新消息列表的广播
            oneInfoNotice();
        }
    }

    private void initView() {
        PrefAppStore.setNoticeNumNew(cnt, 0);
        tvTitleText.setText("通知");
        list = new ArrayList<>();
        List<NoticeModel> queryList = NoticeModel.getListNoticeModel(UserModel.getUserModel().getMemberId());
        list.addAll(queryList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoticeAdapter(cnt, list);
        recyclerView.setAdapter(adapter);
        popupMenuItemList.add("删除");
        oneInfoNotice();

        adapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
            @Override
            public void onItemLongOnclick(View view, int position) {
                PopupList popupList = new PopupList(view.getContext());
                popupList.showPopupListWindow(view, position, mRawX, mRawY, popupMenuItemList, new PopupList.PopupListListener() {

                    @Override
                    public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                        return true;
                    }

                    @Override
                    public void onPopupListClick(View contextView, int contextPosition, int position) {
                        NoticeModel.deleteOneNoticeModel(list.get(contextPosition));
                        list.remove(contextPosition);
                        adapter.notifyDataSetChanged();
                        GetToast.useString(cnt, "删除成功");
                    }
                });
            }

            @Override
            public void OnTouchListenerOnclick(int position, MotionEvent motionEvent) {
                mRawX = motionEvent.getRawX();
                mRawY = motionEvent.getRawY();
            }

            @Override
            public void OnBindClick(NoticeModel noticeModel) {
                if (!TextUtils.isEmpty(noticeModel.getAccountStatus())) {
                    String accountStatus = noticeModel.getAccountStatus();
                    if ("1".equals(accountStatus)) {//1 未绑定  2 未认证
                        startActivity(new Intent(cnt, BindThirdAty.class));
                    } else if ("2".equals(accountStatus)) {
                        queryRealNameVerfyStatus();
                    }
                }

            }
        });
    }

    /**
     * 查询实名认证状态
     */
    private void queryRealNameVerfyStatus() {
        OkGo.<String>post(Urls.queryRealNameVerfyStatus)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("accountId", UserModel.getUserModel().getAccountId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                //状态码0:尚未实名认证 1:已实名认证
                                int realNameStatus = jsonObject.optJSONObject("data").optInt("realNameStatus");
                                switch (realNameStatus) {
                                    case 0:
                                        startActivity(new Intent(cnt, IdentityVerificationAty.class));
                                        break;
                                    case 1:
                                        RxActivityTool.skipActivity(NoticeActivity.this, IdentityVerificationSuccessAty.class);
                                        break;
                                }
                            } else {
                                String error_msg = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(NoticeActivity.this, error_msg);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finishThisActivity();
                break;
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishThisActivity();
    }


    private void finishThisActivity() {
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
        EventBus.getDefault().post(myEventBusModel);
        finish();
    }

    /**
     * 查询通知列表
     */
    private void oneInfoNotice() {
        OkGo.<String>post(Urls.oneInfoNotice)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberMobile", UserModel.getUserModel().getMobile())
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    Gson gson = new Gson();
                                    JSONArray noticeList = data.optJSONArray("list");
                                    for (int i = 0; i < noticeList.length(); i++) {
                                        NoticeModel model = gson.fromJson(noticeList.get(i).toString(), NoticeModel.class);
                                        model.setMemberId(UserModel.getUserModel().getMemberId());
                                        list.add(0, model);
                                        NoticeModel.setNoticeModel(model);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

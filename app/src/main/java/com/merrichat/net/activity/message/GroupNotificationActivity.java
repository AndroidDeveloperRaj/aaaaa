package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.GroupNotificationAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupNotificationModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.GetToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/3/1.
 * 群通知
 */

public class GroupNotificationActivity extends MerriActionBarActivity implements GroupNotificationAdapter.OnClickCallBack, GroupNotificationAdapter.onSwipeListener, GroupNotificationAdapter.OnPhotoClickListener, GroupNotificationAdapter.onRefuseListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private GroupNotificationAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<GroupNotificationModel> groupNotificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notification);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("群通知");
        setLeftBack();
    }

    private void initView() {
        PrefAppStore.setGroupNumNew(cnt,0);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        groupNotificationList = new ArrayList<>();
        adapter = new GroupNotificationAdapter(R.layout.item_group_notification, groupNotificationList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickCallBack(this);
        adapter.setOnDelListener(this);
        adapter.setPhotoOnClick(this);
        adapter.setOnRefuseListener(this);
        communityApplyFor();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishThisActivity();
    }


    private void finishThisActivity(){
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
        EventBus.getDefault().post(myEventBusModel);
        finish();
    }

    /**
     * .加群申请列表
     */
    private void communityApplyFor() {
        OkGo.<String>post(Urls.communityApplyFor)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            groupNotificationList.clear();
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    Gson gson = new Gson();
                                    JSONArray list = data.optJSONArray("list");
                                    for (int i = 0; i < list.length(); i++) {
                                        GroupNotificationModel model = gson.fromJson(list.get(i).toString(), GroupNotificationModel.class);
                                        groupNotificationList.add(model);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void onTongYiClick(int pos) {
        String proposer = groupNotificationList.get(pos).getMemberId() + "";
        String communityId = groupNotificationList.get(pos).getCommunityId();
        String id = groupNotificationList.get(pos).getId();
        isAgreeJoinRequest(proposer, communityId, id, "1");
    }

    @Override
    public void onDel(int pos) {
        String proposer = groupNotificationList.get(pos).getMemberId() + "";
        String communityId = groupNotificationList.get(pos).getCommunityId();
        String id = groupNotificationList.get(pos).getId();
        isAgreeJoinRequest(proposer, communityId, id, "2");
    }

    @Override
    public void onPhotoOClick(int pos) {
        startActivity(new Intent(cnt, HisYingJiAty.class)
                .putExtra("hisMemberId", groupNotificationList.get(pos).getMemberId())
                .putExtra("hisImgUrl", groupNotificationList.get(pos).getHeadImgUrl())
                .putExtra("hisNickName", groupNotificationList.get(pos).getMemberName()));
    }


    @Override
    public void onRefuse(int pos) {
        String proposer = groupNotificationList.get(pos).getMemberId() + "";
        String communityId = groupNotificationList.get(pos).getCommunityId();
        String id = groupNotificationList.get(pos).getId();
        isAgreeJoinRequest(proposer, communityId, id, "0");
    }


    /**
     * 是否同意加群申请
     *
     * @param isJoin 状态0:拒绝 1:同意 2:删除
     */
    private void isAgreeJoinRequest(String proposer, String communityId, String id, final String isJoin) {
        OkGo.<String>post(Urls.isAgreeJoinRequest)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("id", id)
                .params("proposer", proposer)
                .params("communityId", communityId)
                .params("isJoin", isJoin)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    communityApplyFor();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                    communityApplyFor();
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


}

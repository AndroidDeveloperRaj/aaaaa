package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.adapter.NewFriendsAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.NewFriendsModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DrawableCenterTextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/14.
 * 联系人---新的朋友
 */

public class NewFriendsActivity extends BaseActivity implements NewFriendsAdapter.OnClickCallBack, NewFriendsAdapter.onSwipeListener, NewFriendsAdapter.OnItemListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    /**
     * 空白页
     */
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private NewFriendsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<NewFriendsModel.DataBean.InvitationRecordsBean> newFriendsList;
    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvTitleText.setText("新的好友");
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        newFriendsList = new ArrayList<>();
        adapter = new NewFriendsAdapter(R.layout.item_new_friends, newFriendsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickCallBack(this);
        adapter.setOnItemListener(this);
        adapter.setOnDelListener(this);
        inviteFriendsRecord();
    }

    private void inviteFriendsRecord() {
        OkGo.<String>post(Urls.inviteFriendsRecord)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("pageNum", pageNum + "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        body:{"data":{"invitationRecords":[{"createTime":1512195892865,"flag":0,"id":315500058763264,"inviteMemberId":302472807890945,"inviteMemberName":"潇潇","inviteMemberUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","isValid":0,"source":"","toInviteMemberId":302466141044737,"toInviteMemberName":"老鳖","toInviteMemberUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg"}]},"success":true}
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                NewFriendsModel newFriendsModel = new Gson().fromJson(response.body(), NewFriendsModel.class);
                                newFriendsList.addAll(newFriendsModel.getData().getInvitationRecords());
                            }
                            if (newFriendsList.size() > 0) {
                                tvEmpty.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tvEmpty.setVisibility(View.VISIBLE);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });

    }

    @OnClick({R.id.iv_back, R.id.tv_empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_empty:
                inviteFriendsRecord();
                break;
        }
    }

    @Override
    public void onTongYiClick(int position) {
        agreeFriendsRequest(position);
    }

    @Override
    public void onDel(int pos) {
        deleteFriendsRequest(pos);
    }

    /**
     * 同意——接口
     *
     * @param position
     */
    private void agreeFriendsRequest(final int position) {
        OkGo.<String>post(Urls.agreeFriendsRequest)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("memberUrl", UserModel.getUserModel().getImgUrl())
                .params("toMemberId", newFriendsList.get(position).getInviteMemberId())
                .params("toMemberName", newFriendsList.get(position).getInviteMemberName())
                .params("toMemberUrl", newFriendsList.get(position).getInviteMemberUrl())
                .params("id", newFriendsList.get(position).getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                newFriendsList.get(position).setIsValid(1);
                                adapter.notifyItemChanged(position);
                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                myEventBusModel.REFRESH_FRIENDS_LIST = true;
                                EventBus.getDefault().post(myEventBusModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 删除添加好友请求
     *
     * @param position
     */
    private void deleteFriendsRequest(final int position) {
        OkGo.<String>post(Urls.deleteFriendsRequest)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("id", newFriendsList.get(position).getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                newFriendsList.remove(position);
                                adapter.notifyItemRemoved(position);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.REFRESH_FRIENDS_LIST = true;
        EventBus.getDefault().post(myEventBusModel);
        super.onBackPressed();
    }


    @Override
    public void onItemListener(int position) {
        startActivity(new Intent(cnt, HisYingJiAty.class)
                .putExtra("hisMemberId", newFriendsList.get(position).getInviteMemberId())
                .putExtra("hisImgUrl", newFriendsList.get(position).getInviteMemberUrl())
                .putExtra("hisNickName", newFriendsList.get(position).getInviteMemberName()));
    }
}

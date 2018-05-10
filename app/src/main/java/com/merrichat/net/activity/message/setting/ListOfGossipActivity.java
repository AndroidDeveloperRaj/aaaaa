package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.ListOfGossipAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ListOfGossipModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/31.
 * 禁言名单
 */

public class ListOfGossipActivity extends MerriActionBarActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ListOfGossipAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<ListOfGossipModel> listOfGossipList;

    /**
     * 群id
     */
    private String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_gossip);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("禁言名单");
        setLeftBack();
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        listOfGossipList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListOfGossipAdapter(cnt, listOfGossipList,groupId);
        recyclerView.setAdapter(adapter);

        bannedMemberIdList();
    }

    /**
     * 禁言名单
     */
    private void bannedMemberIdList() {
        OkGo.<String>post(Urls.bannedMemberIdList)
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
                                    JSONArray list = data.optJSONArray("list");
                                    Gson gson = new Gson();
                                    for (int i = 0; i < list.length(); i++) {
                                        ListOfGossipModel model = gson.fromJson(list.get(i).toString(), ListOfGossipModel.class);
                                        listOfGossipList.add(model);
                                    }
                                }else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)){
                                        GetToast.useString(cnt,message);
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
}

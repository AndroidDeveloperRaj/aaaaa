package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.PraiseAndCommentAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.PraiseAndCommentModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.GetToast;

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
 * 消息---赞和评论
 */

public class PraiseAndCommentActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private PraiseAndCommentAdapter adapter;

    private List<PraiseAndCommentModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praise_and_comment);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        PrefAppStore.setZanPingLunNum(cnt,0);
        tvTitleText.setText("赞和评论");
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PraiseAndCommentAdapter(cnt, list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PraiseAndCommentAdapter.OnItemClickListener() {
            @Override
            public void huiFuOnitemClick(int position) {
                Intent intent = new Intent(cnt, ReplyToCommentActivity.class);
                intent.putExtra("contentId", list.get(position).getContentId());
                intent.putExtra("name", list.get(position).getName());
                intent.putExtra("replyMemberId", list.get(position).getMemberId());
                intent.putExtra("commentId", list.get(position).getCommentId());
                intent.putExtra("replyCommentId", list.get(position).getRevertId());
                startActivity(intent);
            }
        });

        queryNoticeList();
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

    private void finishThisActivity(){
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
        EventBus.getDefault().post(myEventBusModel);
        finish();
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_PRAISE_COMMENT) {
            list.clear();
            queryNoticeList();
        }

    }

    /**
     * 查询赞和评论列表
     */
    private void queryNoticeList() {
        OkGo.<String>post(Urls.queryNoticeList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("name", UserModel.getUserModel().getRealname())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            list.clear();
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    Gson gson = new Gson();
                                    JSONArray jsonArray = data.optJSONArray("list");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        PraiseAndCommentModel model = gson.fromJson(jsonArray.get(i).toString(), PraiseAndCommentModel.class);
                                        list.add(model);
                                    }
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

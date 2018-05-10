package com.merrichat.net.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.SearchFriendsAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.SearchFriendsModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/2/27.
 * 搜索好友
 */

public class SearchFriendsActivity extends BaseActivity {

    @BindView(R.id.edit_search)
    EditText editSearch;

    @BindView(R.id.tv_cancle)
    TextView tvCancle;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<SearchFriendsModel> list;

    private LinearLayoutManager layoutManager;

    private SearchFriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchFriendsAdapter(R.layout.item_my_friends, list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SearchFriendsModel model = list.get(position);
                Intent intent = new Intent(cnt, SingleChatActivity.class);
                intent.putExtra("receiverMemberId", model.getMemberId());
                intent.putExtra("receiverHeadUrl", model.getImgUrl());
                intent.putExtra("receiverName", model.getNickName());
                startActivity(intent);
            }
        });


        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) editSearch.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchFriendsActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchName = editSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchName)) {
                        queryAllPeople(searchName);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 查询好友
     */
    private void queryAllPeople(final String searchName) {
        OkGo.<String>post(Urls.queryAllPeople)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("name", searchName)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            list.clear();
                            if (jsonObject.optBoolean("success")) {
                                Gson gson = new Gson();
                                JSONObject data = jsonObject.optJSONObject("data");
                                JSONArray memberInfo = data.optJSONArray("memberInfo");
                                for (int i = 0; i < memberInfo.length(); i++) {
                                    SearchFriendsModel model = gson.fromJson(memberInfo.get(i).toString(), SearchFriendsModel.class);
                                    list.add(model);
                                }
                            }
                            adapter.setKeyWord(searchName);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @OnClick({R.id.tv_cancle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancle:
                finish();
                break;
        }
    }
}

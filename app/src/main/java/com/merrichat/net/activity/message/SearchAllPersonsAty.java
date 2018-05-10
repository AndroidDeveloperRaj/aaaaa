package com.merrichat.net.activity.message;

import android.content.Context;
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
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.SearchAllPersonsAdapter;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.SearchFriendsModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/3/8.
 * <p>
 * 找人--全部注册用户中搜索
 */

public class SearchAllPersonsAty extends MerriActionBarActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    ArrayList<SearchFriendsModel> searchAllPersonsList;
    private SearchAllPersonsAdapter searchAllPersonsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all_person);
        ButterKnife.bind(this);
        setTitleGone();
        initView();
    }

    private void initView() {
        searchAllPersonsList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        searchAllPersonsAdapter = new SearchAllPersonsAdapter(R.layout.item_my_friends, searchAllPersonsList);
        recyclerView.setAdapter(searchAllPersonsAdapter);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) editSearch.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchAllPersonsAty.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchName = editSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchName)) {
                        queryAllUser(searchName);
                    }
                }
                return false;
            }
        });
        searchAllPersonsAdapter.setOnItemClickListener(this);
    }

    /**
     * 查询好友
     */
    private void queryAllUser(final String searchName) {
        OkGo.<String>post(Urls.queryAllUser)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("name", searchName)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            searchAllPersonsList.clear();
                            if (jsonObject.optBoolean("success")) {
                                Gson gson = new Gson();
                                JSONObject data = jsonObject.optJSONObject("data");
                                JSONArray memberInfo = data.optJSONArray("memberInfo");
                                if (memberInfo != null && memberInfo.length() > 0) {

                                    for (int i = 0; i < memberInfo.length(); i++) {
                                        SearchFriendsModel model = gson.fromJson(memberInfo.get(i).toString(), SearchFriendsModel.class);
                                        searchAllPersonsList.add(model);
                                    }
                                } else {
                                    RxToast.showToast("未查询到该用户，请重试！");
                                }
                            }
                            searchAllPersonsAdapter.setKeyWord(searchName);
                            searchAllPersonsAdapter.notifyDataSetChanged();

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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        String hisMemberId = searchAllPersonsList.get(position).getMemberId();
        if (hisMemberId.equals(UserModel.getUserModel().getMemberId())) {
            RxActivityTool.skipActivity(this, MyDynamicsAty.class);
        } else {
            bundle.putLong("hisMemberId", Long.parseLong(hisMemberId));
            bundle.putString("hisImgUrl", searchAllPersonsList.get(position).getImgUrl());
            bundle.putString("hisNickName", searchAllPersonsList.get(position).getNickName());
            RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle);
        }
    }
}

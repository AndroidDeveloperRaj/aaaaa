package com.merrichat.net.activity.message.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.groupmarket.GroupAdminstorActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.GroupMembersListAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMembersModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/22.
 * 群成员列表
 */

public class GroupMembersListActivity extends MerriActionBarActivity implements OnRefreshListener, OnLoadmoreListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SmartRefreshLayout swipeRefreshLayout;

    @BindView(R.id.et_search)
    EditText etSearch;

    /**
     * 下拉刷新
     */
    private int PullDownRefresh = 1;

    /**
     * 上拉加载更多
     */
    private int PullupLoading = 2;


    /**
     * 下拉刷新或是上拉加载数据
     */
    private int REFESH_TYPE;

    private LinearLayoutManager layoutManager;
    private GroupMembersListAdapter adapter;
    private List<GroupMembersModel> groupMembersList = new ArrayList<GroupMembersModel>();

    /**
     * 搜索条件
     */
    private String searchData = "";


    /**
     * 每页条数
     */
    private int pageSize = 10;

    /**
     * 当前页
     */
    private int pageNum = 1;
    private String groupId = "";


    /**
     * 1:查询所有群成员，2：过滤群主和自己,3:过滤群主管理员和自己 4:过滤被禁言的和群主 5:过滤被禁言的和管理员和群主还有自己
     */
    private String type = "1";


    /**
     * 是否搜索过 默认false
     * 如果对关键字（姓名、电话）搜索过。 则当搜索框清空时 回归到原来的状态
     */
    private boolean isSearch  = false;


    /**
     * 是否是管理员 0：成员 1：管理员 2：群主
     */
    private int isMaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle("群成员列表");
        setLeftBack();
        groupId = getIntent().getStringExtra("groupId");
        isMaster = getIntent().getIntExtra("isMaster",0);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupMembersListAdapter(cnt, groupMembersList);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
        //查询群成员
        REFESH_TYPE = PullDownRefresh;
        queryAllMember(REFESH_TYPE);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) etSearch.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(GroupMembersListActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchNamePhone= etSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchNamePhone)) {
                        isSearch = true;
                        pageNum = 1;
                        REFESH_TYPE = PullDownRefresh;
                        searchData = searchNamePhone;
                        queryAllMember(REFESH_TYPE);
                    }
                }
                return false;
            }
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isSearch&&TextUtils.isEmpty(etSearch.getText().toString())){
                    isSearch = false;
                    pageNum = 1;
                    searchData = "";
                    REFESH_TYPE = PullDownRefresh;
                    queryAllMember(REFESH_TYPE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        adapter.setOnItemClickListener(new GroupMembersListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GroupMembersModel model = groupMembersList.get(position);
                if (model.getMemberId().equals(UserModel.getUserModel().getMemberId())) {
                    startActivity(new Intent(cnt, MyDynamicsAty.class));
                } else {
                    if (isMaster == 2) {//群主身份
                        startActivity(new Intent(cnt, MemberManagementActivity.class).putExtra("groupId", groupId).putExtra("communityMemberId", model.getMemberId()).putExtra("hisPhone", model.getPhone()).putExtra("headImgUrl", model.getHeadImgUrl()).putExtra("memberName", model.getMemberName()).putExtra("type", 1));
                    } else if (isMaster == 1) {//管理员身份
                        if (model.getIsMater() == 2) {//管理员点击群主头像--进入群主主页
                            Intent intent1 = new Intent(cnt, HisYingJiAty.class);
                            intent1.putExtra("hisMemberId", Long.parseLong(model.getMemberId()));
                            intent1.putExtra("hisImgUrl", model.getHeadImgUrl());
                            intent1.putExtra("hisNickName", model.getMemberName());
                            intent1.putExtra("hisPhone", model.getPhone());
                            startActivity(intent1);
                        } else if (model.getIsMater() == 1) {//管理员点击管理员头像---进入成员管理--只有部分权限
                            startActivity(new Intent(cnt, MemberManagementActivity.class).putExtra("groupId", groupId).putExtra("communityMemberId", model.getMemberId()).putExtra("hisPhone", model.getPhone()).putExtra("headImgUrl", model.getHeadImgUrl()).putExtra("memberName", model.getMemberName()).putExtra("type", 0));
                        } else {//管理员点击群成员头像---进入成员管理---拥有全部权限
                            startActivity(new Intent(cnt, MemberManagementActivity.class).putExtra("groupId", groupId).putExtra("communityMemberId", model.getMemberId()).putExtra("hisPhone", model.getPhone()).putExtra("headImgUrl", model.getHeadImgUrl()).putExtra("memberName", model.getMemberName()).putExtra("type", 1));
                        }
                    }else if (isMaster==0){//群成员身份
                        Intent intent = new Intent(cnt, HisYingJiAty.class);
                        intent.putExtra("hisMemberId", Long.parseLong(model.getMemberId()));
                        intent.putExtra("hisImgUrl", model.getHeadImgUrl());
                        intent.putExtra("hisNickName", model.getMemberName());
                        intent.putExtra("hisPhone", model.getPhone());
                        startActivity(intent);
                    }
                }
            }
        });


    }

    /**
     * 分页查询群成员列表
     * @param REFESH_TYPE
     */
    private void queryAllMember(final int REFESH_TYPE) {
        OkGo.<String>post(Urls.queryAllMember)
                .tag(this)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("searchData", searchData)
                .params("pageSize", pageSize)
                .params("pageNum", pageNum)
                .params("type", type)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                if (REFESH_TYPE == PullDownRefresh) {
                                    groupMembersList.clear();
                                    swipeRefreshLayout.finishRefresh();
                                } else if (REFESH_TYPE == PullupLoading) {
                                    swipeRefreshLayout.finishLoadmore();
                                }
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        Gson gson = new Gson();
                                        JSONArray list = data.optJSONArray("list");
                                        for (int i = 0; i < list.length(); i++) {
                                            GroupMembersModel groupMembersModel = gson.fromJson(list.get(i).toString(),GroupMembersModel.class);
                                            groupMembersList.add(groupMembersModel);
                                        }
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        String message = data.optString("message");
                                        GetToast.useString(cnt,message);
                                    }

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (REFESH_TYPE == PullDownRefresh) {
                                    swipeRefreshLayout.finishRefresh();
                                } else if (REFESH_TYPE == PullupLoading) {
                                    swipeRefreshLayout.finishLoadmore();
                                }

                            }

                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        if (REFESH_TYPE == PullDownRefresh) {
                            groupMembersList.clear();
                            swipeRefreshLayout.finishRefresh();
                        } else if (REFESH_TYPE == PullupLoading) {
                            swipeRefreshLayout.finishLoadmore();
                        }
                    }
                });
    }

    /**
     * 下拉刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum = 1;
        REFESH_TYPE = PullDownRefresh;
        queryAllMember(REFESH_TYPE);

    }

    /**
     * 上拉加载更多
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageNum++;
        REFESH_TYPE = PullupLoading;
        queryAllMember(REFESH_TYPE);
    }
}

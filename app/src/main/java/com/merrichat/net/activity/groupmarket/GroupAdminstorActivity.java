package com.merrichat.net.activity.groupmarket;

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

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.DesignateGagAdapter;
import com.merrichat.net.adapter.GroupHeaderAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMembersModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
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

/**
 * 设置群管理员
 * Created by amssy on 18/1/23.
 */

public class GroupAdminstorActivity extends MerriActionBarActivity implements OnRefreshListener, OnLoadmoreListener {

    @BindView(R.id.rc_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SmartRefreshLayout swipeRefreshLayout;


    /**
     * 选择的人的头像列表
     */
    @BindView(R.id.recycler_view_header)
    RecyclerView recyclerViewHeader;
    /**
     * 搜索
     */
    @BindView(R.id.editText_search)
    EditText editTextSearch;

    private DesignateGagAdapter adapter;
    private LinearLayoutManager layoutManager;


    /**
     * 接口返回全部数据
     */
    private List<GroupMembersModel> groupMembersList;


    /**
     * 接口返回的管理员数据集合
     */
    private List<GroupMembersModel> administratorList;


    /**
     * 选中设置为管理员的数据
     */
    private List<GroupMembersModel> checkedList;


    private GroupHeaderAdapter headerAdapter;
    private int screenWith = 0;  //屏幕宽度
    private LinearLayoutManager linearLayoutManager;


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

    /**
     * 每页条数
     */
    private int pageSize = 10;

    /**
     * 当前页
     */
    private int pageNum = 1;

    /**
     * 群id
     */
    private String groupId = "";


    /**
     * 1:查询所有群成员，2：过滤群主和自己,3:过滤群主管理员和自己 4:过滤被禁言的和群主 5:过滤被禁言的和管理员和群主还有自己
     */
    private String type = "2";


    /**
     * 之前成员列表给返回的管理员json型数组字符串[{"id":34234234},{"id":23423124}]
     */
    private String beforeJson = "";


    /**
     * 设置成管理员的json型数组字符串[{"id":1231231},{"id":23423124}]
     */
    private String jsonStr = "";

    /**
     * adapter是共用的
     * 指定禁言用的adapter为1
     * 设置管理员用的adapter为2
     */
    private int sourceFlag = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_adminstor);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        setTitle("设置群管理员");
        setLeftBack();
        setRightText("确定", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBeforeJson();
            }
        });
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        screenWith = this.getWindowManager().getDefaultDisplay().getWidth();
        initRecyclerView();
        initRecyclerViewHeader();
        //查询群成员
        REFESH_TYPE = PullDownRefresh;
        queryAllMember(REFESH_TYPE,"");

        adapter.setOnItemClickListener(new DesignateGagAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (groupMembersList.get(position).getIsMater() == 1) {
                    GroupMembersModel listBean = groupMembersList.get(position);
                    listBean.setIsMater(0);
                    checkedList.remove(listBean);
                    headerAdapter.notifyDataSetChanged();
                } else {
                    GroupMembersModel listBean = groupMembersList.get(position);
                    listBean.setIsMater(1);
                    checkedList.add(listBean);
                    headerAdapter.notifyDataSetChanged();
                }

                if (editTextSearch.getLayoutParams().height * (groupMembersList.size()) > screenWith * 0.7) {
                    recyclerViewHeader.getLayoutParams().width = (int) (screenWith * 0.7);
                    editTextSearch.getLayoutParams().width = (int) (screenWith * 0.3);
                } else {
                    recyclerViewHeader.getLayoutParams().width = editTextSearch.getLayoutParams().height * (groupMembersList.size());
                    editTextSearch.getLayoutParams().width = screenWith - editTextSearch.getLayoutParams().height * (groupMembersList.size());
                }

                if (checkedList.size() > 0) {
                    bt_right.setText("确定(" + checkedList.size() + ")");
                } else {
                    bt_right.setText("确定");
                }
                if (checkedList.size() > 0) {
                    linearLayoutManager.scrollToPosition(checkedList.size() - 1);
                }
                adapter.notifyDataSetChanged();
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) editTextSearch.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(GroupAdminstorActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchData = editTextSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchData)) {
                        pageNum = 1;
                        REFESH_TYPE = PullDownRefresh;
                        queryAllMember(REFESH_TYPE,searchData);
                    }
                }
                return false;
            }
        });
    }


    private void initRecyclerView() {
        groupMembersList = new ArrayList<>();
        administratorList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DesignateGagAdapter(cnt, groupMembersList, sourceFlag);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
    }

    private void initRecyclerViewHeader() {
        checkedList = new ArrayList<>();
        //设置布局管理器
        linearLayoutManager = new LinearLayoutManager(GroupAdminstorActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewHeader.setLayoutManager(linearLayoutManager);
        headerAdapter = new GroupHeaderAdapter(GroupAdminstorActivity.this, checkedList);
        recyclerViewHeader.setAdapter(headerAdapter);

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
        queryAllMember(REFESH_TYPE,"");
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
        queryAllMember(REFESH_TYPE,"");
    }

    /**
     * 分页查询群成员列表
     *
     * @param REFESH_TYPE
     */
    private void queryAllMember(final int REFESH_TYPE,String searchData) {
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
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.optBoolean("success")) {
                                    JSONObject data = jsonObject.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        Gson gson = new Gson();
                                        JSONArray list = data.optJSONArray("list");
                                        for (int i = 0; i < list.length(); i++) {
                                            GroupMembersModel groupMembersModel = gson.fromJson(list.get(i).toString(), GroupMembersModel.class);
                                            groupMembersList.add(groupMembersModel);
                                        }

                                        filterTheData();
                                    } else {
                                        String message = data.optString("message");
                                        GetToast.useString(cnt, message);
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
     * 过滤数据
     */
    private void filterTheData() {
        administratorList.clear();
        checkedList.clear();
        for (int i = 0; i < groupMembersList.size(); i++) {
            if (groupMembersList.get(i).getIsMater() == 1) {
                administratorList.add(groupMembersList.get(i));
                checkedList.add(groupMembersList.get(i));
            }
        }
        headerAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }


    /**
     * 获取返回的群成员列表中的管理员数据
     */
    private void getBeforeJson() {
        beforeJson = "";
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < administratorList.size(); i++) {
            GroupMembersModel listBean = administratorList.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", listBean.getMemberId());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        beforeJson = jsonArray.toString();
        getJsonStr();
    }

    /**
     * 获取当前设置的管理员数据
     */
    private void getJsonStr() {
        jsonStr = "";
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < checkedList.size(); i++) {
            GroupMembersModel listBean = checkedList.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", listBean.getMemberId());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonStr = jsonArray.toString();
        batchSetMaster();
    }

    /**
     * 批量设置管理员
     */
    private void batchSetMaster() {
        OkGo.<String>post(Urls.batchSetMaster)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("beforeJson", beforeJson)
                .params("jsonStr", jsonStr)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "设置成功");
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

}

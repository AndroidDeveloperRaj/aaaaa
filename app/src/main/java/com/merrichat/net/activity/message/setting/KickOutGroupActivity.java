package com.merrichat.net.activity.message.setting;

import android.content.Context;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.GroupHeaderAdapter;
import com.merrichat.net.adapter.KickOutGroupAdapter;
import com.merrichat.net.app.MyEventBusModel;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/2/3.
 * 踢出群
 */

public class KickOutGroupActivity extends MerriActionBarActivity implements OnRefreshListener, OnLoadmoreListener {

    @BindView(R.id.recycler_view)
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

    private KickOutGroupAdapter adapter;
    private LinearLayoutManager layoutManager;


    /**
     * 接口返回全部数据
     */
    private List<GroupMembersModel> groupMembersList;


    private GroupHeaderAdapter headerAdapter;
    private int screenWith = 0;  //屏幕宽度
    private LinearLayoutManager linearLayoutManager;


    /**
     * 选择要踢出去的人的数据
     */
    private List<GroupMembersModel> checkedList;


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
    private String type = "";


    /**
     *加人或者踢人的memberId json类型的集合字符串[{"id":1213231},{"id":123123123}]
     */
    private String memberJson = "";


    /**
     * 类型1：加人 2：踢人
     */
    private int addOrKockoutType = 2;


    /**
     * 是否是管理员 0：成员 1：管理员 2：群主
     * 此页面为踢人页面  只有群主和管理员才能进来
     * 当身份为群主时候： type为2  过滤掉群主和自己
     * 当身份为管理员时候： type为3  过滤掉群主和自己和管理员
     */
    private int isMaster;



    /**
     * 是否搜索过 默认false
     * 如果对关键字（姓名、电话）搜索过。 则当搜索框清空时 回归到原来的状态
     */
    private boolean isSearch  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kick_out_group);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        setTitle("踢出群");
        setLeftBack();
        setRightText("确定", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedList.size()==0){
                    GetToast.useString(cnt,"请选择要踢的群成员");
                    return;
                }
                kickOutGroup();
            }
        });
    }

    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        isMaster = getIntent().getIntExtra("isMaster",0);
        if (isMaster==2){
            type = "2";
        }else {
            type = "3";
        }
        screenWith = this.getWindowManager().getDefaultDisplay().getWidth();
        initRecyclerView();
        initRecyclerViewHeader();
        //查询群成员
        REFESH_TYPE = PullDownRefresh;
        queryAllMember(REFESH_TYPE);




        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) editTextSearch.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(KickOutGroupActivity.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchNamePhone= editTextSearch.getText().toString().trim();
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


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isSearch&&TextUtils.isEmpty(editTextSearch.getText().toString())){
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



        adapter.setOnItemClickListener(new KickOutGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (groupMembersList.get(position).isChecked()) {
                    GroupMembersModel listBean = groupMembersList.get(position);
                    listBean.setChecked(false);
                    checkedList.remove(listBean);
                    headerAdapter.notifyDataSetChanged();
                } else {
                    GroupMembersModel listBean = groupMembersList.get(position);
                    listBean.setChecked(true);
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
    }


    private void initRecyclerView() {
        groupMembersList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new KickOutGroupAdapter(cnt, groupMembersList);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setOnLoadmoreListener(this);
    }

    private void initRecyclerViewHeader() {
        checkedList = new ArrayList<>();
        //设置布局管理器
        linearLayoutManager = new LinearLayoutManager(KickOutGroupActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewHeader.setLayoutManager(linearLayoutManager);
        headerAdapter = new GroupHeaderAdapter(KickOutGroupActivity.this, checkedList);
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
        queryAllMember(REFESH_TYPE);
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
     * 分页查询群成员列表
     *
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
                                    } else {
                                        String message = data.optString("message");
                                        GetToast.useString(cnt, message);
                                    }
                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                }
                                adapter.notifyDataSetChanged();
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
     * 踢出群
     */
    private void kickOutGroup() {
        memberJson = "";
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i <checkedList.size() ; i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",checkedList.get(i).getMemberId());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        memberJson = jsonArray.toString();
        addOrKickMember();
    }

    /**
     * 群设置批量加人批量踢人(只能群主和管理员操作)
     */
    private void addOrKickMember(){
        OkGo.<String>post(Urls.addOrKickMember)
                .params("cid",groupId)
                .params("memberId",UserModel.getUserModel().getMemberId())
                .params("type",addOrKockoutType)
                .params("memberJson",memberJson)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")){
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")){
                                    GetToast.useString(cnt,"踢除成功");
                                    MyEventBusModel eventBusModel = new MyEventBusModel();
                                    eventBusModel.REFRESH_GROUP_SETTING = true;
                                    EventBus.getDefault().post(eventBusModel);
                                    finish();
                                }else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)){
                                        GetToast.useString(cnt,message);
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

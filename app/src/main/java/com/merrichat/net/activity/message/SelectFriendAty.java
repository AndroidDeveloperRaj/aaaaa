package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.contact.UpdateGroupInfoActivity;
import com.merrichat.net.activity.message.setting.GroupSettingActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.GroupAdminstorAdapter;
import com.merrichat.net.adapter.SelectFriendsAdapter;
import com.merrichat.net.adapter.SelectFriendsHeaderAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.model.GoodFriendModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.PinYinUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.weidget.SideBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 2018/1/25.
 * 选择联系人
 */

public class SelectFriendAty extends MerriActionBarActivity implements SelectFriendsAdapter.onGroupItemClickLinster {

    public static final int activityId = MiscUtil.getActivityId();

    @BindView(R.id.recycler_view)
    ListView recyclerView;
    /**
     *
     */
    @BindView(R.id.dialog)
    TextView dialog;
    /**
     * 字母索引
     */
    @BindView(R.id.sidrbar)
    SideBar sidrbar;
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

    private List<GoodFriendModel> listAdminstor;
    private SelectFriendsAdapter adapter;
    private List<GoodFriendModel> listUrl;
    private SelectFriendsHeaderAdapter headerAdapter;
    private PinyinComparator pinyinComparator;
    private int screenWith = 0;  //屏幕宽度
    private LinearLayoutManager linearLayoutManager;


    /**
     * 群id
     */
    private String groupId = "";
    /**
     * 类型1：加人 2：踢人
     */
    private int type;

    /**
     * 加人或者踢人的memberId json类型的集合字符串[{"id":1213231},{"id":123123123}]
     */
    private String memberJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        screenWith = this.getWindowManager().getDefaultDisplay().getWidth();
        //实例化汉字转拼音类
        pinyinComparator = new PinyinComparator();

        setLeftBack();
        setTitle("选择联系人");
        setRightText_("确定", getResources().getColor(R.color.ff9e9fab));

        if (getIntent().getFlags() == GroupSettingActivity.activityId) {
            groupId = getIntent().getStringExtra("groupId");
        }
        sidrbar.setTextView(dialog);
        // 设置右侧触摸监听
        sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    recyclerView.setSelection(position);
                }
            }
        });

//        //搜索按钮监听
//        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    //点击搜索执行操作
//                    int position = adapter.getPositionForSection(editTextSearch.getText().toString().charAt(0));
//                    if (position != -1) {
//                        recyclerView.setSelection(position);
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(editTextSearch.getText().toString().trim());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        listAdminstor = new ArrayList<>();
        initRecyclerView();
        initRecyclerViewHeader();
        getMyAllGoodFriendsList();
    }

    @OnClick({R.id.bt_right})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bt_right://确定
                if (listUrl.size() > 0) {
                    if (getIntent().getFlags() == GroupSettingActivity.activityId) {
                        type = 1;
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < listUrl.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("id", listUrl.get(i).goodFriendsId);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        memberJson = jsonArray.toString();
                        addOrKickMember();
                    } else {
                        String memberJson = "";
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < listUrl.size(); i++) {
                            JSONObject data = new JSONObject();
                            try {
                                data.put("id", listUrl.get(i).goodFriendsId);
                                data.put("name", listUrl.get(i).goodFriendsName);
                                data.put("phone", listUrl.get(i).mobile);
                                jsonArray.put(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        memberJson = jsonArray.toString();
                        startActivity(new Intent(cnt, UpdateGroupInfoActivity.class).putExtra("memberJson", memberJson));
                    }
                } else {
                    RxToast.showToast("请先选择联系人");
                }
                break;
        }
    }


    /**
     * 群设置批量加人批量踢人(只能群主和管理员操作)
     */
    private void addOrKickMember() {
        OkGo.<String>post(Urls.addOrKickMember)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("type", type)
                .params("memberJson", memberJson)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "添加成功");
                                    MyEventBusModel eventBusModel = new MyEventBusModel();
                                    eventBusModel.REFRESH_GROUP_SETTING = true;
                                    EventBus.getDefault().post(eventBusModel);
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

    private void initRecyclerView() {
        adapter = new SelectFriendsAdapter(SelectFriendAty.this, listAdminstor);
        recyclerView.setAdapter(adapter);
        adapter.setOnGroupItemClickLinster(this);
    }

    private void initRecyclerViewHeader() {
        listUrl = new ArrayList<>();
        //设置布局管理器
        linearLayoutManager = new LinearLayoutManager(SelectFriendAty.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewHeader.setLayoutManager(linearLayoutManager);
        headerAdapter = new SelectFriendsHeaderAdapter(SelectFriendAty.this, listUrl);
        recyclerViewHeader.setAdapter(headerAdapter);

    }

    /**
     * 数据排序
     */
    public static class PinyinComparator implements Comparator<GoodFriendModel> {
        public int compare(GoodFriendModel o1, GoodFriendModel o2) {
            if (TextUtils.isEmpty(o1.firstLetter) || TextUtils.isEmpty(o2.firstLetter))
                LogUtil.e("E: SortLetters is null");
            if (o1.firstLetter.equals("@") || o2.firstLetter.equals("#")) {
                return -1;
            } else if (o1.firstLetter.equals("#") || o2.firstLetter.equals("@")) {
                return 1;
            } else {
                return o1.firstLetter.compareTo(o2.firstLetter);
            }
        }
    }


    /**
     * 往实体类中添加首字符
     *
     * @param list
     */
    private void addSortLetter(List<GoodFriendModel> list) {
        Locale defloc = Locale.getDefault();
        for (int i = 0; i < list.size(); i++) {
            GoodFriendModel customerModel = list.get(i);
            String sitename = customerModel.goodFriendsName;
            if (TextUtils.isEmpty(sitename)) {
                sitename = "#";
            }
            String firstname = sitename.substring(0, 1);
            String str = PinYinUtil.getPinYinHeadChar(firstname);//名称的第一个字的拼音
            String str1 = str.toUpperCase(defloc);//转换成大写
            if (str1.compareTo("A") < 0 || str1.compareTo("z") > 0) {
                list.get(i).firstLetter = "#";//首字母非A-z的，设置首字母为大于z的值，使排序时可以排到最后
            } else {
                list.get(i).firstLetter = str1;
            }
        }
    }

    @Override
    public void onItemClick(int position,GoodFriendModel goodFriendModel) {
        if (!goodFriendModel.flag) {
            if (goodFriendModel.checked) {
                goodFriendModel.checked = false;
                listUrl.remove(goodFriendModel);
                headerAdapter.notifyDataSetChanged();
            } else {
                goodFriendModel.checked = true;
                listUrl.add(goodFriendModel);
                headerAdapter.notifyDataSetChanged();
            }
            if (editTextSearch.getLayoutParams().height * (listUrl.size()) > screenWith * 0.7) {
                recyclerViewHeader.getLayoutParams().width = (int) (screenWith * 0.7);
                editTextSearch.getLayoutParams().width = (int) (screenWith * 0.3);
            } else {
                recyclerViewHeader.getLayoutParams().width = editTextSearch.getLayoutParams().height * (listUrl.size());
                editTextSearch.getLayoutParams().width = screenWith - editTextSearch.getLayoutParams().height * (listUrl.size());
            }

            if (listUrl.size() > 0) {
                setRightText_("确定", getResources().getColor(R.color.normal_red));
                bt_right.setText("确定(" + listUrl.size() + ")");
            } else {
                setRightText_("确定", getResources().getColor(R.color.ff9e9fab));
                bt_right.setText("确定");
            }
            if (listUrl.size() > 0) {
                linearLayoutManager.scrollToPosition(listUrl.size() - 1);
            }

            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 点击空白位置 隐藏软键盘
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 我的所有好友
     */
    private void getMyAllGoodFriendsList() {
        OkGo.<String>post(Urls.getMyAllGoodFriendsList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("cid", groupId)
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        listAdminstor.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                JSONArray list = data.optJSONArray("list");
                                if (list != null && list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        GoodFriendModel goodFriendModel = new Gson().fromJson(list.optString(i), GoodFriendModel.class);
                                        listAdminstor.add(goodFriendModel);
                                    }
                                    addSortLetter(listAdminstor);
                                    Collections.sort(listAdminstor, pinyinComparator);
                                    adapter.notifyDataSetChanged();
                                } else {
                                }
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    private void filterData(String filterStr) {
       List<GoodFriendModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = listAdminstor;
        } else {
            filterDateList.clear();
            for (GoodFriendModel friendModel : listAdminstor) {
                String name = friendModel.goodFriendsName;
                String mobile  = friendModel.mobile;
                if (name.indexOf(filterStr.toString()) != -1||mobile.indexOf(filterStr.toString())!=-1) {
                    filterDateList.add(friendModel);
                }
            }
        }
        addSortLetter(filterDateList);
        Collections.sort(filterDateList, pinyinComparator);
        adapter = new SelectFriendsAdapter(SelectFriendAty.this, filterDateList);
        recyclerView.setAdapter(adapter);
        adapter.setOnGroupItemClickLinster(this);
        adapter.notifyDataSetChanged();

    }


}


package com.merrichat.net.activity.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.adapter.AddressBookFriendsAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ContactModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.InputHelper;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.ConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/12/1.
 * 搜索通讯录好友
 */

public class SearchAty extends BaseActivity {
    @BindView(R.id.et)
    ClearEditText et;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private AddressBookFriendsAdapter addressBookFriendsAdapter;
    private ArrayList<ContactModel.DataBean.ListBean> localContactModelList;
    private ArrayList<ContactModel.DataBean.ListBean> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        localContactModelList = new ArrayList<>();
        searchList = new ArrayList<>();
        localContactModelList.addAll((ArrayList<ContactModel.DataBean.ListBean>) getIntent().getSerializableExtra("localContactModelList"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        addressBookFriendsAdapter = new AddressBookFriendsAdapter(R.layout.item_address_book_friends, searchList, true);
        recyclerView.setAdapter(addressBookFriendsAdapter);
        addressBookFriendsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                JSONArray jsonArray = new JSONArray();
                JSONObject data = new JSONObject();
                try {
                    data.put("nick", searchList.get(position).getNick());
                    data.put("mobile", searchList.get(position).getMobile());
                    jsonArray.put(data);
                    String memberJson = jsonArray.toString();
                    getBookFriendList(memberJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        search();
        et.addTextChangedListener(new MyTextWatcher());
    }

    private void search() {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    // 对应逻辑操作
                    String inputStr = et.getText().toString();
                    try {
                        if (inputStr.getBytes("utf-8").length < 3) {
                            RxToast.showToast("输入的内容过短");
                        } else if (inputStr.getBytes("utf-8").length == 0) {
                            RxToast.showToast("输入要搜索的内容");
                        } else {
                            searchList.clear();
                            for (int i = 0; i < localContactModelList.size(); i++) {


                                if (localContactModelList.get(i).getNick().equals(localContactModelList.get(i).getMobile())) {
                                    if (localContactModelList.get(i).getNick().contains(inputStr)) {
                                        localContactModelList.get(i).setKeyWords(inputStr);
                                        searchList.add(localContactModelList.get(i));
                                    }
                                } else {
                                    if (localContactModelList.get(i).getNick().contains(inputStr)) {
                                        localContactModelList.get(i).setKeyWords(inputStr);
                                        searchList.add(localContactModelList.get(i));
                                    }
                                    if (localContactModelList.get(i).getMobile().contains(inputStr)) {
                                        localContactModelList.get(i).setKeyWords(inputStr);
                                        searchList.add(localContactModelList.get(i));
                                    }
                                }
                            }
                            addressBookFriendsAdapter.notifyDataSetChanged();
                            if (searchList.size() == 0) {
                                RxToast.showToast("没有搜索到");
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                searchList.clear();
                addressBookFriendsAdapter.notifyDataSetChanged();
            } else {
                // 对应逻辑操作
                String inputStr = et.getText().toString();
                try {
                    if (inputStr.getBytes("utf-8").length > 2) {
                        searchList.clear();
                        for (int i = 0; i < localContactModelList.size(); i++) {
                            if (localContactModelList.get(i).getNick().equals(localContactModelList.get(i).getMobile())) {
                                if (localContactModelList.get(i).getNick().contains(inputStr)) {
                                    localContactModelList.get(i).setKeyWords(inputStr);
                                    searchList.add(localContactModelList.get(i));
                                }
                            } else {
                                if (localContactModelList.get(i).getNick().contains(inputStr)) {
                                    localContactModelList.get(i).setKeyWords(inputStr);
                                    searchList.add(localContactModelList.get(i));
                                }
                                if (localContactModelList.get(i).getMobile().contains(inputStr)) {
                                    localContactModelList.get(i).setKeyWords(inputStr);
                                    searchList.add(localContactModelList.get(i));
                                }
                            }
                        }
                        addressBookFriendsAdapter.notifyDataSetChanged();
                    } else {
                        searchList.clear();
                        addressBookFriendsAdapter.notifyDataSetChanged();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @OnClick({R.id.tv_cancle})
    public void onClick(View view) {
        InputHelper.getInstance(getApplicationContext()).hideKeyboard(et);
        switch (view.getId()) {
            case R.id.tv_cancle:
                finish();
                break;
        }
    }

    private void showDialog(final String phone, final String message) {
        final ConfirmDialog dialog = ConfirmDialog.newInstance(ConstantsPath.SEARCH_CONTACT_DIALOG_TYPE);
        dialog.show(getSupportFragmentManager());
        dialog.setDialogOnClickListener(new ConfirmDialog.DialogOnClickListener() {
            @Override
            public void Yes() {
                dialog.dismiss();
                SendSMS(phone, message);
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 从后台获取通讯录好友状态
     */
    private void getBookFriendList(String memberJson) {
        OkGo.<String>post(Urls.getBookFriendList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("pageNum", "0")
                .params("memberJson", memberJson)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ContactModel contactModel = new Gson().fromJson(response.body(), ContactModel.class);
                        if (contactModel.getData().getList().size() > 0) {
                            ContactModel.DataBean.ListBean bean = contactModel.getData().getList().get(0);
                            if (bean.isHasRegister()) {
                                startActivity(new Intent(cnt, HisYingJiAty.class)
                                        .putExtra("hisMemberId", Long.parseLong(bean.getMemberId()))
                                        .putExtra("hisImgUrl", bean.getHeadUrl())
                                        .putExtra("hisNickName", bean.getGoodFriendsName()));
                            } else {
                                showDialog(bean.getMobile(), bean.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * 调起系统发短信功能
     *
     * @param phoneNumber 发送短信的接收号码
     * @param message     短信内容
     */
    public void SendSMS(String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }
}

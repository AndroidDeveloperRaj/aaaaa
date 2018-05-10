package com.merrichat.net.activity.message;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.AddressBookFriendsAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ContactModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.ListPageUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ConfirmDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/14.
 * 联系人---通讯录好友
 */

public class AddressBookFriendsActivity extends BaseActivity implements OnLoadmoreListener, AddressBookFriendsAdapter.OnClickCallBack {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    /**
     * 点击搜索
     */
    LinearLayout llSearch;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private View viewSearch;

    private AddressBookFriendsAdapter addressBookFriendsAdapter;
    private LinearLayoutManager layoutManager;

    private int currentPage = 1;//联系人当前页
    private int totalPage;//联系人分页后，总页数
    private int pageSize = 10;//联系人每页个数
    private ListPageUtil<ContactModel.DataBean.ListBean> listPageUtil;//分页工具类
    private ArrayList<ContactModel.DataBean.ListBean> contactModelList;
    private ArrayList<ContactModel.DataBean.ListBean> localContactModelList;//本地通讯录
    private LoadingDialog loadingDialog;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (localContactModelList.size() > 0) {
                        tvEmpty.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                        listPageUtil = new ListPageUtil<>(localContactModelList, pageSize);
                        totalPage = listPageUtil.getTotalPage();
                        getBookFriendList();
                    } else {
                        closeDialog();
                        tvEmpty.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book_friends);
        ButterKnife.bind(this);
        initView();
        checkPermission();
    }

    private void initHeaderView() {
        viewSearch = getLayoutInflater().inflate(R.layout.layout_header_search, (ViewGroup) recyclerView.getParent(), false);
        llSearch = (LinearLayout) viewSearch.findViewById(R.id.ll_search);
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(cnt, SearchAty.class).putExtra("localContactModelList", localContactModelList));
            }
        });
    }

    private void initView() {
        tvTitleText.setText("通讯录好友");
        loadingDialog = new LoadingDialog(this).setInterceptBack(false).setLoadingText("加载中...");
        initHeaderView();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        contactModelList = new ArrayList<>();
        localContactModelList = new ArrayList<>();
        addressBookFriendsAdapter = new AddressBookFriendsAdapter(R.layout.item_address_book_friends, contactModelList, false);
        addressBookFriendsAdapter.addHeaderView(viewSearch);
        recyclerView.setAdapter(addressBookFriendsAdapter);
        addressBookFriendsAdapter.setOnClickCallBack(this);
        refreshLayout.setEnableRefresh(false);//取消下拉刷新
        refreshLayout.setOnLoadmoreListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取本地通讯录
                getContactList();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            //获取本地通讯录
            getContactList();
        }
    }

    /**
     * 获取本地手机联系人
     */
    private boolean getAllContactInfo() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            ContactModel.DataBean.ListBean model = new ContactModel.DataBean.ListBean();
            if (!number.replace(" ", "").equals(UserModel.getUserModel().getMobile())) {
                model.setMobile(number.replace(" ", ""));
                model.setNick(name.replace(" ", ""));
                localContactModelList.add(model);
            }
        }
        cursor.close();
        return true;
    }

    /**
     * 获取本地通讯录耗时，开线程执行
     */

    private void getContactList() {
        if (!(PackageManager.PERMISSION_GRANTED == cnt.getPackageManager().checkPermission("android.permission.READ_CONTACTS", "com.merrichat.net"))) {
            GetToast.showToast(cnt, "您已禁止获取手机联系人权限，可在手机设置里手动允许");
            return;
        }
        loadingDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getAllContactInfo()) {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }
        });
        thread.start();
    }

    private String getCurrentPageJson() {
        if (listPageUtil == null) {
            return "";
        }
        listPageUtil.getPagedList(currentPage);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < listPageUtil.getPagedList(currentPage).size(); i++) {
            JSONObject data = new JSONObject();
            try {
                data.put("nick", listPageUtil.getPagedList(currentPage).get(i).getNick());
                data.put("mobile", listPageUtil.getPagedList(currentPage).get(i).getMobile());
                jsonArray.put(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    /**
     * 从后台获取通讯录好友
     */
    private void getBookFriendList() {
        String memberJson = getCurrentPageJson();
        OkGo.<String>post(Urls.getBookFriendList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("pageNum", currentPage)
                .params("memberJson", memberJson)
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        refreshLayout.finishLoadmore();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                ContactModel contactModel = new Gson().fromJson(response.body(), ContactModel.class);
                                contactModelList.addAll(contactModel.getData().getList());
                                addressBookFriendsAdapter.notifyDataSetChanged();
                                closeDialog();
                            } else {
                                RxToast.showToast("网络请求失败，请稍后重试");
                                closeDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeDialog();
                        }
                    }
                });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        currentPage++;
        if (currentPage > totalPage) {
            refreshlayout.finishLoadmore();
            RxToast.showToast("没有更多数据了");
        } else {
            getBookFriendList();
        }
    }

    /**
     * 加好友
     *
     * @param position
     */
    @Override
    public void onAddClick(int position) {
        addGoodFriends(position - 1);
    }

    /**
     * 邀请
     *
     * @param position
     */
    @Override
    public void onYaoQingClick(int position) {
        SendSMS(contactModelList.get(position - 1).getMobile(), contactModelList.get(position - 1).getMessage());
//        showDialog(contactModelList.get(position - 1).getMobile(), contactModelList.get(position - 1).getMessage());
    }

    /**
     * 同意
     *
     * @param position
     */
    @Override
    public void onTongYiClick(int position) {
        agreeFriendsRequest(position - 1);
    }

    /**
     * 添加好友——接口
     *
     * @param position
     */
    private void addGoodFriends(final int position) {
        OkGo.<String>post(Urls.addGoodFriends)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("memberUrl", UserModel.getUserModel().getImgUrl())
                .params("toMemberId", contactModelList.get(position).getMemberId())
                .params("toMemberName", contactModelList.get(position).getNick())
                .params("toMemberUrl", contactModelList.get(position).getHeadUrl())
                .params("source", "1")
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                RxToast.showToast(data.optString("message"));
                                contactModelList.get(position).setFlag(4);
                                addressBookFriendsAdapter.notifyItemChanged(position + 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
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
                .params("toMemberId", contactModelList.get(position).getMemberId())
                .params("toMemberName", contactModelList.get(position).getNick())
                .params("toMemberUrl", contactModelList.get(position).getHeadUrl())
                .params("id", contactModelList.get(position).getInviteId())
                .execute(new StringDialogCallback(cnt) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                contactModelList.get(position).setFlag(2);
                                addressBookFriendsAdapter.notifyItemChanged(position + 1);
                                RxToast.showToast("已同意对方的邀请");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 邀请——dialog
     *
     * @param phone
     */
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

    private void closeDialog() {
        if (loadingDialog != null) {
            loadingDialog.close();
            loadingDialog = null;
        }
    }
}

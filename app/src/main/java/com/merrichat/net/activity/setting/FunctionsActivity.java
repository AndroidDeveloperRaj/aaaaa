package com.merrichat.net.activity.setting;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.model.QueryGoodFriendRequestModel;
import com.merrichat.net.model.UploadModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DataCleanManager;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by AMSSY1 on 2017/11/6.
 * <p>
 * 设置--功能
 */

public class FunctionsActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rl_yijian)
    RelativeLayout rlYijian;
    @BindView(R.id.rl_guanyu)
    RelativeLayout rlGuanyu;
    @BindView(R.id.tv_item_name)
    TextView tvItemName;
    /**
     * 缓存大小
     */
    @BindView(R.id.iv_checked)
    TextView ivChecked;
    @BindView(R.id.rl_qingchu)
    RelativeLayout rlQingchu;
    private LoadingDialog loadingDialog;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    RxToast.showToast("清理完成");
                    try {
                        ivChecked.setText(DataCleanManager.getTotalCacheSize(cnt));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null) {
                        loadingDialog.close();
                    }
                    break;
                case 1:
                    if (loadingDialog != null) {
                        loadingDialog.show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        ButterKnife.bind(this);
        initView();
//        ContactsContractListModel contacts = getContacts();
//        for (int i = 0; i < contacts.memberJson.size(); i++) {
//            Log.e("----->>>", contacts.memberJson.get(i).mobile + "" + contacts.memberJson.get(i).mobile);
//        }
//
//
//        ApiManager.getApiManager().getService(WebApiService.class).insertInviteFriendsRecord(UserModel.getUserModel().getMemberId(), UserModel.getUserModel().getMobile(), gson.toJson(List, ContactsContractModel.class))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscribe<UploadModel>() {
//                    @Override
//                    public void onNext(UploadModel uploadModel) {
//                        Log.e("----->>>", uploadModel.msg);
//
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
//                    }
//                });
    }

    private void initView() {
        tvTitleText.setText("功能");
        try {
            ivChecked.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick({R.id.iv_back, R.id.rl_yijian, R.id.rl_guanyu, R.id.rl_qingchu})
    public void onViewClicked(View view) {
        //判断是否是快速点击
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_yijian:
                RxActivityTool.skipActivity(this, OpinionFeedbackAty.class);
                break;
            case R.id.rl_guanyu:
                RxActivityTool.skipActivity(this, AboutActivity.class);
                break;
            case R.id.rl_qingchu:
                loadingDialog = new LoadingDialog(this).setLoadingText("正在清除缓存……").setInterceptBack(true);
                new Thread(new clearCache()).start();

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loadingDialog != null) {
            loadingDialog.close();
            loadingDialog = null;
        }
    }

    class clearCache implements Runnable {
        @Override
        public void run() {
            try {
                handler.sendEmptyMessage(1);
                DataCleanManager.clearAllCache(FunctionsActivity.this);
                Thread.sleep(1000);
                if (DataCleanManager.getTotalCacheSize(FunctionsActivity.this).startsWith("0")) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                return;
            }

        }

    }
//
//
//    Gson gson = new Gson();
//    private ContactsContractListModel getContacts() {
//        ContactsContractListModel contactsContractListModel = new ContactsContractListModel();
//        List<ContactsContractModel> list = new ArrayList<>();
//
//        //联系人的Uri，也就是content://com.android.contacts/contacts
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        //指定获取_id和display_name两列数据，display_name即为姓名
//        String[] projection = new String[]{
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.DISPLAY_NAME
//        };
//        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
//        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
//        int i = 0;
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                Long id = cursor.getLong(0);
//                //获取姓名
//                String name = cursor.getString(1);
//                //指定获取NUMBER这一列数据
//                String[] phoneProjection = new String[]{
//                        ContactsContract.CommonDataKinds.Phone.NUMBER
//                };
//                //根据联系人的ID获取此人的电话号码
//                Cursor phonesCusor = this.getContentResolver().query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        phoneProjection,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
//                        null,
//                        null);
//                //因为每个联系人可能有多个电话号码，所以需要遍历
//                if (phonesCusor != null && phonesCusor.moveToFirst()) {
//                    do {
//                        ContactsContractModel contractModel = new ContactsContractModel();
//                        contractModel.mobile = phonesCusor.getString(0);
//                        list.add(contractModel);
//                        String num = phonesCusor.getString(0);
//                    } while (phonesCusor.moveToNext());
//                }
//                i++;
//            } while (cursor.moveToNext());
//        }
//
//        contactsContractListModel.memberJson = list;
//        return contactsContractListModel;
//    }
//
//    public class ContactsContractModel {
//        public String mobile;
//        public String nick;
//    }
//
//    public class ContactsContractListModel {
//        List<ContactsContractModel> memberJson;
//    }


}

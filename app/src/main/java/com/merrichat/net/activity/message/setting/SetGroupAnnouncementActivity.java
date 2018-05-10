package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/19.
 * 设置/修改群公告
 */

public class SetGroupAnnouncementActivity extends MerriActionBarActivity {

    /**
     * 输入群公告
     */
    @BindView(R.id.et_announcement)
    EditText etAnnouncement;

    /**
     * 字数
     */
    @BindView(R.id.tv_number)
    TextView tvNumber;

    /**
     * 1 建群 设置群公告
     * 2 群设置 修改群公告
     * 3 群设置--修改群资料--修改群公告（这个主要是点击完成之后需要发送广播刷新页面）
     */
    private int type = 0;  //获取点击过来的activity

    private String groupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_group_announcement);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        initTitle();
        initView();
    }


    private void initTitle() {
        if (type == 1) {  //判断是从选则联系人过来的
            setTitle("设置群公告");
        } else {
            setTitle("修改群公告");
            groupId = getIntent().getStringExtra("groupId");
        }
        setLeftBack();
        setRightText("保存", R.color.base_FF3D6F, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(etAnnouncement.getText().toString().length() > 0)) {
                    RxToast.showToast("请输入内容");
                    return;
                }
                if (type == 1) {
                    PrefAppStore.setGroupInfo(cnt, etAnnouncement.getText().toString().trim());
                    finish();
                } else {
                    updateGroupOfNotice();
                }
            }
        });
    }

    private void initView() {
        etAnnouncement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvNumber.setText(etAnnouncement.getText().length() + "/300");

            }
        });
    }

    /**
     * 修改群公告
     */
    private void updateGroupOfNotice() {
        OkGo.<String>post(Urls.updateGroupOfNotice)
                .params("cid", groupId)
                .params("communityNotice", etAnnouncement.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    if (type == 1) {
                                        GetToast.useString(cnt, "设置成功");
                                    } else {
                                        GetToast.useString(cnt, "修改成功");
                                        if (type==3){
                                            MyEventBusModel eventBusModel = new MyEventBusModel();
                                            eventBusModel.REFRESH_GROUP_DATA = true;
                                            EventBus.getDefault().post(eventBusModel);
                                        }
                                    }
                                    PrefAppStore.setGroupInfo(cnt, etAnnouncement.getText().toString());
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

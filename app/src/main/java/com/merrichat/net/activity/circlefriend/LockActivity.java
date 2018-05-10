package com.merrichat.net.activity.circlefriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.LockModel;
import com.merrichat.net.utils.RxTools.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 权限管理
 * Created by amssy on 17/12/30.
 */

public class LockActivity extends BaseActivity {
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.lin_lock_group)
    LinearLayout linLockGroup;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private ImageView[] imageViews;
    private TextView[] textViews;
    private List<LockModel.DataBean> list;
    private String contentId;//帖子
    private int jurisdiction;
    private int jurisdictionGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            contentId = intent.getStringExtra("contentId");
            jurisdictionGet = intent.getIntExtra("jurisdiction", 0);
        }
        tvTitleText.setText("公开范围");
        tvRight.setText("确定");
        tvRight.setTextColor(getResources().getColor(R.color.normal_red));
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLogJurisdictionById();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllJurisdictions();
    }

    private void initLock() {
        imageViews = new ImageView[list.size()];
        textViews = new TextView[list.size()];
        final RelativeLayout[] relativeLayouts = new RelativeLayout[list.size()];
        for (int i = 0; i < list.size(); i++) {
            View view = LayoutInflater.from(cnt).inflate(R.layout.layout_inform_reason, null);
            RelativeLayout rel_inform = (RelativeLayout) view.findViewById(R.id.rel_inform);
            TextView textView = (TextView) view.findViewById(R.id.tv_reason);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_inform_check);
            View v = view.findViewById(R.id.view_inform);

            textViews[i] = textView;
            textViews[i].setText("" + list.get(i).getName());

            imageViews[i] = imageView;
            imageViews[i].setId(i);

            //当前权限
            if (list.get(i).getId() == jurisdictionGet) {
                imageViews[i].setVisibility(View.VISIBLE);
                textViews[i].setTextColor(getResources().getColor(R.color.normal_red));
            } else {
                imageViews[i].setVisibility(View.GONE);
                textViews[i].setTextColor(getResources().getColor(R.color.black_new_two));
            }
            if (i == list.size() - 1) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }

            final int finalI = i;
            relativeLayouts[i] = rel_inform;
            relativeLayouts[i].setId(i);

            linLockGroup.addView(view);
        }

        for (int i = 0; i < list.size(); i++) {
            relativeLayouts[i].setTag(relativeLayouts);
            relativeLayouts[i].setOnClickListener(new TabOnClick());
        }

    }

    class TabOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            RelativeLayout[] linearLayouts = (RelativeLayout[]) v.getTag();
            RelativeLayout linearLayout = (RelativeLayout) v;
            for (int i = 0; i < linearLayouts.length; i++) {
                if (linearLayout.equals(linearLayouts[i])) {
                    imageViews[i].setVisibility(View.VISIBLE);
                    textViews[i].setTextColor(getResources().getColor(R.color.normal_red));
                    jurisdiction = list.get(i).getId();
                } else {
                    textViews[i].setTextColor(getResources().getColor(R.color.black_new_two));
                    imageViews[i].setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 获取全部权限
     */
    private void getAllJurisdictions() {
        OkGo.<String>get(Urls.QUERY_ALL_JURISDICTION)
                .tag(this)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    LockModel lockModel = JSON.parseObject(response.body(), LockModel.class);
                                    list = lockModel.getData();
                                    initLock();
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 修改权限
     */
    private void updateLogJurisdictionById() {
        OkGo.<String>get(Urls.UPDATE_LOG_JURISDICTION)
                .tag(this)
                .params("id", "" + contentId)
                .params("jurisdiction", "" + jurisdiction)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    RxToast.showToast("修改权限成功");
                                    //发送广播 刷新数据
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.COMMENT_EVALUATE4 = true;
                                    EventBus.getDefault().post(myEventBusModel);

                                    finish();
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }
}

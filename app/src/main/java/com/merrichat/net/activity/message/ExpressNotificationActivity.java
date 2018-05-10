package com.merrichat.net.activity.message;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.ExpressNotificationAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.ExpressNotificationModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.PhoneUtils;
import com.merrichat.net.view.PopupList;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/3/20.
 * 快递通知
 */

public class ExpressNotificationActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    private ExpressNotificationAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<ExpressNotificationModel> list;

    private float mRawX;
    private float mRawY;
    private List<String> popupMenuItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_notification);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        tvTitleText.setText("快递通知");
    }

    private void initView() {
        PrefAppStore.setNotificationNumber(cnt, 0);
        list = ExpressNotificationModel.getListNotificationModel(UserModel.getUserModel().getMemberId());
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ExpressNotificationAdapter(cnt, list);
        recyclerView.setAdapter(adapter);
        popupMenuItemList.add("删除");

        adapter.setOnItemClickListener(new ExpressNotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemLongOnclick(View view, int position) {
                final PopupList popupList = new PopupList(view.getContext());
                popupList.showPopupListWindow(view, position, mRawX, mRawY, popupMenuItemList, new PopupList.PopupListListener() {

                    @Override
                    public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                        return true;
                    }

                    @Override
                    public void onPopupListClick(View contextView, int contextPosition, int position) {
                        ExpressNotificationModel.deleteOneNotifitionModel(list.get(contextPosition));
                        list.remove(contextPosition);
                        adapter.notifyDataSetChanged();
                        GetToast.useString(cnt, "删除成功");
                    }
                });
            }

            @Override
            public void OnTouchListenerOnclick(int position, MotionEvent motionEvent) {
                mRawX = motionEvent.getRawX();
                mRawY = motionEvent.getRawY();
            }
        });

        adapter.setOnCallPhoneListener(new ExpressNotificationAdapter.OnCallPhoneListener() {
            @Override
            public void onCallPhone(int position) {
                PhoneUtils.callPhone(cnt, list.get(position).getMemberPhone());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finishThisActivity();
                break;
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishThisActivity();
    }

    private void finishThisActivity() {
        MyEventBusModel eventBusModel = new MyEventBusModel();
        eventBusModel.MESSAGE_IS_NUMBER_REFRESH = true;
        eventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
        EventBus.getDefault().post(eventBusModel);
        finish();
    }
}

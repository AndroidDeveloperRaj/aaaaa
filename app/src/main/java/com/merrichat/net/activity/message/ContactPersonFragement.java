package com.merrichat.net.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.grouporder.GroupListActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.adapter.MyFriendsAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.fragment.MessageFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MyContactModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NoDoubleClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/11/6.
 * 消息---联系人列表
 */

public class ContactPersonFragement extends BaseFragment implements OnRefreshListener {


    @BindView(R.id.swipe_refresh_layout)
    SmartRefreshLayout swipeRefreshLayout;
    /**
     * 新的朋友数量
     */
    TextView tvNewFriendsNum;
    /**
     * 我的好友列表
     */
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    /**
     * 未登录布局
     */
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.btn_login_friend)
    Button btnLoginFriend;
    @BindView(R.id.lin_toast)
    LinearLayout linToast;

    Unbinder unbinder;
    private View view;
    private int pageNum = 1;
    private View viewHeader;

    private boolean isViewInit;
    private MyFriendsAdapter myFriendsAdapter;
    private ArrayList<MyContactModel.DataBean.AttentionGoodFriendsRelationsBean> myContactList;
    private LinearLayoutManager layoutManager;

    /**
     * Fragment 的构造函数。
     */
    public ContactPersonFragement() {
    }

    public static ContactPersonFragement newInstance() {
        return new ContactPersonFragement();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!UserModel.getUserModel().getIsLogin()) {
            swipeRefreshLayout.setVisibility(View.GONE);
            linToast.setVisibility(View.VISIBLE);
            tvNone.setText("你还未登录请前去登录");
            btnLoginFriend.setText("登录");
            btnLoginFriend.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("activityId", MessageFragment.activityId));
                }
            });
            return;
        }
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_person, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        isViewInit = true;
        return view;
    }

    private void initView() {
        initHeaderView();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        myContactList = new ArrayList<>();
        myFriendsAdapter = new MyFriendsAdapter(R.layout.item_my_friends, myContactList);
        myFriendsAdapter.addHeaderView(viewHeader);
        recyclerView.setAdapter(myFriendsAdapter);
        swipeRefreshLayout.setEnableLoadmore(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        myFriendsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(cnt, HisYingJiAty.class)
                        .putExtra("hisMemberId", myContactList.get(position).getGoodFriendsId())
                        .putExtra("hisImgUrl", myContactList.get(position).getGoodFriendsUrl())
                        .putExtra("hisNickName", myContactList.get(position).getGoodFriendsName()));
            }
        });
        if (!UserModel.getUserModel().getIsLogin()) {
            swipeRefreshLayout.setVisibility(View.GONE);
            linToast.setVisibility(View.VISIBLE);
            tvNone.setText("你还未登录请前去登录");
            btnLoginFriend.setText("登录");
            btnLoginFriend.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("activityId", MessageFragment.activityId));
                }
            });
        } else {
            loginSuessDo();
        }
    }


    private void initHeaderView() {
        viewHeader = getActivity().getLayoutInflater().inflate(R.layout.layout_lianxiren_header, (ViewGroup) recyclerView.getParent(), false);
        tvNewFriendsNum = (TextView) viewHeader.findViewById(R.id.tv_new_friends_num);
        /**
         * 搜索框
         */
        viewHeader.findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                startActivity(new Intent(cnt, SearchFriendsActivity.class));
            }
        });
        /**
         * 新的朋友
         */
        viewHeader.findViewById(R.id.ll_new_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                startActivity(new Intent(cnt, NewFriendsActivity.class));
            }
        });


        /**
         * 群组列表
         */
        viewHeader.findViewById(R.id.ll_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                startActivity(new Intent(cnt, GroupListActivity.class));
            }
        });

        /**
         * 手机联系人
         */
        viewHeader.findViewById(R.id.lay_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                startActivity(new Intent(cnt, AddressBookFriendsActivity.class));
            }
        });
    }

    private void getGoodFriendsList() {
        OkGo.<String>post(Urls.getGoodFriendsList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("pageNum", pageNum + "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        myContactList.clear();
//                        {"data":{"attentionGoodFriendsRelations":[],"num":""},"success":true}
                        try {
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.finishRefresh();
                            }
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                MyContactModel myContactModel = new Gson().fromJson(response.body(), MyContactModel.class);
                                myContactList.addAll(myContactModel.getData().getAttentionGoodFriendsRelations());
                                myFriendsAdapter.notifyDataSetChanged();
                                String num = myContactModel.getData().getNum();
                                if (!TextUtils.isEmpty(num)) {
                                    tvNewFriendsNum.setVisibility(View.VISIBLE);
                                    tvNewFriendsNum.setText(num);
                                    PrefAppStore.setNewFriendNum(cnt, Integer.parseInt(num));
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                } else {
                                    PrefAppStore.setNewFriendNum(cnt, 0);
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                    if (tvNewFriendsNum.getVisibility() == View.VISIBLE) {
                                        tvNewFriendsNum.setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.finishRefresh();
                        }
                    }
                });
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_FRIENDS_LIST//新朋友同意好友邀请
                || myEventBusModel.REFRESH_HIS_HOME//删除好友
                ) {//刷新数据
            getGoodFriendsList();
        }
        if (myEventBusModel.REFRESH_LOGIN_SUCESS_ENTER_LOGIN) {//登录成功刷新消息列表的广播
            if (UserModel.getUserModel().getIsLogin()) {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                linToast.setVisibility(View.GONE);
                getGoodFriendsList();
            }
        }
        if (myEventBusModel.REFRESH_NEW_FRIENDS_NUM) {
            getGoodFriendsList();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getGoodFriendsList();
        //下拉刷新建立标志时间戳，促使Glide跟新头像Glide.with(this).load(yourFileDataModel).signature(new StringSignature("1.0.0")).into(imageView);
        PrefAppStore.setMessageHeaderImgTimestamp(getActivity());
    }

    public void updateAdapter() {
        if (myFriendsAdapter != null) {
            myFriendsAdapter.notifyDataSetChanged();
        }
    }

    public void loginSuessDo() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        linToast.setVisibility(View.GONE);
        getGoodFriendsList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewInit) {//chatfragment可见时
            refreshViewOnUserVisibe();
        }
    }


    public void refreshViewOnUserVisibe() {
        if (UserModel.getUserModel().getIsLogin()) {
            loginSuessDo();
        }
    }
}

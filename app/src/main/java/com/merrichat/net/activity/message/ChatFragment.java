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
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.merrichat.net.R;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.adapter.ChatAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.fragment.MessageFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMessage;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NoDoubleClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/11/6.
 * 消息----聊天列表
 */

public class ChatFragment extends BaseFragment implements OnRefreshListener, BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener {
    /**
     * 登录成功请求码
     */
    public final static int REQUEST_LOGIN_SUESS_CODE = 1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SmartRefreshLayout swipeRefreshLayout;
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
    private ChatAdapter adapter;


    private LinearLayoutManager layoutManager;
    private List<MessageListModle> list_message;
    /**
     * 离线消息返回的聊天类型1是单聊2是群聊
     */
    private String offlinemes_type;
    private String senderId;
    private String GroupID;

    /**
     * 头布局
     */
    private View viewHeader;

    /**
     * 通知
     */
    private SwipeMenuLayout smlNotice;
    private LinearLayout llNotice;
    private TextView tvNoticeNum;
    private Button btnDeletNotice;

    /**
     * 赞和评论
     */
    private SwipeMenuLayout smlZanPl;
    private LinearLayout llZanPl;
    private TextView tvPlNum;
    private Button btnDeletZanPl;


    /**
     * 群通知
     */
    private SwipeMenuLayout smlGroupNotice;
    private LinearLayout llGroupNotice;
    private TextView tvGroupNotice;
    private Button btnDeletGroupNotice;

    /**
     * 快递通知
     */
    private SwipeMenuLayout smlExpressNotice;
    private LinearLayout llExpressNotice;
    private TextView tvExpressNotice;
    private Button btnDeletExpressNotice;


    private boolean isViewInit;

    /**
     * Fragment 的构造函数。
     */
    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
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
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        isViewInit = true;
        return view;
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.MESSAGE_IS_REFRESH) {//刷新消息列表的广播
            selectMessageListInfor();
        }
        if (myEventBusModel.REFRESH_LOGIN_SUCESS_ENTER_LOGIN) {//登录成功刷新消息列表的广播
            if (UserModel.getUserModel().getIsLogin()) {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                linToast.setVisibility(View.GONE);
                selectMessageListInfor();
                getNoticeData();
                offline();
            }
        }

        if (myEventBusModel.MESSAGE_IS_NUMBER_REFRESH) {//刷新快递通知显示数量
            smlExpressNotice.setVisibility(View.VISIBLE);
            int notificationNumber = PrefAppStore.getNotificationNumber(cnt);
            if (notificationNumber > 0) {
                tvExpressNotice.setVisibility(View.VISIBLE);
                if (notificationNumber < 100) {
                    tvExpressNotice.setText(notificationNumber + "");
                } else {
                    tvExpressNotice.setText("99+");
                }
            } else {
                tvExpressNotice.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 从数据库里查询列表消息
     */
    private void selectMessageListInfor() {
        list_message.clear();
        /**
         * text1是判断列表数据是否有值，1有（应在列表中显示）0无，是作为查询条件
         * */
        QueryBuilder<MessageListModle> messageListModleQueryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
        List<MessageListModle> list_isTop = messageListModleQueryBuilder.where(MessageListModleDao.Properties.Top.eq(1), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).orderDesc(MessageListModleDao.Properties.Topts, MessageListModleDao.Properties.Msgts).list();
        QueryBuilder<MessageListModle> messageListModleQueryBuilderNoTop = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
        List<MessageListModle> list_noIsTop = messageListModleQueryBuilderNoTop.where(MessageListModleDao.Properties.Top.eq(0), MessageListModleDao.Properties.Text1.eq(1), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).orderDesc(MessageListModleDao.Properties.Msgts).list();
        list_message.addAll(list_isTop);
        list_message.addAll(list_noIsTop);
        adapter.notifyDataSetChanged();
    }


    private void initHeaderView() {
        viewHeader = getActivity().getLayoutInflater().inflate(R.layout.layout_notice_zan_pinglun, (ViewGroup) recyclerView.getParent(), false);


        smlNotice = (SwipeMenuLayout) viewHeader.findViewById(R.id.sml_notice);
        smlZanPl = (SwipeMenuLayout) viewHeader.findViewById(R.id.sml_zan_pl);
        smlGroupNotice = (SwipeMenuLayout) viewHeader.findViewById(R.id.sml_group_notice);
        smlExpressNotice = (SwipeMenuLayout) viewHeader.findViewById(R.id.sml_express_notice);

        llNotice = (LinearLayout) viewHeader.findViewById(R.id.ll_notice);
        llZanPl = (LinearLayout) viewHeader.findViewById(R.id.ll_zan_pl);
        llGroupNotice = (LinearLayout) viewHeader.findViewById(R.id.ll_group_notice);
        llExpressNotice = (LinearLayout) viewHeader.findViewById(R.id.ll_express_notice);

        tvNoticeNum = (TextView) viewHeader.findViewById(R.id.tv_notice_num);
        tvPlNum = (TextView) viewHeader.findViewById(R.id.tv_pl_num);
        tvExpressNotice = (TextView) viewHeader.findViewById(R.id.tv_express_notice);
        tvGroupNotice = (TextView) viewHeader.findViewById(R.id.tv_group_notice);

        btnDeletNotice = (Button) viewHeader.findViewById(R.id.btn_delete_notice);
        btnDeletZanPl = (Button) viewHeader.findViewById(R.id.btn_delete_zan_pl);
        btnDeletGroupNotice = (Button) viewHeader.findViewById(R.id.btn_delete_group_notice);
        btnDeletExpressNotice = (Button) viewHeader.findViewById(R.id.btn_delete_express_notice);


        llNotice.setOnClickListener(this);
        llZanPl.setOnClickListener(this);
        llGroupNotice.setOnClickListener(this);
        llExpressNotice.setOnClickListener(this);

        btnDeletNotice.setOnClickListener(this);
        btnDeletZanPl.setOnClickListener(this);
        btnDeletGroupNotice.setOnClickListener(this);
        btnDeletExpressNotice.setOnClickListener(this);


    }


    /**
     * 删除 通知、赞和评论弹框
     * falg为标记
     * 通知1
     * 赞和评论2
     * 群通知3
     * 快递通知4
     *
     * @param textContent
     * @param flag
     */
    private void showDialog(String textContent, final int flag) {
        final MaterialDialog dialog = new MaterialDialog(cnt);
        dialog.content(textContent)//
                .show();
        dialog.btnText("取消", "确定");
        dialog.title("提示");
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        if (flag == 1) {
                            smlNotice.setVisibility(View.GONE);
                            PrefAppStore.setNoticeNum(cnt, false);
                        } else if (flag == 2) {
                            smlZanPl.setVisibility(View.GONE);
                            PrefAppStore.setZanPingLun(cnt, false);
                        } else if (flag == 3) {
                            smlGroupNotice.setVisibility(View.GONE);
                            PrefAppStore.setGroupNum(cnt, false);
                        } else if (flag == 4) {
                            smlExpressNotice.setVisibility(View.GONE);
                            PrefAppStore.setExpressNotification(cnt, false);
                        }
                        dialog.dismiss();
                    }
                }
        );
    }


    /**
     * 初始化布局
     */
    private void initView() {
        initHeaderView();
        list_message = new ArrayList<MessageListModle>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(cnt);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(R.layout.item_chat_list, list_message);
        adapter.addHeaderView(viewHeader);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(this);
        swipeRefreshLayout.setEnableLoadmore(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        loginStatusDo();
        adapter.setOnDelListener(new ChatAdapter.OnSwipeListener() {
            @Override
            public void onDel(final int position) {
                final MaterialDialog dialog = new MaterialDialog(cnt);
                dialog.content("确定删除吗").btnText("取消", "确定").title("提示").show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {//left btn click listener
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {//right btn click listener
                            @Override
                            public void onBtnClick() {
                                if ("2".equals(list_message.get(position - 1).getType())) {
                                    GroupMessage.clearThisGroupMessage(list_message.get(position - 1).getGroupId());
                                    MessageListModle.clearSingleMessageModel(list_message.get(position - 1).getGroupId());
                                } else if ("1".equals(list_message.get(position - 1).getType())) {
                                    MessageModel.clearSingleMessageModel(list_message.get(position - 1).getSenderId());
                                    MessageListModle.clearSingleMessageModelById(String.valueOf(list_message.get(position - 1).getID()));
                                }
                                list_message.remove(position - 1);
                                adapter.notifyItemRemoved(position);
                                dialog.dismiss();
                            }
                        });
            }
        });
    }


    private void loginStatusDo() {
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

    /**
     * 初始化数据
     */
    private void getNoticeData() {
        if (PrefAppStore.getNoticeNum(cnt)) {
            smlNotice.setVisibility(View.VISIBLE);
        }
        if (PrefAppStore.getZanPingLun(cnt)) {
            smlZanPl.setVisibility(View.VISIBLE);
        }

        if (PrefAppStore.getGroupNum(cnt)) {
            smlGroupNotice.setVisibility(View.VISIBLE);
        }

        if (PrefAppStore.getExpressNotification(cnt)) {
            smlExpressNotice.setVisibility(View.VISIBLE);
        }


        //快递通知
        if (PrefAppStore.getExpressNotification(cnt)) {
            smlExpressNotice.setVisibility(View.VISIBLE);
            int notificationNumber = PrefAppStore.getNotificationNumber(cnt);
            if (notificationNumber > 0) {
                tvExpressNotice.setVisibility(View.VISIBLE);
                if (notificationNumber < 100) {
                    tvExpressNotice.setText(notificationNumber + "");
                } else {
                    tvExpressNotice.setText("99+");
                }
            } else {
                tvExpressNotice.setVisibility(View.GONE);
            }
        } else {
            int notificationNumber = PrefAppStore.getNotificationNumber(cnt);
            if (notificationNumber > 0) {
                PrefAppStore.setExpressNotification(cnt, true);
                smlExpressNotice.setVisibility(View.VISIBLE);
                tvExpressNotice.setVisibility(View.VISIBLE);
                if (notificationNumber < 100) {
                    tvExpressNotice.setText(notificationNumber + "");
                } else {
                    tvExpressNotice.setText("99+");
                }
            }
        }
        messageList();
    }


    /**
     * 查询通知数
     * communityNum:未读群通知
     * noticeNum：未读通知
     * commentNum：未读赞和评论
     */
    private void messageList() {
        OkGo.<String>post(Urls.messageList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.finishRefresh();
                            }
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    int noticeNum = data.optInt("noticeNum");
                                    int commentNum = data.optInt("commentNum");
                                    int communityNum = data.optInt("communityNum");
                                    PrefAppStore.setNoticeNumNew(cnt, noticeNum);
                                    PrefAppStore.setZanPingLunNum(cnt, commentNum);
                                    PrefAppStore.setGroupNumNew(cnt, communityNum);
                                    setHeadLayout(noticeNum, commentNum, communityNum);
                                    MyEventBusModel eventBusModel = new MyEventBusModel();
                                    eventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
                                    EventBus.getDefault().post(eventBusModel);
                                }
                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, "请求失败");
                                }
                            }
                            adapter.notifyDataSetChanged();
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


    /**
     * 设置头布局 --通知、赞和评论是否显示  以及显示样式
     *
     * @param noticeNum
     * @param commentNum
     */
    private void setHeadLayout(int noticeNum, int commentNum, int groupNum) {
        if (PrefAppStore.getNoticeNum(cnt)) {
            smlNotice.setVisibility(View.VISIBLE);
            setNoticeNumIsVisible(noticeNum);
        } else {
            if (noticeNum > 0) {
                PrefAppStore.setNoticeNum(cnt, true);
                smlNotice.setVisibility(View.VISIBLE);
                setNoticeNumIsVisible(noticeNum);
            } else {
                smlNotice.setVisibility(View.GONE);
            }
        }


        if (PrefAppStore.getZanPingLun(cnt)) {
            smlZanPl.setVisibility(View.VISIBLE);
            setZanAndPlIsVisible(commentNum);
        } else {
            if (commentNum > 0) {
                PrefAppStore.setZanPingLun(cnt, true);
                smlZanPl.setVisibility(View.VISIBLE);
                setZanAndPlIsVisible(commentNum);
            } else {
                smlZanPl.setVisibility(View.GONE);
            }
        }


        if (PrefAppStore.getGroupNum(cnt)) {
            smlGroupNotice.setVisibility(View.VISIBLE);
            setGroupNoticeIsVisible(groupNum);
        } else {
            if (groupNum > 0) {
                PrefAppStore.setGroupNum(cnt, true);
                smlGroupNotice.setVisibility(View.VISIBLE);
                setGroupNoticeIsVisible(groupNum);
            } else {
                smlGroupNotice.setVisibility(View.GONE);
            }
        }
    }


    private void setNoticeNumIsVisible(int noticeNum) {
        if (noticeNum > 0) {
            tvNoticeNum.setVisibility(View.VISIBLE);
            if (noticeNum < 100) {
                tvNoticeNum.setText(noticeNum + "");
            } else {
                tvNoticeNum.setText("99+");
            }
        } else {
            tvNoticeNum.setVisibility(View.GONE);
        }
    }


    private void setZanAndPlIsVisible(int commentNum) {
        if (commentNum > 0) {
            tvPlNum.setVisibility(View.VISIBLE);
            if (commentNum < 100) {
                tvPlNum.setText(commentNum + "");
            } else {
                tvPlNum.setText("99+");
            }
        } else {
            tvPlNum.setVisibility(View.GONE);
        }
    }


    private void setGroupNoticeIsVisible(int groupNum) {
        if (groupNum > 0) {
            tvGroupNotice.setVisibility(View.VISIBLE);
            if (groupNum < 100) {
                tvGroupNotice.setText(groupNum + "");
            } else {
                tvGroupNotice.setText("99+");
            }
        } else {
            tvGroupNotice.setVisibility(View.GONE);
        }
    }

    /**
     * 离线消息，消息列表数据
     */
    public void offline() {
        OkGo.<String>post(Urls.offline)
                .params("mid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.finishRefresh();
                            }
                            JSONObject object = new JSONObject(response.body());
                            boolean success = object.getBoolean("success");
                            if (success) {
                                JSONObject data = object.getJSONObject("data");
                                if (null != data) {
                                    /**
                                     * 解析离线消息聊天的人数
                                     * */
                                    JSONArray array = data.getJSONArray("count");
                                    Gson gson = new Gson();
                                    if (array.length() > 0) {
                                        for (int i = 0; i < array.length(); i++) {
                                            MessageListModle listModle = new MessageListModle().parseListJSON(array.optString(i));
                                            for (int j = list_message.size() - 1; j >= 0; j--) {//循环遍历单聊选出重复然后删除
                                                if ("1".equals(list_message.get(j).getType())) {
                                                    if (listModle.getSenderId().equals(list_message.get(j).getSenderId())) {
                                                        QueryBuilder<MessageListModle> messageListModleQueryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
                                                        MessageListModle unique = messageListModleQueryBuilder.where(MessageListModleDao.Properties.SenderId.eq(list_message.get(j).getSenderId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
                                                        GreenDaoManager.getInstance().getNewSession().delete(unique);
                                                        //删除集合中相同的数据
                                                        list_message.remove(j);
                                                        break;
                                                    }
                                                } else {
                                                    if (listModle.getGroupId().equals(list_message.get(j).getGroupId())) {
                                                        QueryBuilder<MessageListModle> messageListModleQueryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
                                                        MessageListModle unique = messageListModleQueryBuilder.where(MessageListModleDao.Properties.GroupId.eq(list_message.get(j).getGroupId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
                                                        GreenDaoManager.getInstance().getNewSession().delete(unique);
                                                        list_message.remove(j);
                                                        break;
                                                    }
                                                }
                                            }
                                            offlinemes_type = listModle.getType();//单聊群聊类型
                                            String name = listModle.getName();
                                            if ("1".equals(offlinemes_type)) {//单聊
                                                senderId = listModle.getSenderId();//单聊模式发送者id
                                                listModle.setName(name);
                                            } else {//群聊
                                                GroupID = listModle.getSenderId();//群聊模式发送者id（在此获取到是为了下面与相对应的离线内容相匹配存到表中）
                                                listModle.setGroup(name);
                                                listModle.setName("");
                                            }
                                            listModle.setPrivateID(UserModel.getUserModel().getMemberId());
                                            listModle.setText1("1");
                                            MessageListModle.setMessageListModle(listModle);
                                            list_message.add(listModle);
                                            /**
                                             * 解析离线每一个人的消息内容
                                             * */
                                            JSONObject detail = data.getJSONObject("detail");
                                            Iterator<String> keys = detail.keys();
                                            ArrayList<String> list = new ArrayList<>();
                                            //得到所有key值（id）
                                            while (keys.hasNext()) {
                                                list.add(keys.next().toString());
                                            }
                                            for (String z : list) {
                                                JSONArray jsonArray = detail.getJSONArray(z);
                                                for (int j = 0; j < jsonArray.length(); j++) {
                                                    if (z.equals(senderId)) {//单聊时候解析放集合
                                                        MessageModel inForModle = new MessageModel().parseListJSONInFor(jsonArray.optString(j));
                                                        inForModle.setPrivate_id(UserModel.getUserModel().getMemberId());
                                                        MessageModel.setMessageModel(inForModle);
                                                    } else if (z.equals(GroupID)) {//群聊时候解析放集合
                                                        GroupMessage groupMessage = new GroupMessage().parseListJSONInFor(jsonArray.optString(j));
                                                        groupMessage.setPrivate_id(UserModel.getUserModel().getMemberId());
                                                        groupMessage.setSendState(2);
                                                        GroupMessage.setMessageModel(groupMessage);
                                                    }
                                                }
                                            }
                                        }
                                        /**
                                         * 加入离线消息后再将数据排序
                                         * */
                                        list_message.clear();
                                        QueryBuilder<MessageListModle> messageListModleQueryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
                                        List<MessageListModle> list_isTop = messageListModleQueryBuilder.where(MessageListModleDao.Properties.Top.eq(1), MessageListModleDao.Properties.Text1.eq(1), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).orderDesc(MessageListModleDao.Properties.Topts, MessageListModleDao.Properties.Msgts).list();

                                        QueryBuilder<MessageListModle> messageListModleQueryBuilderNoTop = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
                                        List<MessageListModle> list_noIsTop = messageListModleQueryBuilderNoTop.where(MessageListModleDao.Properties.Top.eq(0), MessageListModleDao.Properties.Text1.eq(1), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).orderDesc(MessageListModleDao.Properties.Msgts).list();

                                        list_message.addAll(list_isTop);
                                        list_message.addAll(list_noIsTop);
                                        if (adapter == null) {
                                            adapter = new ChatAdapter(R.layout.item_chat_list, list_message);
                                            recyclerView.setAdapter(adapter);
                                        } else {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            } else {
                                GetToast.useString(cnt, object.getString("message"));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    /**
     * 下拉刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getNoticeData();
        offline();
        //下拉刷新建立标志时间戳，促使Glide更新头像Glide.with(this).load(yourFileDataModel).signature(new StringSignature("1.0.0")).into(imageView);
        PrefAppStore.setMessageHeaderImgTimestamp(getActivity());
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseAdapter, View view, final int position) {
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ll_item:
                MessageListModle model = null;
                if ("1".equals(list_message.get(position).getType())) {//单聊
                    Intent intent = new Intent(getActivity(), SingleChatActivity.class);
                    intent.putExtra("receiverMemberId", list_message.get(position).getSenderId());
                    intent.putExtra("receiverHeadUrl", list_message.get(position).getHeadUrl());
                    intent.putExtra("receiverName", list_message.get(position).getName());
//            intent.putExtra("receiverName", list_message.get(position).getAc());
                    startActivity(intent);
                    model = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.SenderId.eq(list_message.get(position).getSenderId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
                } else {//群聊
                    model = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.GroupId.eq(list_message.get(position).getGroupId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
                    Intent intent = new Intent(cnt, GroupChattingAty.class);
                    intent.putExtra("groupId", list_message.get(position).getGroupId());
                    intent.putExtra("group", list_message.get(position).getGroup());
                    intent.putExtra("groupLogoUrl", list_message.get(position).getHeadUrl());
                    startActivity(intent);
//            isBannedOrTrade(list_message.get(position).getGroupId(), list_message.get(position).getGroup(), list_message.get(position).getHeadUrl(), position);
                }
                /**
                 * 查询出来当前条目的model将数量count设为0为了返回时红点消失
                 * */
                if (null != model) {
                    if (0 != model.getCount()) {
                        model.setCount(0);
                        MessageListModle.setMessageListModle(model);
                    }
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_notice:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                tvNoticeNum.setVisibility(View.GONE);
                startActivity(new Intent(cnt, NoticeActivity.class));
                break;
            case R.id.ll_zan_pl:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                tvPlNum.setVisibility(View.GONE);
                startActivity(new Intent(cnt, PraiseAndCommentActivity.class));
                break;
            case R.id.ll_group_notice:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                tvGroupNotice.setVisibility(View.GONE);
                startActivity(new Intent(cnt, GroupNotificationActivity.class));
                break;
            case R.id.ll_express_notice:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                tvExpressNotice.setVisibility(View.GONE);
                startActivity(new Intent(cnt, ExpressNotificationActivity.class));
                break;
            case R.id.btn_delete_notice:
                showDialog("您确定要删除通知吗?", 1);
                break;
            case R.id.btn_delete_zan_pl:
                showDialog("您确定要删除赞和评论吗?", 2);
                break;
            case R.id.btn_delete_group_notice:
                showDialog("您确定要删除群通知吗?", 3);
                break;
            case R.id.btn_delete_express_notice:
                showDialog("您确定要删除群快递通知吗?", 4);
                break;
        }
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
            offline();
            loginSuessDo();
        }
    }

    public void loginSuessDo() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        linToast.setVisibility(View.GONE);
        selectMessageListInfor();
        getNoticeData();
    }
}

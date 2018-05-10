package com.merrichat.net.fragment.circlefriends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.AllCommentsAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AllCommentModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.DrawableCenterTextView;
import com.merrichat.net.view.SoftKeyBoardListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/16.
 * 全部评论
 */

public class AllCommentActivity extends AppCompatActivity implements AllCommentsAdapter.OnItemClickListener, OnLoadmoreListener, OnRefreshListener {
    private static String item_nickNames;
    private static String replyCommentIds;
    @BindView(R.id.iv_back)
    ImageView ivBack;


    @BindView(R.id.tv_title_text)
    TextView tvTitleText;


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    static EditText editTextComment;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;

    private AllCommentsAdapter adapter;
    private LinearLayoutManager layoutManager;

    public static Context mContext;

    private static String replyMemberIds;//被回复人的id
    private static String personIds;//最外层id

    private List<AllCommentModel.DataBean.ShowBarCommentBean> list;
    static List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_replys;

    private String contentId;//帖子ID
    private int currentPage = 1;
    private String memberId;
    private AllCommentModel commentModel;
    private static int commentNum = 0;//0:评论   1:回复评论 2:回复评论2
    private int DETAIL_WHO = 1;//从哪个页面进入的  返回发送广播刷新哪一个界面
    private int REFRESHORLOADMORE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImgTransparent(this);
        setContentView(R.layout.activity_all_comment);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initGetIntent();
        softKeyboardListnenr();
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            contentId = intent.getStringExtra("contentId");
            DETAIL_WHO = intent.getIntExtra("DETAIL_WHO", 0);
            if (UserModel.getUserModel().getIsLogin()) {
                memberId = UserModel.getUserModel().getMemberId();
            } else {
                memberId = "0";
            }
            getCommentList();
        }
    }

    private void initView() {
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);

        editTextComment = (EditText) findViewById(R.id.editText_comment);

        list = new ArrayList<>();
        tvTitleText.setText("全部评论");
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllCommentsAdapter(mContext, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                //发送广播 刷新评论数据
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                switch (DETAIL_WHO) {
                    case 1:
                        myEventBusModel.COMMENT_EVALUATE1 = true;
                        break;
                    case 2:
                        myEventBusModel.COMMENT_EVALUATE2 = true;
                        break;
                    case 3:
                        myEventBusModel.COMMENT_EVALUATE3 = true;
                        break;
                }

                EventBus.getDefault().post(myEventBusModel);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            finish();
            //发送广播 刷新评论数据
            MyEventBusModel myEventBusModel = new MyEventBusModel();
            switch (DETAIL_WHO) {
                case 1:
                    myEventBusModel.COMMENT_EVALUATE1 = true;
                    break;
                case 2:
                    myEventBusModel.COMMENT_EVALUATE2 = true;
                    break;
                case 3:
                    myEventBusModel.COMMENT_EVALUATE3 = true;
                    break;
            }

            EventBus.getDefault().post(myEventBusModel);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 回复的点击事件（最里层item）
     *
     * @param replyMemberId 被回复人的personId
     * @param personId      1级评论personId
     */
    public static void onHuiFuClick(String replyMemberId, String personId, List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_reply, String item_nickName, String replyCommentId) {
        replyMemberIds = replyMemberId;
        personIds = personId;
        list_replys = list_reply;
        item_nickNames = item_nickName;
        replyCommentIds = replyCommentId;
        //打开软键盘必须设置
        editTextComment.setFocusable(true);
        editTextComment.setFocusableInTouchMode(true);
        editTextComment.requestFocus();
        //打开软键盘
        InputMethodManager imm2 = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        commentNum = 2;
    }

    /**
     * 回复的点击事件（外层item）
     *
     * @param replyMemberId
     */
    public static void onHuiFuClickAll(String replyMemberId, String replyCommentId, List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_reply) {
        replyMemberIds = replyMemberId;
        replyCommentIds = replyCommentId;
        list_replys = list_reply;
        //打开软键盘必须设置
        editTextComment.setFocusable(true);
        editTextComment.setFocusableInTouchMode(true);
        editTextComment.requestFocus();
        //打开软键盘
        InputMethodManager imm2 = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        commentNum = 1;
    }

    /**
     * 软键盘显示与隐藏的监听
     */
    private void softKeyboardListnenr() {
        SoftKeyBoardListener.setListener(AllCommentActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {/*软键盘显示*/

            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏*/
                commentNum = 0;
            }
        });
    }

    /**
     * 评论列表
     */
    private void getCommentList() {
        OkGo.<String>get(Urls.GET_COMMENT_LIST)
                .tag(this)
                .params("contentId", contentId)//帖子id
                .params("memberId", memberId)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                if (REFRESHORLOADMORE == 1) {
                                    list.clear();
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishLoadmore();
                                }
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    commentModel = JSON.parseObject(response.body(), AllCommentModel.class);
                                    if (commentModel.getData().getShowBarComment() == null || commentModel.getData().getShowBarComment().size() == 0) {
                                        refreshLayout.setLoadmoreFinished(true);
                                    } else {
                                        list.addAll(commentModel.getData().getShowBarComment());
                                        adapter.notifyDataSetChanged();
                                    }
                                    if (list.size() > 0) {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.GONE);
                                        }
                                    } else {
                                        if (tvEmpty != null) {
                                            tvEmpty.setVisibility(View.VISIBLE);
                                            tvEmpty.setText("暂无评论,赶紧抢占沙发吧...");
                                        }
                                    }
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                    if (tvEmpty != null) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        tvEmpty.setText("暂无数据哦～");
                                    }
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
                        if (tvEmpty != null) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText("网络请求失败，请重试～");
                        }
                        if (refreshLayout != null) {
                            if (REFRESHORLOADMORE == 1) {
                                list.clear();
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadmore();
                            }
                        }
                    }
                });
    }

    @OnClick({R.id.btn_show_release})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show_release://发布按钮
                if (StringUtil.isLogin(AllCommentActivity.this)) {
                    if (!editTextComment.getText().toString().equals("")) {
                        switch (commentNum) {
                            case 0:
                                addComment(editTextComment.getText().toString());
                                break;
                            case 1://外层回复
                                try {
                                    replyDynamic(contentId, replyMemberIds, replyCommentIds, editTextComment.getText().toString(), 2);
                                } catch (Exception e) {

                                }
                                break;
                            case 2://里层回复
                                try {
                                    replyDynamic(contentId, replyMemberIds, personIds, editTextComment.getText().toString(), 3);
                                } catch (Exception e) {

                                }
                                break;
                        }

                    } else {
                        RxToast.showToast("写点你的心得吧～");
                    }
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
        }
    }

    /**
     * 发表评论
     */
    private void addComment(final String context) {
        OkGo.<String>get(Urls.ADD_COMMENT)
                .tag(this)
                .params("memberId", memberId)
                .params("contentId", contentId)//帖子ID
                .params("context", context)//评论内容
                .execute(new StringDialogCallback(this, "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //RxToast.showToast("评论成功，你的评论就是我的动力~");
                                    AllCommentModel.DataBean.ShowBarCommentBean showBarCommentBean = new AllCommentModel.DataBean.ShowBarCommentBean();
                                    UserModel userModel = UserModel.getUserModel();
                                    showBarCommentBean.setCommentHeadImgUrl(userModel.getImgUrl());
                                    showBarCommentBean.setNick(userModel.getRealname());
                                    showBarCommentBean.setContext(context);
                                    showBarCommentBean.setCreateTime(data.optJSONObject("data").optString("createTime"));
                                    showBarCommentBean.setIsLikeComment(false);
                                    showBarCommentBean.setCommentPersonId(Long.valueOf(userModel.getMemberId()));
                                    showBarCommentBean.setLikeCommentNum(0);
                                    showBarCommentBean.setId(Long.parseLong(data.optJSONObject("data").optString("commentId")));

                                    list.add(0, showBarCommentBean);
                                    tvEmpty.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();

                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
                                    editTextComment.setText("");
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
     * 回复评论
     *
     * @param contentId
     * @param replyMemberId
     * @param commentId
     * @param replyContext
     */
    private void replyDynamic(String contentId, String replyMemberId, String commentId, final String replyContext, final int commentType) {
        OkGo.<String>get(Urls.REPLY_DYNAMIC)
                .tag(this)
                .params("contentId", contentId)//帖子id
                .params("memberId", memberId)
                .params("replyMemberId", replyMemberId)//被回复人id
                .params("commentId", commentId)//你要回复的那条评论的id(评论列表查询出来的返回值中的id)
                .params("replyCommentId", replyCommentIds)//回复的评论的子评论ID
                .params("replyContext", replyContext)//回复内容
                .execute(new StringDialogCallback(this, "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //RxToast.showToast("回复成功");
                                    AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean replyCommentBean = new AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean();
                                    UserModel userModel = UserModel.getUserModel();
                                    //回复的Id  点赞需要的commentId
                                    replyCommentBean.setId(data.optJSONObject("data").optLong("replyId"));
                                    replyCommentBean.setCommentHeadImgUrl(userModel.getImgUrl());
                                    replyCommentBean.setNick(userModel.getRealname());
                                    replyCommentBean.setContext(replyContext);
                                    replyCommentBean.setCreateTime(data.optJSONObject("data").optString("createTime"));
                                    replyCommentBean.setIsLikeReplyComment(false);
                                    replyCommentBean.setLikeReplyCommentNum(0);
                                    replyCommentBean.setCommentPersonId(Long.valueOf(userModel.getMemberId()));
                                    replyCommentBean.setReplyNick(item_nickNames);
                                    replyCommentBean.setCommentType(commentType);
                                    list_replys.add(0, replyCommentBean);
                                    AllCommentsAdapter.freshItemAdapter(list_replys);
                                    adapter.notifyDataSetChanged();
                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
                                    editTextComment.setText("");
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

    @Override
    public void allHuiFuOnclick(int position) {
        boolean testFlag = list.get(position).isTestFlag();
        list.get(position).setTestFlag(!testFlag);
        adapter.notifyDataSetChanged();
    }

    /**
     * 上拉加载
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 0;
        currentPage++;
        getCommentList();
    }

    /**
     * 下拉刷新
     *
     * @param refreshlayout
     */
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 1;
        currentPage = 1;
        getCommentList();
    }

}

package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.adapter.AllCommentsAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AllCommentModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ImageUtils;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.DrawableCenterTextView;
import com.merrichat.net.view.SoftKeyBoardListener;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.app.MerriApp.mContext;

/**
 * 视频评论
 * Created by amssy on 18/4/16.
 */

public class VideoCommentDialog extends DialogFragment implements AllCommentsAdapter.OnItemClickListener, OnLoadmoreListener, OnRefreshListener {
    private static String item_nickNames;
    private static String replyCommentIds;
    /**
     * 关闭按钮
     */
    @BindView(R.id.iv_close_comment)
    ImageView ivCloseComment;
    @BindView(R.id.tv_empty)
    DrawableCenterTextView tvEmpty;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.tv_comment_title)
    TextView tvCommentTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rel_title)
    RelativeLayout relTitle;
    @BindView(R.id.editText_comment)
    TextView editTextComment;

    private String contentId;//帖子ID
    private int currentPage = 1;
    private String memberId;
    private AllCommentModel commentModel;
    private static int commentNum = 0;//0:评论   1:回复评论 2:回复评论2
    private int REFRESHORLOADMORE = 1;
    private static String replyMemberIds;//被回复人的id
    private static String personIds;//最外层id

    private List<AllCommentModel.DataBean.ShowBarCommentBean> list = new ArrayList<>();
    static List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_replys = new ArrayList<>();

    private AllCommentsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private NiceDialog niceDialog;
    private ClearEditText showEditText;
    private int commentNumber;//评论总数

    public static VideoCommentDialog getInstance(Context mContext, FragmentManager fm) {
        String tag = VideoCommentDialog.class.getName();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, tag);
            VideoCommentDialog dialogFragment = (VideoCommentDialog) fragment;
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);//设置取消标题栏
            dialogFragment.setCancelable(true);//外围点击 dismiss
            return dialogFragment;
        } else {
            return (VideoCommentDialog) fragment;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        //将对话框内部的背景设为透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setWindowAnimations(R.style.AnimBottom);
        window.setGravity(Gravity.BOTTOM);//居中显示
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        //将对话框外部的背景设为透明
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        windowParams.height = (int) (StringUtil.getHeight(getActivity()) * 0.5);
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(windowParams);

        // 去掉对话框默认标题栏
        View view = inflater.inflate(R.layout.video_comment_dialog, container, false);
        ButterKnife.bind(this, view);

        MerriApp.COMMENT_LOG = 1;
        EventBus.getDefault().register(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            contentId = arguments.getString("contentId");
            commentNumber = arguments.getInt("commentNumber");
        }
        if (UserModel.getUserModel().getIsLogin()) {
            memberId = UserModel.getUserModel().getMemberId();
        } else {
            memberId = "0";
        }

        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllCommentsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        softKeyboardListnenr();

        getCommentList();

        return view;
    }


    /**
     * 判断弹窗是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    /**
     * 显示DialogFragment(注此方法会衍生出状态值问题(本人在正常使用时并未出现过))
     *
     * @param manager
     * @param tag
     * @param isResume 在Fragment中使用可直接传入isResumed()
     *                 在FragmentActivity中使用可自定义全局变量 boolean isResumed 在onResume()和onPause()中分别传人判断为true和false
     */
    public void show(FragmentManager manager, String tag, boolean isResume) {
        if (!isShowing()) {
            if (isResume) {
                //正常显示
                if (!isAdded()) {
                    show(manager, tag);
                } else {
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.show(this);
                    ft.commit();
                }
            } else {
                //注 此方法会衍生出一些状态问题,慎用（在原代码中 需要设置  mDismissed = false 和 mShownByMe = true 并未在此引用到,如果需要用到相关判断值,此方法不可用）
                FragmentTransaction ft = manager.beginTransaction();
                if (!isAdded()) {
                    ft.add(this, tag);
                } else {
                    ft.show(this);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    /**
     * 关闭DialogFragment
     *
     * @param isResume 在Fragment中使用可直接传入isResumed()
     *                 在FragmentActivity中使用可自定义全局变量 boolean isResumed 在onResume()和onPause()中分别传人判断为true和false
     */
    public void dismiss(boolean isResume) {
        if (isResume) {
            dismiss();
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (isShowing()) {
            super.dismissAllowingStateLoss();
        }
    }

    @OnClick({R.id.iv_close_comment, R.id.editText_comment})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_comment:
                dismiss();
                break;
            case R.id.editText_comment://发表评论
                if (StringUtil.isLogin(getActivity())) {
                    niceDialog = NiceDialog.init();
                    niceDialog.setLayoutId(R.layout.item_fabu_pinglun)
                            .setConvertListener(new ViewConvertListener() {
                                @Override
                                public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                    showEditText = holder.getView(R.id.show_editText);
                                    final Button btnShowRelease = holder.getView(R.id.btn_show_release);
                                    btnShowRelease.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (MerriUtils.isFastDoubleClick2()) {
                                                return;
                                            }
                                            String context = showEditText.getText().toString();
                                            if (!TextUtils.isEmpty(context)) {
                                                //评论
                                                addComment(context);

                                            } else {
                                                RxToast.showToast("说点你的心得吧");
                                            }
                                        }
                                    });
                                    showEditText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(showEditText, 0);
                                        }
                                    });
                                }
                            })
                            .setShowBottom(true)
                            .show(getActivity().getSupportFragmentManager());
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
        }
    }

//    /**
//     * 回复的点击事件（最里层item）
//     *
//     * @param replyMemberId 被回复人的personId
//     * @param personId      1级评论personId
//     */
//    public static void onHuiFuClick(String replyMemberId, String personId, List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_reply, String item_nickName, String replyCommentId) {
//        replyMemberIds = replyMemberId;
//        personIds = personId;
//        list_replys = list_reply;
//        item_nickNames = item_nickName;
//        replyCommentIds = replyCommentId;
//
//    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        //外层回复
        if (myEventBusModel.COMMENT_LOG) {
            replyMemberIds = myEventBusModel.replyMemberId;
            replyCommentIds = myEventBusModel.replyCommentId;
            list_replys = myEventBusModel.list_reply;
            item_nickNames = myEventBusModel.item_nickName;
            AddComment(0);
        }
        //里层回复
        else if (myEventBusModel.COMMENT_LOG_1) {
            replyMemberIds = myEventBusModel.replyMemberId;
            personIds = myEventBusModel.personId;
            list_replys = myEventBusModel.list_reply;
            item_nickNames = myEventBusModel.item_nickName;
            replyCommentIds = myEventBusModel.replyCommentId;
            AddComment(1);
        }
    }

    /**
     * 回复评论
     */
    private void AddComment(final int position) {
        if (StringUtil.isLogin(MerriApp.mContext)) {
            niceDialog = NiceDialog.init();
            niceDialog.setLayoutId(R.layout.item_fabu_pinglun)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            showEditText = holder.getView(R.id.show_editText);
                            showEditText.setHint("回复给:" + item_nickNames);
                            final Button btnShowRelease = holder.getView(R.id.btn_show_release);
                            btnShowRelease.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (MerriUtils.isFastDoubleClick2()) {
                                        return;
                                    }
                                    String context = showEditText.getText().toString();
                                    if (!TextUtils.isEmpty(context)) {
                                        if (position == 0) {
                                            //外层回复
                                            try {
                                                replyDynamic(contentId, replyMemberIds, replyCommentIds, context, 2);
                                            } catch (Exception e) {

                                            }
                                        } else {
                                            //里层回复
                                            try {
                                                replyDynamic(contentId, replyMemberIds, personIds, context, 3);
                                            } catch (Exception e) {

                                            }
                                        }

                                    } else {
                                        RxToast.showToast("说点你的心得吧");
                                    }
                                }
                            });
                            showEditText.post(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager imm = (InputMethodManager) MerriApp.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(showEditText, 0);
                                }
                            });
                        }
                    })
                    .setShowBottom(true)
                    .show(getActivity().getSupportFragmentManager());
        } else {
            RxToast.showToast("请先登录哦");
        }
    }
//    /**
//     * 回复的点击事件（外层item）
//     *
//     * @param replyMemberId
//     */
//    public static void onHuiFuClickAll(String replyMemberId, String replyCommentId, List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_reply) {
//        replyMemberIds = replyMemberId;
//        replyCommentIds = replyCommentId;
//        list_replys = list_reply;
//        //打开软键盘必须设置
//        editTextComment.setFocusable(true);
//        editTextComment.setFocusableInTouchMode(true);
//        editTextComment.requestFocus();
//        //打开软键盘
//        InputMethodManager imm2 = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        commentNum = 1;
//    }

    /**
     * 软键盘显示与隐藏的监听
     */
    private void softKeyboardListnenr() {
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
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
                .execute(new StringDialogCallback() {
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
                                    tvCommentTitle.setText("评论(" + commentNumber + ")");
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

    /**
     * 发表评论
     */
    private void addComment(final String context) {
        OkGo.<String>get(Urls.ADD_COMMENT)
                .tag(this)
                .params("memberId", memberId)
                .params("contentId", contentId)//帖子ID
                .params("context", context)//评论内容
                .execute(new StringDialogCallback(getContext(), "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    RxToast.showToast("评论成功，你的评论就是我的动力~");
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
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
                                    niceDialog.dismiss();
                                    commentNumber += 1;
                                    tvCommentTitle.setText("评论(" + commentNumber + ")");
                                    editTextComment.setText("评论才是最真实的哦~");
                                    sendMessage();

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
                .execute(new StringDialogCallback(getContext(), "正在发表评论...") {
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
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
                                    niceDialog.dismiss();
                                    commentNumber += 1;
                                    tvCommentTitle.setText("评论(" + commentNumber + ")");
                                    editTextComment.setText("评论才是最真实的哦~");
                                    sendMessage();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 评论成功 发送消息 刷新公分
     */
    private void sendMessage() {
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.VIDEO_COMMENT_SUCCESS = true;
        myEventBusModel.commentNumber = commentNumber;
        EventBus.getDefault().post(myEventBusModel);
    }
}

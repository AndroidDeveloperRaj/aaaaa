package com.merrichat.net.activity.message;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by amssy on 17/11/14.
 * 回复评论
 */

public class ReplyToCommentActivity extends BaseActivity {
    @BindView(R.id.iv_finish)
    ImageView iv_finish;

    @BindView(R.id.tv_send)
    TextView tv_send;

    @BindView(R.id.et_write_review)
    EditText etWriteReview;

    private String contentId;
    private String replyMemberId;
    private String commentId;
    private String replyCommentId;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_to_comment);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        contentId = getIntent().getStringExtra("contentId");
        replyMemberId = getIntent().getStringExtra("replyMemberId");
        commentId = getIntent().getStringExtra("commentId");
        replyCommentId = getIntent().getStringExtra("replyCommentId");
        name = getIntent().getStringExtra("name");
        etWriteReview.setHint("回复 " + name);
    }

    @OnClick({R.id.iv_finish, R.id.tv_send})
    public void onViewClicked(View view) {
        KeyboardUtil.hideKeyboard(view);
        switch (view.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_send:
                if (TextUtils.isEmpty(etWriteReview.getText())) {
                    RxToast.showToast("请输入评论内容");
                } else {
                    replyDynamic(etWriteReview.getText().toString());
                }
                break;
        }
    }


    /**
     * 回复评论
     *
     * @param replyContext
     */
    private void replyDynamic(final String replyContext) {
        OkGo.<String>get(Urls.REPLY_DYNAMIC)
                .tag(this)
                .params("contentId", contentId)//帖子id
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("replyMemberId", replyMemberId)//被回复人id
                .params("commentId", commentId)//你要回复的那条评论的id(评论列表查询出来的返回值中的id)
                .params("replyCommentId", replyCommentId)//回复的评论的子评论ID
                .params("replyContext", replyContext)//回复内容
                .execute(new StringDialogCallback(this, "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    RxToast.showToast("回复成功");
                                    etWriteReview.setText("");
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.REFRESH_PRAISE_COMMENT = true;
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

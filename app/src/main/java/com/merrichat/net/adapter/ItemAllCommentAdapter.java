package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.circlefriend.VideoCommentDialog;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.circlefriends.AllCommentActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AllCommentModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import xyz.hanks.library.SmallBang;
//import xyz.hanks.library.SmallBangListener;

/**
 * Created by amssy on 17/11/16.
 */

public class ItemAllCommentAdapter extends RecyclerView.Adapter<ItemAllCommentAdapter.ViewHolder> {


    private Context context;
    private List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list;
    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;
    //    private List<AllCommentModel> list;
    private boolean testFlag;

    private String personId;
    private String contentId;//帖子ID
    private String commentId;
    private int parent_position;
    private String evaluateType = "1";////1点赞, 2取消点赞
//    private SmallBang mSmallBang;

    public ItemAllCommentAdapter(Context context, boolean testFlag, int parent_position, List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list, String personId, String commentId) {
        this.context = context;
        this.testFlag = testFlag;
        this.list = list;
        this.personId = personId;
        this.commentId = commentId;
        this.parent_position = parent_position;
//        mSmallBang = SmallBang.attach2Window((Activity) context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_comment_item, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        // 绑定数据
        holder.cvItemPhoto.setImageURI(list.get(position).getCommentHeadImgUrl());

        SpannableString spannableString1 = new SpannableString(list.get(position).getContext());
        SpannableString spannableString = new SpannableString(list.get(position).getContext() + "   " + (TextUtils.isEmpty(list.get(position).getCreateTime()) ? "" : TimeUtil.getStrTime1(String.valueOf(list.get(position).getCreateTime()))));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
        spannableString.setSpan(colorSpan, spannableString1.length() + 3, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tvItemContant.setText(spannableString);

//        holder.tvItemContant.setText(list.get(position).getContext());
//        holder.tvItemTime.setText(TimeUtil.getStrTime1(String.valueOf(list.get(position).getCreateTime())));
        holder.showEvaluateCheck.setChecked(list.get(position).isIsLikeReplyComment());
        holder.showEvaluateCheck.setText(list.get(position).getLikeReplyCommentNum() == 0 ? "" : list.get(position).getLikeReplyCommentNum() + "");

//        holder.tvItemName.setText(list.get(position).getNick());
        if (list.get(position).getCommentType() == 3) {//是否是3  表示都有@
            SpannableString spannableString2 = new SpannableString(list.get(position).getNick() + "回复" + "@" + list.get(position).getReplyNick());
            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#666666"));
            spannableString2.setSpan(colorSpan1, list.get(position).getNick().length(), list.get(position).getNick().length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.tvItemName.setText(spannableString2);
        } else {
            holder.tvItemName.setText(list.get(position).getNick());
        }

        if (list.size() > 2) {
            holder.view_line_bottom.setVisibility(View.VISIBLE);
        } else {
            if (list.size() == 1 || position == 1)
                holder.view_line_bottom.setVisibility(View.GONE);
        }

        holder.cvItemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                long personId = list.get(position).getCommentPersonId();
                String memberId = UserModel.getUserModel().getMemberId();
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                if (memberId.equals(String.valueOf(personId))) {
                    RxActivityTool.skipActivity(context, MyDynamicsAty.class);
                } else {
                    Bundle bundle = new Bundle();
                    if (list != null) {
                        bundle.putLong("hisMemberId", list.get(position).getCommentPersonId());
                        bundle.putString("hisImgUrl", list.get(position).getCommentHeadImgUrl());
                        bundle.putString("hisNickName", list.get(position).getReplyNick());
                    }
                    RxActivityTool.skipActivity(context, HisYingJiAty.class, bundle);
                }
            }
        });

        holder.relGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                //本人不能回复本人
                if (!String.valueOf(list.get(position).getCommentPersonId()).equals(UserModel.getUserModel().getMemberId())) {
                    if (MerriApp.COMMENT_LOG == 0) {
                        AllCommentActivity.onHuiFuClick(String.valueOf(list.get(position).getCommentPersonId()), commentId, list, list.get(position).getNick(), String.valueOf(list.get(position).getId()));
                    } else {
                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                        myEventBusModel.COMMENT_LOG_1 = true;
                        myEventBusModel.replyMemberId = String.valueOf(list.get(position).getCommentPersonId());
                        myEventBusModel.personId = commentId;
                        myEventBusModel.list_reply = list;
                        myEventBusModel.item_nickName = list.get(position).getNick();
                        myEventBusModel.replyCommentId = String.valueOf(list.get(position).getId());
                        EventBus.getDefault().post(myEventBusModel);
                    }
                }
            }
        });

        //点赞
        holder.showEvaluateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    holder.showEvaluateCheck.setChecked(false);
                    return;
                }
                commentId = String.valueOf(list.get(position).getId());
                if (holder.showEvaluateCheck.isChecked()) {
                    evaluateType = "1";
                    addSelect(holder.showEvaluateCheck);
                } else {
                    evaluateType = "2";
                }
                OkGo.<String>get(Urls.LIKE_SHOW_COMMENT)
                        .tag(this)
                        .params("memberId", UserModel.getUserModel().getMemberId())
                        .params("commentId", commentId)
                        .params("flag", "3")
                        .params("type", evaluateType)
                        .execute(new StringDialogCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body());
                                        if (jsonObject.optBoolean("success")) {
                                            if (holder.showEvaluateCheck.isChecked()) {
                                                if (!holder.showEvaluateCheck.getText().toString().equals("") && list.get(position).getLikeReplyCommentNum() != 0) {
                                                    holder.showEvaluateCheck.setText("" + (Integer.valueOf(holder.showEvaluateCheck.getText().toString()) + 1));
                                                    AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean bean = list.get(position);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum((Integer.valueOf(holder.showEvaluateCheck.getText().toString()) + 1));
                                                    list.remove(position);
                                                    list.add(position, bean);
                                                } else if (holder.showEvaluateCheck.getText().toString().equals("") && list.get(position).isIsLikeReplyComment()) {
                                                    holder.showEvaluateCheck.setText("" + list.get(position).getLikeReplyCommentNum());
                                                    AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean bean = list.get(position);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum(list.get(position).getLikeReplyCommentNum());
                                                    list.remove(position);
                                                    list.add(position, bean);
                                                } else {
                                                    holder.showEvaluateCheck.setText("" + (list.get(position).getLikeReplyCommentNum() + 1));
                                                    AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean bean = list.get(position);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum(list.get(position).getLikeReplyCommentNum() + 1);
                                                    list.remove(position);
                                                    list.add(position, bean);
                                                }
                                            } else {
                                                if (!holder.showEvaluateCheck.getText().toString().equals("") && Integer.valueOf(holder.showEvaluateCheck.getText().toString()) != 1) {
                                                    holder.showEvaluateCheck.setText("" + (Integer.valueOf(holder.showEvaluateCheck.getText().toString()) - 1));
                                                    AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean bean = list.get(position);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum((Integer.valueOf(holder.showEvaluateCheck.getText().toString()) - 1));
                                                    list.remove(position);
                                                    list.add(position, bean);
                                                } else {
                                                    holder.showEvaluateCheck.setText("");
                                                    AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean bean = list.get(position);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum(0);
                                                    list.remove(position);
                                                    list.add(position, bean);
                                                }
                                            }
                                        } else {
                                            RxToast.showToast("" + jsonObject.optString("message"));
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
        });
    }

    @Override
    public int getItemCount() {
        int itemCount;
        if (list.size() < 3) {
            itemCount = list.size();
        } else {
            if (testFlag) {
                itemCount = list.size();
            } else {
                itemCount = 2;
            }
        }
        return itemCount;
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 点赞动效
     *
     * @param view
     */
    public void addSelect(View view) {
//        mSmallBang.bang(view, 25, new SmallBangListener() {
//            @Override
//            public void onAnimationStart() {
//            }
//
//            @Override
//            public void onAnimationEnd() {
//
//            }
//        });
    }


    public interface OnItemClickListener {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv_item_photo)
        SimpleDraweeView cvItemPhoto;


        @BindView(R.id.tv_item_name)
        TextView tvItemName;


        @BindView(R.id.tv_item_contant)
        TextView tvItemContant;


        @BindView(R.id.tv_item_time)
        TextView tvItemTime;


        @BindView(R.id.tv_item_hui_fu)
        TextView tvItemHuiFu;

        @BindView(R.id.rel_group)
        RelativeLayout relGroup;

        @BindView(R.id.show_evaluate_check)
        CheckBox showEvaluateCheck;

        @BindView(R.id.tv_hui_fu_dian)
        TextView tvHuiFuDian;

        @BindView(R.id.view_line_bottom)
        TextView view_line_bottom;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

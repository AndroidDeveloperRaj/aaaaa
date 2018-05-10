package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AllCommentModel;
import com.merrichat.net.model.CircleDetailModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.TimeUtil;
import com.merrichat.net.view.DrawableCenterTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/11/17
 */

public class EvaluateDetailAdapter extends RecyclerView.Adapter<EvaluateDetailAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;

    private Context context;

    private List<CircleDetailModel.DataBean.CommentListBean> listBeans;
    private String commentId;
    private String evaluateType = "1";//1点赞, 2取消点赞

    public EvaluateDetailAdapter(Context context, List<CircleDetailModel.DataBean.CommentListBean> listBeans) {
        this.context = context;
        this.listBeans = listBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evalutat_detail, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        //一级评论
        final CircleDetailModel.DataBean.CommentListBean commentListBean = listBeans.get(position);
        final String toMemberId = String.valueOf(commentListBean.getCommentPersonId());
        String headImgUrl = commentListBean.getCommentHeadImgUrl();
        String nickName = commentListBean.getNick();
        String content = commentListBean.getContext();
        String createTime = TimeUtil.getStrTime1(String.valueOf(commentListBean.getCreateTime()));
        //格式化出来的时间也会跟随系统系统自动变化，比如说如果系统默认语言是中文，格式化出来的时间是"一分钟前"，而如果系统语言是英语，格式化的时间就变成"one minute ago"，省去了翻译字符串的麻烦
//        String createTime = (String) DateUtils.getRelativeTimeSpanString(
//                commentListBean.getCreateTime(),
//                System.currentTimeMillis(),
//                DateUtils.MINUTE_IN_MILLIS,
//                DateUtils.FORMAT_SHOW_DATE);

        int commentNum = commentListBean.getLikeCommentNum();
        boolean isLikeComment = commentListBean.isIsLikeComment();

        holder.cvCommentPhoto.setImageURI(headImgUrl);
        holder.tvCommentName.setText(nickName);

        SpannableString spannableString1 = new SpannableString(content);
        SpannableString spannableString = new SpannableString(content + "   " + (TextUtils.isEmpty(createTime) ? "" : String.valueOf(createTime)));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
        spannableString.setSpan(colorSpan, spannableString1.length() + 3, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        holder.tvCommentContant.setText(spannableString);
        //holder.tvDateTime.setText(createTime);
        //是否点
        if (isLikeComment) {
            holder.showEvaluateCheck.setChecked(true);
        } else {
            holder.showEvaluateCheck.setChecked(false);
        }
        //评论点赞
        holder.showEvaluateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (StringUtil.isLogin(context)) {
                    if (isChecked) {
                        likeShowComment(holder, UserModel.getUserModel().getMemberId(), String.valueOf(commentListBean.getId()), "3", "1", commentListBean.getLikeCommentNum(), commentListBean.isIsLikeComment());
                    } else {
                        likeShowComment(holder, UserModel.getUserModel().getMemberId(), String.valueOf(commentListBean.getId()), "3", "2", commentListBean.getLikeCommentNum(), commentListBean.isIsLikeComment());
                    }
                } else {
                    RxToast.showToast("请先登录哦");
                    if (isChecked) {
                        holder.showEvaluateCheck.setChecked(false);
                    } else {
                        holder.showEvaluateCheck.setChecked(true);
                    }
                }
            }
        });

        holder.cvCommentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                long personId = commentListBean.getCommentPersonId();
                String memberId = UserModel.getUserModel().getMemberId();
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                if (memberId.equals(String.valueOf(personId))) {
                    RxActivityTool.skipActivity(context, MyDynamicsAty.class);
                } else {
                    Bundle bundle = new Bundle();
                    if (commentListBean != null) {
                        bundle.putLong("hisMemberId", commentListBean.getCommentPersonId());
                        bundle.putString("hisImgUrl", commentListBean.getCommentHeadImgUrl());
                        bundle.putString("hisNickName", commentListBean.getReplyMemberNick());
                    }
                    RxActivityTool.skipActivity(context, HisYingJiAty.class, bundle);
                }
            }
        });

        //点赞数
        if (commentNum == 0) {
            holder.showEvaluateCheck.setText("");
        } else {
            holder.showEvaluateCheck.setText("" + commentNum);
        }

        //回复
        holder.rel_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                //判断是否是本人（自己不能回复自己）
                if (UserModel.getUserModel().getMemberId().equals(toMemberId)) {
                    onItemClickListener.onHuiFuClick(position);
                } else {

                }
            }
        });

        //子级评论
        final List<CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean> replyCommentBean = commentListBean.getReplyComment();
        if (replyCommentBean != null && replyCommentBean.size() > 0) {
            holder.view_line1.setVisibility(View.VISIBLE);
            holder.view_line2.setVisibility(View.GONE);
            holder.view_line3.setVisibility(View.VISIBLE);

            holder.linChildComment.setVisibility(View.VISIBLE);
            holder.cvItemPhoto.setImageURI(replyCommentBean.get(0).getCommentHeadImgUrl());

            if (replyCommentBean.get(0).getCommentType() == 3) {//是否是3  表示都有@
                SpannableString spannableString2 = new SpannableString(replyCommentBean.get(0).getNick() + "回复" + "@" + replyCommentBean.get(0).getReplyNick());
                ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#666666"));
                spannableString2.setSpan(colorSpan1, replyCommentBean.get(0).getNick().length(), replyCommentBean.get(0).getNick().length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.tvItemName.setText(spannableString2);
            } else {
                holder.tvItemName.setText(replyCommentBean.get(0).getNick());
            }
            holder.showEvaluateCheckChild.setChecked(replyCommentBean.get(0).isIsLikeReplyComment());
            holder.showEvaluateCheckChild.setText(replyCommentBean.get(0).getLikeReplyCommentNum() == 0 ? "" : replyCommentBean.get(0).getLikeReplyCommentNum() + "");
            SpannableString spannableStringtvItemContant = new SpannableString(replyCommentBean.get(0).getContext());
            SpannableString spannableStringtvItemContantS = new SpannableString(replyCommentBean.get(0).getContext() + "   " + (TextUtils.isEmpty(String.valueOf(replyCommentBean.get(0).getCreateTime())) ? "" : TimeUtil.getStrTime1(String.valueOf(replyCommentBean.get(0).getCreateTime()))));
            ForegroundColorSpan colorSpantvItemContant = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
            spannableStringtvItemContantS.setSpan(colorSpantvItemContant, spannableStringtvItemContant.length() + 3, spannableStringtvItemContantS.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.tvItemContant.setText(spannableStringtvItemContantS);

            if (replyCommentBean.size() >= 2) {

                holder.ll_hui_fu.setVisibility(View.VISIBLE);
                holder.linChildComment1.setVisibility(View.VISIBLE);
                holder.view_line_bottom.setVisibility(View.VISIBLE);

                holder.cvItemPhoto1.setImageURI(replyCommentBean.get(1).getCommentHeadImgUrl());

                if (replyCommentBean.get(1).getCommentType() == 3) {//是否是3  表示都有@
                    SpannableString spannableString2 = new SpannableString(replyCommentBean.get(1).getNick() + "回复" + "@" + replyCommentBean.get(1).getReplyNick());
                    ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#666666"));
                    spannableString2.setSpan(colorSpan1, replyCommentBean.get(1).getNick().length(), replyCommentBean.get(1).getNick().length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    holder.tvItemName1.setText(spannableString2);
                } else {
                    holder.tvItemName1.setText(replyCommentBean.get(1).getNick());
                }

                holder.tvItemName1.setText(replyCommentBean.get(1).getNick());
                holder.showEvaluateCheckChild1.setChecked(replyCommentBean.get(1).isIsLikeReplyComment());
                holder.showEvaluateCheckChild1.setText(replyCommentBean.get(1).getLikeReplyCommentNum() == 0 ? "" : replyCommentBean.get(1).getLikeReplyCommentNum() + "");
                SpannableString spannableStringtvItemContant1 = new SpannableString(replyCommentBean.get(1).getContext());
                SpannableString spannableStringtvItemContant1S = new SpannableString(replyCommentBean.get(1).getContext() + "   " + (TextUtils.isEmpty(String.valueOf(replyCommentBean.get(1).getCreateTime())) ? "" : TimeUtil.getStrTime1(String.valueOf(replyCommentBean.get(1).getCreateTime()))));
                ForegroundColorSpan colorSpantvItemContant1 = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
                spannableStringtvItemContant1S.setSpan(colorSpantvItemContant1, spannableStringtvItemContant1.length() + 3, spannableStringtvItemContant1S.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                holder.tvItemContant1.setText(spannableStringtvItemContant1S);
            } else {
                holder.linChildComment1.setVisibility(View.GONE);
                holder.ll_hui_fu.setVisibility(View.GONE);
                holder.view_line_bottom.setVisibility(View.GONE);
            }

        } else {
            holder.linChildComment.setVisibility(View.GONE);
            holder.linChildComment1.setVisibility(View.GONE);
            holder.ll_hui_fu.setVisibility(View.GONE);
            holder.view_line1.setVisibility(View.GONE);
            holder.view_line2.setVisibility(View.VISIBLE);
            holder.view_line3.setVisibility(View.GONE);
        }

        //item点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(pos);
                }
            }
        });

        //全部回复的点击事件
        holder.llHuiFu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                onItemClickListener.onAllHuiFuClick(pos);
            }
        });

        //点赞1
        holder.showEvaluateCheckChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    holder.showEvaluateCheckChild.setChecked(false);
                    return;
                }
                commentId = String.valueOf(replyCommentBean.get(0).getId());
                if (holder.showEvaluateCheckChild.isChecked()) {
                    evaluateType = "1";
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
                                            if (holder.showEvaluateCheckChild.isChecked()) {
                                                holder.showEvaluateCheckChild.setText("" + (replyCommentBean.get(0).getLikeReplyCommentNum() + 1));
                                                CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean bean = replyCommentBean.get(0);
                                                bean.setIsLikeReplyComment(true);
                                                bean.setLikeReplyCommentNum(replyCommentBean.get(0).getLikeReplyCommentNum() + 1);
                                                replyCommentBean.remove(0);
                                                replyCommentBean.add(0, bean);
                                            } else {
                                                if (!holder.showEvaluateCheckChild.getText().toString().equals("") && Integer.valueOf(holder.showEvaluateCheckChild.getText().toString()) != 1) {
                                                    holder.showEvaluateCheckChild.setText("" + (replyCommentBean.get(0).getLikeReplyCommentNum() == 0 ? 0 : (replyCommentBean.get(0).getLikeReplyCommentNum() - 1)));
                                                    CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean bean = replyCommentBean.get(0);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum(replyCommentBean.get(0).getLikeReplyCommentNum() == 0 ? 0 : (replyCommentBean.get(0).getLikeReplyCommentNum() - 1));
                                                    replyCommentBean.remove(0);
                                                    replyCommentBean.add(0, bean);
                                                } else {
                                                    holder.showEvaluateCheckChild.setText("");
                                                    CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean bean = replyCommentBean.get(0);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum(0);
                                                    replyCommentBean.remove(0);
                                                    replyCommentBean.add(0, bean);
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

        //点赞1
        holder.showEvaluateCheckChild1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    holder.showEvaluateCheckChild1.setChecked(false);
                    return;
                }
                commentId = String.valueOf(replyCommentBean.get(1).getId());
                if (holder.showEvaluateCheckChild1.isChecked()) {
                    evaluateType = "1";
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
                                            if (holder.showEvaluateCheckChild1.isChecked()) {
                                                holder.showEvaluateCheckChild1.setText("" + (replyCommentBean.get(1).getLikeReplyCommentNum() + 1));
                                                CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean bean = replyCommentBean.get(1);
                                                bean.setIsLikeReplyComment(true);
                                                bean.setLikeReplyCommentNum(replyCommentBean.get(1).getLikeReplyCommentNum() + 1);
                                                replyCommentBean.remove(1);
                                                replyCommentBean.add(1, bean);

                                            } else {
                                                if (!holder.showEvaluateCheckChild1.getText().toString().equals("") && Integer.valueOf(holder.showEvaluateCheckChild1.getText().toString()) != 1) {
                                                    holder.showEvaluateCheckChild1.setText("" + (Integer.valueOf(holder.showEvaluateCheckChild1.getText().toString()) - 1));
                                                    CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean bean = replyCommentBean.get(1);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum((Integer.valueOf(holder.showEvaluateCheckChild1.getText().toString()) - 1));
                                                    replyCommentBean.remove(1);
                                                    replyCommentBean.add(1, bean);
                                                } else {
                                                    holder.showEvaluateCheckChild1.setText("");
                                                    CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean bean = replyCommentBean.get(1);
                                                    bean.setIsLikeReplyComment(true);
                                                    bean.setLikeReplyCommentNum(0);
                                                    replyCommentBean.remove(1);
                                                    replyCommentBean.add(1, bean);
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

        holder.cvItemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                if (UserModel.getUserModel().getMemberId().equals(toMemberId)) {
                    RxActivityTool.skipActivity(context, MyDynamicsAty.class);
                } else {
                    Bundle bundle = new Bundle();
                    if (commentListBean != null) {
                        bundle.putLong("hisMemberId", replyCommentBean.get(0).getCommentPersonId());
                        bundle.putString("hisImgUrl", replyCommentBean.get(0).getCommentHeadImgUrl());
                        bundle.putString("hisNickName", replyCommentBean.get(0).getReplyNick());
                    }
                    RxActivityTool.skipActivity(context, HisYingJiAty.class, bundle);
                }
            }
        });
        holder.cvItemPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                if (UserModel.getUserModel().getMemberId().equals(toMemberId)) {
                    RxActivityTool.skipActivity(context, MyDynamicsAty.class);
                } else {
                    Bundle bundle = new Bundle();
                    if (commentListBean != null) {
                        bundle.putLong("hisMemberId", replyCommentBean.get(1).getCommentPersonId());
                        bundle.putString("hisImgUrl", replyCommentBean.get(1).getCommentHeadImgUrl());
                        bundle.putString("hisNickName", replyCommentBean.get(1).getReplyNick());
                    }
                    RxActivityTool.skipActivity(context, HisYingJiAty.class, bundle);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listBeans.size() > 2 ? 2 : listBeans.size();
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
     * 评论点赞
     *
     * @param memberId
     * @param commentId 评论Id
     * @param flag      1外部人，2帖子 ,3评论
     * @param type      1点赞, 2取消点赞
     */
    private void likeShowComment(final ViewHolder holder, String memberId, String commentId, String flag, String type, final int likeCommentNum, final boolean isLike) {
        OkGo.<String>get(Urls.LIKE_SHOW_COMMENT)
                .tag(this)
                .params("memberId", memberId)
                .params("commentId", commentId)
                .params("flag", flag)
                .params("type", type)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    if (holder.showEvaluateCheck.isChecked()) {
                                        if (!holder.showEvaluateCheck.getText().toString().equals("") && likeCommentNum != 0) {
                                            holder.showEvaluateCheck.setText("" + (Integer.valueOf(holder.showEvaluateCheck.getText().toString()) + 1));
                                        } else if (holder.showEvaluateCheck.getText().toString().equals("") && isLike) {
                                            holder.showEvaluateCheck.setText("" + likeCommentNum);
                                        } else {
                                            holder.showEvaluateCheck.setText("" + (likeCommentNum + 1));
                                        }
                                    } else {
                                        if (!holder.showEvaluateCheck.getText().toString().equals("") && Integer.valueOf(holder.showEvaluateCheck.getText().toString()) != 1) {
                                            holder.showEvaluateCheck.setText("" + (Integer.valueOf(holder.showEvaluateCheck.getText().toString()) - 1));
                                        } else {
                                            holder.showEvaluateCheck.setText("");
                                        }
                                    }
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


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onHuiFuClick(int position);

        void onAllHuiFuClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 父评论
         */
        @BindView(R.id.cv_comment_photo)
        SimpleDraweeView cvCommentPhoto;
        @BindView(R.id.tv_comment_name)
        TextView tvCommentName;
        @BindView(R.id.tv_comment_contant)
        TextView tvCommentContant;
        @BindView(R.id.tv_date_time)
        TextView tvDateTime;
        @BindView(R.id.tv_hui_fu_dian)
        TextView tvHuiFuDian;
        @BindView(R.id.show_evaluate_check_group)
        CheckBox showEvaluateCheck;
        @BindView(R.id.rel_top)
        RelativeLayout rel_top;

        /**
         * 子评论
         */
        @BindView(R.id.lin_child_comment)
        LinearLayout linChildComment;
        @BindView(R.id.cv_item_photo)
        SimpleDraweeView cvItemPhoto;
        @BindView(R.id.tv_item_name)
        TextView tvItemName;
        @BindView(R.id.tv_item_contant)
        TextView tvItemContant;
        @BindView(R.id.show_evaluate_check_child)
        CheckBox showEvaluateCheckChild;

        @BindView(R.id.lin_child_comment1)
        LinearLayout linChildComment1;
        @BindView(R.id.cv_item_photo1)
        SimpleDraweeView cvItemPhoto1;
        @BindView(R.id.tv_item_name1)
        TextView tvItemName1;
        @BindView(R.id.tv_item_contant1)
        TextView tvItemContant1;
        @BindView(R.id.show_evaluate_check_child1)
        CheckBox showEvaluateCheckChild1;


        @BindView(R.id.tv_all_hui_fu)
        DrawableCenterTextView llHuiFu;

        @BindView(R.id.view_line1)
        View view_line1;
        @BindView(R.id.view_line2)
        View view_line2;
        @BindView(R.id.view_line3)
        View view_line3;
        @BindView(R.id.view_line_bottom)
        View view_line_bottom;

        @BindView(R.id.ll_hui_fu)
        LinearLayout ll_hui_fu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

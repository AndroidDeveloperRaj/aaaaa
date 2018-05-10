package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import com.merrichat.net.activity.circlefriend.VideoDashDialog;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.circlefriends.AllCommentActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AllCommentModel;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.TimeUtil;
import com.merrichat.net.view.DrawableCenterTextView;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

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
 * 全部评论adapter
 */

public class AllCommentsAdapter extends RecyclerView.Adapter<AllCommentsAdapter.ViewHolder> {

    public static ItemAllCommentAdapter itemAllCommentAdapter;
    static List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_reply;
    //    private SmallBang mSmallBang;
    private Context context;
    private LinearLayoutManager layoutManager;
    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private View comment_footer;
    private LinearLayout ll_hui_fu;
    private DrawableCenterTextView tvAllHuiFu;
    private List<AllCommentModel.DataBean.ShowBarCommentBean> list;
    private String evaluateType = "1";////1点赞, 2取消点赞
    private String commentId;

    public AllCommentsAdapter(Context context, List<AllCommentModel.DataBean.ShowBarCommentBean> list) {
        this.context = context;
        this.list = list;
//        mSmallBang = SmallBang.attach2Window((Activity) context);
    }

    public static void freshItemAdapter(List<AllCommentModel.DataBean.ShowBarCommentBean.ReplyCommentBean> list_replys) {
        list_reply = list_replys;
        if (itemAllCommentAdapter != null) {
            itemAllCommentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_comment, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        // 绑定数据
        holder.cvCommentPhoto.setImageURI(list.get(position).getCommentHeadImgUrl());
        holder.tvCommentName.setText(list.get(position).getNick());

        SpannableString spannableString1 = new SpannableString(list.get(position).getContext());
        SpannableString spannableString = new SpannableString(list.get(position).getContext() + "   " + (TextUtils.isEmpty(list.get(position).getCreateTime()) ? "" : TimeUtil.getStrTime1(String.valueOf(list.get(position).getCreateTime()))));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#D1D1D1"));
        spannableString.setSpan(colorSpan, spannableString1.length() + 3, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tvCommentContant.setText(spannableString);

//        holder.tvCommentContant.setText(list.get(position).getContext());
//        holder.tvDateTime.setText(TimeUtil.getStrTime1(String.valueOf(list.get(position).getCreateTime())));


        holder.showEvaluateCheck.setChecked(list.get(position).isIsLikeComment());
        holder.showEvaluateCheck.setText(list.get(position).getLikeCommentNum() == 0 ? "" : list.get(position).getLikeCommentNum() + "");

        //头像的点击事件
        holder.cvCommentPhoto.setOnClickListener(new View.OnClickListener() {
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
                        bundle.putString("hisNickName", list.get(position).getReplyMemberNick());
                    }
                    RxActivityTool.skipActivity(context, HisYingJiAty.class, bundle);
                }
            }
        });

        if (list.get(position).getReplyComment() != null && list.get(position).getReplyComment().size() > 0) {
            holder.view_line1.setVisibility(View.VISIBLE);
            holder.view_line2.setVisibility(View.GONE);
            holder.view_line3.setVisibility(View.VISIBLE);

            boolean testFlag = list.get(position).isTestFlag();
            comment_footer = LayoutInflater.from(context).inflate(R.layout.layout_comments_item_footer, null);
            ll_hui_fu = (LinearLayout) comment_footer.findViewById(R.id.ll_hui_fu);
            tvAllHuiFu = (DrawableCenterTextView) comment_footer.findViewById(R.id.tv_all_hui_fu);
            if (list.get(position).getReplyComment().size() > 2) {
                ll_hui_fu.setVisibility(View.VISIBLE);
                if (testFlag) {
                    tvAllHuiFu.setText("收起所有回复");
                    Drawable drawable = context.getResources().getDrawable(R.mipmap.icon_red_sj_top);
                    /**这一步必须要做,否则不会显示.*/
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//对图片进行压缩
                    /**设置图片位置，四个参数分别方位是左上右下，都设置为null就表示不显示图片*/
                    tvAllHuiFu.setCompoundDrawables(null, null, drawable, null);
                } else {
                    tvAllHuiFu.setText("查看" + (list.get(position).getReplyComment().size() - 2) + "条回复");
                    Drawable drawable = context.getResources().getDrawable(R.mipmap.icon_red_sj_down);
                    /**这一步必须要做,否则不会显示.*/
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//对图片进行压缩
                    /**设置图片位置，四个参数分别方位是左上右下，都设置为null就表示不显示图片*/
                    tvAllHuiFu.setCompoundDrawables(null, null, drawable, null);
                }
            } else {
                ll_hui_fu.setVisibility(View.GONE);
                tvAllHuiFu.setText("");
            }

            ll_hui_fu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int pos = holder.getLayoutPosition();
                        onItemClickListener.allHuiFuOnclick(pos);
                    }
                }
            });
            holder.recyclerViewItem.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            holder.recyclerViewItem.setLayoutManager(layoutManager);
            list_reply = list.get(position).getReplyComment();
            itemAllCommentAdapter = new ItemAllCommentAdapter(context, testFlag, position, list_reply, String.valueOf(list.get(position).getCommentPersonId()), String.valueOf(list.get(position).getId()));
            mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(itemAllCommentAdapter);
            if (list.get(position).getReplyComment().size() > 2) {
                mHeaderAndFooterWrapper.addFootView(comment_footer);
            }
            holder.recyclerViewItem.setAdapter(mHeaderAndFooterWrapper);
            mHeaderAndFooterWrapper.notifyDataSetChanged();
        }else {
            holder.view_line1.setVisibility(View.GONE);
            holder.view_line2.setVisibility(View.VISIBLE);
            holder.view_line3.setVisibility(View.GONE);
        }

        holder.relTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isLogin(context)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                //本人不能回复本人
                if (!String.valueOf(list.get(position).getCommentPersonId()).equals(UserModel.getUserModel().getMemberId())) {
                    if (MerriApp.COMMENT_LOG == 0) {
                        AllCommentActivity.onHuiFuClickAll(String.valueOf(list.get(position).getCommentPersonId()), String.valueOf(list.get(position).getId()), list.get(position).getReplyComment());
                    } else {
                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                        myEventBusModel.COMMENT_LOG = true;
                        myEventBusModel.replyMemberId = String.valueOf(list.get(position).getCommentPersonId());
                        myEventBusModel.replyCommentId = String.valueOf(list.get(position).getId());
                        myEventBusModel.list_reply = list.get(position).getReplyComment();
                        myEventBusModel.item_nickName = list.get(position).getNick();
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
                                        JSONObject data = new JSONObject(response.body());
                                        if (data.optBoolean("success")) {
                                            if (holder.showEvaluateCheck.isChecked()) {
                                                if (!holder.showEvaluateCheck.getText().toString().equals("") && list.get(position).getLikeCommentNum() != 0) {
                                                    holder.showEvaluateCheck.setText("" + (Integer.valueOf(holder.showEvaluateCheck.getText().toString()) + 1));
                                                    AllCommentModel.DataBean.ShowBarCommentBean bean = list.get(position);
                                                    bean.setIsLikeComment(true);
                                                    bean.setLikeCommentNum((Integer.valueOf(holder.showEvaluateCheck.getText().toString()) + 1));
                                                    list.remove(position);
                                                    list.add(position,bean);
                                                } else if (holder.showEvaluateCheck.getText().toString().equals("") && list.get(position).isIsLikeComment()) {
                                                    holder.showEvaluateCheck.setText("" + list.get(position).getLikeCommentNum());
                                                    AllCommentModel.DataBean.ShowBarCommentBean bean = list.get(position);
                                                    bean.setIsLikeComment(true);
                                                    bean.setLikeCommentNum(list.get(position).getLikeCommentNum());
                                                    list.remove(position);
                                                    list.add(position,bean);
                                                } else {
                                                    holder.showEvaluateCheck.setText("" + (list.get(position).getLikeCommentNum() + 1));
                                                    AllCommentModel.DataBean.ShowBarCommentBean bean = list.get(position);
                                                    bean.setIsLikeComment(true);
                                                    bean.setLikeCommentNum(list.get(position).getLikeCommentNum() + 1);
                                                    list.remove(position);
                                                    list.add(position,bean);
                                                }
                                            } else {
                                                if (!holder.showEvaluateCheck.getText().toString().equals("") && Integer.valueOf(holder.showEvaluateCheck.getText().toString()) != 1) {
                                                    holder.showEvaluateCheck.setText("" + (Integer.valueOf(holder.showEvaluateCheck.getText().toString()) - 1));
                                                    AllCommentModel.DataBean.ShowBarCommentBean bean = list.get(position);
                                                    bean.setIsLikeComment(false);
                                                    bean.setLikeCommentNum((Integer.valueOf(holder.showEvaluateCheck.getText().toString()) - 1));
                                                    list.remove(position);
                                                    list.add(position,bean);
                                                } else {
                                                    holder.showEvaluateCheck.setText("");
                                                    AllCommentModel.DataBean.ShowBarCommentBean bean = list.get(position);
                                                    bean.setIsLikeComment(false);
                                                    bean.setLikeCommentNum(0);
                                                    list.remove(position);
                                                    list.add(position,bean);
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
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
        void allHuiFuOnclick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv_comment_photo)
        SimpleDraweeView cvCommentPhoto;


        @BindView(R.id.tv_comment_name)
        TextView tvCommentName;


        @BindView(R.id.tv_comment_contant)
        TextView tvCommentContant;


        @BindView(R.id.tv_date_time)
        TextView tvDateTime;


        @BindView(R.id.tv_hui_fu)
        TextView tvHuiFu;

        @BindView(R.id.rel_top)
        RelativeLayout relTop;

        @BindView(R.id.recycler_view_item)
        RecyclerView recyclerViewItem;

        @BindView(R.id.show_evaluate_check)
        CheckBox showEvaluateCheck;

        @BindView(R.id.tv_hui_fu_dian)
        TextView tvHuiFuDian;

        @BindView(R.id.view_line1)
        View view_line1;

        @BindView(R.id.view_line2)
        View view_line2;

        @BindView(R.id.view_line3)
        View view_line3;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

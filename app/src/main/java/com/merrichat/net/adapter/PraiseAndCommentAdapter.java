package com.merrichat.net.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.activity.circlefriend.TuWenAlbumAty;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.PraiseAndCommentModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.spannable.CircleMovementMethod;
import com.merrichat.net.utils.spannable.SpannableClickable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/9/2.
 * 赞和评论adapter
 */

public class PraiseAndCommentAdapter extends RecyclerView.Adapter<PraiseAndCommentAdapter.ViewHolder> {

    /**
     * 事件回调监听
     */
    private OnItemClickListener onItemClickListener;
    private Context context;
    private List<PraiseAndCommentModel> list;

    public PraiseAndCommentAdapter(Context context, List<PraiseAndCommentModel> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_praise_and_comment2, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 绑定数据
        final PraiseAndCommentModel model = list.get(position);
        String name = model.getName();
        String title = model.getTitle();
        String time = model.getTime();
        String temp = "";
        String content = "";
        if ("2".equals(model.getType())) {
            temp = "赞了你的动态：";
            content = title;
        } else if ("3".equals(model.getType())) {
            temp = "回复你的评论：";
            String revert = model.getRevert();
            if (!TextUtils.isEmpty(revert) && revert.split(":").length > 1) {
                if (null != revert.split(":")[1]) {
                    content = revert.split(":")[1];
                }
            } else {
                content = "";
            }
        } else if ("4".equals(model.getType())) {
            temp = "评论你的动态：";
            content = model.getComment();
        }

        String total = name + temp + content;
        SpannableString spannableString = new SpannableString(total);
        spannableString.setSpan(new SpannableClickable(context.getResources().getColor(R.color.text_6D92EF)) {
            @Override
            public void onClick(View widget) {
                context.startActivity(new Intent(context, HisYingJiAty.class)
                        .putExtra("hisMemberId", model.getMemberId())
                        .putExtra("hisImgUrl", model.getHeadImgUrl())
                        .putExtra("hisNickName", model.getName()));
            }
        }, 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new SpannableClickable() {
            @Override
            public void onClick(View widget) {


                Intent intent = new Intent();
                if (("1").equals(model.getFlag())) {
                    intent = new Intent(context, TuWenAlbumAty.class);
                } else if (("2").equals(model.getFlag())) {
                    intent = new Intent(context, CircleVideoActivity.class);
                }
                intent.putExtra("toMemberId", model.getSendMemberId());
                intent.putExtra("contentId", model.getContentId());
                context.startActivity(intent);
//                bundle.putString("toMemberId", movieList.get(position).getMemberId() + "");
//                bundle.putString("contentId", movieList.get(position).getId() + "");
//                bundle.putInt("activityId", activityId);
//                bundle.putInt("tab_item", 5);
//                if (movieList.get(position).getFlag() == 1) {//1照片 2视频
//                    RxActivityTool.skipActivity(MyDynamicsAty.this, TuWenAlbumAty.class, bundle);
//                } else {
//                    RxActivityTool.skipActivity(MyDynamicsAty.this, CircleVideoActivity.class, bundle);
//                }
            }
        }, name.length(), total.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tvShowTitle.setText(spannableString);
        holder.tvShowTitle.setMovementMethod(new CircleMovementMethod());
//        //2:赞 3：回复 4：评论
//        if ("2".equals(model.getType())) {
//            holder.llContentOne.setVisibility(View.GONE);
//            holder.llContentTwo.setVisibility(View.GONE);
//            holder.llShowBa.setBackgroundColor(context.getResources().getColor(R.color.white_2));
//            holder.tvZanType.setText("赞了你的秀吧");
//            holder.llHuiFu.setVisibility(View.GONE);
//        } else if ("3".equals(model.getType())) {
//            holder.llContentOne.setVisibility(View.VISIBLE);
//            holder.llContentTwo.setVisibility(View.VISIBLE);
//            holder.llShowBa.setBackgroundColor(context.getResources().getColor(R.color.white));
//            holder.tvZanType.setText("回复你的评论");
//            holder.llHuiFu.setVisibility(View.VISIBLE);
//
//            holder.tvContentTwo.setText(model.getComment());
//            holder.tvContentOne.setText(model.getRevert());
//        } else if ("4".equals(model.getType())) {
//            holder.llContentOne.setVisibility(View.VISIBLE);
//            holder.llContentTwo.setVisibility(View.GONE);
//            holder.llShowBa.setBackgroundColor(context.getResources().getColor(R.color.white_2));
//            holder.tvZanType.setText("评论你的秀吧");
//            holder.llHuiFu.setVisibility(View.VISIBLE);
//
//            holder.tvContentOne.setText(model.getComment());
//        }
//
//        Glide.with(context).load(model.getHeadImgUrl()).error(R.mipmap.ic_preloading).into(holder.civTouXiang);
//        holder.tvFriendsName.setText(model.getName());
//        TextPaint paint = holder.tvFriendsName.getPaint();
//        paint.setFakeBoldText(true);
        holder.tvShowTime.setText(time);
//
//        //cover = {\"url\":\"http:\\/\\/okdi.oss-cn-beijing.aliyuncs.com\\/merrichat_image_1515207895.jpg\",\"type\":0,\"text\":\"aaaaaaaaaaaaaaaaaaa\",\"flag\":1,\"width\":720,\"height\":1280}
        try {
            JSONObject jsonObject = new JSONObject(model.getCover());
            String cover = jsonObject.optString("url");
            String text = jsonObject.optString("text");
            Glide.with(context).load(cover).dontAnimate().error(R.mipmap.ic_preloading).placeholder(R.mipmap.ic_preloading).into(holder.ivShowImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        holder.tvShowTitle.setText(model.getTitle());
//        TextPaint paint1 = holder.tvShowTitle.getPaint();
//        paint1.setFakeBoldText(true);
//
//
//        holder.llHuiFu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onItemClickListener != null) {
//                    int pos = holder.getLayoutPosition();
//                    onItemClickListener.huiFuOnitemClick(pos);
//                }
//            }
//        });

    }

    public interface IReciveMeiBi {
        void changeMeiBiViewValue();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        /**
//         * 好友头像
//         */
//        @BindView(R.id.civ_tou_xiang)
//        CircleImageView civTouXiang;
//
//
//        /**
//         * 好友名字
//         */
//        @BindView(R.id.tv_friends_name)
//        TextView tvFriendsName;
//
//        /**
//         * 赞或评论或回复 类型不同  设置不同的文字
//         */
//        @BindView(R.id.tv_zan_type)
//        TextView tvZanType;
//
//        /**
//         * 时间
//         */
//        @BindView(R.id.tv_date_time)
//        TextView tvDateTime;
//
//        /**
//         * 回复按钮
//         */
//        @BindView(R.id.ll_hui_fu)
//        LinearLayout llHuiFu;
//
//        /**
//         * 第一条评论
//         */
//        @BindView(R.id.tv_content_one)
//        TextView tvContentOne;
//        @BindView(R.id.ll_content_one)
//        LinearLayout llContentOne;
//
//
//        /**
//         * 第二条评论
//         */
//        @BindView(R.id.tv_content_two)
//        TextView tvContentTwo;
//        @BindView(R.id.ll_content_two)
//        LinearLayout llContentTwo;
//
//
//        /**
//         * 秀吧内容
//         */
//        @BindView(R.id.ll_show_ba)
//        LinearLayout llShowBa;
//
//        /**
//         * 秀吧封面
//         */
//        @BindView(R.id.iv_show_image)
//        ImageView ivShowImage;
//
//        /**
//         * 秀吧标题
//         */
//        @BindView(R.id.tv_show_title)
//        TextView tvShowTitle;
//
//        /**
//         * 秀吧内容
//         */
//        @BindView(R.id.tv_show_content)
//        TextView tvShowContent;

        /**
         * 秀吧内容
         */
        @BindView(R.id.ll_show_ba)
        LinearLayout llShowBa;

        /**
         * 秀吧封面
         */
        @BindView(R.id.iv_show_image)
        ImageView ivShowImage;

        /**
         * 秀吧标题
         */
        @BindView(R.id.tv_show_title)
        TextView tvShowTitle;

        /**
         * 秀吧内容
         */
        @BindView(R.id.tv_show_time)
        TextView tvShowTime;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public interface OnItemClickListener {
        //        void onItemClick(int position);
        void huiFuOnitemClick(int position);
    }
}


package com.merrichat.net.adapter;

import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.gif.AnimatedGifDrawable;
import com.merrichat.net.activity.message.gif.AnimatedImageSpan;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.view.CircleImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amssy on 17/7/29.
 * 聊天列表adapter
 */

public class ChatAdapter extends BaseQuickAdapter<MessageListModle, BaseViewHolder> {
    private OnSwipeListener mOnSwipeListener;

    public ChatAdapter(int layoutResId, @Nullable List<MessageListModle> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MessageListModle listModle) {

        String type = listModle.getType();
        if ("1".equals(type)) {//单聊
            helper.setText(R.id.tv_chatPerName, listModle.getName());//姓名
        } else {
            helper.setText(R.id.tv_chatPerName, listModle.getGroup());//姓名
        }
        /**
         * 1-静态图片  2-语音 3-gif图片 4-视频文件 5-普通文本消息   -1是草稿
         * */
        String fileType = listModle.getFileType();

        if ("-1".equals(fileType)) {//草稿显示
            SpannableStringBuilder sb = handler((TextView) helper.getView(R.id.tv_chat_message), listModle.getLast());
            ((TextView) helper.getView(R.id.tv_chat_message)).setText(sb);
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.VISIBLE);
        } else if ("2".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[语音]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("3".equals(fileType) || "1".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[图片]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("4".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[视频]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("5".equals(fileType)) {
            SpannableStringBuilder sb = handler((TextView) helper.getView(R.id.tv_chat_message), listModle.getLast());
            ((TextView) helper.getView(R.id.tv_chat_message)).setText(sb);
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("11".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[交易]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("-2".equals(fileType)) {//-2最新一条内容为空
            ((TextView) helper.getView(R.id.tv_chat_message)).setVisibility(View.GONE);
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("8".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[红包]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("7".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[礼物]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("9".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[" + listModle.getLast() + "]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("10".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[转账]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("12".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[位置]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("13".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[手气红包]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        } else if ("14".equals(fileType)) {
            ((TextView) helper.getView(R.id.tv_chat_message)).setText("[拉人红包]");
            ((TextView) helper.getView(R.id.tv_caogao)).setVisibility(View.GONE);
        }
        Glide.with(mContext.getApplicationContext())
                .load(listModle.getHeadUrl())
                .signature(new StringSignature(PrefAppStore.getMessageHeaderImgTimestamp(mContext)))
                .error(R.mipmap.ic_preloading)
                .placeholder(R.mipmap.ic_preloading)
                .dontAnimate()
                .into((CircleImageView) helper.getView(R.id.tv_messageHead));
        long time = listModle.getMsgts();//消息时间
//        ((TextView) helper.getView(R.id.tv_chatTime)).setText(DateTimeUtil.countCommentTime(time, System.currentTimeMillis()));//设置时间
//        ((TextView) helper.getView(R.id.tv_chatTime)).setText(DateTimeUtil.chatMessageTime(time, System.currentTimeMillis()));//设置时间
        ((TextView) helper.getView(R.id.tv_chatTime)).setText(DateTimeUtil.getNewChatTime(time));//设置时间

        /**
         * //是否置顶  0否1是
         * */
        int top = listModle.getTop();
        if (top == 0) {
            helper.getView(R.id.ll_messageitem).setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else if (top == 1) {
            helper.getView(R.id.ll_messageitem).setBackgroundColor(mContext.getResources().getColor(R.color.background));
        }
        /**
         * //是否消息免打扰  0否1是
         * */
        int interru = listModle.getInter();
        if (interru == 0) {
            helper.getView(R.id.iv_nodisturbing).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.iv_nodisturbing).setVisibility(View.VISIBLE);
        }
        /**
         * //消息个数
         * */
        int count = listModle.getCount();
        if (count > 0) {
            ((TextView) helper.getView(R.id.tv_messageNum)).setVisibility(View.VISIBLE);
            if (count > 99) {
                ((TextView) helper.getView(R.id.tv_messageNum)).setText("99+");
            } else {
                ((TextView) helper.getView(R.id.tv_messageNum)).setText(Integer.toString(count));
            }
            ((TextView) helper.getView(R.id.tv_chat_message)).setTextColor(mContext.getResources().getColor(R.color.normal_red));
        } else {
            ((TextView) helper.getView(R.id.tv_messageNum)).setVisibility(View.GONE);
            ((TextView) helper.getView(R.id.tv_chat_message)).setTextColor(mContext.getResources().getColor(R.color.base_888888));
        }

        helper.addOnClickListener(R.id.ll_item);
        helper.getView(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //((CstSwipeDelMenu) holder.itemView).quickClose();
                    mOnSwipeListener.onDel(helper.getAdapterPosition());
                }
            }
        });
    }


    private SpannableStringBuilder handler(final TextView gifTextView,
                                           String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\[face/png/f_static_)\\d{3}(.png\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring(
                        "[face/png/f_static_".length(), tempText.length()
                                - ".png]".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = mContext.getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,
                                new AnimatedGifDrawable.UpdateListener() {
                                    @Override
                                    public void update() {
                                        gifTextView.postInvalidate();
                                    }
                                })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("[".length(),
                        tempText.length() - "]".length());
                try {
                    sb.setSpan(
                            new ImageSpan(mContext, BitmapFactory
                                    .decodeStream(mContext.getAssets()
                                            .open(png))), m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }

    public interface OnSwipeListener {
        void onDel(int pos);
    }


    public OnSwipeListener getOnDelListener() {
        return mOnSwipeListener;
    }

    public void setOnDelListener(OnSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }
}

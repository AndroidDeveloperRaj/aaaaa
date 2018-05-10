package com.merrichat.net.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.merrichat.net.R;
import com.merrichat.net.activity.groupmarket.MarketShopDetailAty;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.message.ChatAmplificationActivity;
import com.merrichat.net.activity.message.RedBaoDetailsActivity;
import com.merrichat.net.activity.message.ShowLocationAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.message.VedioShowActivity;
import com.merrichat.net.activity.message.ZhuanZhangDetialAty;
import com.merrichat.net.activity.message.cim.Constant;
import com.merrichat.net.activity.message.cim.android.CIMPushManager;
import com.merrichat.net.activity.message.cim.model.SentBody;
import com.merrichat.net.activity.message.gif.AnimatedGifDrawable;
import com.merrichat.net.activity.message.gif.AnimatedImageSpan;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupLocationModel;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.MessageModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.MD5;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.camera.FolderManager;
import com.merrichat.net.utils.sound.MediaManage;
import com.merrichat.net.view.ChaiRedBaoDialog;
import com.merrichat.net.view.ChatDeleteDialog;
import com.merrichat.net.view.CircleImageView;
import com.merrichat.net.view.DialogOnClickListener;
import com.merrichat.net.view.RemindDialog;
import com.merrichat.net.view.RoundAngleFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_CHAI_RED_PACKAGE_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_ZHUANZHANG_DETIAL_CODE;


/**
 * 单聊界面适配器
 * Created by amssy on 2016/6/24.
 */
public class SingleChatAdapter extends BaseAdapter {

    private Context mContext;
    private List<MessageModel> msgList;
    private ArrayList<String> imgUrls;
    /**
     * 正在发送的进度动画
     */
    private Animation anim;
    /**
     * 当前正在播放语音的条目索引
     */
    private int voicePlayNowPosition;
    /**
     * 播放语音的动画
     */
    private AnimationDrawable animationDrawable;
    /**
     * 下载文件
     */
    private OkHttpClient okHttpClient;

    /**
     * 3分钟的毫秒值
     */
    private long countTime;

    private int mMinItemWidth; //最小的item宽度
    private int mMaxItemWidth; //最大的item宽度
    ChatDeleteDialog chatDeleteDialog;

    private IReciveMeiBi iReciveMeiBi;

    public SingleChatAdapter(Context mContext, List<MessageModel> msgList) {
        this.mContext = mContext;
        this.msgList = msgList;
        imgUrls = new ArrayList<String>();
        voicePlayNowPosition = -1;
        anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_msg_sending);
        okHttpClient = new OkHttpClient();
        LinearInterpolator lin = new LinearInterpolator();
        anim.setInterpolator(lin);
        countTime = 3 * 60 * 1000;

        //获取屏幕的宽度
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f) - StringUtil.dip2px(mContext, 45);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);

    }


    public IReciveMeiBi getiReciveMeiBi() {
        return iReciveMeiBi;
    }

    public void setiReciveMeiBi(IReciveMeiBi iReciveMeiBi) {
        this.iReciveMeiBi = iReciveMeiBi;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        MyOnclickListener listener;
        MyOnLongClickListener longListener;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_single_chat, null);
            holder = new ViewHolder(convertView);
            //设置点击事件
            listener = new MyOnclickListener(holder);
            longListener = new MyOnLongClickListener(holder);
            holder.civLeftHeadImg.setOnClickListener(listener);
            holder.civRightHeadImg.setOnClickListener(listener);
            holder.ivLeftMessageImage.setOnClickListener(listener);
            holder.llLeftVoiceInfo.setOnClickListener(listener);
            holder.ivLeftVideoImg.setOnClickListener(listener);
            holder.ivRightMessageImage.setOnClickListener(listener);
            holder.ivRightVideoImg.setOnClickListener(listener);
            holder.llRightVoiceInfo.setOnClickListener(listener);
            holder.rlLeftMessage.setOnClickListener(listener);
            holder.rlRightMessage.setOnClickListener(listener);
            holder.llRightMessageRedPackage.setOnClickListener(listener);
            holder.llLeftMessageRedPackage.setOnClickListener(listener);
            holder.tv_right_map_mame.setOnClickListener(listener);
            holder.tv_left_map_mame.setOnClickListener(listener);
//            holder.ivSendFailure.setOnClickListener(listener);

            //设置长按事件
            holder.llLeftVoiceInfo.setOnLongClickListener(longListener);
            holder.llRightVoiceInfo.setOnLongClickListener(longListener);
            holder.rlLeftMessage.setOnLongClickListener(longListener);
            holder.rlRightMessage.setOnLongClickListener(longListener);
            holder.ivLeftVideoImg.setOnLongClickListener(longListener);
            holder.ivRightMessageImage.setOnLongClickListener(longListener);
            holder.ivLeftMessageImage.setOnLongClickListener(longListener);
            holder.ivRightVideoImg.setOnLongClickListener(longListener);


            holder.listener = listener;
            holder.longListener = longListener;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            listener = holder.listener;
            longListener = holder.longListener;
        }
        final MessageModel msg = msgList.get(position);

        listener.setMessage(position, msg);
        longListener.setMessage(position, msg);
        if (Constant.MessageType.TYPE_3.equals(msg.getTypeRevoke())) {
            //系统消息，显示到时间控件中
            holder.tvMessageTime.setVisibility(View.VISIBLE);
            holder.tvMessageTime.setText(DateTimeUtil.getNewChatTime(msg.getTimestamp()));
            holder.tvSysTip.setVisibility(View.VISIBLE);
            holder.tvSysTip.setText(msg.getContent());
            //隐藏左边和右边消息布局
            holder.llLeftMessageAllInfo.setVisibility(View.GONE);
            holder.llRightMessageAllInfo.setVisibility(View.GONE);
        } else {
            holder.tvSysTip.setVisibility(View.GONE);

            if (position == 0) {
                //第一条
                holder.tvMessageTime.setVisibility(View.VISIBLE);
                msg.setShowTimeState(0);
                holder.tvMessageTime.setText(DateTimeUtil.getNewChatTime(msg.getTimestamp()));
                MessageModel.setMessageModel(msg);
//                    msg.save();
            }else {
                //显示时间
                if (msg.getShowTimeState() == -1) {
                    if (position!=0){

                        if ((msg.getTimestamp() - msgList.get(position-1).getTimestamp()) > countTime) {
                            //显示时间
                            msg.setShowTimeState(0);
                            holder.tvMessageTime.setVisibility(View.VISIBLE);
                            holder.tvMessageTime.setText(DateTimeUtil.getNewChatTime(msg.getTimestamp()));
                        } else {
                            msg.setShowTimeState(1);
                            holder.tvMessageTime.setVisibility(View.GONE);
                        }
                        MessageModel.setMessageModel(msg);
//                    msg.save();
                    }
                } else {
                    if (msg.getShowTimeState() == 0) {
                        holder.tvMessageTime.setVisibility(View.VISIBLE);
                        holder.tvMessageTime.setText(DateTimeUtil.getNewChatTime(msg.getTimestamp()));
                    } else {
                        holder.tvMessageTime.setVisibility(View.GONE);

                    }
                }

            }
            //1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息6-交易7-礼物 8-红包 9-红包推送消息10-位置11-转账

            /**
             * 文件类型
             * 1:静态图片, 2:语音,3:gif图片, 4:视频文件, 5:文本, 6:其他, 7:礼物, 8:红包, 9:领红包, 10:转账, 11:发布交易,
             * 12:位置, 13手气红包, 14 拉人红包
             */
            String fileType = msg.getFileType();
            //判断是否是我自己发送的消息
            if (String.valueOf(UserModel.getUserModel().getMemberId()).equals(msg.getSender())) {
                //我自己发送的消息
                if ("1".equals(fileType) || "3".equals(fileType)) {
                    //显示图片
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.VISIBLE);

                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);

                    //设置图片显示
                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        Glide.with(mContext).load(msg.getFile()).error(R.mipmap.icon_message_imageloader_error_bg).placeholder(R.mipmap.icon_message_imageloader_error_bg).override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivRightMessageImage);

                    } else {
                        Glide.with(mContext).load(msg.getFilePath()).error(R.mipmap.icon_message_imageloader_error_bg).placeholder(R.mipmap.icon_message_imageloader_error_bg).override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivRightMessageImage);

                    }


                } else if ("2".equals(fileType)) {
                    //显示语音
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.VISIBLE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    //设置语音时间
                    holder.tvRightMessageVoiceTime.setVisibility(View.VISIBLE);
                    holder.tvRightMessageVoiceTime.setText(msg.getSpeechTimeLength() + "''");
                    //设置语音外布局长度(164px高82， 最大293，82)
                    //1-2s是最短的。2-10s每秒增加一个单位。10-60s每10s增加一个单位。
                    int speechTimeLength = 0;
                    try {
                        speechTimeLength = Integer.parseInt(msg.getSpeechTimeLength());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    int newWidth = mMinItemWidth + (mMaxItemWidth / 60) * speechTimeLength;

                    ViewGroup.LayoutParams layoutParams = holder.llRightVoiceInfo.getLayoutParams();
                    layoutParams.width = newWidth;
                    holder.llRightVoiceInfo.setLayoutParams(layoutParams);

                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        //去下载
                        //开启子线程下载
                        if (!TextUtils.isEmpty(msg.getFile())) {
                            downloadAsyn(msg, msg.getFile(), FolderManager.getVoiceFolder(), ".mp3");
                        }

                    }

                    if (position == voicePlayNowPosition) {
                        holder.ivRightVoice.setImageResource(R.drawable.play_right_voice_message_anim);
                    } else {
                        holder.ivRightVoice.setImageResource(R.mipmap.icon_right_play_voice_3);
                    }

                } else if ("4".equals(fileType)) {
                    //显示视频
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.VISIBLE);

                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(msg.getThumb())) {
                        Glide.with(mContext).load(msg.getThumb()).asBitmap().override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivRightVideoImg);

                    } else if (!TextUtils.isEmpty(msg.getFilePath())) {
                        //获取图片第一帧并保存到手机上
                        Bitmap bitmap = StringUtil.createVideoThumbnail(msg.getFilePath());
                        if (bitmap != null) {
                            String thumb = MD5.Md5(msg.getFilePath()) + ".jpg";
                            File file = new File(FolderManager.getPhotoFolder(), thumb);
                            StringUtil.saveMyBitmap(file, bitmap);
                            msg.setThumb(file.getAbsolutePath());
//                            msg.save();
                            MessageModel.setMessageModel(msg);

                        }
                        Glide.with(mContext).load(msg.getThumb()).asBitmap().placeholder(R.mipmap.icon_message_imageloader_error_bg).override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivRightVideoImg);

                    } else {
                        Glide.with(mContext).load(R.mipmap.icon_message_imageloader_error_bg).asBitmap().override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivRightVideoImg);
                    }
                } else if ("11".equals(fileType)) {
                    //显示商品
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.VISIBLE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    try {
                        final JSONObject transInfo = new JSONObject(msg.getContent());
                        holder.iv_right_shop_cover.setImageURI(transInfo.optString("img"));
                        holder.tv_right_shop_name.setText(transInfo.optString("productName"));
                        holder.tv_right_shop_price.setText(StringUtil.getPrice(String.valueOf(transInfo.optString("productPrice"))));
                        holder.tv_right_shop_buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //跳转详情 传入交易ID
                                mContext.startActivity(new Intent(mContext, MarketShopDetailAty.class).putExtra("id", transInfo.optString("id")).putExtra("groupType", 1));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("12".equals(fileType)) {
                    //显示位置
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.VISIBLE);
                    GroupLocationModel locationModel = new Gson().fromJson(msg.getContent(), GroupLocationModel.class);
                    holder.tv_right_map_mame.setText(locationModel.address);
                    String mapStaticRightUrl = "http://api.map.baidu.com/staticimage/v2?ak=" +
                            "wpoRn9fP7KQspLeI5MqONtBsGXL3Pwlo" +
                            "&copyright=0" +
                            "&mcode=41:84:18:78:7C:82:F8:CA:91:F0:B6:DC:D3:85:D3:54:D9:43:B2:B7;com.merrichat.net" +
                            "&center=" + locationModel.currentLongitude + "," + locationModel.currentLatitude + "&width=440" + "&height=220" + "&zoom=18.png";
                    Glide.with(mContext).load(mapStaticRightUrl).into(holder.iv_right_map);

                    //单击事件监听
                    holder.iv_right_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, ShowLocationAty.class).putExtra("location", msg.getContent()));
                        }
                    });
                } else if ("8".equals(fileType)) {
                    //我发的红包
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.llRightMessageRedPackage.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    holder.tv_right_money_type.setText("讯美红包");
                    holder.tvRightRedPackageContent.setText(msgList.get(position).getContent());
                    if ("0".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_message_no_small_red_package);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_no_right);
                        holder.tvRightRedPackageStatus.setText("待对方领取红包");
                    } else if ("1".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_message_yes_small_red_package);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_right);
                        holder.tvRightRedPackageStatus.setText("已领取红包");
                    } else if ("2".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_message_yes_small_red_package);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_right);
                        holder.tvRightRedPackageStatus.setText("红包已超时");
                    }

                } else if ("10".equals(fileType)) {
                    //我发的转账
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.llRightMessageRedPackage.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    holder.tv_right_money_type.setText("讯美币转账");
                    holder.tvRightRedPackageContent.setText(msgList.get(position).getContent() + "讯美币");
                    if ("0".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_no_right);
                        holder.tvRightRedPackageStatus.setText("转账给" + msgList.get(position).getReceiverName());
                    } else if ("1".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_right);
                        holder.tvRightRedPackageStatus.setText("已收钱");
                    } else if ("10".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_right);
                        holder.tvRightRedPackageStatus.setText("已被领取");
                    } else if ("2".equals(msgList.get(position).getRedStatus())) {
                        holder.ivRightRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                        holder.llRightRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_right);
                        holder.tvRightRedPackageStatus.setText("已超时");
                    }
                } else if ("9".equals(fileType)) {
                    //红包已被领取
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.VISIBLE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    if (msg.getContent().contains("超过24小时")) {
                        holder.tvRedTip.setText(msg.getContent());
                    } else {
                        MessageModel messageModel = GreenDaoManager.getInstance().getNewSession().getMessageModelDao().queryBuilder().where(MessageModelDao.Properties.RedTid.eq(msg.getRedTid()), MessageModelDao.Properties.FileType.eq("8")).unique();
                        SpannableString spannableString = new SpannableString(msg.getContent());
                        RedPackageClickableSpan clickableSpan = new RedPackageClickableSpan(messageModel);
                        spannableString.setSpan(clickableSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F5A152"));
                        spannableString.setSpan(colorSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        holder.tvRedTip.setMovementMethod(LinkMovementMethod.getInstance());
                        holder.tvRedTip.setHighlightColor(Color.parseColor("#F5A152"));
                        holder.tvRedTip.setText(spannableString);
                    }
                } else if ("7".equals(fileType)) {
                    //显示礼物
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.VISIBLE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.ivRightMessageift.setVisibility(View.VISIBLE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setText("");
                    Glide.with(mContext).load(msgList.get(position).getContent()).into(holder.ivRightMessageift);
                } else {
                    //显示文本
                    holder.llRightMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlRightMessage.setVisibility(View.VISIBLE);
                    holder.tvRightMessageContent.setVisibility(View.VISIBLE);
                    holder.ivRightMessageift.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.rafl_right_shop.setVisibility(View.GONE);
                    holder.rafl_right_map.setVisibility(View.GONE);
                    SpannableStringBuilder sb = handler(holder.tvRightMessageContent,
                            msgList.get(position).getContent());
                    holder.tvRightMessageContent.setText(sb);
                }
//                设置头像
                Glide.with(mContext.getApplicationContext())
                        .load(UserModel.getUserModel().getImgUrl())
                        .signature(new StringSignature(PrefAppStore.getMessageHeaderImgTimestamp(mContext)))
                        .error(R.mipmap.ic_preloading)
                        .placeholder(R.mipmap.ic_preloading)
                        .dontAnimate()
                        .into(holder.civRightHeadImg);


                //判断发送状态
                if (MessageModel.SEND_STATE_FAILURE == msg.getSendState()) {//发送失败
                    holder.ivSending.clearAnimation();
                    holder.ivSending.setVisibility(View.GONE);
                    holder.ivSendFailure.setVisibility(View.VISIBLE);

                } else if (MessageModel.SEND_STATE_SUCCEED == msg.getSendState()) {//发送成功
                    holder.ivSending.clearAnimation();
                    holder.ivSending.setVisibility(View.GONE);
                    holder.ivSendFailure.setVisibility(View.GONE);

                } else {//发送中
                    //显示正在加载
                    holder.ivSending.setVisibility(View.VISIBLE);
                    holder.ivSending.startAnimation(anim);
                    //隐藏发送失败按钮
                    holder.ivSendFailure.setVisibility(View.GONE);
                }

            } else {
                //对方发送来的消息
                //1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
                if ("1".equals(fileType) || "3".equals(fileType)) {
                    //显示图片
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.VISIBLE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    //设置图片显示
                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        Glide.with(mContext).load(msg.getThumb()).listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                msgList.get(position).setSendState(2);
                                MessageModel.setMessageModel(msgList.get(position));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                msgList.get(position).setSendState(1);
                                MessageModel.setMessageModel(msgList.get(position));
                                return false;
                            }
                        }).error(R.mipmap.icon_message_imageloader_error_bg).placeholder(R.mipmap.icon_message_imageloader_error_bg).override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivLeftMessageImage);
                    } else {
                        Glide.with(mContext).load(msg.getFilePath()).error(R.mipmap.icon_message_imageloader_error_bg).placeholder(R.mipmap.icon_message_imageloader_error_bg).override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivLeftMessageImage);
                    }

                } else if ("2".equals(fileType)) {
                    //显示语音
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.llLeftVoiceInfo.setVisibility(View.VISIBLE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.VISIBLE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.VISIBLE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setText(msg.getSpeechTimeLength() + "''");
                    //显示未读红点
                    if (msg.getIsReadVoice()) {
                        holder.ivMessageUnreadRed.setVisibility(View.GONE);

                    } else {
                        holder.ivMessageUnreadRed.setVisibility(View.VISIBLE);

                    }

                    //设置语音外布局长度(164px高82， 最大293，82)
                    //1-2s是最短的。2-10s每秒增加一个单位。10-60s每10s增加一个单位。
                    int speechTimeLength = 0;
                    try {
                        speechTimeLength = Integer.parseInt(msg.getSpeechTimeLength());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    int newWidth = mMinItemWidth + (mMaxItemWidth / 60) * speechTimeLength;
                    ViewGroup.LayoutParams layoutParams = holder.llLeftVoiceInfo.getLayoutParams();
                    layoutParams.width = newWidth;
                    holder.llLeftVoiceInfo.setLayoutParams(layoutParams);

                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        if (!TextUtils.isEmpty(msg.getFile())) {
                            //开启子线程下载
                            downloadAsyn(msg, msg.getFile(), FolderManager.getVoiceFolder(), ".mp3");
                        }
                    }

                    if (position == voicePlayNowPosition) {
                        holder.ivLeftVoice.setImageResource(R.drawable.play_left_voice_message_anim);
                    } else {
                        holder.ivLeftVoice.setImageResource(R.mipmap.icon_left_play_voice_3);
                    }

                } else if ("4".equals(fileType)) {
                    //显示视频
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.VISIBLE);

                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    Glide.with(mContext).load(msg.getThumb()).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            msgList.get(position).setSendState(2);
//                            msgList.get(position).save();
                            MessageModel.setMessageModel(msgList.get(position));
                            notifyDataSetChanged();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            msgList.get(position).setSendState(1);
//                            msgList.get(position).save();
                            MessageModel.setMessageModel(msgList.get(position));
                            notifyDataSetChanged();
                            return false;
                        }
                    }).error(R.mipmap.icon_message_imageloader_error_bg).placeholder(R.mipmap.icon_message_imageloader_error_bg).override(DensityUtils.dp2px(mContext, 130), DensityUtils.dp2px(mContext, 135)).centerCrop().into(holder.ivLeftVideoImg);
                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        //没下载去下载
                        holder.ivLeftVideoImg.setImageResource(R.mipmap.icon_message_imageloader_error_bg);
                        if (!TextUtils.isEmpty(msg.getFile())) {
                            downloadAsyn(msg, msg.getFile(), FolderManager.getVideoFolder(), ".mp4");
                        }
                    }

                } else if ("11".equals(fileType)) {
                    //显示商品
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.VISIBLE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    try {
                        final JSONObject transInfo = new JSONObject(msg.getContent());
                        holder.iv_left_shop_cover.setImageURI(transInfo.optString("img"));
                        holder.tv_left_shop_name.setText(transInfo.optString("productName"));
                        holder.tv_left_shop_price.setText(StringUtil.getPrice(String.valueOf(transInfo.optString("productPrice"))));
                        holder.tv_left_shop_buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //跳转详情 传入交易ID
                                mContext.startActivity(new Intent(mContext, MarketShopDetailAty.class).putExtra("id", transInfo.optString("id")).putExtra("groupType", 1));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("12".equals(fileType)) {
                    //显示位置
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.VISIBLE);

                    GroupLocationModel locationModel = new Gson().fromJson(msg.getContent(), GroupLocationModel.class);
                    holder.tv_left_map_mame.setText(locationModel.address);
                    String mapStaticRightUrl = "http://api.map.baidu.com/staticimage/v2?ak=" +
                            "wpoRn9fP7KQspLeI5MqONtBsGXL3Pwlo" +
                            "&copyright=0" +
                            "&mcode=41:84:18:78:7C:82:F8:CA:91:F0:B6:DC:D3:85:D3:54:D9:43:B2:B7;com.merrichat.net" +
                            "&center=" + locationModel.currentLongitude + "," + locationModel.currentLatitude + "&width=440" + "&height=220" + "&zoom=18.png";
                    Glide.with(mContext).load(mapStaticRightUrl).into(holder.iv_left_map);

                    //单击事件监听
                    holder.iv_left_map.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, ShowLocationAty.class).putExtra("location", msg.getContent()));
                        }
                    });

                } else if ("8".equals(fileType)) {
                    //ta的红包
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.llLeftMessageRedPackage.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    holder.tvLeftRedPackageContent.setText(msgList.get(position).getContent());
                    String redStatus = msgList.get(position).getRedStatus();
                    holder.tv_left_money_type.setText("讯美红包");
                    if (redStatus != null) {
                        if (redStatus.equals("0")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_message_no_small_red_package);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_no_left);
                            holder.tvLeftRedPackageStatus.setText("领取红包");
                        } else if (redStatus.equals("1")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_message_yes_small_red_package);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_left);
                            holder.tvLeftRedPackageStatus.setText("已领取红包");
                        } else if (redStatus.equals("2")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_message_yes_small_red_package);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_left);
                            holder.tvLeftRedPackageStatus.setText("红包已超时");
                        }
                    }
                } else if ("10".equals(fileType)) {
                    //ta的转账
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.llLeftMessageRedPackage.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);

                    holder.tv_left_money_type.setText("讯美币转账");
                    holder.tvLeftRedPackageContent.setText(msgList.get(position).getContent() + "讯美币");
                    String redStatus = msgList.get(position).getRedStatus();
                    if (redStatus != null) {
                        if (redStatus.equals("0")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_no_left);
                            holder.tvLeftRedPackageStatus.setText("转账给你");
                        } else if (redStatus.equals("1")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_left);
                            holder.tvLeftRedPackageStatus.setText("已收钱");
                        } else if (redStatus.equals("10")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_left);
                            holder.tvLeftRedPackageStatus.setText("已被领取");
                        } else if (redStatus.equals("2")) {
                            holder.ivLeftRedPackageSmall.setImageResource(R.mipmap.icon_zhuanzhang_small);
                            holder.llLeftRedPackageYellow.setBackgroundResource(R.drawable.icon_red_package_yes_left);
                            holder.tvLeftRedPackageStatus.setText("已超时");
                        }
                    }
                } else if ("9".equals(fileType)) {
                    //红包已被领取
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.rlRightMessage.setVisibility(View.GONE);
                    holder.tvRightMessageContent.setVisibility(View.GONE);
                    holder.llRightVoiceInfo.setVisibility(View.GONE);
                    holder.rlRightShareInfo.setVisibility(View.GONE);
                    holder.ivRightMessageImage.setVisibility(View.GONE);
                    holder.rlRightMessageVideo.setVisibility(View.GONE);
                    holder.tvRightMessageVoiceTime.setVisibility(View.GONE);
                    holder.llRightMessageRedPackage.setVisibility(View.GONE);
                    holder.llLeftMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.VISIBLE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);

                    if (msg.getContent().contains("超过24小时")) {
                        holder.tvRedTip.setText(msg.getContent());
                    } else {
                        MessageModel messageModel = GreenDaoManager.getInstance().getNewSession().getMessageModelDao().queryBuilder().where(MessageModelDao.Properties.RedTid.eq(msg.getRedTid()), MessageModelDao.Properties.FileType.eq("8")).unique();
                        SpannableString spannableString = new SpannableString(msg.getContent());
                        RedPackageClickableSpan clickableSpan = new RedPackageClickableSpan(messageModel);
                        spannableString.setSpan(clickableSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F5A152"));
                        spannableString.setSpan(colorSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        holder.tvRedTip.setMovementMethod(LinkMovementMethod.getInstance());
                        holder.tvRedTip.setHighlightColor(Color.parseColor("#F5A152"));
                        holder.tvRedTip.setText(spannableString);
                    }
                } else if ("7".equals(fileType)) {
                    //显示左边礼物
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.VISIBLE);
                    holder.tvLeftMessageContent.setVisibility(View.GONE);
                    holder.iveftLMessageGift.setVisibility(View.VISIBLE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.VISIBLE);

                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    holder.tvLeftMessageContent.setText("");

                    String json = msgList.get(position).getContent();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        Glide.with(mContext).load(jsonObject.getString("gifUrl")).into(holder.iveftLMessageGift);
                        SpannableString spannableString = new SpannableString(jsonObject.getString("giftStr"));
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F5A152"));
                        spannableString.setSpan(colorSpan, "对方给你赠送了一个".length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        iReciveMeiBi.changeMeiBiViewValue();
                        holder.tvRedTip.setMovementMethod(LinkMovementMethod.getInstance());
                        holder.tvRedTip.setHighlightColor(Color.parseColor("#F5A152"));
                        holder.tvRedTip.setText(spannableString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //显示文本
                    holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
                    holder.rlLeftMessage.setVisibility(View.VISIBLE);
                    holder.tvLeftMessageContent.setVisibility(View.VISIBLE);
                    holder.iveftLMessageGift.setVisibility(View.GONE);
                    holder.llLeftVoiceInfo.setVisibility(View.GONE);
                    holder.rlLeftShareInfo.setVisibility(View.GONE);
                    holder.ivLeftMessageImage.setVisibility(View.GONE);
                    holder.rlLeftMessageVideo.setVisibility(View.GONE);
                    holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
                    holder.ivMessageUnreadRed.setVisibility(View.GONE);
                    holder.llLeftMessageRedPackage.setVisibility(View.GONE);
                    holder.llRightMessageAllInfo.setVisibility(View.GONE);
                    holder.tvRedTip.setVisibility(View.GONE);
                    holder.rafl_left_shop.setVisibility(View.GONE);
                    holder.rafl_left_map.setVisibility(View.GONE);
                    SpannableStringBuilder sb = handler(holder.tvLeftMessageContent, msg.getContent());
                    holder.tvLeftMessageContent.setText(sb);
                }
//            else if(2 == position){
//                //显示分享
//                holder.llLeftMessageAllInfo.setVisibility(View.VISIBLE);
//                holder.rlLeftMessage.setVisibility(View.VISIBLE);
//                holder.rlLeftShareInfo.setVisibility(View.VISIBLE);
//
//
//                holder.ivLeftVoice.setVisibility(View.GONE);
//                holder.tvLeftMessageContent.setVisibility(View.GONE);
//                holder.ivLeftMessageImage.setVisibility(View.GONE);
//                holder.rlLeftMessageVideo.setVisibility(View.GONE);
//                holder.tvLeftMessageVoiceTime.setVisibility(View.GONE);
//
//                holder.llRightMessageAllInfo.setVisibility(View.GONE);
//            }
                if ("1".equals(fileType) || "3".equals(fileType)) {
                } else if ("4".equals(fileType)) {
                } else {
                    holder.iv_message_image_left.setVisibility(View.GONE);
                    holder.iv_message_image_left.clearAnimation();
                    holder.iv_message_image_left_video.setVisibility(View.GONE);
                    holder.iv_message_image_left_video.clearAnimation();
                }
//                设置头像
                Glide.with(mContext.getApplicationContext())
                        .load(msg.getHeader() + msg.getSender() + ".jpg")
                        .signature(new StringSignature(PrefAppStore.getMessageHeaderImgTimestamp(mContext)))
                        .error(R.mipmap.ic_preloading)
                        .placeholder(R.mipmap.ic_preloading)
                        .dontAnimate()
                        .into(holder.civLeftHeadImg);
            }
            if (position == msgList.size() - 1) {
                holder.view_temp.setVisibility(View.VISIBLE);
            } else {
                holder.view_temp.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    /**
     * 长按点击事件
     */
    class MyOnLongClickListener implements View.OnLongClickListener {
        ViewHolder holder;
        MessageModel msg;

        int position;

        public MyOnLongClickListener(ViewHolder holder) {
            this.holder = holder;
        }

        public void setMessage(int position, MessageModel msg) {
            this.position = position;
            this.msg = msg;
        }

        @Override
        public boolean onLongClick(View v) {

            switch (v.getId()) {

                case R.id.ll_left_voice_info://左边语音布局

                case R.id.rl_left_message://左边消息布局

                case R.id.iv_left_video_img://左边视频缩略图

                case R.id.iv_left_message_image://左边图片缩略图


                    //左边列表，对话框列表只有删除
//                    showLongClickDialog(0);
                    if (!msg.getFileType().equals("7") && !msg.getFileType().equals("8") && !msg.getFileType().equals("9")) {
                        initChatListerDialog("0", v, position, "0");
                    }
                    return true;

                case R.id.iv_right_video_img://右边视频缩略图
                case R.id.iv_right_message_image://右边图片缩略图
                case R.id.rl_right_message://右边消息布局
                case R.id.ll_right_voice_info://右边语音布局
                    //判断是否是发送失败状态
                    if (com.merrichat.net.model.MessageModel.SEND_STATE_FAILURE == msg.getSendState() && !msg.getFileType().equals("7") && !msg.getFileType().equals("8") && !msg.getFileType().equals("9")) {//发送失败
                        //列表有删除和重发
//                        showLongClickDialog(1);
                        if ((System.currentTimeMillis() - msg.getTimestamp()) > 120000) {
                            initChatListerDialog("1", v, position, "0");
                        } else {
//                            initChatListerDialog("1", v, position, "1");
                            initChatListerDialog("1", v, position, "0");
                        }
                    } else {
                        //列表只有删除
//                        showLongClickDialog(0);
                        if ((System.currentTimeMillis() - msg.getTimestamp()) > 120000) {
                            initChatListerDialog("0", v, position, "0");
                        } else {
//                            initChatListerDialog("0", v, position, "1");
                            initChatListerDialog("0", v, position, "0");
                        }
                    }

                    break;

            }


            return false;
        }


        /**
         * 重新发送失败消息弹框
         */
        private void showResendMsg() {
            final RemindDialog remindDialog = new RemindDialog(mContext);
            remindDialog.setTitle("提示");
            remindDialog.setContent("确定要重新发送吗");
            remindDialog.setButtonYes("确定");
            remindDialog.setButtonNo("取消");
            remindDialog.setDialogOnClickListener(new DialogOnClickListener() {
                @Override
                public void Yes() {
                    //发送消息
                    SentBody sentBody = new SentBody();
                    sentBody.setKey(Constant.SENT_BODY_KEY);
                    sentBody.put("sender", msg.getSender());
                    sentBody.put("senderName", msg.getSenderName());
                    sentBody.put("receiverName", msg.getReceiverName());
                    sentBody.put("receiver", msg.getReceiver());
                    sentBody.put("type", msg.getType());
                    sentBody.put("fileType", msg.getFileType());
                    sentBody.put("content", msg.getContent());
                    sentBody.put("file", msg.getFile());
                    sentBody.put("filePath", msg.getFilePath());
                    sentBody.put("speechTimeLength", msg.getSpeechTimeLength());
                    //删除之前的数据
//                    msg.delete();
                    //重新设置时间
                    msg.setTimestamp(sentBody.getTimestamp());
                    //重新插入到数据库
//                    msg.save();
                    MessageModel.setMessageModel(msg);
                    msgList.remove(msg);
                    msgList.add(msg);
                    notifyDataSetChanged();

                    CIMPushManager.sendRequest(mContext, sentBody);
                    remindDialog.dismiss();
                }

                @Override
                public void No() {
                    remindDialog.dismiss();
                }
            });
            remindDialog.show();
        }

        private void unDo() {
            //发送消息
            SentBody sentBody = new SentBody();
            sentBody.setKey(Constant.SENT_BODY_KEY);
            sentBody.put("sender", msg.getSender());
            sentBody.put("senderName", msg.getSenderName());
            sentBody.put("receiverName", msg.getReceiverName());
            sentBody.put("receiver", msg.getReceiver());
            sentBody.put("type", msg.getType());
            sentBody.put("fileType", msg.getFileType());
            sentBody.put("content", msg.getContent());
            sentBody.put("file", msg.getFile());
            sentBody.put("filePath", msg.getFilePath());
            sentBody.put("speechTimeLength", msg.getSpeechTimeLength());
            sentBody.put("revoke", "1");//消息撤回标识 (如果不是撤回消息, 可不传) 1: 撤回
            sentBody.put("mid", msg.getMid());//消息id, 撤回消息时需要. 跟revoke字段一起传
            //删除之前的数据
//            msg.delete();
            //重新设置时间
//            msg.setTimestamp(sentBody.getTimestamp());
//            //重新插入到数据库
//            msg.save();
//            msgList.remove(msg);
//            msgList.add(msg);
//            notifyDataSetChanged();
            LogUtil.e("@@@----sent----", sentBody.toString());
            CIMPushManager.sendRequest(mContext, sentBody);

        }

        /**
         * 显示列表对话框(删除、重发)
         *
         * @param type
         */
        public void showLongClickDialog(int type) {
            String[] items = null;
            if (type == 1) {
                //显示删除和重发
                items = new String[]{"删除", "重发"};
            } else {
                items = new String[]{"删除"};
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            //    设置一个下拉的列表选择项
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0://删除
//                            msg.delete();
                            msgList.remove(msg);
                            notifyDataSetChanged();

                            break;
                        case 1://重发
                            showResendMsg();

                            break;
                    }
                }
            });
            builder.show();
        }

        private void initChatListerDialog(String s, View v, final int position, String s1) {//s1 0 没有插销按钮  1有撤销按钮
            int[] locationView = new int[2];
            v.getLocationOnScreen(locationView);
            chatDeleteDialog = new ChatDeleteDialog(mContext, R.style.dialog, s, s1);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            Display display = ((SingleChatActivity) mContext).getWindowManager().getDefaultDisplay();
            display.getMetrics(displayMetrics);
            WindowManager.LayoutParams params = chatDeleteDialog.getWindow().getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.y = display.getHeight() - locationView[1];
//            LogUtil.e("courier_http","locationView[0]:"+locationView[0]+":--------display.getWidth():"+display.getWidth());
            chatDeleteDialog.getWindow().setAttributes(params);
            chatDeleteDialog.setChatListener(new ChatDeleteDialog.ChatListener() {
                @Override
                public void deleteChatClick() {
//                GetToast.useString(mContext,"删除"+position);
//                    msg.delete();
                    MessageModel.clearMessageModel();
                    msgList.remove(msg);
                    notifyDataSetChanged();
                    chatDeleteDialog.dismiss();
                }

                @Override
                public void reSendChatClick() {
//                    GetToast.useString(mContext,"重发"+position);
                    showResendMsg();
                    chatDeleteDialog.dismiss();
                }

                @Override
                public void undoChatClick() {
//                    GetToast.useString(mContext,"撤销"+position);
//                    unDo();
                    chatDeleteDialog.dismiss();
                }
            });
            chatDeleteDialog.setCanceledOnTouchOutside(true);
            chatDeleteDialog.show();
        }
    }

    /**
     * 把接收的信息判断是否有表情，若有，转换成gif/png格式
     *
     * @param gifTextView
     * @param content
     * @return
     */
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


    class MyOnclickListener implements View.OnClickListener {
        ViewHolder holder;
        MessageModel msg;

        int position;

        public MyOnclickListener(ViewHolder holder) {
            this.holder = holder;
        }

        public void setMessage(int position, MessageModel msg) {
            this.position = position;
            this.msg = msg;
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.civ_left_head_img://左边用户头像
                    intent = new Intent(mContext, HisYingJiAty.class);
                    intent.putExtra("fromId", SingleChatActivity.activityId);
                    intent.putExtra("hisMemberId", Long.parseLong(msg.getSender()));
                    intent.putExtra("hisImgUrl", msg.getHeader() + msg.getSender() + ".jpg");
                    intent.putExtra("hisNickName", msg.getSenderName());
                    mContext.startActivity(intent);
                    break;

                case R.id.civ_right_head_img://右边用户头像(自己)
                    //跳转到个人主页
//                    intent = new Intent(mContext, MyHomePageActivity.class);
//                    intent.putExtra("memberId", UserModel.getUserModel().getMemberId());
//                    mContext.startActivity(intent);

                    break;
                case R.id.iv_left_message_image://左边图片
                    imgUrls.clear();
                    imgUrls.add(msg.getFile());
                    intent = new Intent(mContext, ChatAmplificationActivity.class);
                    intent.putStringArrayListExtra("imgUrl", imgUrls);
                    mContext.startActivity(intent);
                    ((SingleChatActivity) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                    break;

                case R.id.ll_left_voice_info://左边语音
                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        GetToast.useString(mContext, "文件正在下载");
                    }

                    //当前条目正在播放时，再次点击时暂停播放,再次点击恢复
                    if (voicePlayNowPosition == position && MediaManage.isPlaying()) {
                        //当前条目语音正在播放,暂停播放
                        MediaManage.release();
                        if (animationDrawable != null && animationDrawable.isRunning()) {
                            animationDrawable.stop();
                            holder.ivLeftVoice.setImageResource(R.mipmap.icon_left_play_voice_3);
                            notifyDataSetChanged();
                        }
                        voicePlayNowPosition = -1;
                        return;
                    }

                    if (animationDrawable != null && animationDrawable.isRunning()) {
                        animationDrawable.stop();
                        notifyDataSetChanged();
                    }
                    voicePlayNowPosition = position;
                    holder.ivLeftVoice.setImageResource(R.drawable.play_left_voice_message_anim);
                    animationDrawable = (AnimationDrawable) holder.ivLeftVoice.getDrawable();

                    playRecorderMethod();
                    //修改语音已读状态
                    msg.setIsReadVoice(true);
//                    msg.save();
                    MessageModel.setMessageModel(msg);
                    break;

                case R.id.iv_left_video_img://左边视频
                    if (!TextUtils.isEmpty(msg.getFilePath()) || !TextUtils.isEmpty(msg.getFile())) {
                        intent = new Intent(mContext, VedioShowActivity.class);
                        intent.putExtra("vedio", TextUtils.isEmpty(msg.getFilePath()) ? msg.getFile() : msg.getFilePath());
                        mContext.startActivity(intent);
                    }
                    break;

                case R.id.iv_right_message_image://右边图片
                    imgUrls.clear();
                    imgUrls.add(TextUtils.isEmpty(msg.getFilePath()) ? msg.getFile() : msg.getFilePath());
                    intent = new Intent(mContext, ChatAmplificationActivity.class);
                    intent.putStringArrayListExtra("imgUrl", imgUrls);
                    mContext.startActivity(intent);
                    ((SingleChatActivity) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                    break;

                case R.id.ll_right_voice_info://右边语音
                    if (TextUtils.isEmpty(msg.getFilePath())) {
                        GetToast.useString(mContext, "文件正在下载");
                    }

                    //当前条目正在播放时，再次点击时暂停播放,再次点击恢复
                    if (voicePlayNowPosition == position && MediaManage.isPlaying()) {
                        //当前条目语音正在播放,暂停播放
                        MediaManage.release();
                        if (animationDrawable != null && animationDrawable.isRunning()) {
                            animationDrawable.stop();
                            holder.ivRightVoice.setImageResource(R.mipmap.icon_right_play_voice_3);
                            notifyDataSetChanged();
                        }
                        voicePlayNowPosition = -1;
                        return;
                    }
                    if (animationDrawable != null && animationDrawable.isRunning()) {
                        animationDrawable.stop();
                        notifyDataSetChanged();
                    }

                    voicePlayNowPosition = position;
                    holder.ivRightVoice.setImageResource(R.drawable.play_right_voice_message_anim);
                    animationDrawable = (AnimationDrawable) holder.ivRightVoice.getDrawable();

                    playRecorderMethod();
                    break;

                case R.id.iv_right_video_img://右边视频
                    if (!TextUtils.isEmpty(msg.getFilePath()) || !TextUtils.isEmpty(msg.getFile())) {
                        intent = new Intent(mContext, VedioShowActivity.class);
                        intent.putExtra("vedio", TextUtils.isEmpty(msg.getFilePath()) ? msg.getFile() : msg.getFilePath());
                        mContext.startActivity(intent);
                    }
                    break;
                case R.id.rl_left_message:
                    if (!msg.getFileType().equals("7") && !msg.getFileType().equals("8") && !msg.getFileType().equals("9")) {
                        intent = new Intent(mContext, ChatAmplificationActivity.class);
                        intent.putExtra("text", msg.getContent());
                        mContext.startActivity(intent);
                        ((SingleChatActivity) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                    }
                    break;
                case R.id.rl_right_message:
                    if (!msg.getFileType().equals("7") && !msg.getFileType().equals("8") && !msg.getFileType().equals("9")) {
                        intent = new Intent(mContext, ChatAmplificationActivity.class);
                        intent.putExtra("text", msg.getContent());
                        mContext.startActivity(intent);
                        ((SingleChatActivity) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                    }
                    break;
                case R.id.ll_right_message_red_package:
                    if (msg.getFileType().equals("10")) {//转账
                        ((Activity) mContext).startActivityForResult(new Intent(mContext, ZhuanZhangDetialAty.class).putExtra("tid", msg.getRedTid())
                                .putExtra("name", msg.getSenderName()).putExtra("money", msg.getContent()).putExtra("id", msg.getID()).putExtra("isLeft", false)
                                .putExtra("senderId", msg.getSender()).putExtra("index", position), REQUEST_ZHUANZHANG_DETIAL_CODE);
                    } else if (msg.getFileType().equals("8")) {//红包
                        redEnState(msg, position, true, false);
                    }
                    break;
                case R.id.ll_left_message_red_package:
                    if (msg.getFileType().equals("10")) {//转账
                        ((Activity) mContext).startActivityForResult(new Intent(mContext, ZhuanZhangDetialAty.class).putExtra("tid", msg.getRedTid())
                                .putExtra("name", msg.getSenderName()).putExtra("money", msg.getContent()).putExtra("id", msg.getID()).putExtra("isLeft", true)
                                .putExtra("senderId", msg.getSender()).putExtra("index", position), REQUEST_ZHUANZHANG_DETIAL_CODE);
                    } else if (msg.getFileType().equals("8")) {//红包
                        redEnState(msg, position, true, true);
                    }
                    break;
//                case R.id.iv_send_failure://发送失败叹号
//                    showResendMsg();
//                    break;
                case R.id.tv_right_map_mame:
                    mContext.startActivity(new Intent(mContext, ShowLocationAty.class).putExtra("location", msg.getContent()));
                    break;
                case R.id.tv_left_map_mame:
                    mContext.startActivity(new Intent(mContext, ShowLocationAty.class).putExtra("location", msg.getContent()));
                    break;
            }
        }


        /**
         * 点击语音时播放语音的方法
         *
         * @Title: playRecorderMethod
         */
        private void playRecorderMethod() {

            animationDrawable.start();
            // 播放音频
            MediaManage.playSound(msg.getFilePath(),
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            animationDrawable.stop();
                            voicePlayNowPosition = -1;
                            if (holder.ivLeftVoice.getVisibility() == View.VISIBLE) {
                                //当前显示的是左边的他人发来的条目
                                holder.ivLeftVoice.setImageResource(R.mipmap.icon_left_play_voice_3);
                            } else {
                                //我自己发的消息
                                holder.ivRightVoice.setImageResource(R.mipmap.icon_right_play_voice_3);
                            }
                            notifyDataSetChanged();

                        }
                    });
        }
    }

    static class ViewHolder {
        /**
         * 聊天时间
         */
        @BindView(R.id.tv_message_time)
        TextView tvMessageTime;
        /**
         * 系统消息
         */
        @BindView(R.id.tv_sys_tip)
        TextView tvSysTip;
        /**
         * 红包已经被领取
         */
        @BindView(R.id.tv_red_tip)
        TextView tvRedTip;

        /***************
         * 左边：TA发来的消息
         **********************/
        @BindView(R.id.ll_left_message_all_info)
        LinearLayout llLeftMessageAllInfo;
        /**
         * TA的头像
         */
        @BindView(R.id.civ_left_head_img)
        CircleImageView civLeftHeadImg;
        /**
         * TA发送的消息的内容
         */
        @BindView(R.id.tv_left_message_content)
        TextView tvLeftMessageContent;

        /**
         * TA发送的语音的布局（随时长变化，点击播放）
         */
        @BindView(R.id.ll_left_voice_info)
        LinearLayout llLeftVoiceInfo;
        /**
         * TA发送的语音
         */
        @BindView(R.id.iv_left_voice)
        ImageView ivLeftVoice;
        /**
         * TA分享的标题
         */
        @BindView(R.id.tv_left_share_title)
        TextView tvLeftShareTitle;
        /**
         * TA分享的图片
         */
        @BindView(R.id.iv_left_share_image)
        ImageView ivLeftShareImage;
        /**
         * TA分享的内容
         */
        @BindView(R.id.tv_left_share_content)
        TextView tvLeftShareContent;
        /**
         * TA分享布局相关
         */
        @BindView(R.id.rl_left_share_info)
        RelativeLayout rlLeftShareInfo;
        /**
         * TA分享布局相关
         */
        @BindView(R.id.iv_left_message_gift)
        ImageView iveftLMessageGift;
        /**
         * TA文本、语音、分享布局
         */
        @BindView(R.id.rl_left_message)
        RelativeLayout rlLeftMessage;
        /**
         * TA发送的红包布局
         */
        @BindView(R.id.ll_left_message_red_package)
        LinearLayout llLeftMessageRedPackage;
        /**
         * 小红包
         */
        @BindView(R.id.iv_left_red_package_small)
        ImageView ivLeftRedPackageSmall;

        /**
         * 布局黄色
         */
        @BindView(R.id.ll_left_red_package_yellow)
        LinearLayout llLeftRedPackageYellow;
        /**
         * 红包寄语
         */
        @BindView(R.id.tv_left_red_package_content)
        TextView tvLeftRedPackageContent;
        /**
         * 红包领取状态
         */
        @BindView(R.id.tv_left_red_package_status)
        TextView tvLeftRedPackageStatus;
        /**
         * 钱包type
         */
        @BindView(R.id.tv_left_money_type)
        TextView tv_left_money_type;
        /**
         * TA发送的图片
         */
        @BindView(R.id.iv_left_message_image)
        ImageView ivLeftMessageImage;
        /**
         * 加载他发送的图片的进度条
         */
        @BindView(R.id.iv_message_image_left)
        ImageView iv_message_image_left;
        /**
         * TA发送的视频的第一帧图片
         */
        @BindView(R.id.iv_left_video_img)
        ImageView ivLeftVideoImg;
        /**
         * TA发送的视频的第一帧图片进度条
         */
        @BindView(R.id.iv_message_image_left_video)
        ImageView iv_message_image_left_video;
        /**
         * TA发送的视频布局
         */
        @BindView(R.id.rl_left_message_video)
        RelativeLayout rlLeftMessageVideo;
        /**
         * TA发送的消息未读红点
         */
        @BindView(R.id.iv_message_unread_red)
        ImageView ivMessageUnreadRed;
        /**
         * TA发送的语音的时长(后面要加上'')
         */
        @BindView(R.id.tv_left_message_voice_time)
        TextView tvLeftMessageVoiceTime;
        /**
         * 商品布局
         */
        @BindView(R.id.rafl_left_shop)
        RoundAngleFrameLayout rafl_left_shop;
        /**
         * 商品封面
         */
        @BindView(R.id.iv_left_shop_cover)
        SimpleDraweeView iv_left_shop_cover;
        /**
         * 商品名字
         */
        @BindView(R.id.tv_left_shop_name)
        TextView tv_left_shop_name;
        /**
         * 商品价格
         */
        @BindView(R.id.tv_left_shop_price)
        TextView tv_left_shop_price;
        /**
         * 商品购买按钮
         */
        @BindView(R.id.tv_left_shop_buy)
        TextView tv_left_shop_buy;

        /**
         * 地图布局
         */
        @BindView(R.id.rafl_left_map)
        RoundAngleFrameLayout rafl_left_map;

        /**
         * 地址名字
         */
        @BindView(R.id.tv_left_map_mame)
        TextView tv_left_map_mame;

        /**
         * 地图
         */
        @BindView(R.id.iv_left_map)
        ImageView iv_left_map;

        /***************
         * 右边：我发送的消息
         *************************/
        @BindView(R.id.ll_right_message_all_info)
        LinearLayout llRightMessageAllInfo;
        /**
         * 我的头像
         */
        @BindView(R.id.civ_right_head_img)
        CircleImageView civRightHeadImg;

        /**
         * 我发送的消息的内容
         */
        @BindView(R.id.tv_right_message_content)
        TextView tvRightMessageContent;
        /**
         * 我发送的礼物
         */
        @BindView(R.id.iv_right_message_gift)
        ImageView ivRightMessageift;

        /**
         * 右边语音布局（随时长变化，点击播放）
         */
        @BindView(R.id.ll_right_voice_info)
        LinearLayout llRightVoiceInfo;
        /**
         * 我发的语音
         */
        @BindView(R.id.iv_right_voice)
        ImageView ivRightVoice;
        /**
         * 我分享的标题
         */
        @BindView(R.id.tv_right_share_title)
        TextView tvRightShareTitle;
        /**
         * 我分享的图片
         */
        @BindView(R.id.iv_right_share_image)
        ImageView ivRightShareImage;
        /**
         * 我分享的内容
         */
        @BindView(R.id.tv_right_share_content)
        TextView tvRightShareContent;
        /**
         * 我分享的相关
         */
        @BindView(R.id.rl_right_share_info)
        RelativeLayout rlRightShareInfo;
        /**
         * 我发送的文本、语音、分享布局相关
         */
        @BindView(R.id.rl_right_message)
        RelativeLayout rlRightMessage;
        /**
         * 我发送的红包布局
         */
        @BindView(R.id.ll_right_message_red_package)
        LinearLayout llRightMessageRedPackage;
        /**
         * 小红包
         */
        @BindView(R.id.iv_right_red_package_small)
        ImageView ivRightRedPackageSmall;

        /**
         * 布局黄色
         */
        @BindView(R.id.ll_right_red_package_yellow)
        LinearLayout llRightRedPackageYellow;
        /**
         * 红包寄语
         */
        @BindView(R.id.tv_right_red_package_content)
        TextView tvRightRedPackageContent;
        /**
         * 红包领取状态
         */
        @BindView(R.id.tv_right_red_package_status)
        TextView tvRightRedPackageStatus;
        /**
         * 钱包type
         */
        @BindView(R.id.tv_right_money_type)
        TextView tv_right_money_type;
        /**
         * 我发送的图片
         */
        @BindView(R.id.iv_right_message_image)
        ImageView ivRightMessageImage;
        /**
         * 我发送的视频（第一帧预览）
         */
        @BindView(R.id.iv_right_video_img)
        ImageView ivRightVideoImg;
        /**
         * 我视频外布局
         */
        @BindView(R.id.rl_right_message_video)
        RelativeLayout rlRightMessageVideo;
        /**
         * 我发的语音时长
         */
        @BindView(R.id.tv_right_message_voice_time)
        TextView tvRightMessageVoiceTime;
        /**
         * 我发送消息的正在发送消息的动画
         */
        @BindView(R.id.iv_sending)
        ImageView ivSending;
        /**
         * 发送失败叹号（点击后重新发送）
         */
        @BindView(R.id.iv_send_failure)
        ImageView ivSendFailure;
        /**
         * 顶部高度
         */
        @BindView(R.id.view_temp)
        View view_temp;
        /**
         * 商品布局
         */
        @BindView(R.id.rafl_right_shop)
        RoundAngleFrameLayout rafl_right_shop;
        /**
         * 商品封面
         */
        @BindView(R.id.iv_right_shop_cover)
        SimpleDraweeView iv_right_shop_cover;
        /**
         * 商品名字
         */
        @BindView(R.id.tv_right_shop_name)
        TextView tv_right_shop_name;
        /**
         * 商品价格
         */
        @BindView(R.id.tv_right_shop_price)
        TextView tv_right_shop_price;
        /**
         * 商品购买按钮
         */
        @BindView(R.id.tv_right_shop_buy)
        TextView tv_right_shop_buy;

        /**
         * 地图布局
         */
        @BindView(R.id.rafl_right_map)
        RoundAngleFrameLayout rafl_right_map;

        /**
         * 地址名字
         */
        @BindView(R.id.tv_right_map_mame)
        TextView tv_right_map_mame;

        /**
         * 地图
         */
        @BindView(R.id.iv_right_map)
        ImageView iv_right_map;

        MyOnclickListener listener;
        MyOnLongClickListener longListener;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setListener(MyOnclickListener listener, MyOnLongClickListener longListener) {
            this.listener = listener;
            this.longListener = longListener;
        }
    }

    /**
     * 取消所有下载文件的请求
     */
    public void cancelOkHttpClient() {
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
    }

    /**
     * 异步下载文件
     *
     * @param downUrl  下载的url
     * @param destFile 本地文件存储的文件夹
     * @param suffix   文件后辍（如.mp3   .mp4）
     */
    private void downloadAsyn(final MessageModel message, final String downUrl, final File destFile, final String suffix) {
        Request request = new Request.Builder().url(downUrl).tag(mContext).build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(destFile, MD5.Md5(downUrl) + suffix);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        LogUtil.d("@@@", "progress=" + progress);

                    }
                    fos.flush();
                    LogUtil.d("@@@", "文件下载成功");
                    message.setFilePath(file.getAbsolutePath());
//                    message.save();
                    MessageModel.setMessageModel(message);
                } catch (Exception e) {
                    LogUtil.d("@@@", "文件下载失败");
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * 红包状态
     *
     * @param msg
     * @param index     红包在消息列表的位置索引
     * @param isControl 是否做界面更新操作
     */
    private void redEnState(final MessageModel msg, final int index, final boolean isControl, final boolean isCanChai) {
        OkGo.<String>post(Urls.redEnState)
                .params("tid", msg.getRedTid())
                .execute(new StringDialogCallback((Activity) mContext) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String amount = data.optString("amount");
                                String status = data.optString("status");
                                if (status.equals("510") && isCanChai) {
                                    final ChaiRedBaoDialog chaiRedBaoDialog = new ChaiRedBaoDialog(mContext, msg.getFileType(), msg.getHeader(), msg.getSender(), msg.getSenderName(), msg.getContent(),"");
                                    chaiRedBaoDialog.show();
                                    chaiRedBaoDialog.setClicklistener(new ChaiRedBaoDialog.ClickListenerInterface() {
                                        @Override
                                        public void chaiRedBao() {
                                            new Handler().postDelayed(new Runnable() {
                                                public void run() {
                                                    chaiRedBaoDialog.dismiss();
                                                    collectRed(msg.getRedTid(), msg.getSenderName(), msg.getSender(), msg.getContent(), index);
                                                }
                                            }, 2000);
                                        }
                                    });
                                } else {
                                    ((Activity) mContext).startActivityForResult(new Intent(mContext, RedBaoDetailsActivity.class)
                                            .putExtra("amount", amount)
                                            .putExtra("senderName", msg.getSenderName())
                                            .putExtra("isControl", isControl)
                                            .putExtra("tid", msg.getRedTid())
                                            .putExtra("redContent", msg.getContent())
                                            .putExtra("index", index)
                                            .putExtra("status", status), REQUEST_CHAI_RED_PACKAGE_CODE);
                                    ((SingleChatActivity) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(mContext, "请求失败，请稍后重试");
                    }
                });
    }


    /**
     * 拆红包
     */
    private void collectRed(final String tid, final String senderName, final String hairMemberId, final String redContent, final int index) {
        OkGo.<String>post(Urls.collectRed)
                .params("tid", tid)
                .params("hairMemberId", hairMemberId)
                .params("collectMemberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback((Activity) mContext) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String amount = data.optString("amount");
                                String status = data.optString("status");
                                ((Activity) mContext).startActivityForResult(new Intent(mContext, RedBaoDetailsActivity.class)
                                        .putExtra("amount", amount)
                                        .putExtra("isChai", true)
                                        .putExtra("senderName", senderName)
                                        .putExtra("isControl", true)
                                        .putExtra("index", index)
                                        .putExtra("tid", tid)
                                        .putExtra("redContent", redContent)
                                        .putExtra("status", status), REQUEST_CHAI_RED_PACKAGE_CODE);
                                ((SingleChatActivity) mContext).overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        GetToast.showToast(mContext, "请求失败，请稍后重试");
                    }
                });
    }

    class RedPackageClickableSpan extends ClickableSpan {

        private MessageModel messageModel;

        public RedPackageClickableSpan(MessageModel msg) {
            this.messageModel = msg;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            redEnState(messageModel, -1, false, false);
        }
    }

    public interface IReciveMeiBi {
        void changeMeiBiViewValue();
    }

}

package com.merrichat.net.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.ContactModel;
import com.merrichat.net.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amssy on 17/11/14.
 * 联系人---通讯录好友adapter
 */

public class AddressBookFriendsAdapter extends BaseQuickAdapter<ContactModel.DataBean.ListBean, BaseViewHolder> {


    private boolean mIsSearch;
    private OnClickCallBack onClickCallBack;

    public AddressBookFriendsAdapter(int layoutResId, ArrayList<ContactModel.DataBean.ListBean> data, boolean isSearch) {
        super(layoutResId, data);
        this.mIsSearch = isSearch;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final ContactModel.DataBean.ListBean item) {
        TextView tvName = helper.getView(R.id.tv_adressbook_friends_name);
        TextView tvPhone = helper.getView(R.id.tv_adressbook_friends_tel);
        CircleImageView iv = helper.getView(R.id.civ_adressbook_friends);
        TextView tv_staus = helper.getView(R.id.tv_staus);


        if (!TextUtils.isEmpty(item.getHeadUrl())) {
            Glide.with(mContext).load(item.getHeadUrl()).dontAnimate().placeholder(R.mipmap.ic_preloading).into(iv);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_preloading).dontAnimate().placeholder(R.mipmap.ic_preloading).into(iv);
        }

        if (TextUtils.isEmpty(item.getKeyWords())) {
            tvName.setText(String.valueOf(item.getNick()));
            tvPhone.setText(String.valueOf(item.getMobile()));
        } else {
            setSpecifiedTextsColor(tvName, item.getNick(), item.getKeyWords(), Color.RED);
            setSpecifiedTextsColor(tvPhone, item.getMobile(), item.getKeyWords(), Color.RED);
        }
        if (mIsSearch) {
            iv.setVisibility(View.GONE);
            tv_staus.setVisibility(View.GONE);
        } else {
            iv.setVisibility(View.VISIBLE);
            tv_staus.setVisibility(View.VISIBLE);
            if (item.getFlag() == 1) {
                tv_staus.setText("加好友");
                tv_staus.setTextColor(Color.parseColor("#FFFFFF"));
                tv_staus.setBackgroundResource(R.drawable.shape_button_video_music);
                tv_staus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickCallBack.onAddClick(helper.getAdapterPosition());
                    }
                });
            } else if (item.getFlag() == 2) {
                tv_staus.setText("已添加");
                tv_staus.setTextColor(Color.parseColor("#888888"));
                tv_staus.setBackground(null);
            } else if (item.getFlag() == 0) {
                tv_staus.setText("  邀请  ");
                tv_staus.setTextColor(Color.parseColor("#FFFFFF"));
                tv_staus.setBackgroundResource(R.drawable.shape_blue);
                tv_staus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickCallBack != null) {
                            onClickCallBack.onYaoQingClick(helper.getAdapterPosition());
                        }
                    }
                });
            } else if (item.getFlag() == 3) {
                tv_staus.setText("  同意  ");
                tv_staus.setTextColor(Color.parseColor("#FFFFFF"));
                tv_staus.setBackgroundResource(R.drawable.shape_button_video_music);
                tv_staus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickCallBack != null) {
                            onClickCallBack.onTongYiClick(helper.getAdapterPosition());
                        }
                    }
                });
            } else if (item.getFlag() == 4) {
                tv_staus.setText("等待同意");
                tv_staus.setTextColor(Color.parseColor("#888888"));
                tv_staus.setBackground(null);
            }
        }
    }

    public void setSpecifiedTextsColor(TextView tv, String text, String specifiedTexts, int color) {
        List<Integer> sTextsStartList = new ArrayList<>();
        int sTextLength = specifiedTexts.length();
        String temp = text;
        int lengthFront = 0;//记录被找出后前面的字段的长度
        int start = -1;
        do {
            start = temp.indexOf(specifiedTexts);
            if (start != -1) {
                start = start + lengthFront;
                sTextsStartList.add(start);
                lengthFront = start + sTextLength;
                temp = text.substring(lengthFront);
            }
        } while (start != -1);
        SpannableStringBuilder styledText = new SpannableStringBuilder(text);
        for (Integer i : sTextsStartList) {
            styledText.setSpan(new ForegroundColorSpan(color), i, i + sTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(styledText);
    }

    public OnClickCallBack getOnClickCallBack() {
        return onClickCallBack;
    }

    public void setOnClickCallBack(OnClickCallBack onClickCallBack) {
        this.onClickCallBack = onClickCallBack;
    }

    public interface OnClickCallBack {
        void onAddClick(int position);

        void onYaoQingClick(int position);

        void onTongYiClick(int position);
    }

}

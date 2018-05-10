package com.merrichat.net.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.SearchFriendsModel;
import com.merrichat.net.utils.RxTools.RxDataTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AMSSY1 on 2018/3/8.
 */

public class SearchAllPersonsAdapter extends BaseQuickAdapter<SearchFriendsModel, BaseViewHolder> {
    private String keyWord = "";

    public SearchAllPersonsAdapter(int viewId, ArrayList<SearchFriendsModel> list) {
        super(viewId, list);

    }

    @Override
    protected void convert(BaseViewHolder helper, SearchFriendsModel item) {

        ImageView headerView = (ImageView) helper.getView(R.id.cv_friends_photo);
        TextView friendName = helper.getView(R.id.tv_friends_name);
        if (!TextUtils.isEmpty(item.getImgUrl())) {
            Glide.with(mContext).load(item.getImgUrl()).into(headerView);
        } else {
            Glide.with(mContext).load(R.mipmap.icon_logo).into(headerView);
        }
        if (RxDataTool.isNullString(keyWord)) {
            helper.setText(R.id.tv_friends_name, item.getNickName());
        } else {
            setSpecifiedTextsColor(friendName, item.getNickName(), keyWord, Color.RED);

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
            styledText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), i, i + sTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(styledText);
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}

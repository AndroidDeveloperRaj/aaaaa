package com.merrichat.net.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merrichat.net.R;
import com.merrichat.net.model.SearchFriendsModel;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amssy on 18/2/27.
 * 联系人列表--搜索好友adapter
 */

public class SearchFriendsAdapter extends BaseQuickAdapter<SearchFriendsModel, BaseViewHolder> {


    private String keyWord = "";
    public SearchFriendsAdapter(int layoutResId, @Nullable List<SearchFriendsModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchFriendsModel item) {
        Glide.with(mContext).load(item.getImgUrl()).into((CircleImageView) helper.getView(R.id.cv_friends_photo));
        helper.setText(R.id.tv_friends_name, item.getNickName());
        TextView friendName = helper.getView(R.id.tv_friends_name);
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

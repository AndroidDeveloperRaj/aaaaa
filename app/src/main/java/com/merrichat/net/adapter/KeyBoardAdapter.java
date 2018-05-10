package com.merrichat.net.adapter;

import android.content.Context;
import android.graphics.Color;

import com.merrichat.net.R;
import com.merrichat.net.adapter.abslistview.CommonAdapter;
import com.merrichat.net.adapter.abslistview.ViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by chenjingjing on 17/8/3.
 */

public class KeyBoardAdapter extends CommonAdapter {

    private List<Map<String, String>> valueList;

    public KeyBoardAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
        this.valueList = datas;

    }

    @Override
    protected void convert(ViewHolder viewHolder, Object item, int position) {
        if (position == 9) {
            viewHolder.setInVisible(R.id.imgDelete);
            viewHolder.setVisible(R.id.btn_keys, true);
            viewHolder.setText(R.id.btn_keys, valueList.get(position).get("name"));
            viewHolder.setBackgroundColor(R.id.btn_keys, Color.parseColor("#bebebe"));
        } else if (position == 11) {
            viewHolder.setBackgroundRes(R.id.btn_keys, R.mipmap.keyboard_delete_img);
            viewHolder.setVisible(R.id.imgDelete, true);
            viewHolder.setBackgroundRes(R.id.imgDelete, R.drawable.item_keyboard_selector_gv_);
            viewHolder.setInVisible(R.id.btn_keys);

        } else {
            viewHolder.setInVisible(R.id.imgDelete);
            viewHolder.setVisible(R.id.btn_keys, true);
            viewHolder.setText(R.id.btn_keys, valueList.get(position).get("name"));
        }

    }
}

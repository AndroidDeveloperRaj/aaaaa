package com.merrichat.net.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.CompanyData;
import com.merrichat.net.model.GetNetsModel;
import com.merrichat.net.model.SortModel;

/**
 * 选择公司列表
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<CompanyData> list = null;
    private Context mContext;

    /**
     * @param mContext
     * @param list     list 数据
     */
    public SortAdapter(Context mContext, List<CompanyData> list) {
        this.mContext = mContext;
        this.list = list;
    }

    //筛选选择搜索列表
    public void updateListView(List<CompanyData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final CompanyData mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.ivIco = (SimpleDraweeView) view.findViewById(R.id.iv_ico);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.code);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        viewHolder.ivIco.setImageURI(mContent.url);
        viewHolder.tvTitle.setText(this.list.get(position).netName);
        return view;

    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        SimpleDraweeView ivIco;
    }

    public int getSectionForPosition(int position) {
        if (list.get(position).code.length() != 0) {
            return list.get(position).code.charAt(0);
        } else {
            return 0;
        }
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).code;
            if (sortStr == null) {
                return -1;
            }
            if (sortStr.length() == 0) {
                return 0;
            }
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
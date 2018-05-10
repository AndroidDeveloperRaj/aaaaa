package com.merrichat.net.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.model.QueryReportingTypesModel;

import java.util.List;

/**
 * 显示举报列表
 */

public class QueryReportingAdapter extends BaseAdapter {
    //    private int page;

    private List<QueryReportingTypesModel.ReportingTypesItem> typesItems;

    private LayoutInflater mInflater;
    private Context mContext;

    public QueryReportingAdapter(Context context, List<QueryReportingTypesModel.ReportingTypesItem> typesItems) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.typesItems = typesItems;
    }

    @Override
    public int getCount() {
        return typesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return typesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_query_reporting, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.warning_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (typesItems.get(position) == null) {
            return convertView;
        }
        viewHolder.mTextView.setText(typesItems.get(position).typeName);
        return convertView;
    }

    private final class ViewHolder {
        TextView mTextView;
    }

}

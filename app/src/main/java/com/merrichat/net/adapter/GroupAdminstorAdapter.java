package com.merrichat.net.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.merrichat.net.R;
import com.merrichat.net.model.GroupAdminstorModel;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 群成员列表（设置管理员）
 */
public class GroupAdminstorAdapter extends BaseListAdapter<GroupAdminstorModel> implements SectionIndexer {
    List<GroupAdminstorModel> list;
    private Context mContext;

    public GroupAdminstorAdapter(Context context, List<GroupAdminstorModel> data) {
        super(context, data);
        this.list = data;
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group_adminstor, null);
            viewHolder.tvCatalog = (TextView) convertView.findViewById(R.id.tv_catalog);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.simpleHeader = (SimpleDraweeView) convertView.findViewById(R.id.simple_header);
            viewHolder.imageViewCheck = (ImageView) convertView.findViewById(R.id.imageView_check);
            viewHolder.relGroup = (RelativeLayout) convertView.findViewById(R.id.rel_group);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(list.get(position).getFirstLetter());
        } else {
            viewHolder.tvCatalog.setVisibility(View.GONE);
        }

        if (list.get(position).isChecked()){
            viewHolder.imageViewCheck.setImageResource(R.mipmap.accept_2x_true);
        }else {
            viewHolder.imageViewCheck.setImageResource(R.mipmap.accept_2x_none);
        }
        viewHolder.tvName.setText(this.list.get(position).getName());
        viewHolder.simpleHeader.setImageURI(this.list.get(position).getImaUrl());

        viewHolder.relGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupItemClickLinster.onItemClick(position);
            }
        });

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstLetter().charAt(0);
    }

    final static class ViewHolder {
        TextView tvCatalog;
        SimpleDraweeView simpleHeader;
        TextView tvName;
        ImageView imageViewCheck;
        RelativeLayout relGroup;
    }
    public onGroupItemClickLinster onGroupItemClickLinster;

    public void setOnGroupItemClickLinster(onGroupItemClickLinster onGroupItemClickLinster){
        this.onGroupItemClickLinster = onGroupItemClickLinster;
    }

    public interface onGroupItemClickLinster{
        void onItemClick(int position);
    }
}

package com.merrichat.net.weidget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.merrichat.net.R;
import com.merrichat.net.activity.meiyu.fragments.view.CircleImageView;
import com.merrichat.net.adapter.EncounterBodyAdapter;
import com.merrichat.net.model.EncounterPersonModel;

import java.util.List;

public class VpAdapter extends PagerAdapter {
    private int selectPosition = -1;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    private List<EncounterPersonModel.EncounterBody> listEncounterBody;// 遇到的人列表
    private Context context;


    public VpAdapter(List<EncounterPersonModel.EncounterBody> listEncounterBody, Context context) {
        this.listEncounterBody = listEncounterBody;
        this.context = context;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listEncounterBody.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = (View) LayoutInflater.from(context).inflate(R.layout.item_view_userurl, null);
        viewHolder = new ViewHolder(view);
        EncounterPersonModel.EncounterBody encounterBody = listEncounterBody.get(position);
        Glide.with(context)
                .load(encounterBody.headImgUrl)
                .priority(Priority.HIGH)
                .into(viewHolder.meat_ico);


        if (selectPosition == position) {
            viewHolder.meat_ico.getLayoutParams().height = 200;
            viewHolder.meat_ico.getLayoutParams().width = 200;
        } else {
            viewHolder.meat_ico.getLayoutParams().height = 100;
            viewHolder.meat_ico.getLayoutParams().width = 100;
        }

        container.addView(view);
        return view;
    }

    private ViewHolder viewHolder;

    class ViewHolder {
        public CircleImageView meat_ico;  // 当前人物的头像

        public ViewHolder(View view) {
            meat_ico = (CircleImageView) view.findViewById(R.id.meat_ico);
        }
    }

}

package com.merrichat.net.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.merrichat.net.R;
import com.merrichat.net.model.WaiteGroupBuyModel;

import org.akita.ui.adapter.AkBaseAdapter;

/**
 * Created by wangweiwei on 2018/1/20.
 */

public class WaiteGroupSendAdapter extends AkBaseAdapter<WaiteGroupBuyModel.Data> {
    private Fragment mFragment;
    private Callback mCallback;

    public WaiteGroupSendAdapter(Context context, Fragment fragment, Callback callback) {
        super(fragment.getActivity());
        mFragment = fragment;
        mCallback = callback;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = (mFragment.getActivity().getLayoutInflater()).inflate(
                    R.layout.item_waite_group_buy, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder {
//
//        private RoundedImageView iv_icon;  //客户头像
//        private ImageView iv_hart; //评价类型图片
//        private ImageView iv_gender;    //客户性别
//        private RelativeLayout rl_call_back;    //回复
//        private TextView tv_name;   //客户名称
//        private TextView tv_time;   //回复时间
//        private TextView tv_evaluate; //回复类型
//
//        private TextView tv_evaluate_content; //回复内容
//        private TextView tv_content_reply;  //老师回复
//
//        private TextView tv_lesson_time; //上课时间
//

        public ViewHolder(View convertView) {
//            iv_icon = (RoundedImageView) convertView.findViewById(R.id.iv_icon);
//            iv_hart = (ImageView) convertView.findViewById(R.id.iv_hart);
//            iv_gender = (ImageView) convertView.findViewById(R.id.iv_gender);
//
//            rl_call_back = (RelativeLayout) convertView.findViewById(R.id.rl_call_back);
//
//            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//            tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//            tv_evaluate = (TextView) convertView.findViewById(R.id.tv_evaluate);
//            tv_evaluate_content = (TextView) convertView.findViewById(R.id.tv_evaluate_content);
//            tv_content_reply = (TextView) convertView.findViewById(R.id.tv_content_reply);
//            tv_lesson_time = (TextView) convertView.findViewById(R.id.tv_lesson_time);


        }
    }


    public interface Callback {
        public void onClick(View view, String courseId);
    }
}

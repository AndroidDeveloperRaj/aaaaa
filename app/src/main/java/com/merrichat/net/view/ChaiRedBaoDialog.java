package com.merrichat.net.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.utils.Rotate3dAnimation;

import java.text.DecimalFormat;

/**
 * Created by amssy on 17/11/20.
 * 拆红包弹框
 */

public class ChaiRedBaoDialog extends Dialog {
    private Context context;
    private ClickListenerInterface clickListenerInterface;

    private String heardUrl;
    private String name;
    private String content;
    private String sendId;
    private String mFileType;
    private String mBalance;

    public interface ClickListenerInterface {
        public void chaiRedBao();
    }


    public ChaiRedBaoDialog(Context context, String fileType, String heardUrl, String sendId, String name, String content, String balance) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.heardUrl = heardUrl;
        this.name = name;
        this.content = content;
        this.sendId = sendId;
        this.mFileType = fileType;
        this.mBalance = balance;
    }

    public ChaiRedBaoDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_chai_red_bao, null);
        setContentView(view);

        CircleImageView cvRedPhoto = (CircleImageView) view.findViewById(R.id.cv_red_photo);
        TextView tvRedName = (TextView) view.findViewById(R.id.tv_red_name);
        TextView tvRedContent = (TextView) view.findViewById(R.id.tv_red_content);
        TextView tvRedChai = (TextView) view.findViewById(R.id.tv_red_chai);
        TextView tvBalance = (TextView) view.findViewById(R.id.tv_balance);


        if (!TextUtils.isEmpty(mBalance)) {
            final DecimalFormat df = new DecimalFormat("#0.00");
            tvBalance.setText(df.format(Double.parseDouble(mBalance.toString())));
            tvBalance.setVisibility(View.VISIBLE);
        } else {
            tvBalance.setText("");
            tvBalance.setVisibility(View.GONE);
        }

        Glide.with(context).load(heardUrl + sendId + ".jpg").asBitmap().error(R.mipmap.ic_preloading).placeholder(R.mipmap.ic_preloading).into(cvRedPhoto);
        tvRedName.setText(name);
        tvRedContent.setText(content);
        if (mFileType.equals("14")) {
            tvRedChai.setText("去邀请");
        } else {
            tvRedChai.setText("拆红包");
        }
        tvRedChai.setOnClickListener(new clickListener());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }


    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.tv_red_chai:
                    ((TextView) v).setText("开");
                    rotateAnim(v);
                    if (clickListenerInterface != null) {
                        clickListenerInterface.chaiRedBao();
                    }
                    break;
            }
        }
    }

    public void rotateAnim(View v) {

        float centerX = v.getWidth() / 2.0f;
        float centerY = v.getHeight() / 2.0f;
        float centerZ = 0f;

        Rotate3dAnimation rotate3dAnimationX = new Rotate3dAnimation(0, 180, centerX, centerY, centerZ, Rotate3dAnimation.ROTATE_Y_AXIS, true);
        rotate3dAnimationX.setDuration(1000);
        rotate3dAnimationX.setRepeatCount(-1);
        v.startAnimation(rotate3dAnimationX);
    }
}

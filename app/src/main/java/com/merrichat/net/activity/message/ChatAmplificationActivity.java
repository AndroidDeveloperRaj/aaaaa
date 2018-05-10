package com.merrichat.net.activity.message;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.gif.AnimatedGifDrawable;
import com.merrichat.net.activity.message.gif.AnimatedImageSpan;
import com.merrichat.net.utils.StatusBarUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 聊天信息----文本格式---点击进入此页面展示其信息
 */
public class ChatAmplificationActivity extends AppCompatActivity {
    @BindView(R.id.tv_text)
    TextView tv_text;

    @BindView(R.id.iv_photo)
    PhotoView iv_photo;

    @BindView(R.id.rl_total_content)
    RelativeLayout rl_total_content;

    @BindView(R.id.sv_scrollview)
    ScrollView sv_scrollview;
    private List<String> imgUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 255);
        setContentView(R.layout.activity_chat_amplification);
        ButterKnife.bind(this);
        String text = getIntent().getStringExtra("text");
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder sb = handler(tv_text,
                    text);
            tv_text.setText(sb);
            tv_text.setVisibility(View.VISIBLE);
            sv_scrollview.setVisibility(View.VISIBLE);
            iv_photo.setVisibility(View.GONE);
            sv_scrollview.setOnTouchListener(new View.OnTouchListener() {
                float yLength = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean isFinish = true;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isFinish = false;
                            yLength = event.getY();
//                        LogUtil.e("courier_http_dowm","ACTION_DOWN--------:"+ Math.abs(yLength));
                            break;
                        case MotionEvent.ACTION_MOVE:
//                        float ymove = event.getY()- yLength;
//                        LogUtil.e("courier_http_move","ACTION_MOVE------:"+ Math.abs(ymove));
                            break;
                        case MotionEvent.ACTION_UP:
                            float y = event.getY() - yLength;
//                        LogUtil.e("courier_http_up","ACTION_UP--------:"+ Math.abs(y)+"event.getY()----:"+event.getY()+"yLength----:"+yLength);
                            if (Math.abs(y) < 100) {
                                finish();
                                return true;
                            }
                            break;
                    }
                    return false;
                }
            });
        } else {
            iv_photo.setVisibility(View.VISIBLE);
            sv_scrollview.setVisibility(View.GONE);
            imgUrls = getIntent().getStringArrayListExtra("imgUrl");
            Glide.with(this).load(imgUrls.get(0)).placeholder(R.mipmap.icon_message_imageloader_error_bg).error(R.mipmap.icon_message_imageloader_error_bg).into(iv_photo);
            iv_photo.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
//                LogUtil.e("courier_http","onPhotoTap"+"x---:"+x+"   ---y----:"+y);
                    finish();
                }

                @Override
                public void onOutsidePhotoTap() {
//                LogUtil.e("courier_http","onOutsidePhotoTap");
                    finish();
                }
            });
        }
//        rl_total_content.setOnClickListener(this);
    }

    /**
     * 把接收的信息判断是否有表情，若有，转换成gif/png格式
     *
     * @param gifTextView
     * @param content
     * @return
     */
    private SpannableStringBuilder handler(final TextView gifTextView,
                                           String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\[face/png/f_static_)\\d{3}(.png\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring(
                        "[face/png/f_static_".length(), tempText.length()
                                - ".png]".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = getApplication().getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,
                                new AnimatedGifDrawable.UpdateListener() {
                                    @Override
                                    public void update() {
                                        gifTextView.postInvalidate();
                                    }
                                })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("[".length(),
                        tempText.length() - "]".length());
                try {
                    sb.setSpan(
                            new ImageSpan(getApplication(), BitmapFactory
                                    .decodeStream(getApplication().getAssets()
                                            .open(png))), m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.rl_total_content:
//                finish();
//                break;
//        }
//    }
}

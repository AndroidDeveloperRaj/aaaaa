package com.merrichat.net.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.merrichat.net.R;

/**
 * Created by amssy on 2016/5/4.
 */
public class ChatDeleteDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String flagType = "0";// 0是删除   1是重发
    private String flagType2 = "0";// 0没有撤销按钮  1是有撤销按钮
    TextView tv_delete_chat;
    TextView tv_resend_chat;
    TextView tv_undo_chat;
    View view_chat_1;
    View view_chat_2;
    ChatListener chatListener;


    public ChatDeleteDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;

    }

    public ChatDeleteDialog(Context context, int theme, String flagType, String flagType2) {
        super(context, theme);
        this.context = context;
        this.flagType = flagType;
        this.flagType2 = flagType2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.chatdeletedialog);
        tv_delete_chat = (TextView) findViewById(R.id.tv_delete_chat);
        tv_resend_chat = (TextView) findViewById(R.id.tv_resend_chat);
        tv_undo_chat = (TextView) findViewById(R.id.tv_undo_chat);
        view_chat_1 = findViewById(R.id.view_chat_1);
        view_chat_2 = findViewById(R.id.view_chat_2);
        if ("1".equals(flagType)) {//重发
            tv_resend_chat.setVisibility(View.VISIBLE);
            view_chat_1.setVisibility(View.VISIBLE);
        } else {
            tv_resend_chat.setVisibility(View.GONE);
            view_chat_1.setVisibility(View.GONE);
        }
        if ("1".equals(flagType2)) {//有撤销按钮
            tv_undo_chat.setVisibility(View.VISIBLE);
            view_chat_2.setVisibility(View.VISIBLE);
        } else {
            tv_undo_chat.setVisibility(View.GONE);
            view_chat_2.setVisibility(View.GONE);
        }
        tv_delete_chat.setOnClickListener(this);
        tv_resend_chat.setOnClickListener(this);
        tv_undo_chat.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_delete_chat:
                chatListener.deleteChatClick();
                break;
            case R.id.tv_resend_chat:
                chatListener.reSendChatClick();
                break;
            case R.id.tv_undo_chat:
                chatListener.undoChatClick();
                break;
            default:
                break;
        }
    }

    public interface ChatListener {
        void deleteChatClick();

        void reSendChatClick();

        void undoChatClick();
    }

    public void setChatListener(ChatListener chatListener) {
        this.chatListener = chatListener;
    }

}

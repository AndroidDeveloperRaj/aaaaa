package com.merrichat.net.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.merrichat.net.R;
import com.merrichat.net.utils.ConstantsPath;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.ViewHolder;

public class ConfirmDialog extends BaseNiceDialog {
    private String type;
    private DialogOnClickListener listener;

    /**
     * 自定义弹窗类型
     *
     * @param type
     * @return
     */
    public static ConfirmDialog newInstance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setMargin(40);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        type = bundle.getString("type");
    }

    @Override
    public int intLayoutId() {
        return R.layout.confirm_layout;
    }

    @Override
    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
        if (ConstantsPath.PERSONALINFO_BACK.equals(type)) {
            holder.setText(R.id.title, "提示");
            holder.setText(R.id.message, "是否保存已修改的内容！");
            holder.setText(R.id.ok, "保存！");
        } else if (ConstantsPath.SEARCH_CONTACT_DIALOG_TYPE.equals(type)) {
            holder.setText(R.id.title, "提示");
            holder.setText(R.id.message, "该用户还没注册，确定通知他注册吗？");
        }
        holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    listener.No();
                }
            }
        });

        holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    listener.Yes();
                }
            }
        });
    }

    public void setDialogOnClickListener(DialogOnClickListener listener) {
        this.listener = listener;
    }

    public interface DialogOnClickListener {
        void Yes();

        void No();
    }
}

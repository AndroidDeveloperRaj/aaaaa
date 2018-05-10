package com.merrichat.net.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.activity.merrifunction.CameraPreviewAty;
import com.merrichat.net.activity.video.editor.VideoEditorActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/4.
 */

public class FriendFragment extends BaseFragment {

    @BindView(R.id.btn_short_video)
    Button btnShortVideo;
    @BindView(R.id.btn_picture)
    Button btnPicture;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_editor)
    Button btnEditor;

    private UserModel userModel;

    /**
     * Fragment 的构造函数。
     */
    public FriendFragment() {
    }

    public static FriendFragment newInstance() {
        return new FriendFragment();
    }

    @OnClick({R.id.btn_short_video, R.id.btn_picture, R.id.btn, R.id.btn_login, R.id.btn_editor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_short_video:
                OkGo.<File>get(Urls.URL_DOWNLOAD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
//                .params("file1",new File("文件路径"))   //这种方式为一个key，对应一个文件
//                .params("file2",new File("文件路径"))
//                        .params("file", new File("/storage/emulated/0/pic/r5r_003.png"))
//                        .addFileParams("file", files)
                        .execute(new FileCallback("MerrChat.apk") {
                            @Override
                            public void onSuccess(Response<File> response) {
                                RxToast.showToast("成功");
                            }

                            @Override
                            public void downloadProgress(Progress progress) {
                                super.downloadProgress(progress);
                                System.out.println("uploadProgress: " + progress);

                            }
                        });

                break;
            case R.id.btn_picture:
                String accountId = userModel.getAccountId();
//                Toast.makeText(this, accountId, Toast.LENGTH_LONG).show();
                RxToast.showToast("成功");
//                RxActivityTool.skipActivity(getContext(), MyActivity.class);
                RxActivityTool.skipActivity(getContext(), HisYingJiAty.class);
                break;
            case R.id.btn:
                startActivity(new Intent(getActivity(), CameraPreviewAty.class));
                break;
            case R.id.btn_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.btn_editor:
                startActivity(new Intent(getActivity(), VideoEditorActivity.class));
                break;
        }
    }


    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_friend, null);
        ButterKnife.bind(this, view);
        userModel = UserModel.getUserModel();
        return view;
    }

}

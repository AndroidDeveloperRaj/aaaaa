/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.merrichat.net.callback;

import android.content.Context;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 描    述：我的Github地址  https://github.com/jeasonlzy
 * 修订历史：
 * ================================================
 */
public abstract class StringDialogCallback extends StringCallback {

    private LoadingDialog dialog;

    public StringDialogCallback(Context activity) {
        dialog = new LoadingDialog(activity);
        dialog.setLoadingText("加载中...");
    }

    public StringDialogCallback(Context activity, String toastMessage) {
        dialog = new LoadingDialog(activity);
        dialog.setInterceptBack(false);
        dialog.setLoadingText(toastMessage);
    }

    public StringDialogCallback() {

    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (dialog != null)
            dialog.show();
    }


    @Override
    public void onSuccess(Response<String> response) {
        if (dialog != null) {
            dialog.close();
            dialog = null;
        }
    }

    @Override
    public void onFinish() {
        if (dialog != null) {
            dialog.close();
            dialog = null;
        }
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<String> response) {
        if (dialog != null) {
            dialog.close();
            dialog = null;
        }
        super.onError(response);

    }
}

//public abstract class StringDialogCallback extends StringCallback {
//
//    private LoadingDialog dialog;
//    private HttpParams params;
//    private String string;
//    private boolean isSucess;
//
//    public StringDialogCallback(Context activity) {
//        dialog = new LoadingDialog(activity);
//        dialog.setLoadingText("加载中...");
//    }
//
//    public StringDialogCallback(Context activity, String toastMessage) {
//        dialog = new LoadingDialog(activity);
//        dialog.setInterceptBack(false);
//        dialog.setLoadingText(toastMessage);
//    }
//
//    public StringDialogCallback() {
//
//    }
//
//    @Override
//    public void onStart(Request<String, ? extends Request> request) {
//        Logger.e("onStart瞎想");
//        request.getBaseUrl();
//        params = request.getParams();
//        Logger.e("----------->HttpParams:" + params.toString());
//        if (dialog != null)
//            dialog.show();
//    }
//
//    @Override
//    public String convertResponse(okhttp3.Response response) throws Throwable {
//
//        String tempResponse = new StringConvert().convertResponse(response);
//        response.close();
//
//        if (tempResponse.contains("interceptStatus")) {
//            Logger.e("convertResponse瞎想:" + tempResponse);
//            JSONObject jsonObject = JSON.parseObject(tempResponse);
//            String interceptStatus = jsonObject.getString("interceptStatus").trim();
//            if (TextUtils.equals("0", interceptStatus) || TextUtils.equals("1", interceptStatus)) {
//                RxToast.showToast("登录失效，请重新登录");
//                SysApp.stopNoticeService(MerriApp.mContext);
//                SysApp.lognOut(MerriApp.mContext, true);
//            } else if (("2").equals(interceptStatus)) {
//                OkGo.getInstance().cancelTag(MerriApp.mContext);
//                getAccessToken();
//            }
//            if (isSucess) {
//                return string;
//            } else {
//                return "";
//            }
//        } else {
//            return tempResponse;
//        }
//    }
//
//
//    private void getAccessToken() {
//        OkGo.<String>post(Urls.getAccessToken)
//                .params("memberId", UserModel.getUserModel().getMemberId())
//                .params("refreshToken", UserModel.getUserModel().getRefreshToken())
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        //{"accessToken":"82bd5c4025651bd491322cd6a94b7706","success":true}
//                        JSONObject jsonObject = JSON.parseObject(response.body());
//                        if (jsonObject.getBoolean("success")) {
//                            String accessToken = jsonObject.getString("accessToken");
//                            UserModel userModel = UserModel.getUserModel();
//                            userModel.setAccessToken(accessToken);
//                            userModel.setUserModel(userModel);
//                            HttpHeaders headers = new HttpHeaders();
//                            headers.put("token", accessToken);
//                            headers.put("memberid", userModel.getMemberId() + "");
//                            headers.put("version", MerriApp.curVersion);
//                            OkGo.getInstance().addCommonHeaders(headers);
//
//                            OkGo.<String>post(Urls.queryPromoWordsList)//
//                                    .tag(this)//
//                                    .params("memberId", UserModel.getUserModel().getMemberId())
//                                    .execute(new StringDialogCallback() {
//                                        @Override
//                                        public void onSuccess(Response<String> response) {
//                                            string = response.body().trim();
//                                            isSucess = true;
//                                        }
//
//                                        @Override
//                                        public void onError(Response<String> response) {
//                                            super.onError(response);
//                                        }
//                                    });
//
//                        } else {
//                            RxToast.showToast("登录失效，请重新登录");
//                            SysApp.stopNoticeService(MerriApp.mContext);
//                            SysApp.lognOut(MerriApp.mContext, true);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                        RxToast.showToast("登录失效，请重新登录");
//                        SysApp.stopNoticeService(MerriApp.mContext);
//                        SysApp.lognOut(MerriApp.mContext, true);
//                    }
//                });
//    }
//
//    @Override
//    public void onSuccess(Response<String> response) {
//        Logger.e("onSuccess瞎想");
//        if (dialog != null) {
//            dialog.close();
//            dialog = null;
//        }
//    }
//
//    @Override
//    public void onFinish() {
//        Logger.e("onFinish瞎想");
//        if (dialog != null) {
//            dialog.close();
//            dialog = null;
//        }
//    }
//
//    @Override
//    public void onError(com.lzy.okgo.model.Response<String> response) {
//        Logger.e("onError瞎想");
//        if (dialog != null) {
//            dialog.close();
//            dialog = null;
//        }
//        super.onError(response);
//
//    }
//}

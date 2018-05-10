package com.merrichat.net.activity.picture.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.merrichat.net.R;
import com.merrichat.net.adapter.MagicStyleAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.model.QueryAllMagicTypeModel;
import com.merrichat.net.model.QueryMagicListModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangweiwei on 2018/3/31.
 */

@SuppressLint("ValidFragment")
public class MagicStyleFragment extends BaseFragment {

    private int currentPosition;  //当前页面的数字

    private int currentPage = 1;  //当前页数
    private QueryAllMagicTypeModel.MagicDataType magicDataType;

    private PullToRefreshGridView mPullToRefreshListView;
    public MagicStyleAdapter magicStyleAdapter;

    /**
     * @param position 页面state
     */
    @SuppressLint("ValidFragment")
    public MagicStyleFragment(int position, QueryAllMagicTypeModel.MagicDataType magicDataType) {
        this.magicDataType = magicDataType;
        this.currentPosition = position;
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_magic_style, container, false);
        mPullToRefreshListView = (PullToRefreshGridView) view.findViewById(R.id.magic_type_list);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    private void initViews() {
        magicStyleAdapter = new MagicStyleAdapter(getActivity(), this);
        mPullToRefreshListView.setAdapter(magicStyleAdapter);

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                currentPage = 1;
                getOrderStateList(currentPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                getOrderStateList(currentPage);
            }
        });

        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                magicStyleAdapter.setSelectPosition(position);
                magicStyleAdapter.notifyDataSetChanged();
                if (magicStyleAdapter.getItem(position) != null && magicStyleAdapter.getItem(position).imgUrl != null) {
                    fileDownload(magicStyleAdapter.getItem(position).imgUrl);
                    PrefAppStore.setMagicSelectPage(getContext(), currentPosition);
                } else {
                    MyEventBusModel eventBusModel = new MyEventBusModel();
                    eventBusModel.MAGIC_TACK_STYLE = true;
                    eventBusModel.MAGIC_TACK_STYLE_INFO = "none";
                    EventBus.getDefault().post(eventBusModel);
                }

            }
        });

        getOrderStateList(currentPage);

    }

    public void getOrderStateList(final int currentPosition) {
        ApiManager.getApiManager().getService(WebApiService.class).queryMagicList(magicDataType.typeName, currentPosition, 60)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryMagicListModel>() {
                    @Override
                    public void onNext(QueryMagicListModel magicList) {
                        if (magicList.success) {
                            if (currentPage == 1) {
                                magicStyleAdapter.clearItems();
                                QueryMagicListModel.MagicType magicType = new QueryMagicListModel.MagicType();
                                magicStyleAdapter.addItem(magicType);
                            }
                            magicStyleAdapter.addItemList(magicList.data.list);
                            magicStyleAdapter.notifyDataSetChanged();
                            currentPage++;
                        }
                        mPullToRefreshListView.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPullToRefreshListView.onRefreshComplete();
                        e.printStackTrace();
                        Toast.makeText(getContext(), "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void fileDownload(final String imgUrl) {
        final MyEventBusModel eventBusModel = new MyEventBusModel();
        String fileSuffix = "";
        final File file = new File(imgUrl);
        if (file != null || file.exists()) {
            if (file.isFile()) {
                fileSuffix = file.getName();
            }
        }

        if (isFileExit(pathListFile(FileUtils.photoMagicPath), file.getName())) {
            eventBusModel.MAGIC_TACK_STYLE_INFO = FileUtils.photoMagicPath + file.getName();
            eventBusModel.MAGIC_TACK_STYLE = true;
            EventBus.getDefault().post(eventBusModel);
        } else {
            OkGo.<File>get(imgUrl).tag(this)
                    .execute(new FileCallback(FileUtils.photoMagicPath, fileSuffix) {
                        @Override
                        public void onStart(Request<File, ? extends Request> request) {
                        }

                        @Override
                        public void onSuccess(Response<File> response) {
                            magicStyleAdapter.notifyDataSetChanged();
                            eventBusModel.MAGIC_TACK_STYLE = true;
                            eventBusModel.MAGIC_TACK_STYLE_INFO = FileUtils.photoMagicPath + file.getName();
                            EventBus.getDefault().post(eventBusModel);
                        }

                        @Override
                        public void onError(Response<File> response) {
                        }

                        @Override
                        public void downloadProgress(Progress progress) {
                        }
                    });

        }
    }


    public List<String> pathListFile(String filePath) {
        List<String> filePathUrl = new ArrayList<>();
        File[] files = new File(filePath).listFiles();
        for (int i = 0; i < files.length; i++) {
            filePathUrl.add(files[i].getName());
        }
        return filePathUrl;
    }

    public boolean isFileExit(List<String> fileList, String fileName) {
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).equals(fileName)) {
                return true;
            }
        }
        return false;
    }

}

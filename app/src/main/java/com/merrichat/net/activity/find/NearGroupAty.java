package com.merrichat.net.activity.find;

import android.os.Bundle;
import android.view.View;

import com.merrichat.net.R;
import com.merrichat.net.activity.message.AddGroupFragment;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.view.BottomDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by AMSSY1 on 2018/3/14.
 * <p>
 * 附近的群
 */

public class NearGroupAty extends MerriActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_group);
        setTitle("附近的群");
        setLeftBack();
        android.support.v4.app.FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fr_near_group, new AddGroupFragment());
        fragmentTransaction.commit();
        setRightImage(R.mipmap.fujin_shaixuan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallDialog();
            }
        });

    }


    /**
     * 筛选dialog
     */
    private void showCallDialog() {
        final BottomDialog bottomDialog = new BottomDialog(this, "全部", "交流群", "电商群", "集市群");
        bottomDialog.showAnim(null);
        bottomDialog.show();
        bottomDialog.setOnViewClick(new BottomDialog.OnViewClick() {
            @Override
            public void onClick(View v) {
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.FILTER_NEAR_GROUP = true;
                switch (v.getId()) {
                    case R.id.tv_first://全部
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "";
                        break;
                    case R.id.tv_second://交流群
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "0";
                        break;
                    case R.id.tv_third://交流群
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "1";
                        break;
                    case R.id.tv_four://电商群
                        myEventBusModel.FILTER_NEAR_GROUP_TYPE = "2";
                        break;
                }
                EventBus.getDefault().post(myEventBusModel);
            }
        });
    }
}

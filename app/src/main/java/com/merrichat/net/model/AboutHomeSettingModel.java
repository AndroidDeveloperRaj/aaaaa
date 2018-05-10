package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by AMSSY1 on 2017/11/6.
 */

public class AboutHomeSettingModel implements Serializable {
    private String settingItemName;
    private boolean isChecked;

    public AboutHomeSettingModel(String settingItemName, boolean isChecked) {
        this.settingItemName = settingItemName;
        this.isChecked = isChecked;
    }

    public AboutHomeSettingModel() {

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSettingItemName() {
        return settingItemName;
    }

    public void setSettingItemName(String settingItemName) {
        this.settingItemName = settingItemName;
    }


}

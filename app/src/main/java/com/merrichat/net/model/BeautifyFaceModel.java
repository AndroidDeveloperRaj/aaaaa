package com.merrichat.net.model;

/**
 * Created by amssy on 17/11/9.
 */

public class BeautifyFaceModel {
    private String filterName;
    private int filterIndex;
    private int blurLevel;
    private int colorLevel;
    private int cheekthinLevel;
    private int enlargeEyeLevel;
    private String effectFaceName;
    private int effectIndex;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public int getBlurLevel() {
        return blurLevel;
    }

    public void setBlurLevel(int blurLevel) {
        this.blurLevel = blurLevel;
    }

    public int getColorLevel() {
        return colorLevel;
    }

    public void setColorLevel(int colorLevel) {
        this.colorLevel = colorLevel;
    }

    public int getCheekthinLevel() {
        return cheekthinLevel;
    }

    public void setCheekthinLevel(int cheekthinLevel) {
        this.cheekthinLevel = cheekthinLevel;
    }

    public int getEnlargeEyeLevel() {
        return enlargeEyeLevel;
    }

    public void setEnlargeEyeLevel(int enlargeEyeLevel) {
        this.enlargeEyeLevel = enlargeEyeLevel;
    }

    public String getEffectFaceName() {
        return effectFaceName;
    }

    public void setEffectFaceName(String effectFaceName) {
        this.effectFaceName = effectFaceName;
    }

    public int getFilterIndex() {
        return filterIndex;
    }

    public void setFilterIndex(int filterIndex) {
        this.filterIndex = filterIndex;
    }

    public int getEffectIndex() {
        return effectIndex;
    }

    public void setEffectIndex(int effectIndex) {
        this.effectIndex = effectIndex;
    }
}

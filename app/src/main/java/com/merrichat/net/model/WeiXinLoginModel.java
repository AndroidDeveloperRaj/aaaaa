package com.merrichat.net.model;

import com.merrichat.net.utils.JSONObjectEx;

import org.json.JSONException;

/**
 * 调用第三方微信登陆获取微信的信息.
 */
public class WeiXinLoginModel {
    private String openid;
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String headimgurl;
    private String unionid;
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }


    public static WeiXinLoginModel parseJSON(String response) throws JSONException {
        // TODO Auto-generated method stub
        WeiXinLoginModel entity = new WeiXinLoginModel();

        JSONObjectEx jo = new JSONObjectEx(response);
        /**
         * "openid":"OPENID",
         "nickname":"NICKNAME",
         "sex":1,
         "province":"PROVINCE",
         "city":"CITY",
         "country":"COUNTRY",
         "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
         "privilege":[
         "PRIVILEGE1",
         "PRIVILEGE2"
         ],
         "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
         */
        entity.setOpenid(jo.getString("openid"));
        entity.setNickname(jo.getString("nickname"));
        entity.setProvince(jo.getString("province"));
        entity.setHeadimgurl(jo.getString("headimgurl"));
        entity.setCity(jo.getString("city"));
        entity.setSex(jo.getString("sex"));
        entity.setUnionid(jo.getString("unionid"));
        return entity;
    }

}



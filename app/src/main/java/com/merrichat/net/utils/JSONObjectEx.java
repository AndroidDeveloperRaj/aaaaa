package com.merrichat.net.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

public class JSONObjectEx extends JSONObject {


    private final int NIL = 0;

    public JSONObjectEx(JSONObject o) throws JSONException {
        super(o.toString());
    }

    public JSONObjectEx() {
        super();
    }

    public JSONObjectEx(JSONObject copyFrom, String[] names)
            throws JSONException {
        super(copyFrom, names);
    }

    public JSONObjectEx(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }


    @SuppressWarnings("rawtypes")
    public JSONObjectEx(Map copyFrom) {
        super(copyFrom);
    }

    public JSONObjectEx(String json) throws JSONException {
        super(json);
    }

    @Override
    public boolean getBoolean(String name) {
        if (super.isNull(name)) return false;
        try {
            return super.getBoolean(name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public double getDouble(String name) {
        if (super.isNull(name) || TextUtils.isEmpty(name)) return 0.0;
        try {
            return super.getDouble(name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public int getInt(String name) {
        if (super.isNull(name)) return NIL;
        try {
            return super.getInt(name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return NIL;
    }

    @Override
    public long getLong(String name) {
        if (super.isNull(name)) return NIL;
        try {
            return super.getLong(name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return NIL;
    }

    @Override
    public String getString(String name) {
        if (super.isNull(name)) return "";
        try {
            return super.getString(name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}

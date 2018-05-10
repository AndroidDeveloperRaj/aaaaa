package com.merrichat.net.model.dao.utils;

import android.database.sqlite.SQLiteDatabase;

import com.merrichat.net.app.MerriApp;
import com.merrichat.net.model.dao.DaoMaster;
import com.merrichat.net.model.dao.DaoSession;


/**
 * 描    述:GreenDao管理类，用于初次初使化数据库，获取DaoMaster、DaoSession
 */
public class GreenDaoManager {
    private final String TAG = "GreenDaoManager";
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static GreenDaoManager mInstance; //单例

    private GreenDaoManager() {
        if (mInstance == null) {
            MigrationHelper.DEBUG = true; //如果你想查看日志信息，请将DEBUG设置为true
            MySQLiteOpenHelper daoHelper = new MySQLiteOpenHelper(MerriApp.getContext(), "merrichat-db", null);
            SQLiteDatabase database = daoHelper.getWritableDatabase();
            mDaoMaster = new DaoMaster(database);
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {//保证异步处理安全操作

                if (mInstance == null) {
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}

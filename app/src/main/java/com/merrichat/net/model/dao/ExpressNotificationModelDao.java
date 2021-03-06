package com.merrichat.net.model.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.merrichat.net.model.ExpressNotificationModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EXPRESS_NOTIFICATION_MODEL".
*/
public class ExpressNotificationModelDao extends AbstractDao<ExpressNotificationModel, Long> {

    public static final String TABLENAME = "EXPRESS_NOTIFICATION_MODEL";

    /**
     * Properties of entity ExpressNotificationModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ID = new Property(0, Long.class, "ID", true, "_id");
        public final static Property CreateTime = new Property(1, String.class, "createTime", false, "CREATE_TIME");
        public final static Property NetName = new Property(2, String.class, "netName", false, "NET_NAME");
        public final static Property ImageUrl = new Property(3, String.class, "imageUrl", false, "IMAGE_URL");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property MemberPhone = new Property(5, String.class, "memberPhone", false, "MEMBER_PHONE");
        public final static Property Number = new Property(6, String.class, "number", false, "NUM_BER");
        public final static Property SendContent = new Property(7, String.class, "sendContent", false, "SEND_CONTENT");
        public final static Property PickupAddr = new Property(8, String.class, "pickupAddr", false, "PICKUP_ADDR");
        public final static Property ReceiverPhone = new Property(9, String.class, "receiverPhone", false, "RECEIVER_PHONE");
        public final static Property MemberId = new Property(10, String.class, "memberId", false, "MEMBER_ID");
    }


    public ExpressNotificationModelDao(DaoConfig config) {
        super(config);
    }
    
    public ExpressNotificationModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EXPRESS_NOTIFICATION_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: ID
                "\"CREATE_TIME\" TEXT," + // 1: createTime
                "\"NET_NAME\" TEXT," + // 2: netName
                "\"IMAGE_URL\" TEXT," + // 3: imageUrl
                "\"NAME\" TEXT," + // 4: name
                "\"MEMBER_PHONE\" TEXT," + // 5: memberPhone
                "\"NUM_BER\" TEXT," + // 6: number
                "\"SEND_CONTENT\" TEXT," + // 7: sendContent
                "\"PICKUP_ADDR\" TEXT," + // 8: pickupAddr
                "\"RECEIVER_PHONE\" TEXT," + // 9: receiverPhone
                "\"MEMBER_ID\" TEXT);"); // 10: memberId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EXPRESS_NOTIFICATION_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ExpressNotificationModel entity) {
        stmt.clearBindings();
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(1, ID);
        }
 
        String createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindString(2, createTime);
        }
 
        String netName = entity.getNetName();
        if (netName != null) {
            stmt.bindString(3, netName);
        }
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(4, imageUrl);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String memberPhone = entity.getMemberPhone();
        if (memberPhone != null) {
            stmt.bindString(6, memberPhone);
        }
 
        String number = entity.getNumber();
        if (number != null) {
            stmt.bindString(7, number);
        }
 
        String sendContent = entity.getSendContent();
        if (sendContent != null) {
            stmt.bindString(8, sendContent);
        }
 
        String pickupAddr = entity.getPickupAddr();
        if (pickupAddr != null) {
            stmt.bindString(9, pickupAddr);
        }
 
        String receiverPhone = entity.getReceiverPhone();
        if (receiverPhone != null) {
            stmt.bindString(10, receiverPhone);
        }
 
        String memberId = entity.getMemberId();
        if (memberId != null) {
            stmt.bindString(11, memberId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ExpressNotificationModel entity) {
        stmt.clearBindings();
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(1, ID);
        }
 
        String createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindString(2, createTime);
        }
 
        String netName = entity.getNetName();
        if (netName != null) {
            stmt.bindString(3, netName);
        }
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(4, imageUrl);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String memberPhone = entity.getMemberPhone();
        if (memberPhone != null) {
            stmt.bindString(6, memberPhone);
        }
 
        String number = entity.getNumber();
        if (number != null) {
            stmt.bindString(7, number);
        }
 
        String sendContent = entity.getSendContent();
        if (sendContent != null) {
            stmt.bindString(8, sendContent);
        }
 
        String pickupAddr = entity.getPickupAddr();
        if (pickupAddr != null) {
            stmt.bindString(9, pickupAddr);
        }
 
        String receiverPhone = entity.getReceiverPhone();
        if (receiverPhone != null) {
            stmt.bindString(10, receiverPhone);
        }
 
        String memberId = entity.getMemberId();
        if (memberId != null) {
            stmt.bindString(11, memberId);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ExpressNotificationModel readEntity(Cursor cursor, int offset) {
        ExpressNotificationModel entity = new ExpressNotificationModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // ID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // createTime
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // netName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imageUrl
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // memberPhone
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // number
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // sendContent
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // pickupAddr
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // receiverPhone
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // memberId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ExpressNotificationModel entity, int offset) {
        entity.setID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCreateTime(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNetName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setImageUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMemberPhone(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNumber(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSendContent(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPickupAddr(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setReceiverPhone(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setMemberId(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ExpressNotificationModel entity, long rowId) {
        entity.setID(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ExpressNotificationModel entity) {
        if(entity != null) {
            return entity.getID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ExpressNotificationModel entity) {
        return entity.getID() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

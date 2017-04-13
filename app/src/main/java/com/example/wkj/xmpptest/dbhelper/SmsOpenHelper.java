package com.example.wkj.xmpptest.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by wkj_pc on 2017/3/28.
 */

public class SmsOpenHelper extends SQLiteOpenHelper {
    public static final String T_SMS="t_sms";
    public class SmsTable implements BaseColumns{
        /*
        * from_account 发送者
        * to_account  接收者
        * body 消息内容
        * status 会话状态
        * type  会话类型
        * time      会话时间
        * session_account //会话id 帮不帮   最近你和谁聊天了
        *
        * */
        public  static final String FROM_ACCOUNT=" from_account";
        public static final String TO_ACCOUNT=" to_account";
        public static final String BODY="body";
        public static final String STATUS="status";
        public static final String TYPE="type";
        public static final String TIME="time";
        public static final String SESSION_ACCOUNT="session_account";
    }

    public SmsOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "sms.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE "+T_SMS+"(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                SmsTable.FROM_ACCOUNT+" TEXT," +
                SmsTable.TO_ACCOUNT+" TEXT," +
                SmsTable.BODY+" TEXT," +
                SmsTable.STATUS+" TEXT," +
                SmsTable.TYPE+" TEXT," +
                SmsTable.TIME+" TEXT," +
                SmsTable.SESSION_ACCOUNT+" TEXT" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

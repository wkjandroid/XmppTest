package com.example.wkj.xmpptest.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by wkj on 2017/3/23.
 */

public class ContactOpenHelper extends SQLiteOpenHelper {
    public static final String T_CONTACT="t_contact";
    public class ContactTable implements BaseColumns{       //就是会默认给我们添加一列 _id
        public static final String ACCOUNT="account";//帐号
        public static final String NICKNAME="nickname";//昵称
        public static final String AVATAR="avatar";//头像
        public static final String PINYIN="pinyin";//帐号拼音
    }
    public ContactOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //定义表结构
        String sql="create table "+T_CONTACT+"(_id integer primary key autoincrement," +
                ContactTable.ACCOUNT+" text,"+
                ContactTable.NICKNAME+" text,"+
                ContactTable.AVATAR+" text,"+
                ContactTable.PINYIN+" text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

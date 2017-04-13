package com.example.wkj.xmpptest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.test.AndroidTestCase;

import com.example.wkj.xmpptest.dbhelper.SmsOpenHelper;
import com.example.wkj.xmpptest.provider.SmsContentProvider;

import java.util.AbstractCollection;

/**
 * Created by wkj_pc on 2017/3/28.
 */

public class TestSmsProvider extends AndroidTestCase{
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
    public void testInsert(){
        ContentValues values=new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,"wkj@wkj.com");
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,"wkj1@wkj.com");
        values.put(SmsOpenHelper.SmsTable.BODY,"nihao");
        values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
        values.put(SmsOpenHelper.SmsTable.TYPE,"chat");
        values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,"wkj1@wkj.com");
        Uri insert = getContext().getContentResolver().insert(SmsContentProvider.SMS_URI, values);
        String i = insert.getPathSegments().get(1);

    }
    public void testUpdate(){
        ContentValues values=new ContentValues();
        values.put(SmsOpenHelper.SmsTable.BODY,"gg");
        getContext().getContentResolver().update(SmsContentProvider.SMS_URI,values,SmsOpenHelper.SmsTable.FROM_ACCOUNT+
                "=?",new String[]{"wkj@wkj.com"});
    }
    public void testDelete(){
        getContext().getContentResolver().delete(SmsContentProvider.SMS_URI,
                SmsOpenHelper.SmsTable.FROM_ACCOUNT+"=?",
                new String []{"wkj@wkj.com"});
    }
    public void testQuery(){
        Cursor query = getContext().getContentResolver().query(SmsContentProvider.SMS_URI,
                null, null, null, null, null);
    }
}

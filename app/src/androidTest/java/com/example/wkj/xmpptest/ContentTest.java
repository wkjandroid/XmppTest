package com.example.wkj.xmpptest;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.wkj.xmpptest.dbhelper.ContactOpenHelper;
import com.example.wkj.xmpptest.provider.ContactsProvider;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by wkj on 2017/3/23.
 */

public class ContentTest extends AndroidTestCase{
    public void testPinyin(){
        String s = PinyinHelper.convertToPinyinString("嘎嘎", "", PinyinFormat.WITHOUT_TONE);

    }
    public void testAdd(){
        ContentValues values=new ContentValues();
        values.put(ContactOpenHelper.ContactTable.ACCOUNT,"ligggg");
        values.put(ContactOpenHelper.ContactTable.AVATAR,"ggg");
        values.put(ContactOpenHelper.ContactTable.NICKNAME,"ggg");
        values.put(ContactOpenHelper.ContactTable.PINYIN,"ttt");
        getContext().getContentResolver().insert(ContactsProvider.URI_CONTACT,values);

    }
    public void testDelete(){
        getContext().getContentResolver().delete(ContactsProvider.URI_CONTACT,
                ContactOpenHelper.ContactTable.ACCOUNT+"=?",new String[]{"ligggg"});
    }
    public void testUpdate(){
        ContentValues values=new ContentValues();
        values.put(ContactOpenHelper.ContactTable.AVATAR,"ggggg");
        values.put(ContactOpenHelper.ContactTable.NICKNAME,"ggg");
        values.put(ContactOpenHelper.ContactTable.PINYIN,"ttt");
        getContext().getContentResolver().update(ContactsProvider.URI_CONTACT,values,
                ContactOpenHelper.ContactTable.ACCOUNT+"=?",new String[]{"ligggg"});
    }
    public void testQuery(){
        Cursor query = getContext().getContentResolver().query(ContactsProvider.URI_CONTACT, null, null, null, null);
        int columnCount=query.getColumnCount();
        while (query.moveToNext()){
            for(int i=0;i<columnCount;i++){
                System.out.print(query.getString(i)+"   ---");
            }
            System.out.println();
        }

    }
}

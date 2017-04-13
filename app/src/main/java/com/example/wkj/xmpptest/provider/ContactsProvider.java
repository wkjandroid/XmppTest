package com.example.wkj.xmpptest.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.UriMatcher;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.wkj.xmpptest.dbhelper.ContactOpenHelper;


/**
 * Created by wkj on 2017/3/23.
 */

public class ContactsProvider extends ContentProvider {
    public static final String AUTHORIIES=ContactsProvider.class.getCanonicalName();
    static UriMatcher matcher;
    //对应联系人表中的一个uri常量
    public static Uri URI_CONTACT=Uri.parse("content://"+AUTHORIIES+"/contact");
    private static final int CONTACT = 1;
    static {
        //初始化
        matcher=new UriMatcher(UriMatcher.NO_MATCH);
        //添加一个匹配规则
        matcher.addURI(AUTHORIIES,"/contact",CONTACT);
        //content://com.example.wkj.xmpptest.provider.ContactsProvider/contact-->CONTACT
    }

    private ContactOpenHelper helper;

    @Override
    public boolean onCreate() {
        helper = new ContactOpenHelper(getContext());
        if (null!= helper){
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = matcher.match(uri);
        Cursor query=null;
        switch (code){
            case CONTACT:
                SQLiteDatabase db = helper.getReadableDatabase();
                query = db.query(ContactOpenHelper.T_CONTACT, projection, selection, selectionArgs,
                        sortOrder, null, null);
                System.out.println("query ----------sucess");
                break;
            default:
                break;
        }
        return query;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //创建数据库文件通过 sqliteopenhelter
        int code=matcher.match(uri);
        switch (code){
            case CONTACT:
                SQLiteDatabase db = helper.getWritableDatabase();
                long i = db.insert(ContactOpenHelper.T_CONTACT, "", values);
                if (i!=-1){
                    System.out.println("insert-------- seccuss");
                    uri= ContentUris.withAppendedId(uri,i);
                    //注册监听通知手机数据改变
                    getContext().getContentResolver().notifyChange(URI_CONTACT,null);
                    //制定那一个observer可以收到null表示都可以收到

                }
                break;
            default:
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       int code= matcher.match(uri);
        int deleteCount=0;
        switch (code)
        {
            case CONTACT:
                SQLiteDatabase db = helper.getWritableDatabase();
                //影响的行数
                deleteCount = db.delete(ContactOpenHelper.T_CONTACT,selection,selectionArgs);
                if (deleteCount>0){
                    System.out.println("delete ------seccuss");

                }
                getContext().getContentResolver().notifyChange(URI_CONTACT,null);
                //制定那一个observer可以收到null表示都可以收到
                break;
            default:
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code =matcher.match(uri);
        int updateCount=0;
        switch (code){
            case CONTACT:
                SQLiteDatabase db = helper.getWritableDatabase();
                updateCount = db.update(ContactOpenHelper.T_CONTACT,values,selection,selectionArgs);
                if (updateCount>0){
                    System.out.println("update -------seccuss");
                }
                getContext().getContentResolver().notifyChange(URI_CONTACT,null);
                //制定那一个observer可以收到null表示都可以收到
                break;
            default:
                break;
        }
        return updateCount;
    }
}

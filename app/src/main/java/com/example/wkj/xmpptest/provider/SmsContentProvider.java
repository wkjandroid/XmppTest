package com.example.wkj.xmpptest.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.wkj.xmpptest.dbhelper.SmsOpenHelper;

public class SmsContentProvider extends ContentProvider {
    public static final String AUTHORITIES =SmsContentProvider.class.getCanonicalName();
    static UriMatcher matcher;
    private SmsOpenHelper helper;
    public static final int SMS=1;
    public static Uri SMS_URI=Uri.parse("content://"+AUTHORITIES+"/sms");
    static {
        matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITIES,"/sms",SMS);
    }
    public SmsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
         int deleteCount=0;
        switch(matcher.match(uri)){
            case SMS:
                SQLiteDatabase db = helper.getWritableDatabase();
                deleteCount = db.delete(SmsOpenHelper.T_SMS,selection,selectionArgs);
               // System.out.println("delete------sucesss");
                getContext().getContentResolver().notifyChange(SMS_URI,null);
                break;
        }
        return deleteCount;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch(matcher.match(uri)){
            case SMS:
                SQLiteDatabase db = helper.getWritableDatabase();
                long insertCount = db.insert(SmsOpenHelper.T_SMS,"",values);
                uri= ContentUris.withAppendedId(uri,insertCount);
                System.out.println(" message----insertCount------sucesss");
                getContext().getContentResolver().notifyChange(SMS_URI,null);
                break;
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        helper = new SmsOpenHelper(getContext(),null,null,1);
        if (helper !=null){
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor query=null;
        switch(matcher.match(uri)){
            case SMS:
                SQLiteDatabase db = helper.getReadableDatabase();
                query = db.query(SmsOpenHelper.T_SMS,projection,selection,selectionArgs,
                        null,null,sortOrder);
              //  System.out.println("query------sucesss");

                break;
        }

        return query;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updateCount=0;
        switch(matcher.match(uri)){
            case SMS:
                SQLiteDatabase db = helper.getWritableDatabase();
                updateCount = db.update(SmsOpenHelper.T_SMS,values,selection,selectionArgs);
               // System.out.println("update------sucesss");
                getContext().getContentResolver().notifyChange(SMS_URI,null);
            break;
        }

        return updateCount;
    }
}

package com.example.wkj.xmpptest.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wkj.xmpptest.R;

import com.example.wkj.xmpptest.activity.ChatActivity;
import com.example.wkj.xmpptest.dbhelper.ContactOpenHelper;
import com.example.wkj.xmpptest.provider.ContactsProvider;

import com.example.wkj.xmpptest.utils.ThreadUtils;


/**
 * A simple {@link Fragment} subclass.
 * 联系人fragment
 */
public class ContactsFragment extends Fragment {

    private ListView contactsListView;

    private CursorAdapter adapter;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }
    private void init(){
        registerContentObserver();
    }
    private void initView(View view) {
        contactsListView = (ListView)view.findViewById(R.id.contacts_listview);
    }
    private void initData() {
        //开启线程。同步花名册
        //设置adapter
        setOrUpdateAdapter();
    }
    //设置或者更新adapter
    private void setOrUpdateAdapter(){
        if (null!=adapter){
            adapter.getCursor().requery();
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                final Cursor query = getActivity().getContentResolver().
                        query(ContactsProvider.URI_CONTACT, null, null, null, null);
      /*  while (query.moveToNext()){
            System.out.println("-------");
        }*/
                if (query.getCount()<=0){
                    return;
                }
                ThreadUtils.runInUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //如果converview为空的话创建新视图
                        //设置显示数据
                        adapter = new CursorAdapter(getContext(),query){

                            @Override
                            //如果converview为空的话创建新视图
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                View view= LayoutInflater.from(context).inflate(R.layout.contacts_item,parent,false);
                                return view;
                            }
                            //设置显示数据
                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                TextView accountView= (TextView) view.findViewById(R.id.account);
                                TextView nickNameView=(TextView)view.findViewById(R.id.nickname);
                                String account=cursor.getString(cursor.getColumnIndexOrThrow(
                                        ContactOpenHelper.ContactTable.ACCOUNT
                                ));
                                String nickName=cursor.getString(cursor.getColumnIndexOrThrow(
                                        ContactOpenHelper.ContactTable.NICKNAME
                                ));
                                accountView.setText(account);
                                nickNameView.setText(nickName);
                            }
                        };
                        contactsListView.setAdapter(adapter);
                    }
                });
            }
        });

    }
    private void initListener() {
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c=adapter.getCursor();
                c.moveToPosition(position);
                String account = c.getString(c.getColumnIndexOrThrow(ContactOpenHelper.ContactTable.ACCOUNT));
                String nickName=c.getString(c.getColumnIndexOrThrow(ContactOpenHelper.ContactTable.NICKNAME));
                Intent intent =new Intent(getContext(),ChatActivity.class);
                intent.putExtra(ChatActivity.ACCOUNT,account);
                intent.putExtra(ChatActivity.NICKNAME,nickName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);

    }

    //监听数据库记录改变
    MyContentObserver observer=new MyContentObserver(new Handler());
    //注册监听
    public void registerContentObserver(){
        getActivity().getContentResolver().registerContentObserver(
                ContactsProvider.URI_CONTACT,true,observer);
    }
    //取消监听
    public void unregisterContentObserver(){
        getActivity().getContentResolver().unregisterContentObserver(observer);
    }
    class MyContentObserver extends ContentObserver{
        public MyContentObserver(Handler handler) {
            super(handler);
        }
//如果数据库数据改变会在这个方法中收到通知
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //更新adapter或者刷新
            setOrUpdateAdapter();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
    }
}

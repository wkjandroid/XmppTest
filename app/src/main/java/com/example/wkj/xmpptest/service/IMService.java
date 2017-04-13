package com.example.wkj.xmpptest.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.wkj.xmpptest.activity.LoginActivity;
import com.example.wkj.xmpptest.dbhelper.ContactOpenHelper;
import com.example.wkj.xmpptest.fragment.ContactsFragment;
import com.example.wkj.xmpptest.provider.ContactsProvider;
import com.example.wkj.xmpptest.utils.PinyinUtil;
import com.example.wkj.xmpptest.utils.ThreadUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

import static org.jivesoftware.smackx.pubsub.AccessModel.roster;

/**
 * Created by wkj on 2017/3/22.
 */

public class IMService extends Service
{
    public static XMPPConnection connection;
    private MyRosterListener rosterListener=new MyRosterListener();
    private Roster roster;
    private String name;
    private String account;
    public static String mCAccount="";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //设置监听
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //同步联系人
                //得到花名册对象
                final XMPPConnection conn=IMService.connection;
                roster = conn.getRoster();
                //设置roster监听器
                roster.addRosterListener(rosterListener);
                final Collection<RosterEntry> entries = roster.getEntries();
                //得到所有的联系人*/
                for (RosterEntry entry:entries){
                    //先更新后插入
                    updateOrSave(entry);
                }
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //按照常理，我们fragment销毁了，那么我们就不应该去继续监听
        //但是我们需要一直监听对应roster的改变
        //所以，我们把联系人的监听和同步操作放到IMservice去
        if(roster!=null && null!=rosterListener){
            roster.removeRosterListener(rosterListener);
        }
        super.onDestroy();

    }
    private void updateOrSave(RosterEntry entry) {
        ContentValues values=new ContentValues();
        account = entry.getUser();
        account=account.substring(0,account.indexOf("@"))+"@"+ LoginActivity.SERVICENAME;
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, account);
        values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
        name = entry.getName();
        if (name==null|| "".equals(name)){
            name=account.substring(0,account.indexOf("@"));
        }
        values.put(ContactOpenHelper.ContactTable.NICKNAME, name);
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinyinUtil.getPinyin(entry.getUser()));
        int updateCount= getContentResolver().update(ContactsProvider.URI_CONTACT,values,ContactOpenHelper
                .ContactTable.ACCOUNT+"=?",new String[]{account});
        if (updateCount<=0){
            getContentResolver().insert(ContactsProvider.URI_CONTACT,values);
        }

    }
    class MyRosterListener implements RosterListener {
        @Override
        public void entriesAdded(Collection<String> collection) {
            //联系人增加了
            System.out.println("add");
            for(String address:collection){
                RosterEntry entry=roster.getEntry(address);
                updateOrSave(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {
            //联系人更新了
            System.out.println("gengxin");
            for(String address:collection){
                RosterEntry entry=roster.getEntry(address);
                updateOrSave(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {
            //联系人删除了
            System.out.println("shanchu");
            for(String address:collection){
                // RosterEntry entry=roster.getEntry(address);
                getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactOpenHelper.ContactTable.ACCOUNT+"=?",new String[]{
                                address});
            }


        }

        @Override
        public void presenceChanged(Presence presence) {
            //联系人状态改变了
            System.out.println("zhuangtaigaibian");
        }
    }

}

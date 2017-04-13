package com.example.wkj.xmpptest.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wkj.xmpptest.R;
import com.example.wkj.xmpptest.dbhelper.SmsOpenHelper;
import com.example.wkj.xmpptest.provider.SmsContentProvider;
import com.example.wkj.xmpptest.service.IMService;
import com.example.wkj.xmpptest.utils.ThreadUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {
    public static final String ACCOUNT = "chatAccount";
    public static final String NICKNAME = "chatNickName";
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.et_body)
    EditText etBody;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.message_list)
    ListView messageList;
    private String clickAccount;
    private String nickName;
    private CursorAdapter adapter;
    private MyMessageListener listener = new MyMessageListener();
    private MyContentObserver observer=new MyContentObserver(new Handler());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        init();
        initView();
        initData();

    }
    private void initView() {
        title.setText("聊天室");
    }

    private void init() {
        registerContentObserver();
    }

    private void initData() {
        clickAccount = getIntent().getStringExtra(ACCOUNT);
        clickAccount = clickAccount.substring(0, clickAccount.indexOf("@"));
        nickName = getIntent().getStringExtra(NICKNAME);
        setAdapterOrNotify();
    }

    private void setAdapterOrNotify() {
        if (adapter!=null){
            adapter.getCursor().requery();
           // messageList.setSelection(adapter.getCursor().getCount()-1);
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run()
            {
                final Cursor query = getContentResolver().query(SmsContentProvider.SMS_URI, null,
                        null, null, null);

                if (null==query ||query.getCount()<=0 ){
                    return ;
                }
                ThreadUtils.runInUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CursorAdapter(ChatActivity.this, query) {
                            public static final int RECEIVE=1;
                            public static final int SEND=1;
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                ViewHolder holder;
                                if (getItemViewType(position)==RECEIVE){
                                    if (convertView==null){
                                        View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.item_chat_receiver,
                                                parent, false);
                                        holder=new ViewHolder();
                                        holder.mbody= (TextView) view.findViewById(R.id.chat_message);
                                        holder.avatar= (ImageView) view.findViewById(R.id.chat_avatar);
                                        holder.time= (TextView) view.findViewById(R.id.chat_time);
                                        view.setTag(holder);
                                    }else {
                                        holder= (ViewHolder) convertView.getTag();
                                    }
                                }else {
                                    if (convertView==null){
                                        View view = LayoutInflater.from(ChatActivity.this).inflate
                                                (R.layout.item_chat_send,parent, false);
                                        holder=new ViewHolder();
                                        holder.mbody= (TextView) view.findViewById(R.id.chat_message);
                                        holder.avatar= (ImageView) view.findViewById(R.id.chat_avatar);
                                        holder.time= (TextView) view.findViewById(R.id.chat_time);
                                        view.setTag(holder);
                                    }else {
                                        holder= (ViewHolder) convertView.getTag();
                                    }
                                }
                                //得到数据，展示数据
                                query.moveToPosition(position);
                                String body = query.getString(query.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
                                String time=query.getString(query.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
                                String formatTime=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(Long.parseLong(time)));
                                holder.time.setText(time);
                                holder.mbody.setText(body);
                                return super.getView(position, convertView, parent);
                            }
                            @Override
                            public int getItemViewType(int position) {
                                query.moveToPosition(position);
                                String sessionAccount = query.getString
                                        (query.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                                if (!sessionAccount.equals(IMService.mCAccount)){
                                    //shoudao
                                    return RECEIVE;
                                }else{
                                    return SEND;
                                }
                                //return super.getItemViewType(position);     //1或者0
                            }
                            @Override
                            public int getViewTypeCount() {
                                return super.getViewTypeCount()+1;  //2
                            }

                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return null;
                            }
                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                            }
                            class ViewHolder{
                                ImageView avatar;
                                TextView mbody;
                                TextView time;
                            }
                        };
                        messageList.setAdapter(adapter);
                    }
                });
            }

        });
    }

    @OnClick(R.id.send)
    public void send(View view) {
        final String body = etBody.getText().toString();
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
       /*
       * 1获取消息的管理者
       * */
                    XMPPConnection conn = IMService.connection;
                    ChatManager chatManager = conn.getChatManager();
        /*2创建聊天的对象
        目的账户clickAccount
         */
                    Chat chat = chatManager.createChat(clickAccount + "@119.29.154.229", listener);
       /*3发送消息*/
                    chatManager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean b) {
                            chat.addMessageListener(listener);
                        }
                    });
                    Message message = new Message();
                    message.setFrom(IMService.mCAccount);  //当前登录的账户
                    message.setTo(clickAccount);
                    message.setType(Message.Type.chat);
                    //  message.setProperty("key","value"); //而外的信息，这里我们用不到
                    message.setBody(body);
                    chat.sendMessage(message);
                    /*
                    * 保存消息*/
                    saveMessage(clickAccount, message);
                    //清空输入框
                    ThreadUtils.runInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etBody.setText("");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

//保存消息
    private void saveMessage(String sessionAcount,Message msg) {
        ContentValues values=new ContentValues();
        //我發的消息，
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,msg.getFrom());
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,msg.getTo());
        values.put(SmsOpenHelper.SmsTable.BODY,msg.getBody());
        values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
        values.put(SmsOpenHelper.SmsTable.TYPE,msg.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,sessionAcount);
        getContentResolver().insert(SmsContentProvider.SMS_URI,values);
    }
    //监听收到的message
    class MyMessageListener implements MessageListener{
        @Override
        public void processMessage(Chat chat, Message message) {
            String participant = chat.getParticipant();
            //System.out.println("------"+message.getBody()+message.getTo()+message.getFrom());
            if (null!=participant && (!TextUtils.isEmpty(message.getBody()))){
                saveMessage(participant,message);
            }

        }
    }
    //注册监听contentprovider
    private void registerContentObserver(){
        getContentResolver().registerContentObserver(SmsContentProvider.SMS_URI,true,observer);
    }
    //解除监听 contentProvider
    private void unregisterContentObserver(){
        getContentResolver().unregisterContentObserver(observer);

    }

    class MyContentObserver extends ContentObserver{
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            setAdapterOrNotify();
         //   messageList.setSelection(adapter.getCount()-1);
            super.onChange(selfChange);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterContentObserver();
        super.onDestroy();
    }
}

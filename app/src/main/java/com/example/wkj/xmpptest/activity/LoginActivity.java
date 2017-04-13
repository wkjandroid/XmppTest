package com.example.wkj.xmpptest.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wkj.xmpptest.R;
import com.example.wkj.xmpptest.service.IMService;
import com.example.wkj.xmpptest.utils.ThreadUtils;
import com.example.wkj.xmpptest.utils.ToastUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.RosterPacket;

public class LoginActivity extends AppCompatActivity {
    public static final String HOST ="119.29.154.229";
    public static final int PORT=5222;
    private Button btnLogin;
    private TextView etUserName;
    private EditText etPassword;
    public static final String SERVICENAME="wkj.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }
    public void initView(){
        etUserName = (TextView)findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button)  findViewById(R.id.login_btn);

    }
    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断用户是否为空
                //判断密码是否为空
                final String userName = etUserName.getText().toString();
                final String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    etUserName.setError("账户不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("密码不能为空");
                    return;
                }
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //创建连接对象
                            ConnectionConfiguration conf=new ConnectionConfiguration(HOST,PORT,"");
                            conf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                            conf.setDebuggerEnabled(true);
                           conf.setSASLAuthenticationEnabled(true);
                            XMPPConnection conn=new XMPPConnection(conf);
                            //连接成功了
                            //开始登录
                            conn.connect();
                            conn.login(userName,password);
                            IMService.connection=conn;
                            ThreadUtils.runInUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(LoginActivity.this, "登录成功");
                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    //保存当前账户
                                    String account=userName+"@"+LoginActivity.SERVICENAME;
                                   IMService.mCAccount=account;
                                    //启动IMservice
                                    Intent service =new Intent(LoginActivity.this,IMService.class);
                                    startService(service);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            ThreadUtils.runInUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(LoginActivity.this, "登录失败");

                                }
                            });
                        }
                    }
                });
            }
        });
    }

}

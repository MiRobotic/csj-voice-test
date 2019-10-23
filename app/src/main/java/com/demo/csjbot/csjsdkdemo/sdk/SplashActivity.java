package com.demo.csjbot.csjsdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.listener.OnConnectListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(CsjRobot.getInstance().getState().isConnect()){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
        }else{
            CsjRobot.getInstance().registerConnectListener(new OnConnectListener() {
                @Override
                public void success() {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }

                @Override
                public void faild() {

                }

                @Override
                public void timeout() {

                }

                @Override
                public void disconnect() {

                }
            });
        }

    }
}

package com.csjbot.voiceclient;


import com.csjbot.voiceclient.constant.VoiceClientConstant;
import com.csjbot.voiceclient.core.VoiceClientManager;
import com.csjbot.voiceclient.core.inter.ActionListener;
import com.csjbot.voiceclient.core.inter.Callbacker;
import com.csjbot.voiceclient.core.util.ClientListenerWrapper;
import com.csjbot.voiceclient.core.util.ExecutorCallbacker;
import com.csjbot.voiceclient.listener.VoiceClientEvent;
import com.csjbot.voiceclient.listener.VoiceEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 * <p>
 * 直接面向用户的类，直接对外的封装
 */
public class VoiceClientAgent {
    private VoiceClientManager mClientManager;
    private VoiceEventListener mEventListener;
    private Callbacker mCallbacker;
    private boolean isConnected = false;


    public void connect(String hostName, int port) {
        mClientManager.init(hostName, port, new ActionListener() {
            @Override
            public void onSuccess() {
                isConnected = true;
                notifyEvent(new VoiceClientEvent(VoiceClientConstant.EVENT_CONNECT_SUCCESS));
            }

            @Override
            public void onFailed(int errorCode) {
                notifyEvent(new VoiceClientEvent(VoiceClientConstant.EVENT_CONNECT_FAILD, errorCode));
            }
        });
    }


    public VoiceClientAgent(VoiceEventListener listener) {
        mClientManager = new VoiceClientManager();
        mEventListener = listener;
        mCallbacker = new ExecutorCallbacker("RosClientAgentCallback");
        mClientManager.setRequestListener(new ClientListenerWrapper(mClientManager, mCallbacker, mEventListener));

    }


    private void notifyEvent(VoiceClientEvent event) {
        mCallbacker.notifyEvent(mEventListener, event);
    }

    /**
     * 关闭所有的东西
     */
    public void destroy() {
        mClientManager.destroy();
        mCallbacker.destroy();

        mClientManager = null;
        mCallbacker = null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disConnect() {
        isConnected = false;
        mClientManager.disConnect();
    }


    public String getVersionName() {
        Properties prop = new Properties();
        String value = null;
        try {
            InputStream inputStream = VoiceClientAgent.class.getClassLoader().getResourceAsStream("config/config.properties");
            prop.load(inputStream);
            value = prop.getProperty("versionName");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

//    public int getVersionCode() {
//        Properties prop = new Properties();
//        int value = 0;
//        try {
//            InputStream inputStream = VoiceClientAgent.class.getResourceAsStream("/com/csjbot/voiceclient/config/config.properties");
//            prop.load(inputStream);
//            value = prop.getProperty("versionCode");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return value;
//    }
}

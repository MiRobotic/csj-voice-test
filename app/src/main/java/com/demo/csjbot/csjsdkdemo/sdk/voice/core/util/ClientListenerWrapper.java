package com.csjbot.voiceclient.core.util;


import com.csjbot.voiceclient.core.VoiceClientManager;
import com.csjbot.voiceclient.core.inter.Callbacker;
import com.csjbot.voiceclient.core.inter.RequestListener;
import com.csjbot.voiceclient.listener.VoiceClientEvent;
import com.csjbot.voiceclient.listener.VoiceEventListener;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 * <p>
 * 封装了 VoiceEventListener  VoiceClientManager 和Callbacker ，使之可以一体使用
 */
public class ClientListenerWrapper implements RequestListener {
    private VoiceEventListener mListener;
    private VoiceClientManager mClientManager;
    private Callbacker mCallbacker;

    public ClientListenerWrapper(VoiceClientManager clientManager, Callbacker threadHelper, VoiceEventListener listener) {
        this.mListener = listener;
        this.mClientManager = clientManager;
        this.mCallbacker = threadHelper;
    }

    @Override
    public void onReqeust(byte[] voiceData) {
        mCallbacker.notifyRequest(mListener, voiceData);
    }

    @Override
    public void onEvent(VoiceClientEvent event) {
        mCallbacker.notifyEvent(mListener, event);
    }

}
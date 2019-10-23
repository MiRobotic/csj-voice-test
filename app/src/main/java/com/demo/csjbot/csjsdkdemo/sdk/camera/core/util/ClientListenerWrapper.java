package com.csjbot.cameraclient.core.util;


import com.csjbot.cameraclient.core.ClientManager;
import com.csjbot.cameraclient.core.inter.Callbacker;
import com.csjbot.cameraclient.core.inter.RequestListener;
import com.csjbot.cameraclient.entity.MessagePacket;
import com.csjbot.cameraclient.listener.CameraEventListener;
import com.csjbot.cameraclient.listener.ClientEvent;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 * <p>
 * 封装了 CameraEventListener  ClientManager 和Callbacker ，使之可以一体使用
 */
public class ClientListenerWrapper implements RequestListener {
    private CameraEventListener mListener;
    private ClientManager mClientManager;
    private Callbacker mCallbacker;

    public ClientListenerWrapper(ClientManager clientManager, Callbacker threadHelper, CameraEventListener listener) {
        this.mListener = listener;
        this.mClientManager = clientManager;
        this.mCallbacker = threadHelper;
    }

    @Override
    public void onReqeust(MessagePacket packet) {
        mCallbacker.notifyRequest(mListener, packet);
    }

    @Override
    public void onEvent(ClientEvent event) {
        mCallbacker.notifyEvent(mListener, event);
    }

}
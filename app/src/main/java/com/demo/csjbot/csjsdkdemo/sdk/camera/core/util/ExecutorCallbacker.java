package com.csjbot.cameraclient.core.util;


import com.csjbot.cameraclient.constant.ClientConstant;
import com.csjbot.cameraclient.core.inter.Callbacker;
import com.csjbot.cameraclient.entity.MessagePacket;
import com.csjbot.cameraclient.listener.CameraEventListener;
import com.csjbot.cameraclient.listener.ClientEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 * <p>
 * CallBacker的具体实现，定义了一个线程来处理event
 */
public class ExecutorCallbacker implements Callbacker {
    private ExecutorService mCallbackWorker;

    public ExecutorCallbacker(final String workerName) {
        mCallbackWorker = Executors.newSingleThreadExecutor(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, workerName);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    @Override
    public void notifyRequest(final CameraEventListener listener, final MessagePacket packet) {
        postCallback(new Runnable() {
            @Override
            public void run() {
                listener.onCameraEvent(new ClientEvent(ClientConstant.EVENT_PACKET, packet));
            }
        });
    }

    @Override
    public void notifyEvent(final CameraEventListener listener, final ClientEvent event) {
        postCallback(new Runnable() {

            @Override
            public void run() {
                listener.onCameraEvent(event);
            }
        });
    }

    private void postCallback(Runnable r) {
        if (!mCallbackWorker.isShutdown()) {
            mCallbackWorker.execute(r);
        }
    }

    @Override
    public void destroy() {
        mCallbackWorker.shutdownNow();
    }
}

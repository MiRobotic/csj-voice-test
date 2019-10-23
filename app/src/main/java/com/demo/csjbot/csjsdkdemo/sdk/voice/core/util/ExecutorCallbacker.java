package com.csjbot.voiceclient.core.util;


import com.csjbot.voiceclient.constant.VoiceClientConstant;
import com.csjbot.voiceclient.core.inter.Callbacker;
import com.csjbot.voiceclient.listener.VoiceClientEvent;
import com.csjbot.voiceclient.listener.VoiceEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        mCallbackWorker = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, workerName);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public void notifyRequest(final VoiceEventListener listener, final byte[] voiceData) {
        postCallback(() -> listener.onEvent(new VoiceClientEvent(VoiceClientConstant.EVENT_PACKET, voiceData)));
    }

    @Override
    public void notifyEvent(final VoiceEventListener listener, final VoiceClientEvent event) {
        postCallback(() -> listener.onEvent(event));
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

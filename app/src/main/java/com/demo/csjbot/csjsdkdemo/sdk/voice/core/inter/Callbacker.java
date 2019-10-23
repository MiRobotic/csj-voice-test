package com.csjbot.voiceclient.core.inter;


import com.csjbot.voiceclient.listener.VoiceClientEvent;
import com.csjbot.voiceclient.listener.VoiceEventListener;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 */
public interface Callbacker {

    void notifyRequest(VoiceEventListener listener, byte[] voiceData);

    void notifyEvent(VoiceEventListener listener, VoiceClientEvent event);

    void destroy();

}
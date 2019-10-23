package com.csjbot.voiceclient.core.inter;

import com.csjbot.voiceclient.listener.VoiceClientEvent;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 */
public interface RequestListener {
    void onReqeust(byte[] voiceData);

    void onEvent(VoiceClientEvent event);

}
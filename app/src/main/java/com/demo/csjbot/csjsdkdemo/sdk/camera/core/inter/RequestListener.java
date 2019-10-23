package com.csjbot.cameraclient.core.inter;


import com.csjbot.cameraclient.entity.MessagePacket;
import com.csjbot.cameraclient.listener.ClientEvent;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 */
public interface RequestListener {
    void onReqeust(MessagePacket packet);

    void onEvent(ClientEvent event);

}
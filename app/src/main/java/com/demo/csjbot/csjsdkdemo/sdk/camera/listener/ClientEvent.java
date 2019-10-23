package com.csjbot.cameraclient.listener;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 * <p>
 * 用户层的事件，所有的用户事件都是这个类
 */
public class ClientEvent {

    /**
     * @see com.csjbot.cameraclient.constant.ClientConstant
     */
    public int eventType;

    public Object data;

    public ClientEvent(int eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public ClientEvent(int eventType) {
        this.eventType = eventType;
        this.data = null;
    }
}

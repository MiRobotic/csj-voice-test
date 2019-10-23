package com.csjbot.voiceclient.core.inter;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 */
public interface ActionListener {
    public void onSuccess();

    public void onFailed(int errorCode);
}

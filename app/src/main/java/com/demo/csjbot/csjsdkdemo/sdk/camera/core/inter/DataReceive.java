package com.csjbot.cameraclient.core.inter;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved.
 * www.example.com
 * <p>
 * Created by 浦耀宗 at 2016/11/17 0017-12:34.
 * Email: puyz@example.com
 */

public interface DataReceive {
    void onReceive(byte[] data);

    void onError(int errorCode);
}

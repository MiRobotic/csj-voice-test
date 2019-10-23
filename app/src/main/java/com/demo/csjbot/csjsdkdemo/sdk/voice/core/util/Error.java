package com.csjbot.voiceclient.core.util;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 */
public class Error {

    public class SocketError {
        public static final int CONNECT_SUCCESS = 0;
        public static final int CONNECT_TIME_OUT = 1;
        public static final int UNKONWN_HOST = 2;
        public static final int CONNECT_NETWORK_UNREACHABLE = 3;
        public static final int CONNECT_OTHER_IO_ERROR = 4;


        public static final int SEND_SUCCESS = 0;
        public static final int SEND_SOCKET_OR_OUT_NULL = 1;
        public static final int SEND_IO_ERROR = 2;
    }
}

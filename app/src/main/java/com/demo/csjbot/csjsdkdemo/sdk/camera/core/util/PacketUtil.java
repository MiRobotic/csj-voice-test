package com.csjbot.cameraclient.core.util;


import com.csjbot.cameraclient.entity.MessagePacket;
import com.csjbot.cameraclient.entity.PicturePacket;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 */
public class PacketUtil {
    public static MessagePacket parser(byte[] data) {
        return (PicturePacket) new PicturePacket().decodeBytes(data);
    }
}

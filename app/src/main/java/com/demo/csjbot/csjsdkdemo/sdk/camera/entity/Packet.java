package com.csjbot.cameraclient.entity;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 * <p>
 * Packet 接口
 */
public interface Packet {
    /**
     * 数据包编码成byte数组
     *
     * @return btye数组
     */
    byte[] encodeBytes() throws RosConnectException;

    /**
     * 从byte数组解码数据包
     *
     * @param rawData 原始byte数组
     * @return 解码包
     */
    Packet decodeBytes(byte[] rawData) throws RosConnectException;
}
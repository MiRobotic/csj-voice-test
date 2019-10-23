package com.csjbot.cameraclient.entity;


import com.csjbot.cameraclient.utils.NetDataTypeTransform;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 * <p>
 * <pre>
 *
 *
 *  header
 *  length
 *  content
 *
 *
 * </pre>
 */
public abstract class MessagePacket implements Packet {

    private PacketHeader header = null;
    protected byte[] mContent;
    private int messageLen;

    public abstract byte[] getContent();

    public abstract void setContent(byte[] content);

    @Override
    public byte[] encodeBytes() {
        byte[] contentData = getContent();
        byte[] header = new PacketHeader().getHeaderByte();
        messageLen = contentData.length;
        byte[] bytes = new byte[header.length + 4 + messageLen];

        int offset = 0;
        System.arraycopy(header, 0, bytes, 0, header.length);
        offset += header.length;

        System.arraycopy(NetDataTypeTransform.intToBytesQiPa(messageLen), 0, bytes, offset, 4);
        offset += 4;

        System.arraycopy(contentData, 0, bytes, offset, messageLen);

        return bytes;
    }

    @Override
    public Packet decodeBytes(byte[] rawData) {
//        if (!checkPacket(rawData)) {
//            return null;
//        }
//        header = new PacketHeader(rawData);
//        messageLen = NetDataTypeTransform.bytesToInt2(rawData, 20);
//
////        CsjLogger.debug("messageLen is " + String.valueOf(messageLen));
//
////        if (messageLen + 24 != rawData.length) {
////            CsjLogger.error("messasgeLen is " + messageLen + " not match rawData len " + rawData.length);
////            return null;
////        }
//
//        this.mContent = new byte[messageLen];
//        System.arraycopy(rawData, 0, this.mContent, 0, messageLen);
        this.mContent = rawData;
        return this;
    }

    private boolean checkPacket(byte[] data) {
        return data != null && data.length > 24;
    }
}

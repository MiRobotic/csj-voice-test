package com.csjbot.cameraclient.entity;


import com.csjbot.cameraclient.utils.NetDataTypeTransform;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com
 */
/*
* u32 m_dwSrcnode;     //本端节点号 (这个对你来说无所谓，可填0)
* u32 m_dwDstnode;    //对端节点号 (这个对你来说也无所谓，可填0)
* u32 m_dwSrcid;        //本端实例ID    填65
* u32 m_dwDstid;       //对端实例ID    填25
* u16 m_wMsgType;      //消息体类型      填1
* u16 m_wEvent;        //消息事件号     填 10002 吧
 */
public class PacketHeader implements Cloneable {
    private int m_dwSrcnode = 1;
    private int m_dwDstnode = 1;
    private int m_dwSrcid = 1;
    private int m_dwDstid = 25;
    private short m_wMsgType = 1;
    private short m_wEvent = 7002;

    public PacketHeader() {

    }

    public PacketHeader(byte[] data) {
        int offset = 0;
        m_dwSrcnode = NetDataTypeTransform.bytesToInt2(data, offset);
        offset += 4;

        m_dwDstnode = NetDataTypeTransform.bytesToInt2(data, offset);
        offset += 4;

        m_dwSrcid = NetDataTypeTransform.bytesToInt2(data, offset);
        offset += 4;

        m_dwDstid = NetDataTypeTransform.bytesToInt2(data, offset);
        offset += 4;

        m_wMsgType = NetDataTypeTransform.bytesToShort(data, offset);
        offset += 2;

        m_wEvent = NetDataTypeTransform.bytesToShort(data, offset);
    }

    public byte[] getHeaderByte() {
        byte[] bytes = new byte[20];
        int offset = 0;
        System.arraycopy(NetDataTypeTransform.intToBytesQiPa(m_dwSrcnode), 0, bytes, offset, 4);
        offset += 4;

        System.arraycopy(NetDataTypeTransform.intToBytesQiPa(m_dwDstnode), 0, bytes, offset, 4);
        offset += 4;

        System.arraycopy(NetDataTypeTransform.intToBytesQiPa(m_dwSrcid), 0, bytes, offset, 4);
        offset += 4;

        System.arraycopy(NetDataTypeTransform.intToBytesQiPa(m_dwDstid), 0, bytes, offset, 4);
        offset += 4;


        System.arraycopy(NetDataTypeTransform.shortToBytesQiPa(m_wMsgType), 0, bytes, offset, 2);
        offset += 2;

        System.arraycopy(NetDataTypeTransform.shortToBytesQiPa(m_wEvent), 0, bytes, offset, 2);
        return bytes;
    }

}

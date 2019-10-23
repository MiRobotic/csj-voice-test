package com.csjbot.voiceclient.core.netty_core;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.example.com
 * <p>
 * Created by 浦耀宗 at 2018/04/25 0025-15:43.
 * Email: puyz@example.com
 */

public class voiceDataDecoder extends ByteToMessageDecoder {
    /**
     * Decode the from one {@link ByteBuf} to an other. This method will be called till either the input
     * {@link ByteBuf} has nothing to read when return from this method or till nothing was read from the input
     * {@link ByteBuf}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int cnt = in.readableBytes();
        in.markReaderIndex();

        byte[] body = new byte[cnt];
        in.readBytes(body);

        out.add(body);
    }
}

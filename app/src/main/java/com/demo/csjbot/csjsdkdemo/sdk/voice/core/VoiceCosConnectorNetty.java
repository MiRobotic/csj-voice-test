package com.csjbot.voiceclient.core;

import com.csjbot.voiceclient.core.inter.DataReceive;
import com.csjbot.voiceclient.core.inter.IConnector;
import com.csjbot.voiceclient.core.netty_core.voiceDataDecoder;
import com.csjbot.voiceclient.core.util.Error;
import com.csjbot.voiceclient.listener.VoiceClientStateListener;
import com.csjbot.voiceclient.utils.VoiceLogger;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogLevel;

/**
 * Copyright (c) 2018, SuZhou CsjBot. All Rights Reserved.
 * www.example.com
 * <p>
 * Created by 浦耀宗 at 2018/04/11 0011-19:38.
 * Email: puyz@example.com
 */

public class VoiceCosConnectorNetty implements IConnector {
    private static final int MAX_HB_LIFE = 3;

    private ChannelFuture future;
    private DataReceive dataReceive;
    private boolean channelActive = false, futureSuccess;
    private String mHostName;
    private int mPort;
    private VoiceClientStateListener clientStateListener;
    private Bootstrap bootstrap;
    private int lossConnectCount = MAX_HB_LIFE;
    private Channel channel;

    private static final int RECONNECT_TIME = 5;
    private static final int READ_IDLE_TIME = 10;


    public VoiceCosConnectorNetty() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new voiceDataDecoder())
                                .addLast(new ClientHandler())
                                .addLast(new IdleStateHandler(READ_IDLE_TIME, 0, 0, TimeUnit.SECONDS))
                                .addLast( new LoggingHandler(String.valueOf(InternalLogLevel.INFO)));
                    }
                });
    }


    private void doConnect() {
        VoiceLogger.debug("doConnect");
        future = bootstrap.connect(mHostName, mPort);
        future.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                VoiceLogger.warn("operationComplete isSuccess");
                futureSuccess = true;
            } else {
                VoiceLogger.warn("operationComplete not isSuccess");
                futureSuccess = false;
                future.channel().eventLoop().schedule(() -> doConnect(), RECONNECT_TIME, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * 连接服务器
     *
     * @param hostName ip地址
     * @param port     端口
     * @return 返回 {@link  Error.SocketError} 则连接成功；返回其他则失败，参照各个实现
     * @see Error.SocketError
     */
    @Override
    public int connect(final String hostName, final int port, final VoiceClientStateListener listener) {
        if (channelActive) {
            VoiceLogger.warn("connect but channel Active ");
            return -1;
        }

        VoiceLogger.debug("connect");
        int ret;
        clientStateListener = listener;
        mHostName = hostName;
        mPort = port;

        doConnect();
        return 0;
    }


    private class ClientHandler extends SimpleChannelInboundHandler<byte[]> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {

            if (dataReceive != null) {
                dataReceive.onReceive(msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            cause.printStackTrace();
            VoiceLogger.debug("exceptionCaught  " + cause.getMessage());
            if (clientStateListener != null) {
//                clientStateListener.stateChanged(Error.SocketError.CONNECT_OTHER_IO_ERROR);
            }
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            VoiceLogger.debug("channel Active");
            if (clientStateListener != null) {
                clientStateListener.stateChanged(Error.SocketError.SEND_SUCCESS);
            }
            channel = ctx.channel();
            channelActive = true;
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            if (clientStateListener != null) {
                clientStateListener.stateChanged(Error.SocketError.CONNECT_OTHER_IO_ERROR);
            }
            VoiceLogger.debug("channel inActive");

            channelActive = false;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                switch (e.state()) {
                    case READER_IDLE:
                        lossConnectCount--;

                        VoiceLogger.debug("READER_IDLE lossConnectCount = 【"
                                + String.valueOf(lossConnectCount) + "】 发送一条心跳");

                        if (lossConnectCount <= 0) {
                            lossConnectCount = MAX_HB_LIFE;

                            if (clientStateListener != null && channelActive) {
                                channelActive = false;
                                ctx.close();

                                clientStateListener.stateChanged(Error.SocketError.CONNECT_OTHER_IO_ERROR);
                            }
                        }
//                        CosLogger.debug("userEventTriggered READER_IDLE");
                        break;
                    case WRITER_IDLE:
                        break;
                    case ALL_IDLE:
                        break;
                    default:
                        break;
                }
            }
        }
    }


    /**
     * 发送数据
     *
     * @param data 要发送的二进制数据
     * @return 返回 {@link Error.SocketError} 则发送成功；返回其他则失败
     * @see Error.SocketError
     */
    @Override
    public int sendData(byte[] data) {
        int ret = Error.SocketError.SEND_SUCCESS;
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(data);
            ret = Error.SocketError.SEND_SUCCESS;
            VoiceLogger.warn("Error.SocketError.SEND_SUCCESS");
        } else {
            ret = Error.SocketError.SEND_IO_ERROR;
            VoiceLogger.warn("Error.SocketError.SEND_IO_ERROR");
        }
        return ret;
    }

    /**
     * 接受数据的回调设置
     *
     * @param receive 接收数据的回调
     * @see DataReceive
     */
    @Override
    public void setDataReceive(DataReceive receive) {
        this.dataReceive = receive;
    }

    /**
     * IConnector 是否连接
     *
     * @return 返回true则已经连接，返回false则没有连接
     */
    @Override
    public boolean isRunning() {
//        VoiceLogger.warn("isRunning " + String.valueOf(channelActive));
        return false;
    }

    /**
     * 销毁 IConnector， 参见各个实现，保证所有的都设置为空
     */
    @Override
    public void destroy() {
        future.channel().close();
        future.channel().eventLoop().shutdownGracefully();
    }

    @Override
    public void disConnect() {
        futureSuccess = false;
        if (future != null && future.channel() != null) {
            VoiceLogger.error("disconnect close channel");
            future.channel().close();
        }
    }

    @Override
    public boolean sendUrgentData() {
        return false;
    }
}

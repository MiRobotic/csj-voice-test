package com.csjbot.cameraclient.core;


import com.csjbot.cameraclient.constant.ClientConstant;
import com.csjbot.cameraclient.core.inter.DataReceive;
import com.csjbot.cameraclient.core.inter.IConnector;
import com.csjbot.cameraclient.core.util.Errors;
import com.csjbot.cameraclient.utils.CameraLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved.
 * www.example.com
 * <p>
 * Created by 浦耀宗 at 2016/11/29 0029-09:00.
 * Email: puyz@example.com
 * <p>
 * * 具体的连接实现层,NIO
 */

class CameraConnectorNIO implements IConnector {
    private DataReceive dataReceive;
    /**
     * 信道选择器
     */
    private Selector mSelector;
    /**
     * 服务器通信的信道
     */
    private SocketChannel mChannel;

    /**
     * 是否加载过的标识
     */
    private boolean isInit = false;

    /**
     * 读取buff的大小
     */
    private static final int READ_BUFF_SIZE = 1024;

    /**
     * 每次读完数据后,需要重新注册selector读取数据
     *
     * @return 如果成功就返回真，反之返回假
     */
    private synchronized boolean repareRead() {
        boolean ret = false;
        try {
            //打开并注册选择器到信道
            mSelector = Selector.open();
            if (mSelector != null) {
                mChannel.register(mSelector, SelectionKey.OP_READ);
                ret = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            CameraLogger.error(e.getMessage());
            ret = false;
        }

        return ret;
    }

    private class receiveMsgRunnable implements Runnable {
        @Override
        public void run() {
            receiveMsg();
        }
    }

    private void receiveMsg() {
        if (mSelector == null) {
            return;
        }

        while (isInit) {
            if (!isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }

            try {
                //有数据就一直接收
                while (isInit && mSelector.select() > 0) {
                    for (SelectionKey selectionKey : mSelector.selectedKeys()) {
                        //如果有可读数据
                        if (selectionKey.isReadable()) {
                            //使用NIO读取channel中的数据
                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            //读取缓存
                            ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFF_SIZE);
                            //实际的读取流
                            ByteArrayOutputStream read = new ByteArrayOutputStream();
                            int nRead, nLength = 0;
                            //单个读取流
                            byte[] bytes;
                            //读完为止
                            while ((nRead = sc.read(readBuffer)) > 0) {
                                //整理
                                readBuffer.flip();
                                bytes = new byte[nRead];
                                nLength += nRead;
                                //将读取的数据拷贝到字节流中
                                readBuffer.get(bytes);
                                //将字节流添加到实际读取流中
                                read.write(bytes);

                                readBuffer.clear();
                            }

                            if (nLength > 0) {
                                if (dataReceive != null) {
                                    dataReceive.onReceive(read.toByteArray());
                                } else {
                                    CameraLogger.error("read error " + Arrays.toString(read.toByteArray()));
                                }
                            }

                            //为下一次读取做准备
                            selectionKey.interestOps(SelectionKey.OP_READ);
                        }

                        //删除此SelectionKey
                        mSelector.selectedKeys().remove(selectionKey);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int connect(String hostName, int port) {
        CameraLogger.info(getClass().getSimpleName() + " connect to host " + hostName);

        InetAddress address;
        try {
            address = InetAddress.getByName(hostName);
            CameraLogger.info("hostName " + hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return Errors.SocketError.UNKONWN_HOST;
        }

        SocketAddress socketAddress = new InetSocketAddress(address.getHostAddress(), port);

        try {
            //打开监听信道,并设置为非阻塞模式
            mChannel = SocketChannel.open(socketAddress);
            if (mChannel != null) {
                mChannel.socket().setTcpNoDelay(false);
                mChannel.socket().setKeepAlive(true);

                //设置超时时间
                mChannel.socket().setSoTimeout(ClientConstant.CONNECT_TIME_OUT);
                mChannel.configureBlocking(false);
                isInit = repareRead();

                //创建读线程
                new Thread(new receiveMsgRunnable()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
           CameraLogger.error(e.getMessage());
            return Errors.SocketError.CONNECT_TIME_OUT;
        } finally {
            if (!isInit) {
                destroy();
            }
        }
        return Errors.SocketError.CONNECT_SUCCESS;
    }

    @Override
    public int sendData(byte[] data) {
        int ret = -1;
        if (!isInit) {
            return ret;
        }

        try {
            ByteBuffer buf = ByteBuffer.wrap(data);
            int nCount = mChannel.write(buf);
            if (nCount > 0) {
                ret = Errors.SocketError.SEND_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
           CameraLogger.error(e.getMessage());
            ret = Errors.SocketError.SEND_IO_ERROR;
        }

        return ret;
    }

    @Override
    public void setDataReceive(DataReceive receive) {
        dataReceive = receive;
    }

    @Override
    public boolean isRunning() {
        if (!isInit) {
            return false;
        }
        return mChannel.isConnected();
    }

    @Override
    public void destroy() {
        isInit = false;
        try {
            if (mSelector != null) {
                mSelector.close();
            }
            if (mChannel != null) {
                mChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
        try {
            if (mSelector != null) {
                mSelector.close();
            }
            if (mChannel != null) {
                mChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendUrgentData() {
        if (mChannel != null) {
            try {
                mChannel.socket().sendUrgentData(0xff);
            } catch (IOException e) {
               CameraLogger.error(e.getMessage());

                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}

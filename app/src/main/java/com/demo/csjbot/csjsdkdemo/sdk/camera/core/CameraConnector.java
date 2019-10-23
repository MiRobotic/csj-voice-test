package com.csjbot.cameraclient.core;


import com.csjbot.cameraclient.constant.ClientConstant;
import com.csjbot.cameraclient.core.inter.DataReceive;
import com.csjbot.cameraclient.core.inter.IConnector;
import com.csjbot.cameraclient.core.util.Errors;
import com.csjbot.cameraclient.utils.CameraLogger;
import com.csjbot.cameraclient.utils.NetDataTypeTransform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br>
 * www.example.com<br>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br>
 * Email: puyz@example.com
 * <p>
 * 具体的连接实现层
 */
class CameraConnector implements IConnector {

    private boolean isRunning;
    private Socket socket = null;
    private InputStream in;
    private OutputStream out;
    private DataReceive dataReceive;

    private static final int MAX_LOO_PSIZ = 15;
    private int loopcnt = 0;

    private int duplicatePicLenCnt = 0;
    private int lastPicLen = Integer.MAX_VALUE;

    /**
     * 用源生 Socket 连接 Ros
     *
     * @param hostName 主机名，可以是 ip（192.168.2.2） 也可以是 域名（www.baidu.com）
     * @param port     连接的端口
     * @return 返回状态 int： Errorss.SocketError.UNKONWN_HOST 未知的主机
     * Errorss.SocketError.CONNECT_TIME_OUT   连接超时
     * Errorss.SocketError.CONNECT_SUCCESS    连接成功
     * @see Errors.SocketError
     */
    @Override
    public int connect(String hostName, int port) {
//        CameraLogger.info(getClass().getSimpleName() + " connect to host " + hostName);

        InetAddress address;
        try {
            address = InetAddress.getByName(hostName);
//            CameraLogger.info("hostName " + hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return Errors.SocketError.UNKONWN_HOST;
        }

        final InetSocketAddress socketAddress = new InetSocketAddress(address.getHostAddress(), port);

        try {
            socket = new Socket();
            socket.connect(socketAddress, ClientConstant.CONNECT_TIME_OUT);
            socket.setKeepAlive(true);
            out = socket.getOutputStream();
            if (!isRunning) {
                isRunning = true;
            }
            receiveData();
        } catch (SocketTimeoutException e) {
            CameraLogger.error(e.getMessage());
            return Errors.SocketError.CONNECT_TIME_OUT;
        } catch (ConnectException e) {
            CameraLogger.error(e.getMessage());
            return Errors.SocketError.CONNECT_NETWORK_UNREACHABLE;
        } catch (IOException e) {
            CameraLogger.error(e.getClass().getSimpleName() + "  " + e.getMessage());
            return Errors.SocketError.CONNECT_OTHER_IO_ERROR;
        }

        return Errors.SocketError.CONNECT_SUCCESS;
    }


    @Override
    public void setDataReceive(DataReceive receive) {
        dataReceive = receive;
    }

    @Override
    public int sendData(byte[] data) {
        if (socket == null || out == null) {
            return Errors.SocketError.SEND_SOCKET_OR_OUT_NULL;
        }

        try {
            out.write(data);
            out.flush();
            return Errors.SocketError.SEND_SUCCESS;
        } catch (IOException e) {
            return Errors.SocketError.SEND_IO_ERROR;
        }
    }

    private static final byte[] mBuffer = new byte[100 * 1024];
    private int offset = 0;


    private static byte[] head = new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC, (byte) 0xFB, (byte) 0xFA, (byte) 0xD8, 0x00};
    private int headLen = head.length;

    private void clearBuffer() {
        Arrays.fill(mBuffer, (byte) 0);
        offset = 0;
    }

    private void splitBuffer() {
        if (offset < headLen) {
            return;
        }

        loopcnt++;
        if (loopcnt >= MAX_LOO_PSIZ) {
            CameraLogger.warn("loopcnt is too many, reset!!!");
            loopcnt = 0;
            return;
        }

        if (!Arrays.equals(Arrays.copyOf(mBuffer, headLen), head)) {
//            CameraLogger.warn("offset = " + offset);
            clearBuffer();
        } else {
            int picLen = NetDataTypeTransform.bytesToInt(mBuffer, headLen);
            CameraLogger.error("picLen = " + picLen);

            if (picLen <= 0) {
                CameraLogger.error("picLen <=0  [" + picLen + "],  clearBuffer ");
                clearBuffer();
                return;
            }

            int wholePacketLen = picLen + headLen * 2 + 4;

            if (lastPicLen == picLen) {
                duplicatePicLenCnt++;
                if (duplicatePicLenCnt > 5) {
                    if (dataReceive != null) {
                        dataReceive.onError(Errors.PictureError.CAMERA_USB_ERROR);
//                        CameraLogger.debug("-------------------------- duplicatePicLenCnt !!!!");
                    }
                    duplicatePicLenCnt = 0;
                    lastPicLen = Integer.MAX_VALUE;
                }
            } else {
                duplicatePicLenCnt = 0;
                lastPicLen = picLen;
            }

            if (offset < wholePacketLen) {
                return;
            }

            byte[] out = new byte[picLen];
            System.arraycopy(mBuffer, headLen + 4, out, 0, picLen);

            dataReceive.onReceive(out);
//            i++;
            if (offset + wholePacketLen >= mBuffer.length) {
                clearBuffer();
                return;
            }
            System.arraycopy(mBuffer, wholePacketLen, mBuffer, 0, wholePacketLen);
        }

        splitBuffer();
    }

    /**
     * 接收数据，新建一个线程
     */
    private void receiveData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        in = socket.getInputStream();
                        byte[] data = inputStreamToByte(in);

                        if (data != null && data.length > 0 && dataReceive != null) {
                            if (offset + data.length >= mBuffer.length || offset < 0) {
                                clearBuffer();
                                continue;
                            }
                            System.arraycopy(data, 0, mBuffer, offset, data.length);
                            offset += data.length;
//                            CameraLogger.warn("data.length is " + data.length);
//                            CameraLogger.warn(NetDataTypeTransform.dumpHex(Arrays.copyOf(mBuffer, offset)));
                            loopcnt = 0;
                            splitBuffer();
                        }
                    } catch (IOException e) {
                        CameraLogger.error(e.toString());
                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        CameraLogger.error(e.toString());
                    }
                }
            }
        }).start();
    }


    /**
     * InputStream 转为 byte
     *
     * @param inStream Socket的InputStream
     * @return 读入的字节数组
     * @throws IOException
     */
    private byte[] inputStreamToByte(InputStream inStream) throws IOException {
        int count = 0;
        while (count == 0) {
            if (inStream != null) {
                count = inStream.available();
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        byte[] b = null;
        if (count >= 0) {
            b = new byte[count];
        }

        if (null != b) {
            int ret = inStream.read(b);
            if (ret != count) {
//                CameraLogger.info("读取 inStream 错误， 预计读取 " + count + " 实际读取 " + ret);
            }
        }

        return b;
    }


    @Override
    public void destroy() {
        isRunning = false;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
        destroy();
    }

    @Override
    public boolean sendUrgentData() {
//        if (socket != null) {
//            try {
//                socket.sendUrgentData(0xff);
//            } catch (IOException e) {
//                CameraLogger.error(e.getMessage());
//                return false;
//            }
//        } else {
//            return false;
//        }

        return true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}

package com.csjbot.voiceclient.core;

import com.csjbot.voiceclient.constant.VoiceClientConstant;
import com.csjbot.voiceclient.core.inter.ActionListener;
import com.csjbot.voiceclient.core.inter.DataReceive;
import com.csjbot.voiceclient.core.inter.IConnector;
import com.csjbot.voiceclient.core.inter.RequestListener;
import com.csjbot.voiceclient.listener.VoiceClientEvent;
import com.csjbot.voiceclient.listener.VoiceClientStateListener;
import com.csjbot.voiceclient.listener.VoiceEventListener;
import com.csjbot.voiceclient.utils.VoiceLogger;
import com.csjbot.voiceclient.core.util.Error;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2016, SuZhou CsjBot. All Rights Reserved. <br/>
 * www.example.com<br/>
 * <p>
 * Created by 浦耀宗 at 2016/11/07 0007-19:19.<br/>
 * Email: puyz@example.com<br/>
 * <p>
 * RosConntor的事件封装层，处理RosConnector的各种数据，承上启下
 */
public class VoiceClientManager implements DataReceive {
    private static final long SEND_TIME_OUT = 25;
    /**
     * 发送池，所有的MessagePacket进过包装之后都会丢在这里，按照先进先出的顺序来操作
     * 有两个线程来操作，所以这里的操作都是要加同步来实现
     */
    private boolean mIsRunning = false;
    private ExecutorService mMainExecutor;
    private RequestListener mRequestListener;
    private IConnector mConnector = null;
    private ScheduledExecutorService mHeartBeatThread, mReConnectTread;
    private Thread mSendThread;
    private String mHostName;
    private int mPort;
    private ActionListener mListener = null;
    private int RECONNECT_INTERVAL = 5000;

    // 次数
    private static final int MAX_HB_LIFE = 3;

    // 心跳间隔时间
    public static final int HEART_BEAT_INTERVAL = 5;
    private Integer receiveHeartBeatCounter = MAX_HB_LIFE;

    private boolean needSendSucceed = true;

    private final Object locker = new Object();

    public VoiceClientManager() {
    }


    @Override
    public void onReceive(byte[] data) {
        if (mIsRunning) {
            mRequestListener.onReqeust(data);
        }
    }

    /**
     * 初始化Manager
     *
     * @param hostName ip地址
     * @param port     端口
     * @param listener 注册的回调，所有的动作都可以抽象为 成功{@link ActionListener#onSuccess}
     *                 和 失败（原因）{@link ActionListener#onFailed}，
     *                 进而封装成事件（动作的种类 + 结果） {@link VoiceEventListener#onEvent}
     */
    public void init(final String hostName, final int port, final ActionListener listener) {
        mIsRunning = true;
        mConnector = new VoiceCosConnectorNetty();
        mHostName = hostName;
        mPort = port;
        mListener = listener;

        mHeartBeatThread = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            /**
             * Constructs a new {@code Thread}.  Implementations may also initialize
             * priority, name, daemon status, {@code ThreadGroup}, etc.
             *
             * @param r a runnable to be executed by new thread instance
             * @return constructed thread, or {@code null} if the request to
             * create a thread is rejected
             */
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "HeartBeatThreadPool");
            }
        });

        mMainExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread ret = new Thread(r, "mMainExecutor");
                ret.setDaemon(true);
                return ret;
            }
        });

        mMainExecutor.submit(connectRunnable);

        mReConnectTread = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "mReConnectTread");
            }
        });
    }

    private VoiceClientStateListener clientStateListener = new VoiceClientStateListener() {
        @Override
        public void stateChanged(int state) {
            switch (state) {
                case Error.SocketError.CONNECT_SUCCESS:
                    mHeartBeatThread.scheduleAtFixedRate(heartBeatRunnable, 1, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
                    if (!mIsRunning) {
                        mSendThread.start();
                    }
                    break;
                default:
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mListener.onFailed(state);
                    mConnector.connect(mHostName, mPort, clientStateListener);
                    break;
            }
        }
    };

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            VoiceLogger.info("MainExecutor 启动");
            mConnector.setDataReceive(VoiceClientManager.this);

            mConnector.connect(mHostName, mPort, clientStateListener);
        }
    };


    private Runnable reConnectRunnable = new Runnable() {
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            if (!mConnector.isRunning()) {
                VoiceLogger.info("重新连接");

                mConnector.setDataReceive(VoiceClientManager.this);
                int init_status = mConnector.connect(mHostName, mPort, clientStateListener);
//                if (init_status == Error.SocketError.CONNECT_SUCCESS) {
//                    receiveHeartBeatCounter = MAX_HB_LIFE;
//                } else if (init_status == Error.SocketError.CONNECT_TIME_OUT) {
//                    mRequestListener.onEvent(new VoiceClientEvent(VoiceClientConstant.EVENT_CONNECT_TIME_OUT));
//                } else {
//                    mListener.onFailed(init_status);
//                }
            }
        }
    };

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
//            VoiceLogger.info("heartBeatRunnable");
            if (mConnector.isRunning()) {
                VoiceLogger.info("receiveHeartBeatCounter = " + receiveHeartBeatCounter);
                receiveHeartBeatCounter--;

                if (receiveHeartBeatCounter == 0) {
                    mConnector.disConnect();
                    needSendSucceed = true;
                    mRequestListener.onEvent(new VoiceClientEvent(VoiceClientConstant.EVENT_DISCONNET));
                    mReConnectTread.scheduleAtFixedRate(reConnectRunnable, 0, RECONNECT_INTERVAL, TimeUnit.MILLISECONDS);
                }
            }
        }
    };


    public void setRequestListener(RequestListener listener) {
        mRequestListener = listener;
    }


    public void destroy() {
        mMainExecutor.shutdown();
        mConnector.destroy();
        mIsRunning = false;

    }

    public void disConnect() {
        mConnector.disConnect();
    }
}
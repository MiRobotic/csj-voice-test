/**
 * Project Name:TEST_LOG4J_SOCKET
 * File Name:LOlogger2.java
 * Package Name:com.example.utility
 * Date:2014年1月22日下午3:09:36
 * Copyright (c) 2014, ShangHai Leadon IOT Technology Co.,Ltd.  All Rights Reserved.
 */

package com.csjbot.voiceclient.utils;


import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * ClassName:CsjLogger <br/>
 * 用法：在类中定义 <br/>
 * private static final CsjLogger loLogger = new CsjLogger(xxxx.class); <br/>
 * loLogger.info(xxxxx); Date: 2014年1月22日 下午3:09:36 <br/>
 *
 * @author "浦耀宗"
 * @see
 */
public class VoiceLogger implements Serializable {
    /**
     * serialVersionUID:TODO(用一句话描述这个变量表示什么).
     */
    private static final long serialVersionUID = 1245549555563181056L;
    private static final Logger DEFAULT_LOGGER = Logger.getLogger("VoiceLogger");
    private static boolean isEnable = true;

    public static void setEnable(boolean enable) {
        isEnable = enable;
    }

    // =============== debug================/
    // =============== debug================/
    public static void debug(String msg) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.INFO, msg + getLineOut());
        }
    }

    public static void debug(String format, Object... arguments) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.INFO, format + getLineOut(), arguments);
        }
    }

    // =============== info================/
    // =============== info================/
    public static void info(String msg) {
        if (isEnable) {
            DEFAULT_LOGGER.info(msg + getLineOut());
        }
    }

    public static void info(String format, Object... arguments) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.INFO, format + getLineOut(), arguments);
        }
    }
    // =============== warn================/
    // =============== warn================/

    public static void warn(String msg) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.WARNING, msg + getLineOut());
        }
    }

    public static void warn(String msg, Throwable t) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.WARNING, msg + getLineOut(), t);
        }
    }

    public static void warn(String format, Object... arguments) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.WARNING, format + getLineOut(), arguments);
        }
    }
    // =============== error================/
    // =============== error================/

    public static void error(String msg) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.SEVERE, msg + getLineOut());
        }
    }

    public static void error(Throwable t) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.SEVERE, "error", t);
        }
    }

    public static void error(String msg, Throwable t) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.SEVERE, msg + getLineOut(), t);
        }
    }

    public static void error(String format, Object... arguments) {
        if (isEnable) {
            DEFAULT_LOGGER.log(Level.SEVERE, format + getLineOut(), arguments);
        }
    }
    // =============== Debug Line================/
    // =============== Debug Line================/


    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private static String getLineOut() {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("getLineOut") == 0) {
                currentIndex = i + 2;
                break;
            }
        }

        return " [" +
                stackTraceElement[currentIndex].getMethodName() +
                " : " +
                Thread.currentThread().getName() +
                "] (" +
                stackTraceElement[currentIndex].getFileName() +
                ":" +
                stackTraceElement[currentIndex].getLineNumber() +
                ")";
    }
}

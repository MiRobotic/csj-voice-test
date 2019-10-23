package com.csjbot.voiceclient.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * ClassName: NetDataTypeTransform 数据类型转换 <br>
 * date: 2013年4月27日 下午2:39:42 <br>
 *
 * @author 浦耀宗
 */
public class NetDataTypeTransform {

    /**
     * byte转换为char
     *
     * @param b
     * @return
     */
    @Deprecated
    public static char bytesToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

    /**
     * bytesToChar: bytesToChar(byte[] b)的更好的实现方法. <br/>
     *
     * @param b      整个byte数组，不用new 然后 system.arraycopy
     * @param offset 从b数组的第几个字节开始
     * @return
     * @author "浦耀宗"
     */
    public static char bytesToChar(byte[] b, int offset) {
        char c = (char) (((b[0 + offset] & 0xFF) << 8) | (b[1 + offset] & 0xFF));
        return c;
    }

    /**
     * bytesToInt: byte数组转化为int 将低字节在前转为int. <br/>
     *
     * @param res    整个byte数组，不用new 然后 system.arraycopy
     * @param offset 从b数组的第几个字节开始
     * @return 高字节在后的int
     * @author "浦耀宗"
     */
    public static int bytesToInt(byte[] res, int offset) {
        if (res.length < offset + 4) {
            return 0;
        }
        return (res[0 + offset] & 0xff) | ((res[1 + offset] << 8) & 0xff00) | ((res[2 + offset] << 24) >>> 8)
                | (res[3 + offset] << 24);
    }

    /**
     * 4个字节 高字节在前
     *
     * @param b
     * @return
     */
    public static int bytesToInt2(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

    /**
     * bytesToInt2: 4个字节 高字节在前. <br/>
     * 上面的方法的替代，这样就不用new byte了，直接把要转的 byte[] 传进来
     *
     * @param b      整个byte数组，不用new 然后 system.arraycopy
     * @param offset 从b数组的第几个字节开始
     * @return 转换过的int
     * @author "浦耀宗"
     */
    public static int bytesToInt2(byte[] b, int offset) {
        int intValue = 0;
        for (int i = 0; i < 4; i++) {
            intValue += (b[i + offset] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

    /**
     * byte2short: byte 转 short. <br/>
     *
     * @param b
     * @return
     * @author "浦耀宗"
     */
    public static short bytesToShort(byte[] b, int index) {
        // return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
        short s = 0;
        short s0 = (short) (b[0 + index] & 0xff);// 最低位
        short s1 = (short) (b[1 + index] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * bytes2UShort: 把Byte转换为unsigned short，因为java 没有unsigned short，所以转换为char. <br/>
     *
     * @param b
     * @param index
     * @return
     * @author "浦耀宗"
     */
    public static char bytesToUShort(byte[] b, int index) {
        return (char) (((((b[index + 1] & 0xFF) << 8) | b[index + 0] & 0xff)) & 0xFFFF);
    }

    /**
     * bytesTo32BitLong: 把Byte转换为32bit的long，因为arm的long是32位的. <br/>
     *
     * @param bb
     * @param index
     * @return
     * @author "浦耀宗"
     */
    public static long bytesTo32BitLong(byte[] bb, int index) {
        return ((((long) bb[index + 3] & 0xff) << 24) | (((long) bb[index + 2] & 0xff) << 16)
                | (((long) bb[index + 1] & 0xff) << 8) | (((long) bb[index + 0] & 0xff) << 0));
    }

    /**
     * bytesTo64BitLong: 看下，可能字节序不对. <br>
     *
     * @param b
     * @param index
     * @return
     * @author "浦耀宗"
     * @date 2014年5月12日 下午4:59:07
     */
    public static long bytesTo64BitLong(byte[] b, int index) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(b, index, 8);
        buffer.flip();// need flip
        return buffer.getLong();
    }

    /**
     * 将byte数组转化成String
     */
    public static String bytesToString(byte[] valArr, int offset, int length) {
        String result = null;
        try {
            result = new String(valArr, offset, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将byte数组转化成String
     */
    public static String bytesToString(byte[] valArr, int offset) {
        String result = null;
        int byteCount = 0;
        while (byteCount + offset < valArr.length) {
            if (valArr[byteCount + offset] == 0) {
                break;
            }
            byteCount++;
        }

        try {
            result = new String(valArr, offset, byteCount, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将byte数组转化成String
     */
    public static String bytesToString(byte[] valArr) {
        String result = null;
        int index = 0;
        while (index < valArr.length) {
            if (valArr[index] == 0) {
                break;
            }
            index++;
        }

        try {
            result = new String(valArr, 0, index, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * char 转换为byte
     *
     * @param c
     * @return
     */
    public static byte[] charToBytes(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) (c & 0xFF);
        b[1] = (byte) ((c & 0xFF00) >> 8);
        return b;
    }

    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    /**
     * @param inStream
     * @return 读入的字节数组
     * @throws Exception
     * @方法功能 InputStream 转为 byte
     */
    public static byte[] inputStreamToByte(InputStream inStream) throws Exception {
        int count = 0;
        while (count == 0) {
            if (inStream != null) {
                count = inStream.available();
            }
        }
        byte[] b = null;
        if (count > 2048) {
            b = new byte[2048];
        } else {
            if (count != -1) {
                b = new byte[count];
            }
        }
        if (null != b) {
            inStream.read(b);
        }
        return b;
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 以大端模式将int转成byte[], 高字节在前
     *
     * @param value 要转换的数字
     * @return 转换之后的数组
     */
    public static byte[] intToBytesQiPa(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * long2Bytes:(这里用一句话描述这个方法的作用).
     *
     * @param num
     * @return
     * @author "浦耀宗"
     */
    public static byte[] longToBytes(long num) {

        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (num >>> (56 - (i * 8)));
        }
        return b;
    }

    public static byte[] shortToBytesQiPa(short s) {
        byte[] ret = new byte[2];
        ret[1] = (byte) (s & 0xff);
        ret[0] = (byte) ((s >> 8) & 0xff);

        return ret;
    }

    public static byte[] shortToBytes(short s) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (s & 0xff);
        ret[1] = (byte) ((s >> 8) & 0xff);

        return ret;
    }

    /**
     * 将String转化成byte数组
     *
     * @param str
     * @return
     */
    public static byte[] stringToBytes(String str) {
        byte[] temp = null;
        try {
            if (str == null) {
                temp = new byte[]{0};
            } else {
                temp = str.getBytes("UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static String dumpHex(byte[] src) {
        String num = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte aSrc : src) {
            int high = aSrc >> 4 & 0x0f;
            int low = aSrc & 0x0f;
            sb.append(num.charAt(high)).append(num.charAt(low)).append(" ");
        }
        sb.append(" ]");

        return sb.toString();
    }
}

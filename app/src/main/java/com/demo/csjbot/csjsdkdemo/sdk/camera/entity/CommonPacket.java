package com.csjbot.cameraclient.entity;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class CommonPacket extends MessagePacket {
    public CommonPacket(byte[] content) {
        mContent = content;
    }

    public CommonPacket() {
    }

    @Override
    public byte[] getContent() {
        return mContent;
    }

    @Override
    public void setContent(byte[] content) {
        this.mContent = content;
    }

    public String getContentJson() {
        return new String(mContent);
    }
}

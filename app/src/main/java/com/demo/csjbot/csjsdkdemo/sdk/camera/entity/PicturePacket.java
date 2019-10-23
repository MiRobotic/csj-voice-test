package com.csjbot.cameraclient.entity;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class PicturePacket extends MessagePacket {
    public PicturePacket(byte[] content) {
        mContent = content;
    }

    public PicturePacket() {
    }

    @Override
    public byte[] getContent() {
        return mContent;
    }

    @Override
    public void setContent(byte[] content) {
        this.mContent = content;
    }

}

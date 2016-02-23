package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * relive
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1651 extends BasicMessage {

    public int sceneId;
    public int x;
    public int y;
    public int flag;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.sceneId = iba.readInt();
        this.x = iba.readShort();
        this.y = iba.readShort();
    }

    @Override
    public byte[] encode(byte[] data) {
        OutputByteArray oba = new OutputByteArray();
        oba.writeByte(0);
        oba.writeByte(this.flag); // 0 means free to relive
        return super.encode(oba.toByteArray());
    }

    @Override
    public String toString() {
        return "M1651{" +
                "sceneId=" + sceneId +
                ", x=" + x +
                ", y=" + y +
                ", flag=" + flag +
                '}';
    }
}

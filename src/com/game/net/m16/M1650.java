package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * dead
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1650 extends BasicMessage {

    public long playerId;
    public int x;
    public int y;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.playerId = iba.readUnsignedInt();
        this.x = iba.readShort();
        this.y = iba.readShort();
    }

    @Override
    public String toString() {
        return "M1650{" +
                "playerId=" + playerId +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

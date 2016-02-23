package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * in the event that being attacked,receive this packet
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1643 extends BasicMessage {

    public long targetId;
    public int currHP;
    public int currMP;
    public int maxHP;
    public int maxMP;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.targetId = iba.readUnsignedInt();
        this.currHP = iba.readInt();
        this.currMP = iba.readInt();
        this.maxHP = iba.readInt();
        this.maxMP = iba.readInt();
    }


    @Override
    public String toString() {
        return "M1643{" +
                "targetId=" + targetId +
                ", currHP=" + currHP +
                ", currMP=" + currMP +
                ", maxHP=" + maxHP +
                ", maxMP=" + maxMP +
                '}';
    }
}

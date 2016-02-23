package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 *
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1690 extends BasicMessage {

    public int flag;
    public int count;
    public int money;
    public String killerName;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.flag = iba.readByte();
        this.count = iba.readByte();
        this.money = iba.readInt();
        this.killerName = iba.readUTF();
    }

    @Override
    public String toString() {
        return "M1690{" +
                "flag=" + flag +
                ", count=" + count +
                ", money=" + money +
                ", killerName='" + killerName + '\'' +
                '}';
    }
}

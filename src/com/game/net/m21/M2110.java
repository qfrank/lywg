package com.game.net.m21;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * disband team
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M2110 extends BasicMessage {

    public long result;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.result = iba.readByte();
    }

    @Override
    public String toString() {
        return "M2110{" +
                "result=" + result +
                '}';
    }
}

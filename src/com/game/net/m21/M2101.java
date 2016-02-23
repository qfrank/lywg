package com.game.net.m21;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * create team
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M2101 extends BasicMessage {

    /**
     * when already in team, result value is 1, means create fail
     */
    public long result;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.result = iba.readByte();
    }

    @Override
    public String toString() {
        return "M2101{" +
                "result=" + result +
                '}';
    }
}

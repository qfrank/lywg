package com.game.net.m21;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * join target team
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M2103 extends BasicMessage {

    public long result;
    public int _id;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.result = iba.readByte();
    }

    @Override
    public byte[] encode(byte[] data) {
        OutputByteArray oba = new OutputByteArray();
        oba.writeByte(0);
        oba.writeInt(_id);
        return super.encode(oba.toByteArray());
    }

    @Override
    public String toString() {
        return "M2103{" +
                "result=" + result +
                ", _id=" + _id +
                '}';
    }
}

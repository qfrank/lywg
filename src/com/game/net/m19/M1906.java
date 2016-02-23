package com.game.net.m19;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1906 extends BasicMessage {

    public String content;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.content = iba.readUTF();
    }

    @Override
    public byte[] encode(byte[] data) {
        OutputByteArray oba = new OutputByteArray();
        oba.writeByte(0);
        return super.encode(oba.toByteArray());
    }

    @Override
    public String toString() {
        return "M1906{" +
                "content='" + content + '\'' +
                '}';
    }
}

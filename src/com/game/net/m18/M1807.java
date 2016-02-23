package com.game.net.m18;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * User: Franklyn <br/>
 * Date: 14-11-29<br/>
 * Time: 下午3:48<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1807 extends BasicMessage {

    public int result;

    public int _id;

    @Override
    public void decode(byte[] data) {
        InputByteArray dis = new InputByteArray(data);
        dis.skipBytes(1);
        this.result = dis.readByte();
    }

    @Override
    public byte[] encode(byte[] data) {
        OutputByteArray dos = new OutputByteArray();
        dos.writeByte(0);
        dos.writeInt(this._id);
        return super.encode(dos.toByteArray());
    }
}

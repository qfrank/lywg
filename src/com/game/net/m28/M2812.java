package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class M2812 extends BasicMessage {

    public long coinNum;
    public long goldNum;
    public List<Map> arr;
    public int len;

    @Override
    public void decode(byte[] data) {
        InputByteArray dis = new InputByteArray(data);
        dis.skipBytes(1);

        this.coinNum = dis.readUnsignedInt();
        this.goldNum = dis.readUnsignedInt();
        this.len = dis.readByte();
        this.arr = new ArrayList<Map>();
        while (this.len-- > 0) {
            Map _local2 = new HashMap();
            _local2.put("index", dis.readByte());
            _local2.put("id", dis.readUnsignedInt());
            _local2.put("type", dis.readUnsignedInt());
            _local2.put("num", dis.readShort());
            _local2.put("slevel", dis.readShort());
            _local2.put("luck", dis.readShort());
            _local2.put("bpatt", dis.readShort());
            _local2.put("bpdef", dis.readShort());
            _local2.put("bmatt", dis.readShort());
            _local2.put("bmdef", dis.readShort());
            _local2.put("btatt", dis.readShort());
            if (dis.available() > 0) {
                _local2.put("snum", dis.readByte());
            }
            this.arr.add(_local2);
        }

    }

    @Override
    public String toString() {
        return "M2812{" +
                "coinNum=" + coinNum +
                ", goldNum=" + goldNum +
                ", arr=" + arr +
                ", len=" + len +
                '}';
    }
}

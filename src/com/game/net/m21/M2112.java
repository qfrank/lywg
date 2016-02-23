package com.game.net.m21;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * query my team info,include team member info
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M2112 extends BasicMessage {

    public int leader;
    public int mode;
    public List<Map> data;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);
        this.leader = iba.readInt();
        this.mode = iba.readByte();
        this.data = new ArrayList<Map>();
        int _local2 = iba.readShort();
        while (_local2-->0) {
            Map _local3 = new HashMap();
            _local3.put("id",iba.readInt());
            _local3.put("level",iba.readShort());
            _local3.put("job",iba.readByte());
            _local3.put("sex",iba.readByte());
            _local3.put("name",iba.readUTF());
            _local3.put("status",iba.readByte());
            _local3.put("mhp",iba.readInt());
            _local3.put("chp",iba.readInt());
            _local3.put("mmp",iba.readInt());
            _local3.put("cmp",iba.readInt());
            this.data.add(_local3);
        }
    }

    @Override
    public String toString() {
        return "M2112{" +
                "leader=" + leader +
                ", mode=" + mode +
                ", data=" + data +
                '}';
    }
}

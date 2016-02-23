package com.game.net.m15;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.io.StringReader;

/**
 * onUpdateUserInfo,e.g. object={"id":1877615,"group":2}<br/>
 * when using M2101 create team,will receive M1513,the id means the team leader player id,group value is 2<br/>
 * when using M2110 disband team,will receive M1513,the id means the team leader player id,group value is 0<br/>
 * when M2115 means team have been disbanded
 * User: Frank Tang <br/>
 * Date: 14-12-16<br/>
 * Time: 下午8:29<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class M1513 extends BasicMessage {

    public JsonObject object;

    @Override
    public void decode(byte[] data) {
        InputByteArray iba = new InputByteArray(data);
        iba.skipBytes(1);

        String json = iba.readUTF();
        JsonParser jp = new JsonParser();
        JsonReader jr = new JsonReader(new StringReader(json.toLowerCase()));
        jr.setLenient(true);
        JsonElement je = jp.parse(jr);
        object = je.getAsJsonObject();
    }

    @Override
    public String toString() {
        return "M1513{" +
                "object=" + object +
                '}';
    }
}

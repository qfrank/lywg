package com.game.net.m15;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.io.StringReader;

/**
 * onGetSceneInfoHandler
 *
 */
public class M1500 extends BasicMessage{

	public long result;
	
	public JsonArray players;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		
		JsonParser jp = new JsonParser();
		JsonReader jr = new JsonReader(new StringReader(dis.readUTF().toLowerCase()));
		jr.setLenient(true);
		JsonElement je = jp.parse(jr);
		players = je.getAsJsonArray();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1500 [result=" + result + ", players=" + players + "]";
	}
	
}

package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.util.Global;
import wg.util.FlashSimulator;


/**
 * Heartbeat
 *
 */
public class M1310 extends BasicMessage{
	
	public long pong;
	public Number UNIXTIME = 0;
	public long _ping;
	public long _latency;
	
	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.pong = dis.readUnsignedInt();
		if (((dis.available()>0) && ((this.UNIXTIME.intValue() == 0)))){
			this.UNIXTIME = dis.readUnsignedInt() - FlashSimulator.getTimer();
			Global.setData("UNIXTIME", this.UNIXTIME);
		};
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
        dos.writeInt((int)this._ping);
        dos.writeInt((int)this._latency);
		return (super.encode(dos.toByteArray()));
	}

	@Override
	public String toString() {
		return "M1310 [pong=" + pong + ", UNIXTIME=" + UNIXTIME + ", _ping="
				+ _ping + ", _latency=" + _latency + "]";
	}
	
}

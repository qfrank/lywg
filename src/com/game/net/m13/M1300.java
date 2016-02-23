package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.util.Global;
import wg.util.FlashSimulator;


public class M1300 extends BasicMessage{
	
	public long result;
	public long newVersion = 0;
	public String _accId;
	public int _code = 0;
	
	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.result = dis.readByte();
		if (dis.available()>0){
			this.newVersion = dis.readByte();
		}
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeUTF(this._accId);
		this._code = Global.getData("1201_code");
		dos.writeInt(this._code);
		dos.writeUTF(FlashSimulator.Capabilities_serverString);
		return (super.encode(dos.toByteArray()));
	}
	
}

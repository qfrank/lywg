package com.game.net.m14;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * onRemoveBuffStateHandler
 * 
 *
 */
public class M1422 extends BasicMessage {
	
	public long buffId;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);

		this.buffId = dis.readUnsignedInt();
	}

	@Override
	public String toString() {
		return "M1422 [buffId=" + buffId + "]";
	}
	
	

}

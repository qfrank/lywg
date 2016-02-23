package com.game.net.m14;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * getBuffNonskillData
 * 
 *
 */
public class M1423 extends BasicMessage {

	public long buffId;
	public long type;
	public long index;
	public long times;
	public long timespan;
	public long exp;
	public long add_hp;
	public long add_mp;
	public long exp_per;
	public long minphysicsattack;
	public long maxphysicsattack;
	public long minphysicsguard;
	public long maxphysicsguard;
	public long minspellattack;
	public long maxspellattack;
	public long minspellguard;
	public long maxspellguard;
	public long mintaoattack;
	public long maxtaoattack;
	public long lifeupperlimit;
	public long Magicupperlimit;
	public long lifeupperlimitper;
	public long magicupperlimitper;
	public long physics_absorbs;
	public long magic_absorbs;
	public long physics_raise;
	public long magic_raise;
	public long hp_total;
	public long extrahurt;
	public long extrarate;
	public long addintervalue;
	public long recoverintervalue;
	public int _buffId;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);

		this.buffId = dis.readUnsignedInt();
		this.type = dis.readUnsignedInt();
		this.index = dis.readUnsignedInt();
		this.times = dis.readUnsignedInt();
		this.timespan = dis.readUnsignedInt();
		this.exp = dis.readUnsignedInt();
		this.add_hp = dis.readUnsignedInt();
		this.add_mp = dis.readUnsignedInt();
		this.exp_per = dis.readUnsignedInt();
		this.minphysicsattack = dis.readUnsignedInt();
		this.maxphysicsattack = dis.readUnsignedInt();
		this.minphysicsguard = dis.readUnsignedInt();
		this.maxphysicsguard = dis.readUnsignedInt();
		this.minspellattack = dis.readUnsignedInt();
		this.maxspellattack = dis.readUnsignedInt();
		this.minspellguard = dis.readUnsignedInt();
		this.maxspellguard = dis.readUnsignedInt();
		this.mintaoattack = dis.readUnsignedInt();
		this.maxtaoattack = dis.readUnsignedInt();
		this.lifeupperlimit = dis.readUnsignedInt();
		this.Magicupperlimit = dis.readUnsignedInt();
		this.lifeupperlimitper = dis.readUnsignedInt();
		this.magicupperlimitper = dis.readUnsignedInt();
		this.physics_absorbs = dis.readUnsignedInt();
		this.magic_absorbs = dis.readUnsignedInt();
		this.physics_raise = dis.readUnsignedInt();
		this.magic_raise = dis.readUnsignedInt();
		this.hp_total = dis.readUnsignedInt();
		this.extrahurt = dis.readUnsignedInt();
		this.extrarate = dis.readUnsignedInt();
		this.addintervalue = dis.readUnsignedInt();
		if (dis.available() > 0) {
			this.recoverintervalue = dis.readUnsignedInt();
		}
		
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this._buffId);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1423 [buffId=" + buffId + ", type=" + type + ", index=" + index + ", times=" + times + ", timespan=" + timespan + ", exp=" + exp + ", add_hp="
				+ add_hp + ", add_mp=" + add_mp + ", exp_per=" + exp_per + ", minphysicsattack=" + minphysicsattack + ", maxphysicsattack=" + maxphysicsattack
				+ ", minphysicsguard=" + minphysicsguard + ", maxphysicsguard=" + maxphysicsguard + ", minspellattack=" + minspellattack + ", maxspellattack="
				+ maxspellattack + ", minspellguard=" + minspellguard + ", maxspellguard=" + maxspellguard + ", mintaoattack=" + mintaoattack
				+ ", maxtaoattack=" + maxtaoattack + ", lifeupperlimit=" + lifeupperlimit + ", Magicupperlimit=" + Magicupperlimit + ", lifeupperlimitper="
				+ lifeupperlimitper + ", magicupperlimitper=" + magicupperlimitper + ", physics_absorbs=" + physics_absorbs + ", magic_absorbs="
				+ magic_absorbs + ", physics_raise=" + physics_raise + ", magic_raise=" + magic_raise + ", hp_total=" + hp_total + ", extrahurt=" + extrahurt
				+ ", extrarate=" + extrarate + ", addintervalue=" + addintervalue + ", recoverintervalue=" + recoverintervalue + ", _buffId=" + _buffId + "]";
	}
	
	

}

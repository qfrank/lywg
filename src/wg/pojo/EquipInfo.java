package wg.pojo;

public class EquipInfo {

	public int id;
	
	public int type;
	
	/**
	 * ×°±¸Î»ÖÃ
	 */
	public byte part;
	
	public short slevel;
	
	public byte snum;
	
	public short luck;
	
	public short bpatt;
	
	public short bpdef;
	
	public short bmatt;
	
	public short bmdef;
	
	public short btatt;
	
	public byte bind;

	@Override
	public String toString() {
		return "EquipInfo [id=" + id + ", type=" + type + ", part=" + part + ", slevel=" + slevel + ", snum=" + snum + ", luck=" + luck + ", bpatt=" + bpatt
				+ ", bpdef=" + bpdef + ", bmatt=" + bmatt + ", bmdef=" + bmdef + ", btatt=" + btatt + ", bind=" + bind + "]";
	}
	
}

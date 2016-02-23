package wg.pojo;

public class PackageItem {
	
	public int id;
	
	public int type;
	
	public int index;
	
	public int num;
	
	public short slevel;
	
	public byte snum;
	
	public short luck;
	
	public short bpatt;

	public short bpdef;
	
	public short bmatt;
	
	public short bmdef;
	
	public short btatt;
	
	/**
	 * 0£ºÎ´°ó¶¨
	 * 1£ºÒÑ°ó¶¨
	 */
	public byte bind;

	@Override
	public String toString() {
		return "PackageItem{" +
				"id=" + id +
				", type=" + type +
				", index=" + index +
				", num=" + num +
				", slevel=" + slevel +
				", snum=" + snum +
				", luck=" + luck +
				", bpatt=" + bpatt +
				", bpdef=" + bpdef +
				", bmatt=" + bmatt +
				", bmdef=" + bmdef +
				", btatt=" + btatt +
				", bind=" + bind +
				'}';
	}
}

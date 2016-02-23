package wg.pojo.map;

import flash.geom.Point;

import java.util.HashMap;
import java.util.Map;

public class PortInfo {

	public int id;
	public String name;
	public Point pos;
	public int x;
	public int y;
	public int next;
	public String type;
	public Map to;
	public Number fix = 1.5;

	public PortInfo(Map _arg1) {
		this.parse(_arg1);
	}

	private void parse(Map _arg1) {
		this.id = ((Long) _arg1.get("id")).intValue();
		this.name = (String) _arg1.get("name");
		this.x = ((Short) _arg1.get("x")).intValue();
		this.y = ((Short) _arg1.get("y")).intValue();
		this.next = ((Long) _arg1.get("next")).intValue();
		this.type = (String) _arg1.get("type");
		this.pos = new Point(x, y);
		if (_arg1.get("jmapid") != null) {
			this.to = new HashMap();
			this.to.put("scene", ((Long) _arg1.get("jmapid")).intValue());
			this.to.put("x", ((Short) _arg1.get("jx")).intValue());
			this.to.put("y", ((Short) _arg1.get("jy")).intValue());
		}
		// if (_arg1.hasOwnProperty("fix")){
		// this.fix = int(_arg1.fix);
		// }
	}

	@Override
	public String toString() {
		return "PortInfo [id=" + id + ", name=" + name + ", pos=" + pos + ", x=" + x + ", y=" + y + ", next=" + next + ", type=" + type + ", to=" + to
				+ ", fix=" + fix + "]";
	}
	
	
}

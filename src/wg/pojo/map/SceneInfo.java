package wg.pojo.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SceneInfo {
	public long id;
	public int resourceId;
	public String name;
	public List<PortInfo> ports;

	public SceneInfo(Map _arg1) {
		this.parse(_arg1);
	}

	private void parse(Map _arg1) {
		this.id = ((Long) _arg1.get("mapid"));
		this.resourceId = ((Long) _arg1.get("sourceid")).intValue();
		this.name = (String) _arg1.get("map_name");
		this.ports = new ArrayList();
		if (_arg1.get("portArray") != null) {
			List<Map> portArray = (List) _arg1.get("portArray");
			for (Map _local2 : portArray) {
				this.ports.add(new PortInfo(_local2));
			}
		}
	}

	public PortInfo hasPort(int _arg1) {
		for (PortInfo _local2 : ports) {
			if (_local2.id == _arg1) {
				return (_local2);
			}
		}
		return (null);
	}

	@Override
	public String toString() {
		return "SceneInfo [id=" + id + ", resourceId=" + resourceId + ", name=" + name + ", ports=" + ports + "]";
	}
	
	
}

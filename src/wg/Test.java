package wg;

import jp.develop.common.util.AmfUtil;
import jp.develop.common.util.amf.AmfObject;
import org.frkd.util.ZLibUtil;
import wg.pojo.ItemInfo;

import java.io.*;
import java.util.*;

public class Test {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.setProperty("appdir", "/dev/lywg");

		File f = new File("/dev/lywg/misc/data/source/items.data");
		InputStream is = new FileInputStream(f);
		byte[] buf = new byte[is.available()];
		is.read(buf);
		buf = ZLibUtil.decompress(buf);
		Map<String,AmfObject> data = AmfUtil.decode(buf, HashMap.class);
		
		List<ItemInfo> list = new ArrayList();
		for (String key : data.keySet()) {
			AmfObject val = data.get(key);
			ItemInfo ii = new ItemInfo();
			ii.entryId = Integer.parseInt(key);
			ii.name = (String) val.get("name1");
			ii.description = val.get("description").toString();
			ii.requiredLevel = (Integer) val.get("requiredlevel");
			ii.itemLevel = (Integer) val.get("itemlevel");
			list.add(ii);
		}
		Collections.sort(list, new Comparator<ItemInfo>() {
			@Override
			public int compare(ItemInfo o1, ItemInfo o2) {
				return o1.requiredLevel-o2.requiredLevel;
			}
		});
		
		File out = new File("d:/item.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		for(ItemInfo ii:list){
			bw.write(ii.toString());
			bw.write("\n");
		}
		bw.close();
	}
	
}

package wg.mod.impl;

import com.game.net.m17.M1707;
import com.game.net.m17.M1711;
import com.game.net.m17.M1728;
import jp.develop.common.util.AmfUtil;
import jp.develop.common.util.amf.AmfObject;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.IFlashMessageHandler;
import org.frkd.util.PropertyUtil;
import org.frkd.util.ZLibUtil;
import wg.mod.BaseModule;
import wg.pojo.ItemInfo;
import wg.pojo.PackageItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ItemModule extends BaseModule {

	private static Map<Integer, ItemInfo> ITEM_MAP = new HashMap();

	static {
		try {
			InputStream is = new FileInputStream(PropertyUtil.getProperty("appdir") + File.separatorChar + "misc" + File.separatorChar + "data" + File.separatorChar
					+ "source" + File.separatorChar + "items.data");
			byte[] buf = new byte[is.available()];
			is.read(buf);
			buf = ZLibUtil.decompress(buf);
			Map<String, AmfObject> data = AmfUtil.decode(buf, HashMap.class);
			for (String key : data.keySet()) {
				AmfObject val = data.get(key);
				ItemInfo ii = new ItemInfo();
				ii.entryId = Integer.parseInt(key);
				ii.name = (String) val.get("name1");
				ii.description = val.get("description").toString();
				ii.requiredLevel = (Integer) val.get("requiredlevel");
				ii.itemLevel = (Integer) val.get("itemlevel");
				ITEM_MAP.put(ii.entryId, ii);
			}
		} catch (Exception e) {
			globalLogger.log(Level.SEVERE, "Error happend in class " + ItemModule.class.getName(), e);
		}

	}

	public static ItemInfo getItemInfo(int typeId) {
		return ITEM_MAP.get(typeId);
	}

	public static boolean isFbj(PackageItem item){
		return item.type == 10189;
	}

	/**
	 * 从商城购买物品并使用
	 * 
	 * @param itemTypeId
	 *            商品ID
	 * @param num
	 *            购买个数
	 */
	public void buyAndUsingItem(int itemTypeId, int num) {
		final SocketX socket = getCurrentGameSocket();
		registerMessageHander(M1707.class, new IFlashMessageHandler<M1707>() {
			@Override
			public void handle(M1707 m) {
				if (m.result == 0) {
					registerMessageHander(M1711.class, new IFlashMessageHandler<M1711>() {
						public void handle(M1711 m) {
							if (m.item != null) {
								M1728 m1728 = new M1728();
								m1728._id = m.item.id;
								socket.send(m1728);
								
								//m.item.num--;
							}
						};
					});
				}
			}
		});
		M1707 m1707 = new M1707();
		m1707._goodId = itemTypeId;
		m1707._num = num;
		socket.send(m1707);
	}

	public int getLoadSequence() {
		return Integer.MIN_VALUE + 2;
	}

}

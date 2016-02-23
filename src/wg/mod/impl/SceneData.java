package wg.mod.impl;

import com.game.net.m13.M1300;
import com.game.net.m15.M1532;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.IMessageHandler;
import wg.mod.BaseModule;
import wg.pojo.map.SceneInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneData extends BaseModule {

	private Map<Long, SceneInfo> scenesDic = new HashMap();

	public int getLoadSequence() {
		return Integer.MIN_VALUE + 1;
	}
	
	public SceneInfo getSceneInfo(Long sceneId){
		return scenesDic.get(sceneId);
	}

	@Override
	public void init() {
		registerMessageHander(M1300.class, new IMessageHandler<M1300>() {
			@Override
			public void handle(M1300 m) {
				SocketX socket = getCurrentGameSocket();
				socket.send(new M1532());
			}
		});

		// onReceivePortData
		registerMessageHander(M1532.class, new IMessageHandler<M1532>() {
			@Override
			public void handle(M1532 m) {
				List<Map> sceneArray = m.sceneArray;
				for (Map scene : sceneArray) {
					SceneInfo si = new SceneInfo(scene);
					if (si.id > 0) {
						scenesDic.put(si.id, si);
						//logger.finest("收到场景数据：" + si.toString());
					}
				}

			}
		});
	}
}

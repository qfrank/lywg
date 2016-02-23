package wg.mod.impl;

import com.game.net.m15.M1510;
import wg.mod.BaseModule;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 千服刷元宝BOSS时，捡元宝模块
 */
public class QianFuModule extends BaseModule {

	private List<M1510> list = new CopyOnWriteArrayList();

	private Timer timer;

	@Override
	public void init() {
		/*if (ModuleToggleHolder.QIAN_FU_ENABLED) {
			ParserManager pm = getParserManager();
			pm.registerMessageHander(M1510.class, new IMessageHandler<M1510>() {

				@Override
				public void handle(M1510 m1510) {
//					NavigatorModule nav = getData(NavigatorModule.class);
//					nav.go(new Point(m1510.x, m1510.y));
//					SocketX socket = getCurrentGameSocket();
//					M1512 m1512 = new M1512();
//					m1512._id = m1510.id;
//					socket.send(m1512);
					list.add(m1510);
					if (timer == null) {
						timer = new Timer(true);
						timer.schedule(new TimerTask() {
							public void run() {
								final Player player = getCurrentPlayer();
								java.awt.Point p = player.currentLocation;
								while (list.size() > 0) {
									M1510 m1510 = list.get(0);
									for (int i = 1; i < list.size(); i++) {
										M1510 m2 = list.get(i);
										if (m2.type == 60214) {// ????????
											m1510 = m2;
											break;
										} else if (m2.type == 19099) {// 1000???
											m1510 = m2;
											break;
										} else {
											double d1 = MathUtil.getDistance(p, new java.awt.Point(m1510.x, m1510.y));
											double d2 = MathUtil.getDistance(p, new java.awt.Point(m2.x, m2.y));
											if (d2 < d1) {
												m1510 = m2;
											}
										}
									}
									list.remove(m1510);

									NavigatorModule nav = getData(NavigatorModule.class);
									nav.go(new Point(m1510.x, m1510.y));
									SocketX socket = getCurrentGameSocket();
									M1512 m1512 = new M1512();
									m1512._id = m1510.id;
									socket.send(m1512);
								}
								timer = null;
							};
						}, 20);
					}
				}

			});

			pm.registerMessageHander(M1511.class, new IMessageHandler<M1511>() {
				@Override
				public void handle(M1511 m1511) {
					for (M1510 m1510 : list) {
						if (m1511.id == m1510.id) {
							list.remove(m1510);
							break;
						}
					}
				}
			});

		}*/

	}

}

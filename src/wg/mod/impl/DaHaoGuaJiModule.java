package wg.mod.impl;

import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;

import java.util.Timer;

/**
 * 会员挂机图6,坐标81,169;44,106;
 * 
 *
 * 
 */
public class DaHaoGuaJiModule extends BaseModule {

	private Timer skillTimer;

	@Override
	public void init() {
		addEventListener(IEventType.PLAYER_INFO_INITED, new IOnceListener() {

			@Override
			public void onAction(Event evt) {
				// NavigatorModule nav = getData(NavigatorModule.class);
				// nav.go(new flash.geom.Point(81, 169));
				start();
			}
		});
	}

	public void start() {
//		if (ModuleToggleHolder.DA_HAO_GUA_JI_ENABLED) {
//			final SocketX socket = getCurrentGameSocket();
//			// 获得挂机参数
//			getParserManager().registerMessageHander(M1688.class, new IFlashMessageHandler<M1688>() {
//				@Override
//				public void handle(M1688 m) {
//					M1680 m1680 = new M1680();
//					m1680._skill_alone = m._skill_alone;
//					m1680._skill_group = m._skill_group;
//					m1680._open = m._open;
//					m1680._hp_signal = m._hp_signal;
//					m1680._hp_item = m._hp_item;
//					m1680._hp_interval = m._hp_interval;
//					m1680._mp_signal = m._mp_signal;
//					m1680._mp_item = m._mp_item;
//					m1680._mp_interval = m._mp_interval;
//					m1680._quit = m._quit;
//					m1680._quit_item = m._quit_item;
//					m1680._filterLevel = m._filterLevel;
//					// 开启挂机
//					socket.send(m1680);
//				}
//			});
//
//			// 请求获取挂机参数
//			socket.send(new M1688());
			
			/*logger.finest("开启大号挂机.");
			skillTimer = new Timer(true);
			final SocketX socket = getCurrentGameSocket();
			final int[] skills = { 0x18, 0x21, 0x17, 0x58 };
			skillTimer.scheduleAtFixedRate(new TimerTask() {

				int skillIdx;

				public void run() {
					Point p = getCurrentPlayer().currentLocation;
					if (p != null) {
						M1630 m1630;
						skillIdx = skillIdx % skills.length;
						int skillId = skills[skillIdx];
						if (skillId == 0x18) {
							m1630 = new M1630();
							m1630._skillId = skillId;
							m1630._x = p.x;
							m1630._y = p.y;
							m1630._targetId = 0L;
							m1630._tx = p.x;
							m1630._ty = 0;
							m1630._delay_time = 0;
						} else if (skillId == 0x58) {
							m1630 = new M1630();
							m1630._skillId = skillId;
							m1630._x = p.x;
							m1630._y = p.y;
							m1630._targetId = 0L;
							m1630._tx = p.x + 2;
							m1630._ty = p.y + 2;
							m1630._delay_time = 0;
						} else {
							m1630 = new M1630();
							m1630._skillId = skillId;
							m1630._x = p.x;
							m1630._y = p.y;
							m1630._targetId = 0L;
							m1630._tx = p.x;
							m1630._ty = p.y;
							m1630._delay_time = 0;
						}
						skillIdx++;
						socket.send(m1630);
					}
				};
			}, 500, 860);*/
//		}
	}

	@Override
	public void destroy() {
		if (skillTimer != null) {
			skillTimer.cancel();
		}
	}

}

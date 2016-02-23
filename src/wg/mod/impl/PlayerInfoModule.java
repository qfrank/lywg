package wg.mod.impl;

import com.game.cons.CaseType;
import com.game.net.m14.M1400;
import com.game.net.m14.M1401;
import com.game.net.m15.M1503;
import com.game.net.m15.M1504;
import com.game.net.m15.M1505;
import com.game.net.m17.M1711;
import com.game.net.m17.M1732;
import org.frkd.net.socket.protocol.IMessageHandler;
import wg.bgthread.LocationWatcher;
import wg.event.Event;
import wg.event.IEventType;
import wg.mod.BaseModule;
import wg.pojo.PackageInfo;
import wg.pojo.PackageItem;
import wg.pojo.Player;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PlayerInfoModule extends BaseModule {

    public int getLoadSequence() {
        return Integer.MIN_VALUE + 3;
    }

    @Override
    public void init() {
        registerMessageHander(M1400.class, new IMessageHandler<M1400>() {
            @Override
            public void handle(M1400 m1400) {
                final Player player = getCurrentPlayer();
                Map data = m1400.data;
                player.roleName = (String) data.get("name");
                player.currentLocation = new Point(((Short) data.get("x")).intValue(), ((Short) data.get("y")).intValue());
                player.sceneId = ((Integer) data.get("sceneid")).longValue();
                player.level = (Byte) data.get("level");
                player.job = (Byte) data.get("job");
                player.sex = (Byte) data.get("sex");
                player.exp = (Long) data.get("exp");
                player.needExp = (Long) data.get("needexp");
                player.gold = (Long) data.get("gold");
                player.bgold = (Long) data.get("bgold");
                player.yb = (Long) data.get("ingot");
                player.lyb = (Long) data.get("bingot");
                player.hp = (Long) data.get("hp");
                player.mp = (Long) data.get("mp");

                if (player.initLevel == null)
                    player.initLevel = player.level;

                logger.fine("获得角色信息：" + player);
                dispachEvent(new Event(IEventType.PLAYER_INFO_INITED, player.sceneId));


            }
        });

        registerMessageHander(M1503.class, new IMessageHandler<M1503>() {
            @Override
            public void handle(M1503 m1503) {
                Player player = getCurrentPlayer();
                if (m1503.playerId == player.id) {
                    player.currentLocation = new Point(m1503.x, m1503.y);
                    LocationWatcher.refreshLocation(getCurrentAccount().getUserId());
                    if (logger.isLoggable(Level.FINE))
                        logger.fine(getCurrentAccount().getUserId() + "，" + player.roleName + "【" + player.level + "级】，当前位置：(" + player.sceneId + "," + m1503.x + "," + m1503.y + ")");
                }
            }
        });

        registerMessageHander(M1504.class, new IMessageHandler<M1504>() {
            @Override
            public void handle(M1504 m1504) {
                Player player = getCurrentPlayer();
                player.currentLocation = new Point(m1504.x, m1504.y);
                LocationWatcher.refreshLocation(getCurrentAccount().getUserId());
            }
        });

        registerMessageHander(M1505.class, new IMessageHandler<M1505>() {
            @Override
            public void handle(M1505 m1505) {
                if (m1505.result == 0) {
                    Player player = getCurrentPlayer();
                    player.currentLocation = new Point(m1505.x, m1505.y);
                    LocationWatcher.refreshLocation(getCurrentAccount().getUserId());
                    long oldSceneId = player.sceneId;
                    player.sceneId = (long) m1505.id;
                    if (oldSceneId != player.sceneId) {
                        if (logger.isLoggable(Level.FINE))
                            logger.fine("场景发生变化：" + oldSceneId + "->" + player.sceneId);
                        dispachEvent(new Event(IEventType.SCENE_CHANGED, player.sceneId));
                    }
                }
            }
        });

        // 背包数据
        registerMessageHander(M1732.class, new IMessageHandler<M1732>() {
            @Override
            public void handle(M1732 m1732) {
                if (m1732.pos == CaseType.PACKAGE) {
                    List<PackageItem> items = m1732.items;
                    Player player = getCurrentPlayer();
                    PackageInfo pi = player.packageInfo;
                    pi.clear();
                    for (int i = 0; i < items.size(); i++) {
                        PackageItem item = (PackageItem) items.get(i);
                        pi.addItem(item);
                    }
                    pi.inited = true;
                    dispachEvent(new Event(IEventType.PACKAGE_LOADED, pi));
                }

                if (m1732.pos == CaseType.STORE) {
                    dispachEvent(new Event(IEventType.STORE_LOADED, m1732));
                }

            }
        });

        registerMessageHander(M1401.class, new IMessageHandler<M1401>() {
            @Override
            public void handle(M1401 m) {
                Player player = getCurrentPlayer();
                if (m.data.get("gold") != null)
                    player.gold = m.data.get("gold").getAsLong();

                if (m.data.get("bgold") != null)
                    player.bgold = m.data.get("bgold").getAsLong();

                if (m.data.get("ingot") != null)
                    player.yb = m.data.get("ingot").getAsLong();

                if (m.data.get("bingot") != null)
                    player.lyb = m.data.get("bingot").getAsLong();

                if (m.data.get("level") != null)
                    player.level = m.data.get("level").getAsByte();

                if (m.data.get("exp") != null)
                    player.exp = m.data.get("exp").getAsLong();

                if (m.data.get("needexp") != null)
                    player.needExp = m.data.get("needexp").getAsLong();

                if (m.data.get("hp") != null)
                    player.hp = m.data.get("hp").getAsLong();

                if (m.data.get("mp") != null)
                    player.hp = m.data.get("mp").getAsLong();
            }
        });

        registerMessageHander(M1711.class, new IMessageHandler<M1711>() {
            @Override
            public void handle(M1711 m1711) {
                if (m1711.pos == CaseType.PACKAGE) {
                    PackageInfo pi = getCurrentPlayer().packageInfo;
                    if (m1711.result == 0) {
                        //if (m1711.item != null)
                        pi.removeItem(m1711.index);
                    } else {
                        pi.addItem(m1711.item);
                    }
                }
            }
        });

    }

}

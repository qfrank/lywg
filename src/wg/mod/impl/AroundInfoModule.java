package wg.mod.impl;

import com.game.net.m15.M1501;
import com.game.net.m15.M1502;
import com.game.net.m15.M1503;
import com.game.net.m15.M1505;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.MathUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.mod.BaseModule;
import wg.pojo.Player;
import wg.util.ContextUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AroundInfoModule extends BaseModule {

    private Map<Long, M1501> aroundPlayersMap = new ConcurrentHashMap();

    @Override
    public int getLoadSequence() {
        return Integer.MIN_VALUE + 2;
    }

    @Override
    public void init() {

        registerMessageHander(M1501.class, new IMessageHandler<M1501>() {
            @Override
            public void handle(M1501 m1501) {// 周围出现新的玩家或怪物或火墙或...
                if ((Long) m1501.info.get("id") < 10000000) {
                    aroundPlayersMap.put((Long) m1501.info.get("id"), m1501);
                    dispachEvent(new Event(IEventType.NEW_PLAYER_APPEARED, m1501));
                }
            }
        });

        registerMessageHander(M1502.class, new IMessageHandler<M1502>() {
            @Override
            public void handle(M1502 m1502) {// 周围玩家或怪物或物品消失
                Long playerId = m1502.playerId;
                M1501 m1501 = aroundPlayersMap.remove(playerId);
                if (m1501 != null) {
                    dispachEvent(new Event(IEventType.PLAYER_DISAPPEARED, m1501));
                }
            }
        });

        registerMessageHander(M1503.class, new IMessageHandler<M1503>() {
            @Override
            public void handle(M1503 m1503) {
                Player currentPlayer = getCurrentPlayer();
                if (m1503.playerId != currentPlayer.id) {
                    M1501 player = aroundPlayersMap.get(m1503.playerId);
                    if (player != null) {
                        Map info = player.info;
                        info.put("x", (short) m1503.x);
                        info.put("y", (short) m1503.y);
                    }
                }
            }
        });

        registerMessageHander(M1505.class, new IMessageHandler<M1505>() {
            @Override
            public void handle(M1505 m1505) {
                if (m1505.result == 0) {
                    aroundPlayersMap.clear();
                }
            }
        });

    }

    public List<M1501> currentAroundPlayers() {
        Point p1 = ContextUtil.getCurrentPlayer().currentLocation;
        Collection<M1501> players = aroundPlayersMap.values();
        List<Long> dirtyPlayerIds = new ArrayList();
        List<M1501> playerList = new ArrayList();
        if (p1 != null) {
            for (M1501 player : players) {
                Map info = player.info;
                Point p2 = new Point(((Short) info.get("x")).intValue(), ((Short) info.get("y")).intValue());
                double d = MathUtil.getDistance(p1, p2);
                if (d >= 13) {
                    dirtyPlayerIds.add((Long) info.get("id"));
                } else {
                    if ((Long) info.get("id") >= 10000000) {// 怪物

                    } else {// 玩家或物品等其它信息，具体该如何判断参考SpriteManager.reqCreatePlayer及ItemManager.onObjShowHandler
                        playerList.add(player);
                    }
                }
            }
        }

        for (Long it : dirtyPlayerIds) {
            aroundPlayersMap.remove(it);
        }

        return playerList;
    }

}

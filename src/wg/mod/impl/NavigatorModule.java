package wg.mod.impl;

import com.game.net.m15.M1503;
import com.game.net.m15.M1504;
import com.legend.fp1.Astar;
import com.legend.util.NavUtil;
import com.legend.util.SceneUtil;
import flash.geom.Point;
import org.frkd.method.ICallBack;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.PropertyUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IListener;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.pojo.Player;
import wg.pojo.PointCallBack;
import wg.pojo.map.SimpleSceneInfo;
import wg.util.NpcDataHolder;
import wg.util.SceneCacheUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class NavigatorModule extends BaseModule {

    private static Map<Long, Astar> astarMap = new HashMap<Long, Astar>();

    private volatile Point lastEndPoint;

    private volatile Point lastSyncPoint;

    private volatile ICallBack lastCallBack;

    private Long currentPlayerId;

    static {
        File mapDir = new File(PropertyUtil.getProperty("appdir") + File.separatorChar + "map");
        File[] mapFiles = mapDir.listFiles();
        for (File f : mapFiles) {
            String name = f.getName();
            if (name.endsWith(".svn"))
                continue;
            int dotPosition = name.indexOf('.');
            String sceneIdStr = name.substring(0, dotPosition);
            Long sceneId = Long.parseLong(sceneIdStr);
            SimpleSceneInfo info = SceneUtil.parseFile(f);
            SceneCacheUtil.cache(sceneId, info);
        }
    }

    private static Astar getAstar(Long sceneId) {
        Astar astar = astarMap.get(sceneId);
        if (astar == null)
            astar = new Astar();
        return astar;
    }

    public int getLoadSequence() {
        return Integer.MIN_VALUE + 4;
    }

    private class SceneLoader implements IListener {
        @Override
        public void onAction(Event evt) {
            loadScene((Long) evt.eventData);
        }
    }

    public void loadScene(long sceneId) {
        SimpleSceneInfo info = SceneCacheUtil.getSceneInfo(sceneId);

        NpcDataHolder.setBlock(sceneId, info.map);
        Astar astar = getAstar(sceneId);
        if (!astar.isInited()) {
            astar.init(info.map, info.gridWSize, info.gridHSize);
            astarMap.put(sceneId, astar);
        }
    }

    @Override
    public void init() {

        addEventListener(IEventType.PLAYER_INFO_INITED, new SceneLoader());

        addEventListener(IEventType.SCENE_CHANGED, new SceneLoader());

        addEventListener(IEventType.PLAYER_LOGINED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                Player player = (Player) evt.eventData;

                // cache current player id
                currentPlayerId = player.id;
            }
        });

        registerMessageHander(M1503.class, callBackWrapper);
        registerMessageHander(M1504.class, synchMoveHanlder);

    }

    public void go(Point end) {
        lastEndPoint = end;

        Player player = getCurrentPlayer();

        if(logger.isLoggable(Level.FINE))
            logger.fine("Ç°Íù×ø±ê£º("+end.x+","+end.y+")£¬³¡¾°±àºÅ£º"+player.sceneId);

        Point start = new Point(player.currentLocation.x, player.currentLocation.y);
        List<Point> pathPoints = getAstar(player.sceneId).seekRoad(start, end);
        if (pathPoints.size() > 0) {
            List<M1503> m1503List = NavUtil.trans2M1503(pathPoints);
            for (M1503 m : m1503List) {
                send(m);
                player.currentLocation = new java.awt.Point(m._x, m._y);
            }
        }
    }

    public void goAndDo(int x, int y, ICallBack callBack) {
        goAndDo(new Point(x, y), callBack);
    }

    public void goAndDo(final Point end, final ICallBack callBack) {

        reset();

        java.awt.Point currentLocation = getCurrentPlayer().currentLocation;
        if (end.x == currentLocation.x && end.y == currentLocation.y) {
            callBack.call(null);
            return;
        }
        lastCallBack = callBack;

        go(end);
    }

    public void doInSequence(final List<PointCallBack> pointCallBacks) {
        if (pointCallBacks.size() > 0) {
            final PointCallBack pcb = pointCallBacks.remove(0);
            goAndDo(pcb.end, new ICallBack() {
                @Override
                public Object call(Object param) {
                    pcb.callBack.call(null);
                    doInSequence(pointCallBacks);
                    return null;
                }
            });
        }

    }

    public void reset() {
        this.lastCallBack = null;
        this.lastEndPoint = null;
    }

    private IMessageHandler<M1504> synchMoveHanlder = new IMessageHandler<M1504>() {
        @Override
        public void handle(M1504 m1504) {
            if (lastEndPoint != null) {
                if (lastSyncPoint != null && lastSyncPoint.x == m1504.x && lastSyncPoint.y == m1504.y) {
                    return;
                }
                lastSyncPoint = new Point(m1504.x, m1504.y);
                go(lastEndPoint);
            }
        }
    };

    private IMessageHandler<M1503> callBackWrapper = new IMessageHandler<M1503>() {

        @Override
        public void handle(M1503 m1503) {
            if (lastEndPoint != null) {
                Point end = lastEndPoint;
                if (m1503.x == end.x && m1503.y == end.y && m1503.playerId == currentPlayerId) {
                    lastEndPoint = null;
                    if (lastCallBack != null)
                        lastCallBack.call(null);

                }
            }

        }
    };

}

package wg.mod.impl;

import com.game.cons.CaseType;
import com.game.net.m15.M1504;
import com.game.net.m15.M1505;
import com.game.net.m16.M1650;
import com.game.net.m16.M1651;
import com.game.net.m17.M1716;
import com.game.net.m17.M1732;
import com.game.net.m19.M1930;
import com.game.net.m24.M2401;
import com.game.net.m24.M2402;
import com.game.net.m24.M2403;
import com.game.net.m24.M2411;
import org.frkd.method.ICallBack;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.net.socket.protocol.IFlashMessageHandler;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.ThreadUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.pojo.PackageInfo;
import wg.pojo.PackageItem;
import wg.pojo.Player;
import wg.task.ITask;
import wg.util.CommandUtil;

import java.util.Arrays;
import java.util.logging.Level;

public class YaSongLingShouModule extends BaseModule  implements IExclusiveModule {

    private volatile boolean needAcceptTask41 = false;

    private int taskCount = 0;

    private M2402 m2402 = new M2402();

    private Task41Handler task41Handler;

    private ReliveHandler reliveHandler;

    @Override
    public boolean stop() {
        if (canStop && task41Handler != null) {
            getParserManager().removeMessageHandler(M2403.class, task41Handler);
            task41Handler = null;
        }
        return super.stop();
    }

    @Override
    public void run() {

        canStop = false;

        addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {

            @Override
            public void onAction(final Event packageLoadedEvt) {
                PackageItem[] items = getCurrentPlayer().packageInfo.getItems();
                boolean contain60Weapon = false;
                for (PackageItem item : items) {
                    if (item.type >= 20014 && item.type <= 20016) {
                        contain60Weapon = true;
                        break;
                    }
                }

                task41Handler = new Task41Handler(contain60Weapon);

                registerMessageHander(M2403.class, task41Handler);

                getCurrentGameSocket().send(new M2403());

                if (contain60Weapon)
                    store60LevelWeapon();

            }
        });

        CommandUtil.getPackageInfo(getCurrentGameSocket());
    }

    private void start() {
        Player player = getCurrentPlayer();
        boolean diffScene = player.sceneId != 12;
        startTask(diffScene);
    }

    private void store60LevelWeapon() {
        addEventListener(IEventType.STORE_LOADED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                final M1732 m = (M1732) evt.eventData;

                M1716 m1716 = new M1716();
                PackageInfo packageInfo = getCurrentPlayer().packageInfo;
                m1716._pos = CaseType.PACKAGE;
                m1716._new_pos = CaseType.STORE;

                boolean[] blanks = new boolean[120];
                Arrays.fill(blanks, true);
                for (PackageItem item : m.items) {
                    blanks[item.index] = false;
                }

                int lastIdx = 0;
                for (PackageItem packageItem : packageInfo.getItems()) {
                    if (packageItem.type >= 20014 && packageItem.type <= 20016) {
                        for (int i = lastIdx; i < blanks.length; i++) {
                            if (blanks[i]) {
                                lastIdx = i + 1;
                                m1716._index = packageItem.index;
                                m1716._new_index = i;
                                getCurrentGameSocket().send(m1716);
                                blanks[i] = false;
                                break;
                            }
                        }
                    }
                }

                if (needAcceptTask41) {
                    acceptTask41();
                    return;
                }

                boolean hasBlanks = false;
                for (int i = 0; i < blanks.length; i++) {
                    if (blanks[i]) {
                        hasBlanks = true;
                        break;
                    }
                }

                if (hasBlanks)
                    start();
                else startGj();
            }
        });

        CommandUtil.goZY(getCurrentGameSocket());
        M1732 m1732 = new M1732();
        m1732._pos = CaseType.STORE;
        getCurrentGameSocket().send(m1732);
    }

    private void acceptTask41() {
        M2411 m2411 = new M2411();
        m2411._sceneId = 12;
        m2411._x = 0x4C;
        m2411._y = 0x66;
        getCurrentGameSocket().send(m2411);

        registerMessageHander(M2401.class, new IFlashMessageHandler() {
            @Override
            public void handle(BasicMessage message) {

                ThreadUtil.startWithGlobal(new Runnable() {
                    @Override
                    public void run() {
                        ThreadUtil.sleep(3000);
                        m2402._npcId = 0x2f49;
                        m2402._type = 0x16;
                        getCurrentGameSocket().send(m2402);

                        if (task41Handler != null) {
                            getParserManager().removeMessageHandler(M2403.class, task41Handler);
                            task41Handler = null;
                        }

                        startGj();
                    }
                });

            }
        });

        M2401 m2401 = new M2401();
        m2401._npcId = 0x2f49;
        getCurrentGameSocket().send(m2401);
    }

    private class Task41Handler implements IMessageHandler<M2403> {

        private boolean contain60Weapon;

        private boolean beginCheck;

        private boolean started;

        public Task41Handler(boolean contain60Weapon) {
            this.contain60Weapon = contain60Weapon;
        }

        @Override
        public void handle(M2403 m2403) {
            if (ITask.t41.equals(m2403.taskTree))
                needAcceptTask41 = true;

            if (m2403.taskTree.contains("押镖") || m2403.taskTree.contains("主线")) {
                beginCheck = true;
            }

            if (beginCheck && !started) {
                if (needAcceptTask41 && !contain60Weapon) {
                    started = true;
                    acceptTask41();
                    return;
                }

                if (!contain60Weapon) {
                    logger.fine("背包中不包含60武器，直接开始押送任务");
                    started = true;
                    start();
                }
            }
        }
    }

    @Override
    public void init() {
        if (reliveHandler == null)
            reliveHandler = new ReliveHandler();

        registerMessageHander(M1650.class, reliveHandler);

    }

    private void handleRelive(M1650 m1650) {
        logger.fine("被挂了");

        final Player player = getCurrentPlayer();

        if (m1650.playerId == player.id) {
            registerMessageHander(M1651.class, new IFlashMessageHandler<M1651>() {
                @Override
                public void handle(M1651 m1651) {

                    if (m1651.sceneId != player.sceneId) {
                        dispachEvent(new Event(IEventType.SCENE_CHANGED, (long) m1651.sceneId));
                    }

                    player.currentLocation = new java.awt.Point(m1651.x, m1651.y);

                    logger.fine("已回城复活");
                    startTask(false);
                }
            });

            // 请求免费复活
            getCurrentGameSocket().send(new M1651());
        }

    }

    private void doYaSongTask() {
        final SocketX socket = getCurrentGameSocket();

        registerMessageHander(M1930.class, new IMessageHandler<M1930>() {
            @Override
            public void handle(M1930 m) {
                logger.finest(m.toString());
                String content = m.content;
                boolean isTarget = content.contains("玄角青龙");
                if (m.type == 10 && (isTarget || content.contains("金币不足"))) {
                    getParserManager().removeMessageHandler(M1930.class, this);
                    acceptYSTask();
                    return;
                }

                if (m.type == 10 && content.contains("请购买")) {
                    getParserManager().removeMessageHandler(M1930.class, this);
                    canStop = true;
                    return;
                }

                if (m.type == 10 && !isTarget) {// && success) {
                    logger.finest(m.content);
                    m2402._npcId = 0x2F49;// 请求刷新灵兽等级
                    m2402._type = 2;
                    socket.send(m2402);
                }
            }
        });

        m2402._npcId = 0x2F49;
        m2402._type = 0x2;// 请求刷新灵兽等级
        socket.send(m2402);
    }

    private void acceptYSTask() {
        final NavigatorModule nav = getData(NavigatorModule.class);
        final SocketX socket = getCurrentGameSocket();

        m2402._npcId = 0x2F49;// 请求接受押送任务
        m2402._type = 1;
        socket.send(m2402);

        nav.goAndDo(9, 7, new ICallBack() {
            @Override
            public Object call(Object param) {
                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {

                    @Override
                    public void onAction(Event evt) {
                        if (evt.eventData.equals(11L)) {

                            nav.goAndDo(15, 54, new ICallBack() {
                                @Override
                                public Object call(Object param) {
                                    m2402._npcId = 0x2B2C;// 交付灵兽
                                    m2402._type = 1;
                                    socket.send(m2402);

                                    if (logger.isLoggable(Level.FINE))
                                        logger.fine("到达押送灵兽终点");

                                    taskCount++;

                                    startTask(true);
                                    return null;
                                }
                            });


                        }

                    }
                });

                M1505 m1505 = new M1505();
                m1505._target = 11;
                m1505._x = 131;
                m1505._y = 24;
                m1505.mapId = 12;
                m1505.portX = 9;
                m1505.portY = 7;
                socket.send(m1505);
                return null;
            }
        });


    }

    /**
     * 领取灵兽
     */
    private void doClaimTask() {
        final SocketX socket = getCurrentGameSocket();

        final M2402 m2402 = new M2402();

        // 查询灵兽精魂值
        registerMessageHander(M2401.class, new IFlashMessageHandler<M2401>() {
            @Override
            public void handle(M2401 m2401) {
                String t = m2401.talkObj.get("talk").getAsString();
                final String key = "<font color=\"#00ff00\">";
                int p1 = t.indexOf(key);
                int p2 = t.indexOf('点');
                int val = Integer.parseInt(t.substring(p1 + key.length(), p2));

                if (logger.isLoggable(Level.FINE))
                    logger.fine("截取到精魂值：" + val);

                if (val >= 5000)
                    m2402._type = 0x2E;// 领取战神
                else
                    m2402._type = 0x2C;// 领取玄角青龙

                socket.send(m2402);

                canStop = true;

                startGj();
            }
        });
        m2402._npcId = 0x2F49;
        m2402._type = 0x4;
        socket.send(m2402);

    }

    private void startGj() {

        CommandUtil.goCZ(getCurrentGameSocket());

        startExclusiveModule(XiaoHaoGuaJiModule.class);

    }

    private void startTask(boolean diffScene) {

        final SocketX socket = getCurrentGameSocket();

        logger.finest("开始灵兽押送任务.");

        if (diffScene) {
            addEventListener(IEventType.SCENE_CHANGED,
                    new IOnceListener() {

                        @Override
                        public void onAction(Event evt) {
                            startTask();
                        }
                    });
        } else {
            registerMessageHander(M1504.class,
                    new IFlashMessageHandler<M1504>() {
                        @Override
                        public void handle(M1504 m) {
                            startTask();
                        }
                    });
        }

        CommandUtil.goBJ(socket);

    }

    private void startTask() {

        final NavigatorModule nav = getData(NavigatorModule.class);
        nav.goAndDo(76, 102, new ICallBack() {
            @Override
            public Object call(Object param) {
                if (taskCount == 2) {
                    doClaimTask();
                    return null;
                }
                if (taskCount > 2) {// should never happen!
                    globalLogger.severe("taskCount>=2，" + getBasicLoginInfo());
                    exit();
                    return null;
                }

                doYaSongTask();

                return null;
            }
        });


    }

    private class ReliveHandler implements IMessageHandler<M1650> {
        @Override
        public void handle(M1650 message) {
            handleRelive(message);
        }
    }

}

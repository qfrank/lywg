package wg.mod.impl;

import com.game.cons.CaseType;
import com.game.net.m15.M1501;
import com.game.net.m15.M1504;
import com.game.net.m16.M1650;
import com.game.net.m16.M1651;
import com.game.net.m16.M1680;
import com.game.net.m16.M1681;
import com.game.net.m17.*;
import com.game.net.m24.M2402;
import com.game.net.m24.M2409;
import com.legend.util.SceneUtil;
import org.frkd.method.ICallBack;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.net.socket.protocol.IFlashMessageHandler;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.ThreadUtil;
import wg.bgthread.LocationWatcher;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IListener;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.pojo.Account;
import wg.pojo.PackageInfo;
import wg.pojo.PackageItem;
import wg.pojo.Player;
import wg.pojo.config.RobotParam;
import wg.util.CommandUtil;
import wg.util.IContextCons;
import wg.util.RobotParamHolder;

import java.util.Arrays;
import java.util.Map;

/**
 * 小号挂机
 *
 *
 */
public class XiaoHaoGuaJiModule extends BaseModule implements IExclusiveModule{

    private static LocationWatcher locationWatcher = new LocationWatcher();

    private M1680 defaultRobotParam1680;

    public void requestStartRobotMode() {
        Boolean robotMode = getData(IContextCons.robot_mode);
        if (robotMode == null || !robotMode)
            return;

        if (defaultRobotParam1680 != null && getCurrentPlayer().sceneId != 12L)
            getCurrentGameSocket().send(defaultRobotParam1680);
    }

    @Override
    public void init() {

        if (!locationWatcher.isAlive())
            locationWatcher.start();

        final Account acc = getCurrentAccount();

        registerMessageHander(M1680.class, new IMessageHandler<M1680>() {
            @Override
            public void handle(M1680 m) {
                if (m.result != 0) {
                    logger.severe(acc.getUserId() + "开启挂机失败，重试..");
                    SocketX socket = getCurrentGameSocket();
                    socket.send(defaultRobotParam1680);
                } else {
                    logger.info(acc.getUserId() + "开启挂机成功.");
                    setData(IContextCons.robot_mode, true);
                }
            }
        });

        registerMessageHander(M1681.class, new IMessageHandler<M1681>() {
            @Override
            public void handle(M1681 m) {
                if (m.result != 0) {
                    logger.severe(acc.getUserId() + "取消挂机失败.");
                } else {
                    setData(IContextCons.robot_mode, false);
                    logger.info(acc.getUserId() + "取消挂机成功.");
                    dispachEvent(new Event(IEventType.ROBOT_MODE_CANCELED, null));
                }
            }
        });

        registerMessageHander(M1711.class, new IMessageHandler<M1711>() {
            public void handle(final M1711 m1711) {
                if (m1711.pos == CaseType.PACKAGE && m1711.item != null) {
                    final SocketX socket = getCurrentGameSocket();
                    int type = m1711.item.type;
                    if (type >= 20014 && type <= 20016) {// 60武器
                        store60LevelWeapon(m1711);
                    }
                    // 回收掉50级武器和衣服、60级武器 （|| (type >= 20014 && type <= 20016)）
                    else if ((type >= 20011 && type <= 20013) || (type >= 30018 && type <= 30022)) {
                        addEventListener(IEventType.ROBOT_MODE_CANCELED, new IOnceListener() {

                            @Override
                            public void onAction(Event evt) {

                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {

                                    @Override
                                    public void onAction(Event evt) {
                                        NavigatorModule nav = getData(NavigatorModule.class);
                                        nav.goAndDo(124, 120, new ICallBack() {
                                            @Override
                                            public Object call(Object param) {

                                                M2402 m2402 = new M2402();
                                                m2402._npcId = 0x2EEC;
                                                m2402._type = 0x044D;
                                                socket.send(m2402);

                                                m2402._type = 0x0457;
                                                socket.send(m2402);

                                                m2402._type = 0x0835;
                                                socket.send(m2402);

                                                XiaoHaoGuaJiModule.this.run();
                                                return null;
                                            }
                                        });
                                    }
                                });

                                M2409 m2409 = new M2409();
                                m2409._uiId = 0x09;
                                m2409._funId = 0x68;
                                socket.send(m2409);

                            }
                        });

                        stop();

                    }

                    // 50级的垃圾，诛魔手镯、玄冥手镯、霸天手镯、灵韵碎片、时空碎片
                    else if ((type >= 60010 && type <= 60012) || (type == 10142 || type == 10143 || type == 10144 || type == 10145) || type == 10178) {
                        // 直接卖掉
                        M1722 m1722 = new M1722();
                        m1722._id = m1711.item.id;
                        m1722._num = 1;
                        socket.send(m1722);
                        String user = "账户：" + getCurrentAccount().getUserId() + " 玩家：" + getCurrentPlayer().roleName;

                        logger.finest(user + " 卖掉" + ItemModule.getItemInfo(type).name);
                        ThreadUtil.sleep(500);
                    }

                    // 吃掉招财进宝(小中大)力、魂力丹（普通高级超级）、神石碎片（小中大）
                    else if ((type >= 10300 && type <= 10302) || (type >= 10072 && type <= 10074) || (type >= 10117 && type <= 10119) || (type >= 10122 && type <= 10124)) {
                        M1728 m1728 = new M1728();
                        m1728._id = m1711.item.id;
                        socket.send(m1728);
                        String user = "账户：" + getCurrentAccount().getUserId() + " 玩家：" + getCurrentPlayer().roleName;
                        logger.finest(user + " 吃掉" + ItemModule.getItemInfo(type).name);
                        ThreadUtil.sleep(1000);
                    }

                }
            }

            ;
        });

        addEventListener(IEventType.NEW_PLAYER_APPEARED, new IListener() {

            private final int randomStone = 10055;

            private boolean usedRandomStone;

            @Override
            public void onAction(Event evt) {
                Player player = getCurrentPlayer();
                Boolean robotMode = getData(IContextCons.robot_mode);
                if (robotMode != null && robotMode) {

                    boolean shouldEscape = false;

                    if (!SceneUtil.isSafe(player.sceneId)) {
                        M1501 m1501 = (M1501) evt.eventData;
                        Map info = m1501.info;
                        String faction = null;
                        if (info != null) {
                            faction = (String) info.get("faction");
                        }
                        if (faction != null) {
                            faction = faction.trim();
                            if (faction.length() > 0) {
                                shouldEscape = true;
                            }
                        }
                    }

                    AroundInfoModule aroundInfoModule = getData(AroundInfoModule.class);
                    RobotParam rp = getRobotParam();
                    String user = "账户：" + getCurrentAccount().getUserId() + " 玩家：" + player.roleName;
                    if (aroundInfoModule.currentAroundPlayers().size() >= rp.maxAroundPlayerNum || shouldEscape) {
                        SocketX socket = getCurrentGameSocket();
                        PackageInfo pi = player.packageInfo;
                        if (pi.inited && !usedRandomStone) {
                            for (PackageItem item : pi.getItems()) {
                                if (item != null && item.type == randomStone) {
                                    registerMessageHander(M1504.class, new IFlashMessageHandler() {
                                        @Override
                                        public void handle(BasicMessage message) {
                                            usedRandomStone = false;
                                        }
                                    });
                                    M1728 m1728 = new M1728();
                                    m1728._id = item.id;
                                    socket.send(m1728);
                                    item.num--;
                                    usedRandomStone = true;
                                    logger.finest(user + " 成功地使用了背包里的随机石.");
                                    break;
                                }

                            }

                            if (!usedRandomStone) {
                                registerMessageHander(M1504.class, new IFlashMessageHandler() {
                                    @Override
                                    public void handle(BasicMessage message) {
                                        usedRandomStone = false;
                                    }
                                });
                                ItemModule itemModule = getData(ItemModule.class);
                                itemModule.buyAndUsingItem(0x47, 1);
                                usedRandomStone = true;
                                logger.finest(user + " 成功地购买并使用了随机石.");
                            }

                        }
                    }
                }
            }
        });


        registerMessageHander(M1650.class, new IMessageHandler<M1650>() {
            @Override
            public void handle(M1650 m1650) {
                final Player player = getCurrentPlayer();
                if (m1650.playerId == player.id) {
                    logger.fine("被挂了");

                    registerMessageHander(M1651.class, new IFlashMessageHandler<M1651>() {
                        @Override
                        public void handle(M1651 m1651) {
                            if (m1651.sceneId != player.sceneId) {
                                dispachEvent(new Event(IEventType.SCENE_CHANGED, (long) m1651.sceneId));
                                player.currentLocation = new java.awt.Point(m1651.x, m1651.y);
                                LocationWatcher.refreshLocation(getCurrentAccount().getUserId());
                            }
                            logger.fine("已回城复活");
                            final RobotParam rp = getRobotParam();
                            if (rp != null)
                                startRobotMode(rp.mapId);

                        }
                    });

                    // 请求免费复活
                    getCurrentGameSocket().send(new M1651());
                }


            }
        });


    }

    public void run() {

        canStop = false;

        if (defaultRobotParam1680 == null) {
            Player player = getCurrentPlayer();
            byte job = player.job;
            defaultRobotParam1680 = RobotParamHolder.getRobotParam(job);
        }

        final RobotParam rp = getRobotParam();
        if (rp != null) {
            final SocketX socket = getCurrentGameSocket();

            // 领取在线奖励
            for (int i = 1; i <= 7; i++) {
                M2409 m2409 = new M2409();
                m2409._uiId = 2;
                m2409._funId = i;
                socket.send(m2409);
                ThreadUtil.sleep(500);
            }

            addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {

                @Override
                public void onAction(Event evt) {
                    cleanPackage();
                    startRobotMode(rp.mapId);
                }
            });

            CommandUtil.getPackageInfo(getCurrentGameSocket());

        }
    }

    private void startRobotMode(int type) {
        final SocketX socket = getCurrentGameSocket();

        // 去挂机地图
        CommandUtil.go2Scene(socket, type);

        //socket.send(new M1688());
        socket.send(defaultRobotParam1680);
    }

    private void cleanPackage() {
        PackageInfo pi = getCurrentPlayer().packageInfo;
        SocketX socket = getCurrentGameSocket();
        for (PackageItem item : pi.getItems()) {
            int type = item.type;
            if ((type >= 60010 && type <= 60012) || (type == 10142 || type == 10143 || type == 10144 || type == 10145) || type == 10178) {
                // 直接卖掉
                M1722 m1722 = new M1722();
                m1722._id = item.id;
                m1722._num = item.num;
                socket.send(m1722);
                String user = "账户：" + getCurrentAccount().getUserId() + " 玩家：" + getCurrentPlayer().roleName;
                logger.info(user + "清理包裹-->卖掉" + ItemModule.getItemInfo(type).name);
                ThreadUtil.sleep(500);
            }
        }
    }

    public boolean stop() {
        getCurrentGameSocket().send(new M1681());
        canStop = true;
        return super.stop();
    }

    /**
     * 去土城庄园NPC
     */
    private void goZY() {
        CommandUtil.goZY(getCurrentGameSocket());
    }

    private void store60LevelWeapon(final M1711 m1711) {
        addEventListener(IEventType.ROBOT_MODE_CANCELED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                goZY();
                addEventListener(IEventType.STORE_LOADED, new IOnceListener() {
                    @Override
                    public void onAction(Event evt) {

                        final M1732 m1732 = (M1732) evt.eventData;
                        boolean[] blanks = new boolean[PackageInfo.storeLen];
                        Arrays.fill(blanks, true);
                        for (PackageItem item : m1732.items) {
                            blanks[item.index] = false;
                        }

                        int blankIdx = -1;
                        for (int i = 0; i < blanks.length; i++) {
                            if (blanks[i]) {
                                blankIdx = i;
                                break;
                            }
                        }
                        if (blankIdx != -1) {
                            M1716 m1716 = new M1716();
                            m1716._pos = CaseType.PACKAGE;
                            m1716._new_pos = CaseType.STORE;
                            m1716._index = m1711.index;
                            m1716._new_index = blankIdx;

                            getCurrentGameSocket().send(m1716);
                        }

                        final RobotParam rp = getRobotParam();
                        if (rp != null)
                            startRobotMode(rp.mapId);
                    }
                });

                M1732 m1732 = new M1732();
                m1732._pos = CaseType.STORE;
                getCurrentGameSocket().send(m1732);

            }
        });

        stop();
    }

}

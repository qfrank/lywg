package wg.mod.impl;

import com.game.legend.net.HeartBeat;
import com.game.net.m12.M1200;
import com.game.net.m12.M1201;
import com.game.net.m13.*;
import com.game.net.m14.M1400;
import com.game.net.m14.M1401;
import com.game.net.m14.M1403;
import com.game.net.m14.M1420;
import com.game.net.m15.M1500;
import com.game.net.m15.M1514;
import com.game.net.m15.M1530;
import com.game.net.m16.M1652;
import com.game.net.m23.M2320;
import com.game.net.m24.M2403;
import com.game.net.m24.M2409;
import com.game.net.m24.M2418;
import org.frkd.net.socket.ISocketErrorHandler;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.IFlashMessageHandler;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.Global;
import org.frkd.util.ThreadUtil;
import wg.Main;
import wg.event.Event;
import wg.event.IEventType;
import wg.mod.BaseModule;
import wg.mod.ModuleManager;
import wg.pojo.Account;
import wg.pojo.GateWayConnectionInfo;
import wg.pojo.Player;
import wg.pojo.Server;
import wg.pojo.config.AccountSchema;
import wg.pojo.config.ModuleToggle;
import wg.util.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class LoginModule extends BaseModule {

    private static AtomicInteger totalCreatedRole = new AtomicInteger(0);

    private static AtomicInteger totalDeletedRole = new AtomicInteger(0);

    private SocketX gateWaySocket;

    private SocketX gameSocket;

    private HeartBeat heartBeat;

    private M1310 pingpong = new M1310();

    private boolean newRole;

    public int getLoadSequence() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void destroy() {
        if (heartBeat != null) {
            heartBeat.stop();
        }
    }

    public void loginAgain() {

        ModuleManager.destroyModules();

        int server = getCurrentServer();
        int avatarIndex = getCurrentAvatarIndex();
        Account acc = getCurrentAccount();
        AccountSchema accountSchema = getSchema();
        ContextUtil.unBindContext(acc.getUserId());
        Global.getLocalMap().clear();

        ThreadUtil.sleep(5000L);
        Main.loginAccount(acc, server, accountSchema, avatarIndex);
    }

    public void loginAgain(boolean needCloseGameSocket) {
        if (needCloseGameSocket && gameSocket != null) {
            gameSocket.closeQuietly();
        }
        loginAgain();
    }

    private class GateWayErrorHandler implements ISocketErrorHandler {
        @Override
        public void handle(Exception error) {

            String info = "Captured gateway socket error." + getBasicLoginInfo();
            logAll(info, Level.WARNING, error);
            if (gateWaySocket != null) {
                gateWaySocket.closeQuietly();
            }

            if (Global.getLocalMap().isEmpty()) {
                return;
            }

            loginAgain();
        }
    }

    private class GameSocketErrorHandler implements ISocketErrorHandler {
        @Override
        public void handle(Exception error) {

            if (gameSocket != null) {
                gameSocket.closeQuietly();
            }

            if (Global.getLocalMap().isEmpty()) {
                return;
            }

            String info = "Captured game socket error." + getBasicLoginInfo();
            logAll(info, Level.WARNING, error);

            loginAgain();
        }
    }

    private void sendLoginCmd() {
        gateWaySocket.closeQuietly();

        String gameIp = Global.getData("game_ip");
        Integer port = Global.getData("game_port");
        gameSocket = new SocketX(gameIp, port.intValue(), super.getParserManager());
        gameSocket.getReader().setNeedDecode(true);

        while (!gameSocket.connect2Server()) {
            ThreadUtil.sleep(5000);
        }
        gameSocket.setErrorHandler(new GameSocketErrorHandler());
        gameSocket.listen();
        logger.fine("启动Game SocketX监听线程");

        if (heartBeat == null) {
            FlashSimulator.startFlash();
            heartBeat = new HeartBeat();
            heartBeat.setHandler(new Runnable() {
                public void run() {
                    pingpong._ping = FlashSimulator.getTimer();
                    gameSocket.send(pingpong);
                    logger.finest("发送HeartBeat：" + pingpong);
                }
            });
        }

        Global.setData(IContextCons.game_socket, gameSocket);

        Account acc = getCurrentAccount();
        Global.setData("user_name", acc.getUserId());
        Global.setData("user_pw", acc.getEncrptString());
        M1300 m1300 = new M1300();
        m1300._accId = acc.getUserId();
        gameSocket.send(m1300);

    }

    @Override
    public void init() {
        logger.fine("初始化登录模块.");
        registerMessageHander(M1200.class, new IMessageHandler<M1200>() {
            @Override
            public void handle(M1200 m1200) {
                logger.fine("m1200.result:" + m1200.result);
                if (m1200.result == 0) {
                    M1201 m1201 = new M1201();
                    String server_id = GateWayConnectionInfo.server_id;
                    if (server_id != null) {
                        m1201._id = server_id;
                    } else {
                        m1201._id = "server_id";
                    }
                    logger.fine("发送m1201,_id:" + m1201._id);
                    gateWaySocket.send(m1201);
                } else {
                    if (m1200.result == 1) {
                        logger.severe("IP访问受限，账号：" + getCurrentAccount().getUserId());
                    } else {
                        if (m1200.result == 3) {
                            logger.severe("账户永久冻结，账号：" + getCurrentAccount().getUserId());
                        } else {
                            if (m1200.result == 4) {
                                logger.severe("账号不可行，用户名或密码错误 ，账号：" + getCurrentAccount().getUserId());
                            } else {
                                if (m1200.result == 12) {
                                    logger.severe("账户被冻结 ，账号：" + getCurrentAccount().getUserId());
                                } else {
                                    logger.severe("未知错误，账号：" + getCurrentAccount().getUserId());
                                }
                            }
                        }
                    }
                }
                if (m1200.result_1 == 1) {
                    Global.setData("firstInGame", true);
                } else {
                    Global.setData("firstInGame", false);
                }
            }
        });

        registerMessageHander(M1201.class, new IMessageHandler<M1201>() {
            @Override
            public void handle(M1201 m1201) {
                logger.fine("获取游戏服务器地址请求结果：" + m1201.result);
                if (m1201.result == 0) {
                    String[] ipAndPortArr = m1201.logic.split(":");
                    logger.fine(Arrays.toString(ipAndPortArr));
                    String gameIp = ipAndPortArr[0].trim();
                    Integer gamePort = Integer.parseInt(ipAndPortArr[1].trim());
                    Global.setData("game_ip", gameIp);
                    Global.setData("game_port", gamePort);

                    logger.info("正在连接游戏服务器：" + gameIp + ":" + gamePort);
                    sendLoginCmd();

                } else {
                    if (m1201.result == 1) {
                        logger.severe("该服务器未注册使用，账号：" + getCurrentAccount().getUserId());
                    } else {
                        logger.severe("未知错误，账号：" + getCurrentAccount().getUserId());
                    }
                }
            }
        });

        registerMessageHander(M1300.class, new IMessageHandler<M1300>() {
            @Override
            public void handle(M1300 m1300) {
                long result = m1300.result;
                if (result == 0) {
                    logger.info("M1300登录成功，账号：" + getCurrentAccount().getUserId());
                    gameSocket.send(new M1301());

                    if (!NpcDataHolder.isInited()) {
                        // fetch npc data
                        gameSocket.send(new M1530());
                    }
                } else if (result == 1) {
                    logger.severe("M1300 验证出错，账号：" + getCurrentAccount().getUserId());
                } else if (result == 2) {
                    logger.info("M1300 之前已登录过，踢出以前登录的用户，尝试重新登录，账号：" + getCurrentAccount().getUserId());
                    ThreadUtil.sleep(500L);
                    sendLoginCmd();
                } else if (result == 3) {
                    logger.severe("M1300 游戏人数达到上限，账号：" + getCurrentAccount().getUserId());
                } else if (result == 4) {
                    logger.severe("M1300 该账号在其他终端同时登陆，请注意账号安全，账号：" + getCurrentAccount().getUserId());
                } else if (result == 5) {
                    logger.severe("M1300 系统检测到您正在使用非法软件，已被强制下线，账号：" + getCurrentAccount().getUserId());
                } else {
                    logger.severe("M1300 未知错误，账号：" + getCurrentAccount().getUserId());
                    ThreadUtil.sleep(500L);
                    sendLoginCmd();
                }

            }
        });

        registerMessageHander(M1304.class, new IMessageHandler<M1304>() {
            @Override
            public void handle(M1304 message) {
                if (message.result == 5) {
                    String info = "账号【" + getCurrentAccount().getUserId() + "】被封！";
                    logAll(info, Level.WARNING);
                    deleteRoleAndCreateNew(getCurrentPlayer(), getCurrentAccount());
//					try {
//						FileWriter fw = new FileWriter("d:/被封账号.txt", true);
//						fw.write(getCurrentAccount().getUserId()+"\n");
//						fw.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
                    return;
                }

                if (message.result == 0) {

                    gameSocket.send(new M2418(0));
                    gameSocket.send(new M1400());
                    gameSocket.send(new M1401());

                    CommandUtil.getPackageInfo(gameSocket);

                    gameSocket.send(new M1500());
                    gameSocket.send(new M1652());
                    gameSocket.send(new M1514());
                    gameSocket.send(new M2320());

                    // 获取缓冲信息
                    gameSocket.send(new M1420());

                    // 获取装备信息
                    M1403 m1403 = new M1403();
                    m1403._id = 0;
                    m1403.flg = 1;
                    gameSocket.send(m1403);

                    // 获取任务信息
                    gameSocket.send(new M2403());

                    // 领取离线经验奖励
                    M2409 m2409 = new M2409();
                    m2409._uiId = 0x6;
                    m2409._funId = 0x1;
                    gameSocket.send(m2409);

					/*// 降3级换飞升经验
                    M2409 m2409 = new M2409();
					m2409._uiId = 0x11;
					m2409._funId = 0x02;
					gameSocket.send(m2409);
					gameSocket.send(m2409);
					gameSocket.send(m2409);
					m2409._funId = 0x03;
					// 确定飞升
					gameSocket.send(m2409);*/

                    dispachEvent(new Event(IEventType.PLAYER_LOGINED, getCurrentPlayer()));
                }
            }
        });

        registerMessageHander(M1301.class, new IMessageHandler<M1301>() {
            @Override
            public void handle(M1301 m) {
                Account acc = getCurrentAccount();
                int avatarIndex = getData(IContextCons.selected_avatar_index);

                /*System.out.println(m.avatars);
                for (int i = 0; i < m.avatars.size(); i++) {
                    Map row = m.avatars.get(i);
                    if(row.get("id").equals(1793776L)){
                        avatarIndex = i;
                    }
                }*/

                ModuleToggle toggle = getModuleToggle();
                if (toggle == ModuleToggle.tr) {
                    if (avatarIndex >= m.avatars.size() && avatarIndex < 6) {
                        logger.info("角色下标大于角色列表最大下标值，现创建新角色..");
                        autoCreateNewRole();
                        return;
                    }
                }

                Map avatar = null;
                if (avatarIndex < m.avatars.size()) {
                    avatar = m.avatars.get(avatarIndex);
                }

                if (avatar == null) {
                    logger.info("没有角色可选择，现退出！角色下标值：[" + avatarIndex + "]," + getBasicLoginInfo());
                    ContextUtil.exit(acc.getUserId());
                    return;
                }

                // 绑定角色信息
                Player player = new Player();
                player.id = (Long) avatar.get("id");
                player.roleName = (String) avatar.get("name");
                player.level = ((Short) avatar.get("level")).byteValue();
                player.job = ((Short) avatar.get("job")).byteValue();

                if (toggle == ModuleToggle.tr && player.level < 60 && !newRole) {
                    deleteRoleAndCreateNew(player, acc);
                    return;
                }

                Global.setData(IContextCons.current_player, player);

                if (logger.isLoggable(Level.INFO))
                    logger.info("登录玩家：" + player.roleName + "，等级【" + player.level + "】，角色ID：" + player.id);

//                try {
//                    FileWriter fw = new FileWriter("/Users/Franklyn/账号角色名列表.txt", true);
//                    fw.write(getCurrentAccount().getUserId()+","+getCurrentAccount().getPassword()+","+player.roleName+"\n");
//                    fw.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                // Special for 1870 server
                String code = acc.getSecurityCode();
                if (getCurrentServer() == 1870 && avatarIndex != 0)
                    code = "";

                gameSocket.send(new M1304(player.id, code));

            }
        });

        registerMessageHander(M1310.class, new IMessageHandler<M1310>() {
            @Override
            public void handle(M1310 message) {
                logger.finest("收到HeartBeat：" + message.toString());
                pingpong._latency = (FlashSimulator.getTimer() - pingpong._ping);
            }
        });

    }

    private void deleteRoleAndCreateNew(Player player, Account acc) {
        M1303 m1303 = new M1303();
        m1303._id = player.id.intValue();
        m1303._password = "";
        getCurrentGameSocket().send(m1303);

        String info = "删除角色【" + player.roleName + "】，账号【" + acc.getUserId() + "】，等级【" + player.level + "】，服务器【" + getCurrentServer() + "】";
        logAll(info, Level.INFO);

        int totalDeleted = totalDeletedRole.addAndGet(1);
        if (globalLogger.isLoggable(Level.FINE))
            globalLogger.fine(getCurrentServer() + "区累计创建角色：" + totalCreatedRole.get() + "，累计删除角色：" + totalDeleted);

        autoCreateNewRole();
    }

    private void autoCreateNewRole() {
        registerMessageHander(M1305.class, new IFlashMessageHandler<M1305>() {
            @Override
            public void handle(M1305 m1305) {
                registerMessageHander(M1302.class, new IFlashMessageHandler<M1302>() {
                    @Override
                    public void handle(M1302 m1302) {
                        if (m1302.result == 0) {
                            newRole = true;
                            getCurrentGameSocket().send(new M1301());
                            logger.info("自动创建角色成功，账号：" + getCurrentAccount().getUserId());

                            int totalCreated = totalCreatedRole.addAndGet(1);
                            if (globalLogger.isLoggable(Level.FINE))
                                globalLogger.fine(getCurrentServer() + "区累计创建角色：" + totalCreated + "，累计删除角色：" + totalDeletedRole.get());

                        } else {
                            logger.warning("自动创建角色失败，账号：" + getCurrentAccount().getUserId());
                        }
                    }
                });
                M1302 m1302 = new M1302();
                m1302._roleName = m1305.randomName.trim();
                m1302._job = 3;

                if (logger.isLoggable(Level.INFO))
                    logger.info("获得随机角色名：" + m1302._roleName);

                getCurrentGameSocket().send(m1302);
            }
        });
        getCurrentGameSocket().send(new M1305());
    }

    @Override
    public void run() {
        Account acc = getCurrentAccount();
        int serverSeq = getCurrentServer();
        Server server = ServerConfigUtil.getServer(serverSeq);
        gateWaySocket = new SocketX(server.serverIp, server.port, super.getParserManager());
        while (!gateWaySocket.connect2Server()) {
            ThreadUtil.sleep(2000);
        }
        gateWaySocket.setErrorHandler(new GateWayErrorHandler());
        gateWaySocket.listen();
        logger.fine("启动GateWay SocketX监听线程");
        M1200 m1200 = new M1200();
        m1200._accId = acc.getUserId();
        m1200._key = acc.getEncrptString();
        m1200.src = GateWayConnectionInfo.source;
        m1200.sub_src = GateWayConnectionInfo.sub_source;
        m1200.flags = Integer.parseInt(GateWayConnectionInfo.flags);
        m1200.time = Integer.parseInt(GateWayConnectionInfo.time);
        gateWaySocket.send(m1200);
    }

}

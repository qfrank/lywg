package wg.mod.impl;

import com.game.net.m15.M1504;
import com.game.net.m16.*;
import com.game.net.m17.M1728;
import com.game.net.m18.M1807;
import com.game.net.m19.M1930;
import com.game.net.m21.M2101;
import com.game.net.m21.M2103;
import com.game.net.m21.M2110;
import com.game.net.m24.M2401;
import com.game.net.m24.M2402;
import com.game.net.m24.M2403;
import com.game.net.m24.M2411;
import flash.geom.Point;
import org.frkd.method.ICallBack;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.IFlashMessageHandler;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.ThreadUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.mod.ModuleManager;
import wg.pojo.PackageInfo;
import wg.pojo.PackageItem;
import wg.pojo.Player;
import wg.pojo.PointCallBack;
import wg.task.*;
import wg.util.CommandUtil;
import wg.util.ContextUtil;
import wg.util.IContextCons;
import wg.util.RobotParamHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * User: Franklyn <br/>
 * Date: 14-11-27<br/>
 * Time: 上午12:06<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class TrainingModule extends BaseModule implements IExclusiveModule {

    private Map<String, ITaskExecutor> executorMap = new ExecutorMap();

    private NavigatorModule navigatorModule = (NavigatorModule) ModuleManager.getModule(NavigatorModule.class);

    private static volatile Long LAST_TEAM_LEADER_PLAYER_ID;

    private static volatile Long TOTAL_CREATED_ROLE_NUM = 0L;

    private static AtomicInteger TOTAL_FINISHED_TASK_ROLE_NUM = new AtomicInteger(0);

    private M1680 requestRobotMode;

    /**
     * 最大天机任务次数/天
     */
    private final static int MAX_TJ_TASK_COUNT = 12;

    /**
     * 最大封魔任务次数/天
     */
    private int MAX_FM_TASK_COUNT = 3;

    private boolean isFMCountInited = false;

    /**
     * 当前已完成封魔次数
     */
    private int currentFinishedFMTaskCount = 0;

    /**
     * 当前完成天机任务次数
     */
    private volatile int currentFinishedTJTaskCount = 0;

    private volatile boolean shouldGoOnTJ = true;

    private volatile long lastFinishTJTime;

    private volatile boolean isTJFinished = true;

    private volatile Runnable callBackAfterTJFinished;

    private M2402 m2402 = new M2402();

    private M2411 m2411 = new M2411();

    private static final Point[] bowManPoints = {
            new Point(13, 59),
            new Point(16, 56),
            new Point(19, 53),
            new Point(22, 50),
            new Point(26, 46),
            new Point(30, 44),
            new Point(34, 45),
            new Point(38, 45),
            new Point(42, 45),
            new Point(45, 45),
            new Point(48, 47),
            new Point(49, 48),
            new Point(53, 52),
            new Point(56, 54),
            new Point(61, 61),
            new Point(61, 65)
    };

    @Override
    public void run() {

        // whatever try disband previous team
        send(new M2110());

        Player player = getCurrentPlayer();
        Byte level = player.level;
        if (level == 1) {
            synchronized (TOTAL_CREATED_ROLE_NUM) {
                if (TOTAL_CREATED_ROLE_NUM % 15 == 0) {
                    LAST_TEAM_LEADER_PLAYER_ID = player.id;

                    // create team
                    send(new M2101());

                } else {
                    // join team
                    M2103 m2103 = new M2103();
                    m2103._id = LAST_TEAM_LEADER_PLAYER_ID.intValue();
                    send(m2103);
                }

                ++TOTAL_CREATED_ROLE_NUM;

            }
        }

        requestRobotMode = RobotParamHolder.getRobotParam(player.job);

        if (level >= 50 && level <= 80) {
            logger.fine("开始做封魔任务..");
            doFMTask();
        }

        if (level > 80) {
            loginNextRole();
        }
    }

    @Override
    public void init() {

        initExecutor();

        registerMessageHander(M1930.class, new IMessageHandler<M1930>() {
            @Override
            public void handle(M1930 m1930) {
                if (m1930.type == 110 && m1930.content.startsWith("降妖除魔任务已完成,回城领取任务奖励吧")) {
                    logger.fine("完成天机任务.");

                    makeSureStoppedRobot(new ICallBack() {
                        @Override
                        public Object call(Object param) {
                            finishTJ();
                            currentFinishedTJTaskCount++;

                            if (callBackAfterTJFinished != null) {
                                callBackAfterTJFinished.run();
                            }

                            if (currentFinishedTJTaskCount < MAX_TJ_TASK_COUNT && shouldGoOnTJ) {
                                acceptTJ();
                            }

                            return null;
                        }
                    });
                }
            }
        });

        registerMessageHander(M1681.class, new IMessageHandler<M1681>() {
            @Override
            public void handle(M1681 m1681) {
                if (m1681.result != 0) {
                    logger.warning(getCurrentAccount().getUserId() + "取消挂机失败.");
                } else {
                    logger.fine(getCurrentAccount().getUserId() + "取消挂机成功.");
                    setData(IContextCons.robot_mode, false);
                }

            }
        });

        registerMessageHander(M1680.class, new IMessageHandler<M1680>() {
            @Override
            public void handle(M1680 m) {
                if (m.result != 0) {
                    logger.warning(getCurrentAccount().getUserId() + "开启挂机失败，重试..");
                    SocketX socket = getCurrentGameSocket();
                    socket.send(new M1688());
                } else {
                    logger.fine(getCurrentAccount().getUserId() + "开启挂机成功.");
                    setData(IContextCons.robot_mode, true);
                }
            }
        });

        registerMessageHander(M1643.class, new IMessageHandler<M1643>() {
            @Override
            public void handle(M1643 m1643) {
                Player player = getCurrentPlayer();
                if (m1643.targetId == player.id && player.level > 40 && m1643.currHP < 1500) {
                    if (isRobotMode() && player.job == 3) {
                        M1630 m1630 = new M1630();
                        m1630._skillId = 0x12;
                        if (logger.isLoggable(Level.FINE))
                            logger.fine(player.roleName + "：血量小于1500，隐身并停止挂机");

                        send(new M1681());
                        send(m1630);

                    }
                }
            }
        });

        registerMessageHander(M1650.class, new IMessageHandler<M1650>() {
            @Override
            public void handle(M1650 m1650) {
                Player player = getCurrentPlayer();
                if (player.id == m1650.playerId) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("【" + player.roleName + "】被挂掉了..重新创建角色刷级..角色详细信息：" + player);

                    loginAgain();
                }
            }
        });

        registerMessageHander(M2403.class, new IMessageHandler<M2403>() {
            @Override
            public void handle(M2403 m2403) {
                ITaskExecutor executor = executorMap.get(m2403.taskTree);
                if (executor != null) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine(getCurrentAccount().getUserId() + "," + getCurrentPlayer().roleName + "【" + getCurrentPlayer().level + "】执行任务：" + m2403.taskTree);
                    executor.execute();
                }
            }
        });

    }

    private void initExecutor() {

        executorMap.put(ITask.t1, new PointCallBackExecutor(navigatorModule) {

            public void initPointCallBacks(List<PointCallBack> pcbs) {

                pcbs.add(new PointCallBack(new Point(59, 187), new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2402._npcId = 0x3e9;
                        m2402._type = 0x1;
                        send(m2402);
                        return null;
                    }
                }));

                goAndStartRobot(pcbs, new Point(45, 173));

            }
        });

        executorMap.put(ITask.t2, new PointCallBackExecutor(navigatorModule) {
            @Override
            public void initPointCallBacks(final List<PointCallBack> pcbs) {

                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        pcbs.add(new PointCallBack(new Point(44, 158), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3ea);
                                return null;
                            }
                        }));

                        goAndStartRobot(pcbs, new Point(55, 145));

                        return null;
                    }
                });

            }
        });

        executorMap.put(ITask.t3, new PointCallBackExecutor(navigatorModule) {
            @Override
            public void initPointCallBacks(final List<PointCallBack> pcbs) {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        pcbs.add(new PointCallBack(new Point(42, 132), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3eb);
                                return null;
                            }
                        }));
                        pcbs.add(new PointCallBack(new Point(19, 110), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3ec);
                                return null;
                            }
                        }));
                        pcbs.add(new PointCallBack(new Point(29, 100), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                startRobotMode();
                                return null;
                            }
                        }));
                        return null;
                    }
                });

            }
        });

        executorMap.put(ITask.t4, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 1;
                        m2411._x = 26;
                        m2411._y = 83;
                        //去NPC 受伤的村民位置
                        send(m2411);

                        finishAndAccNew(0x3ed);
                        goAndStartRobotMode(35, 74);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t5, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 1;
                        m2411._x = 48;
                        m2411._y = 81;
                        //去NPC 游历法师位置
                        send(m2411);

                        finishAndAccNew(0x3ee);
                        goAndStartRobotMode(69, 90);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t6, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(75, 96), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3ef);
                                goAndStartRobotMode(97, 105);
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t7, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 1;
                        m2411._x = 101;
                        m2411._y = 126;
                        //去NPC 天尊弟子位置
                        send(m2411);

                        finishAndAccNew(0x3f0);
                        goAndStartRobotMode(124, 115);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t8, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(132, 100), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3f1);
                                navigatorModule.goAndDo(new Point(144, 102), new ICallBack() {
                                    @Override
                                    public Object call(Object param) {
                                        startRobotMode();
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t9, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 1;
                        m2411._x = 151;
                        m2411._y = 78;
                        //去NPC 寺庙守卫位置
                        send(m2411);

                        finishAndAccNew(0x3f2);
                        goAndStartRobotMode(126, 55);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t10, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(125, 46), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3f3);
                                navigatorModule.goAndDo(new Point(114, 43), new ICallBack() {
                                    @Override
                                    public Object call(Object param) {
                                        startRobotMode();
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t11, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(107, 36), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3f4);
                                navigatorModule.goAndDo(new Point(99, 35), new ICallBack() {
                                    @Override
                                    public Object call(Object param) {
                                        startRobotMode();
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t12, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                            @Override
                            public void onAction(Event evt) {

                                M2402 m2402 = new M2402();
                                m2402._npcId = 0x2f44;
                                m2402._type = 0x5;
                                send(m2402);
                                m2402._type = 0x6;
                                send(m2402);

                                List<M2411CallBack> callBacks = new ArrayList<M2411CallBack>();
                                M2411CallBack cb = new M2411CallBack();

                                cb = new M2411CallBack();
                                // 去王城统领位置
                                m2411 = new M2411();
                                m2411._sceneId = 12;
                                m2411._x = 117;
                                m2411._y = 51;
                                cb.m2411 = m2411;
                                cb.runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        finishAndAccNew(0x2f45);
                                    }
                                };
                                callBacks.add(cb);

                                cb = new M2411CallBack();
                                // 去药店老板位置
                                m2411 = new M2411();
                                m2411._sceneId = 12;
                                m2411._x = 143;
                                m2411._y = 128;
                                cb.m2411 = m2411;
                                cb.runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        finishAndAccNew(0x2ee2);
                                    }
                                };
                                callBacks.add(cb);

                                cb = new M2411CallBack();
                                // 去武器店老板位置
                                m2411 = new M2411();
                                m2411._sceneId = 12;
                                m2411._x = 73;
                                m2411._y = 148;
                                cb.m2411 = m2411;
                                cb.runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        finishAndAccNew(0x2ee3);
                                    }
                                };
                                callBacks.add(cb);

                                cb = new M2411CallBack();
                                // 去服饰店老板位置
                                m2411 = new M2411();
                                m2411._sceneId = 12;
                                m2411._x = 95;
                                m2411._y = 163;
                                cb.m2411 = m2411;
                                cb.runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        finishAndAccNew(0x2ee8);
                                    }
                                };
                                callBacks.add(cb);

                                cb = new M2411CallBack();
                                // 去首饰店老板位置
                                m2411 = new M2411();
                                m2411._sceneId = 12;
                                m2411._x = 132;
                                m2411._y = 162;
                                cb.m2411 = m2411;
                                cb.runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        finishAndAccNew(0x2ee4);
                                    }
                                };
                                callBacks.add(cb);

                                cb = new M2411CallBack();
                                // 去王城统领位置
                                m2411 = new M2411();
                                m2411._sceneId = 12;
                                m2411._x = 117;
                                m2411._y = 51;
                                cb.m2411 = m2411;
                                cb.runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        M2402 m2402 = new M2402();
                                        m2402._npcId = 0x2f45;
                                        m2402._type = 0x3;
                                        send(m2402);
                                        m2402._type = 0x4;
                                        send(m2402);
                                    }
                                };
                                callBacks.add(cb);

                                doInSequenceWithM2411(callBacks);
                            }
                        });

                        //去NPC 去烈焰城守卫队长位置
                        m2411._sceneId = 12;
                        m2411._x = 55;
                        m2411._y = 53;
                        send(m2411);

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t13, new ITaskExecutor() {
            @Override
            public void execute() {
                //
                m2411._sceneId = 105;
                m2411._x = 40;
                m2411._y = 70;
                send(m2411);

                finishAndAccNew(0x2905);

            }
        });

        executorMap.put(ITask.t133, new ITaskExecutor() {
            @Override
            public void execute() {
                navigatorModule.goAndDo(new Point(73, 60), new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        finishAndAccNew(0x2906);
                        goAndStartRobotMode(89, 75);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t14, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        List<PointCallBack> pcbs = new ArrayList<PointCallBack>();
                        pcbs.add(new PointCallBack(new Point(77, 63), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                sendPairM2402(0x2906, 0x3);
                                return null;
                            }
                        }));
                        pcbs.add(new PointCallBack(new Point(49, 30), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x2907);
                                return null;
                            }
                        }));
                        pcbs.add(new PointCallBack(new Point(27, 50), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                startRobotMode();
                                return null;
                            }
                        }));
                        navigatorModule.doInSequence(pcbs);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t15, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(48, 30), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                sendPairM2402(0x2907, 0x3);

                                m2411._sceneId = 151;
                                m2411._x = 70;
                                m2411._y = 301;
                                send(m2411);

                                startRobotMode();
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t16, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        List<PointCallBack> pcbs = new ArrayList<PointCallBack>();
                        pcbs.add(new PointCallBack(new Point(55, 282), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3afd);
                                return null;
                            }
                        }));
                        pcbs.add(new PointCallBack(new Point(75, 261), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                startRobotMode();
                                return null;
                            }
                        }));

                        navigatorModule.doInSequence(pcbs);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t17, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        List<PointCallBack> pcbs = new ArrayList<PointCallBack>();
                        pcbs.add(new PointCallBack(new Point(90, 240), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x3afe);
                                return null;
                            }
                        }));
                        pcbs.add(new PointCallBack(new Point(135, 270), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                startRobotMode();
                                return null;
                            }
                        }));

                        navigatorModule.doInSequence(pcbs);
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t18, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 151;
                        m2411._x = 99;
                        m2411._y = 240;
                        send(m2411);

                        sendPairM2402(0x3afe, 0x3);

                        m2411._x = 68;
                        m2411._y = 193;
                        send(m2411);

                        addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                            @Override
                            public void onAction(Event evt) {
                                navigatorModule.goAndDo(new Point(24, 29), new ICallBack() {
                                    @Override
                                    public Object call(Object param) {
                                        startRobotMode();
                                        return null;
                                    }
                                });
                            }
                        });

                        finishAndAccNew(0x3aff);

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t19, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(25, 21), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                M2401 m2401 = new M2401();
                                m2401._npcId = 0x3bc5;
                                send(m2401);

                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                                    @Override
                                    public void onAction(Event evt) {

                                        navigatorModule.goAndDo(new Point(118, 51), new ICallBack() {
                                            @Override
                                            public Object call(Object param) {

                                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                                                    @Override
                                                    public void onAction(Event evt) {
                                                        navigatorModule.goAndDo(new Point(69, 198), new ICallBack() {
                                                            @Override
                                                            public Object call(Object param) {
                                                                startRobotMode();
                                                                return null;
                                                            }
                                                        });
                                                    }
                                                });

                                                sendPairM2402(0x2f45, 0x5);
                                                return null;
                                            }
                                        });

                                    }
                                });

                                finishAndAccNew(0x3bc5);

                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t20, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(72, 181), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x2908);
                                m2411._sceneId = 0xA1;
                                m2411._x = 90;
                                m2411._y = 60;
                                send(m2411);
                                startRobotMode();
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t21, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 0xA1;
                        m2411._x = 77;
                        m2411._y = 52;
                        send(m2411);

                        finishAndAccNew(0x3ee5);

                        m2411._x = 68;
                        m2411._y = 38;
                        send(m2411);

                        startRobotMode();

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t22, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(75, 47), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                sendPairM2402(0x3ee5, 0x3);

                                m2411._sceneId = 0xA1;
                                m2411._x = 37;
                                m2411._y = 70;
                                send(m2411);
                                startRobotMode();
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t23, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 0xA1;
                        m2411._x = 22;
                        m2411._y = 81;
                        send(m2411);

                        finishAndAccNew(0x3ee6);

                        m2411._x = 48;
                        m2411._y = 95;
                        send(m2411);

                        // 这个任务完成后，服务器会自动把角色定位到军需官附近，如果此处开启挂机，可能出现定位到军需官时仍在挂机的情况；
                        // 因为是挂机角色职业是道士，带着宝宝，此处就直接让宝宝打了 或者此处程序控制角色打怪
                        //startRobotMode();

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t24, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        goTJ();
                        m2402._npcId = 0x2f47;
                        m2402._type = 0x1db;
                        send(m2402);
                        m2402._type = 0x1e0;
                        send(m2402);

                        m2411._sceneId = 12;
                        m2411._x = 143;
                        m2411._y = 128;
                        send(m2411);

                        sendPairM2402(0x2ee2, 0x3);

                        acceptTJ();

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t25, new ITaskExecutor() {
            @Override
            public void execute() {
                shouldGoOnTJ = false;

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        m2411._sceneId = 12;
                        m2411._x = 143;
                        m2411._y = 128;
                        send(m2411);

                        m2402._npcId = 0x2ee2;
                        m2402._type = 0x4;
                        send(m2402);

                        m2411._sceneId = 106;
                        m2411._x = 46;
                        m2411._y = 72;
                        send(m2411);

                        finishAndAccNew(0x2969);
                        m2411._x = 68;
                        m2411._y = 121;
                        send(m2411);

                        startRobotMode();
                    }
                };

                if (isTJFinished)
                    r.run();
                else
                    callBackAfterTJFinished = r;

            }
        });

        executorMap.put(ITask.t26, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 106;
                        m2411._x = 46;
                        m2411._y = 79;
                        send(m2411);

                        sendPairM2402(0x2969, 0x3);

                        m2411._x = 38;
                        m2411._y = 114;
                        send(m2411);

                        startRobotMode();
                        return null;
                    }
                });
            }
        });


        executorMap.put(ITask.t27, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 106;
                        m2411._x = 17;
                        m2411._y = 103;
                        send(m2411);

                        finishAndAccNew(0x296a);

                        m2411._x = 25;
                        m2411._y = 46;
                        send(m2411);

                        startRobotMode();
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t28, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 106;
                        m2411._x = 13;
                        m2411._y = 100;
                        send(m2411);

                        sendPairM2402(0x296a, 0x3);

                        m2411._x = 25;
                        m2411._y = 25;
                        send(m2411);

                        finishAndAccNew(0x296b);

                        m2411._x = 37;
                        m2411._y = 19;
                        send(m2411);

                        M1807 m1807 = new M1807();
                        m1807._id = 0x3e9;
                        send(m1807);

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t29, new ITaskExecutor() {
            @Override
            public void execute() {
                navigatorModule.goAndDo(new Point(29, 22), new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        sendPairM2402(0x296b, 0x3);

                        m2411._sceneId = 171;
                        m2411._x = 39;
                        m2411._y = 38;
                        send(m2411);

                        finishAndAccNew(0x42cd);

                        startRobotMode();
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t30, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(43, 41), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                sendPairM2402(0x42cd, 0x3);
                                startRobotMode();
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t31, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        m2411._sceneId = 171;
                        m2411._x = 110;
                        m2411._y = 65;
                        send(m2411);

                        finishAndAccNew(0x42ce);

                        startRobotMode();
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t32, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(new Point(109, 68), new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                m2402._npcId = 0x42ce;
                                m2402._type = 0x3;
                                send(m2402);

                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                                    @Override
                                    public void onAction(Event evt) {
                                        navigatorModule.goAndDo(new Point(52, 56), new ICallBack() {
                                            @Override
                                            public Object call(Object param) {
                                                startRobotMode();
                                                return null;
                                            }
                                        });
                                    }
                                });

                                m2402._type = 0x4;
                                send(m2402);

                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t33, new ITaskExecutor() {
            @Override
            public void execute() {
                navigatorModule.goAndDo(new Point(72, 76), new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                            @Override
                            public void onAction(Event evt) {
                                navigatorModule.goAndDo(new Point(137, 35), new ICallBack() {
                                    @Override
                                    public Object call(Object param) {
                                        sendPairM2402(0x2ee9, 0x15);

                                        navigatorModule.goAndDo(new Point(121, 48), new ICallBack() {
                                            @Override
                                            public Object call(Object param) {
                                                sendPairM2402(0x2f45, 0x7);
                                                shouldGoOnTJ = true;
                                                acceptTJ();
                                                return null;
                                            }
                                        });
                                        return null;
                                    }
                                });
                            }
                        });

                        sendPairM2402(0x445d, 0x5);

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t34, new ITaskExecutor() {
            @Override
            public void execute() {
                shouldGoOnTJ = false;

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        m2411._sceneId = 12;
                        m2411._x = 117;
                        m2411._y = 51;
                        send(m2411);

                        m2402._npcId = 0x2f45;
                        m2402._type = 0x8;
                        send(m2402);

                        m2411._sceneId = 0x6A;
                        m2411._x = 49;
                        m2411._y = 74;
                        send(m2411);

                        sendPairM2402(0x2969, 0x5);

                        m2411._x = 57;
                        m2411._y = 85;
                        send(m2411);

                        updatePlayerLocation(57, 85);
                        navigatorModule.loadScene(0x6A);

                        M1807 m1807 = new M1807();
                        m1807._id = 0x3ea;
                        send(m1807);

                    }
                };

                if (isTJFinished)
                    r.run();
                else
                    callBackAfterTJFinished = r;

            }
        });

        executorMap.put(ITask.t35, new ITaskExecutor() {
            @Override
            public void execute() {
                navigatorModule.goAndDo(50, 77, new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        sendPairM2402(0x2969, 0x7);

                        m2411._sceneId = 0x6A;
                        m2411._x = 35;
                        m2411._y = 145;
                        send(m2411);

                        //updatePlayerLocation(35, 145);

                        finishAndAccNew(0x296c);

                        m2411._x = 38;
                        m2411._y = 166;
                        send(m2411);
                        startRobotMode();

                        //goAndStartRobotMode(38,165);

                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t36, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(33, 151, new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                m2402._npcId = 0x296c;
                                m2402._type = 0x3;
                                send(m2402);

                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                                    @Override
                                    public void onAction(Event evt) {
                                        navigatorModule.goAndDo(71, 112, new ICallBack() {
                                            @Override
                                            public Object call(Object param) {

                                                executorMap.put(ITask.t37, new ITaskExecutor() {
                                                    @Override
                                                    public void execute() {
                                                        makeSureStoppedRobot(new ICallBack() {
                                                            @Override
                                                            public Object call(Object param) {
                                                                navigatorModule.goAndDo(71, 111, new ICallBack() {
                                                                    @Override
                                                                    public Object call(Object param) {
                                                                        sendPairM2402(0x4719, 0x3);
                                                                        goAndStartRobotMode(102, 82);
                                                                        return null;
                                                                    }
                                                                });
                                                                return null;
                                                            }
                                                        });
                                                    }
                                                });

                                                finishAndAccNew(0x4719);
                                                goAndStartRobotMode(80, 103);
                                                return null;
                                            }
                                        });
                                    }
                                });
                                m2402._type = 0x4;
                                send(m2402);


                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t38, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(115, 64, new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                finishAndAccNew(0x471a);
                                goAndStartRobotMode(103, 52);
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t39, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(111, 60, new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                                    @Override
                                    public void onAction(Event evt) {
                                        goAndStartRobotMode(23, 41);
                                    }
                                });

                                sendPairM2402(0x471a, 0x3);

                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

        executorMap.put(ITask.t40, new ITaskExecutor() {
            @Override
            public void execute() {
                makeSureStoppedRobot(new ICallBack() {
                    @Override
                    public Object call(Object param) {
                        navigatorModule.goAndDo(16, 37, new ICallBack() {
                            @Override
                            public Object call(Object param) {
                                addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {
                                    @Override
                                    public void onAction(Event evt) {
                                        navigatorModule.goAndDo(138, 34, new ICallBack() {
                                            @Override
                                            public Object call(Object param) {
                                                sendPairM2402(0x2ee9, 0x17);

                                                m2411._sceneId = 12;
                                                m2411._x = 132;
                                                m2411._y = 162;
                                                send(m2411);

                                                sendPairM2402(0x2ee4, 0x3);

                                                m2411._x = 143;
                                                m2411._y = 128;
                                                send(m2411);

                                                cleanPackage(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sendPairM2402(0x2ee2, 0x5);

                                                        m2411._x = 76;
                                                        m2411._y = 102;
                                                        send(m2411);

                                                        sendPairM2402(0x2f49, 0x15);

                                                        doFMTask();

                                                    }
                                                });

                                                return null;
                                            }
                                        });
                                    }
                                });
                                sendPairM2402(0x4845, 0x5);
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        });

    }

    private void updatePlayerLocation(int x, int y) {
        Player player = getCurrentPlayer();
        player.currentLocation = new java.awt.Point(x, y);
    }

    private void cleanPackage(final Runnable callBackAfterCleaned) {
        addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                PackageInfo pi = (PackageInfo) evt.eventData;
                PackageItem[] items = pi.getItems();
                SocketX socket = getCurrentGameSocket();
                for (int i = 0; i < items.length; i++) {
                    PackageItem item = items[i];
                    if(ItemModule.isFbj(item))
                        continue;
                    CommandUtil.sellItem(socket, item);
                }

                callBackAfterCleaned.run();
            }
        });

        CommandUtil.getPackageInfo(getCurrentGameSocket());
    }

    private void sendPairM2402(int npcId, int baseType) {
        m2402._npcId = npcId;
        m2402._type = baseType;
        send(m2402);
        m2402._type = baseType + 1;
        send(m2402);
    }

    private void goAndStartRobotMode(int x, int y) {
        navigatorModule.goAndDo(x, y, new ICallBack() {
            @Override
            public Object call(Object param) {
                startRobotMode();
                return null;
            }
        });
    }

    private void finishTJ() {

        goTJ();

        M2402 m2402 = new M2402();
        m2402._npcId = 0x2f47;
        m2402._type = 0x7;
        send(m2402);

        isTJFinished = true;
    }

    private void acceptTJ() {

        goTJ();

        if (logger.isLoggable(Level.FINE))
            logger.fine("账号：" + getCurrentAccount().getUserId() + "，角色：" + getCurrentPlayer().roleName + "【" + getCurrentPlayer().level + "】" + "开始天机任务");

        M2402 m2402 = new M2402();
        m2402._npcId = 0x2f47;
        m2402._type = 0x1;
        send(m2402);

        startRobotMode();

        isTJFinished = false;

    }

    /**
     * 去天机老人NPC处
     */
    private void goTJ() {
        CommandUtil.goTJ(getCurrentGameSocket());
    }

    /**
     * 去封魔NPC处
     */
    private void goFM() {
        CommandUtil.goFM(getCurrentGameSocket());
    }

    /**
     * 免费领取封魔经验
     */
    private void claimFMExp(final Runnable callback) {
        registerMessageHander(M2401.class, new IMessageHandler<M2401>() {
            @Override
            public void handle(M2401 message) {
                if (message.talkObj.toString().startsWith("{\"type\":0,\"npcid\":12015,\"talk\":\"#&task_describe#&\\n\\n <font color=\\\"#ffff00\\\">你当前未领取的封魔岭经验：0</font>\\n\\n <u><a href='event:m2402,200'>免费领取")) {
                    getParserManager().removeMessageHandler(M2401.class, this);
                    ThreadUtil.startWithGlobal(new Runnable() {
                        @Override
                        public void run() {
                            logger.fine("已完成免费领取封魔经验.");
                            ThreadUtil.sleep(3000);
                            callback.run();
                        }
                    });
                }
            }
        });
        m2402._npcId = 0x2eef;
        m2402._type = 0xc8;
        send(m2402);
    }

    private void makeSureInitedFMTaskCount(final Runnable callback) {

        if (!isFMCountInited) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    registerMessageHander(M2401.class, new IMessageHandler<M2401>() {
                        @Override
                        public void handle(M2401 m2401) {
                            String talkObj = m2401.talkObj.toString();
                            if (talkObj.startsWith(ITask.t100)) {
                                isFMCountInited = true;
                                getParserManager().removeMessageHandler(M2401.class, this);
                                char count = talkObj.charAt(165);
                                MAX_FM_TASK_COUNT = count - 48;

                                if (logger.isLoggable(Level.INFO))
                                    logger.info("封魔任务次数：" + MAX_FM_TASK_COUNT);

                                callback.run();
                            }
                        }
                    });

                    M2401 m2401 = new M2401();
                    m2401._npcId = 0x2eef;
                    send(m2401);
                }
            };

            cleanPackage(r);
        } else
            callback.run();

    }

    private void doFMTask() {

        goFM();

        makeSureInitedFMTaskCount(new Runnable() {
            @Override
            public void run() {
                claimFMExp(new Runnable() {
                    @Override
                    public void run() {
                        if (checkFMCount())
                            intoFM();
                    }
                });
            }
        });

    }

    private boolean checkFMCount() {
        if (currentFinishedFMTaskCount >= MAX_FM_TASK_COUNT) {

            String accId = getCurrentAccount().getUserId();
            Player player = getCurrentPlayer();
            String result = "【" +
                    accId +
                    "," +
                    player.roleName +
                    "," +
                    player.initLevel +
                    "->" +
                    player.level +
                    "】已完成所有任务.当前累计完成任务账号数：" +
                    TOTAL_FINISHED_TASK_ROLE_NUM.addAndGet(1) +
                    " 当前在线人数：" + ContextUtil.getCurrentOnlinePlayerNum();

            logAll(result, Level.INFO);

            loginNextRole();

            return false;
        }

        return true;
    }

    private void intoFM() {

        addEventListener(IEventType.SCENE_CHANGED, new IOnceListener() {

            @Override
            public void onAction(Event evt) {

                logger.fine("已进入封魔岭.");
                Byte level = getCurrentPlayer().initLevel;

                if (level != 1 && ((currentFinishedFMTaskCount == 1 && MAX_FM_TASK_COUNT == 3) || (currentFinishedFMTaskCount == 1 && MAX_FM_TASK_COUNT == 2))) {
                    PackageItem[] items = getCurrentPlayer().packageInfo.getItems();
                    int count = 0;
                    for (PackageItem item : items) {
                        if (item != null && item.type == 10187 || item.type == 10188) {
                            count += item.num;
                        }
                    }

                    if (count <= 8) {
                        logger.fine("2次封魔合并成一次来做.");
                        doFMTask();
                        return;
                    }
                }

                addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
                    @Override
                    public void onAction(Event evt) {
                        PackageInfo pi = (PackageInfo) evt.eventData;
                        PackageItem[] items = pi.getItems();

                        final List<PackageItem> bowManItems = new ArrayList<PackageItem>();
                        final int maxCount = 16;

                        for (int i = 0; i < items.length && bowManItems.size() < maxCount; i++) {
                            PackageItem item = items[i];
                            if (item.type == 10187 || item.type == 10188) {
                                for (int j = 0; j < item.num && bowManItems.size() < maxCount; j++) {
                                    bowManItems.add(item);
                                }
                            }
                        }

                        placeBowMans(bowManItems);

                    }
                });

                logger.fine("查询背包弓箭手物品..");
                CommandUtil.getPackageInfo(getCurrentGameSocket());

            }
        });

        currentFinishedFMTaskCount++;

        goingInFM();
        logger.fine("请求进入封魔岭..");
    }

    private void placeBowMans(final List<PackageItem> bowManItems) {
        List<PointCallBack> pcbs = new ArrayList<PointCallBack>();

        final M1728 m1728 = new M1728();
        final int bowManItemsSize = bowManItems.size();
        for (int i = 0; i < bowManPoints.length && i < bowManItemsSize; i++) {
            pcbs.add(new PointCallBack(bowManPoints[i], new ICallBack() {
                @Override
                public Object call(Object param) {
                    m1728._id = bowManItems.remove(0).id;
                    send(m1728);
                    ThreadUtil.sleep(1000);
                    return null;
                }
            }));
        }

        pcbs.add(new PointCallBack(18, 67, new ICallBack() {
            @Override
            public Object call(Object param) {
                logger.fine("开启封魔任务..");
                m2402._npcId = 0x7531;
                m2402._type = 0x65;
                send(m2402);

                ThreadUtil.startWithGlobal(new Runnable() {
                    @Override
                    public void run() {
                        logger.fine("封魔任务中..等待21分钟...");
                        ThreadUtil.sleep(21 * 60 * 1000);
                        logger.fine("封魔任务完成，退出封魔岭.");
                        if (getCurrentGameSocket() != null)
                            doFMTask();
                    }
                });

                return null;
            }
        }));

        logger.fine("开始放置弓箭手..");
        navigatorModule.doInSequence(pcbs);

    }

    private void goingInFM() {
        m2402._npcId = 0x2eef;
        m2402._type = 0x1;
        send(m2402);

    }

    private void doInSequenceWithM2411(final List<M2411CallBack> callBacks) {
        if (callBacks.size() > 0) {
            final M2411CallBack callBack = callBacks.remove(0);
            registerMessageHander(M1504.class, new IFlashMessageHandler<M1504>() {
                @Override
                public void handle(M1504 m1504) {
                    callBack.runnable.run();
                    doInSequenceWithM2411(callBacks);
                }
            });
            send(callBack.m2411);
        }
    }

    private void makeSureStoppedRobot(final ICallBack callBack) {

        navigatorModule.reset();

        logger.fine("发送取消挂机请求.");

        send(new M1681());

        callBack.call(null);
    }

    private void goAndStartRobot(List<PointCallBack> pointCallBacks, Point end) {
        pointCallBacks.add(new PointCallBack(end, new ICallBack() {
            @Override
            public Object call(Object param) {
                startRobotMode();
                return null;
            }
        }));
    }

    private void finishAndAccNew(int npcId) {
        M2402 m2402 = new M2402();
        m2402._npcId = npcId;
        m2402._type = 0x1;
        send(m2402);
        m2402._npcId = npcId;
        m2402._type = 0x2;
        send(m2402);
    }

    private void startRobotMode() {
        send(requestRobotMode);
    }

}

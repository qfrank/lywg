package wg.mod.impl;

import com.game.net.m15.M1501;
import com.game.net.m28.*;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.ThreadUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IListener;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.mod.ModuleManager;
import wg.pojo.PackageInfo;
import wg.pojo.PackageItem;
import wg.pojo.Player;
import wg.util.CommandUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class TradeModule extends BaseModule implements IExclusiveModule {

    public static int MAX_TRADE_NUM;;

    public static AtomicInteger totalTradeItem = new AtomicInteger(0);

    public static String TRADE_PLAYER = "";

    public static volatile boolean totalInfoOutputed;

    // for current logined role
    private final ReentrantLock tradeLock = new ReentrantLock();

    private final Condition nextTradeCondition = tradeLock.newCondition();

    private volatile List<PackageItem> packageItems = new ArrayList();

    private volatile List<PackageItem> tradeItems = new ArrayList();

    private volatile int tradeTargetId;

    private volatile boolean exitDirectly;

    private volatile StopableTrading lastTrading;

    public static void reset() {
        MAX_TRADE_NUM = 0;
        TRADE_PLAYER = "";
        totalInfoOutputed = false;
        totalTradeItem.set(0);
    }

    private void ready2NextTrade() {
        tradeLock.lock();
        try {
            nextTradeCondition.signal();
        } finally {
            tradeLock.unlock();
        }
    }

    @Override
    public void init() {

        // 交易开始
        registerMessageHander(M2801.class, new IMessageHandler<M2801>() {
            @Override
            public void handle(M2801 m) {
                for (int i = 0; i < tradeItems.size(); i++) {
                    PackageItem item = tradeItems.get(i);
                    M2807 m2807 = new M2807();
                    m2807.packageIndex = item.index;
                    m2807.tradeIndex = i;
                    send(m2807);
                }

                // 确认交易交易栏中的物品
                if (tradeItems.size() > 0)
                    send(new M2804());

            }
        });

        registerMessageHander(M2811.class, new IMessageHandler<M2811>() {
            @Override
            public void handle(M2811 m) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("获得交易状态:" + m.result);

                if (m.result == 8) {// 交易完成

                    // 发送私聊信息
                    String msg = "目标玩家【" + TRADE_PLAYER + "】已累计完成交易" + totalTradeItem.addAndGet(tradeItems.size()) + "把.剩余" + (MAX_TRADE_NUM - totalTradeItem.get());
                        /*M1908 m1908 = new M1908();
                        m1908._id = tradeTargetId;
                        m1908._content = msg;
                        send(m1908);*/

                    logAll(msg, Level.INFO);
                    ready2NextTrade();
                } else if (m.result == 16) {// 当前发起与对方交易的玩家过多，歇会
                    packageItems.addAll(tradeItems);
                    tradeLock.lock();
                    try {
                        nextTradeCondition.await(1, TimeUnit.SECONDS);
                        nextTradeCondition.signal();
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "Error happend in 'TradeModule' while wait for next trade request.", e);
                    } finally {
                        tradeLock.unlock();
                    }
                } else if (m.result != 4 && m.result != 1 && m.result != 2 && m.result != 7) {
                    packageItems.addAll(tradeItems);
                    ready2NextTrade();
                }
            }
        });

        // 对方拒绝交易请求
        registerMessageHander(M2813.class, new IMessageHandler<M2813>() {
            @Override
            public void handle(M2813 m) {
                packageItems.addAll(tradeItems);
                ready2NextTrade();
            }
        });

        addEventListener(IEventType.NEW_PLAYER_APPEARED, newPlayerHandler);

        addEventListener(IEventType.PLAYER_DISAPPEARED, new IListener() {
            @Override
            public void onAction(Event evt) {
                M1501 m1501 = (M1501) evt.eventData;
                String name = getName(m1501);
                if (TRADE_PLAYER.equals(name)) {
                    if (lastTrading != null)
                        lastTrading.stop();
                    ready2NextTrade();
                }
            }
        });

    }

    @Override
    public void run() {

        SocketX socket = getCurrentGameSocket();

        CommandUtil.goJX(socket);

        if (!totalInfoOutputed) {
            globalLogger.info("交易模块已启动.待交易数量：" + MAX_TRADE_NUM + "，玩家：" + TRADE_PLAYER);
            totalInfoOutputed = true;
        }

        if (lastTrading != null) {
            lastTrading.stop();
            ready2NextTrade();
        }

        addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                PackageInfo pi = (PackageInfo) evt.eventData;
                boolean exist60Weapon = false;
                for (PackageItem item : pi.getItems()) {
                    if (item.type >= 20014 && item.type <= 20016) {
                        exist60Weapon = true;
                        break;
                    }
                }
                if (!exist60Weapon) {
                    exitDirectly = true;
                    exit();
                }
            }
        });

        CommandUtil.getPackageInfo(socket);

        AroundInfoModule aroundInfoModule = (AroundInfoModule) ModuleManager.getModule(AroundInfoModule.class);
        for (M1501 m1501 : aroundInfoModule.currentAroundPlayers()) {
            newPlayerHandler.onAction(new Event(IEventType.NEW_PLAYER_APPEARED, m1501));
        }

    }

    private String getName(M1501 m1501) {
        String name = (String) m1501.info.get("name");
        try {
            name = new String(name.trim().getBytes("GBK"), "GBK");
            return name;
        } catch (UnsupportedEncodingException e) {
            globalLogger.log(Level.SEVERE, "Name transfer to gbk error,source name:" + name, e);
            return null;
        }
    }

    private IListener newPlayerHandler = new IListener() {

        @Override
        public void onAction(Event evt) {
            final M1501 m1501 = (M1501) evt.eventData;
            String name = getName(m1501);

            if (!TRADE_PLAYER.equals(name.trim()))
                return;

            tradeTargetId = ((Long) (m1501.info.get("id"))).intValue();

            if (logger.isLoggable(Level.FINE))
                logger.fine("发现交易目标玩家:" + TRADE_PLAYER);

            addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
                @Override
                public void onAction(Event evt) {
                    if (exitDirectly)
                        return;

                    final PackageInfo pi = (PackageInfo) evt.eventData;
                    packageItems = new ArrayList(Arrays.asList(pi.getItems()));
                    lastTrading = new StopableTrading();
                    ThreadUtil.startWithGlobal(lastTrading);
                }
            });

            CommandUtil.getPackageInfo(getCurrentGameSocket());

        }
    };

    private class StopableTrading implements Runnable {

        private volatile boolean stopped = false;

        public void stop() {
            this.stopped = true;
        }

        protected boolean isStopped() {
            return stopped;
        }

        private void exitIf() {
            Player player = getCurrentPlayer();
            boolean stillExist60WeaponAfterTrade = false;
            for (PackageItem item : packageItems) {
                if (item.type >= 20014 && item.type <= 20016) {
                    stillExist60WeaponAfterTrade = true;

                    if (logger.isLoggable(Level.FINE))
                        logger.fine(player.roleName + "交易完成后背包中还存在60武器.");

                    break;
                }
            }

            if (!stillExist60WeaponAfterTrade) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine(player.roleName + "交易完成后背包中不存在60武器.现退出");

                exit();
            }
        }

        private void select60LevelWeapons() throws InterruptedException {
            tradeItems.clear();

            // select 60 level weapons
            while (packageItems.size() > 0) {
                PackageItem item = packageItems.remove(0);
                if (item.bind == 1) {
                    continue;
                }
                if (item.type == 10055 || item.type == 10056 || item.type == 10189) {// 随机石或回城石或副本卷
                    continue;
                }
                if (item.type >= 20014 && item.type <= 20016) {// 60武器
                    tradeItems.add(item);
                } else {
                    // TODO 如果是50级衣服和武器 会卖不掉 存留在背包中
                    // 直接卖掉
                    CommandUtil.sellItem(getCurrentGameSocket(), item);
                }

                if (tradeItems.size() == 12 || totalTradeItem.get() + tradeItems.size() >= MAX_TRADE_NUM) {
                    break;
                }

                if (tradeItems.size() > 0) {
                    // request trade to target
                    M2800 m2800 = new M2800();
                    m2800.tradeTargetId = tradeTargetId;
                    send(m2800);

                    // wait util next trade is triggered
                    nextTradeCondition.await();
                }
            }
        }

        public void run() {
            try {
                tradeLock.lock();

                globalLogger.fine(getCurrentPlayer().roleName + "已取到交易锁，可以开始交易了.");

                while (!isStopped()) {

                    if (totalTradeItem.get() < MAX_TRADE_NUM) {

                        select60LevelWeapons();

                    }

                }

                exitIf();

            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Error happend in 'TradeModule' while trading.", e);
            } finally {

                tradeLock.unlock();

            }
        }

    }
}

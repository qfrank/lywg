package wg.mod.impl;

import com.game.net.m15.M1503;
import com.game.net.m19.M1908;
import com.game.net.m24.M2402;
import com.game.net.m28.*;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.StringUtil;
import org.frkd.util.ThreadUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.pojo.PackageInfo;
import wg.pojo.Player;
import wg.util.CommandUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * User: Frank Tang <br/>
 * Date: 15/1/21<br/>
 * Time: 上午3:05<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class SixtyLevelWeaponAcceptTradeModule extends BaseModule  implements IExclusiveModule {

    public static AtomicInteger totalTrade = new AtomicInteger(0);

    public static int targetTotalTrade;

    private TradeWatcher tradeWatcher;

    private TradeProcessor tradeProcessor;

    private TradePlayer currentTradePlayer;

    private int tradeItemNum;

    private volatile boolean canBeginNextTrade;

    private volatile boolean isCanceledBySelf;

    private volatile boolean exitTradeProcessor;

    private volatile boolean exitTradeWatcher;

    private LinkedBlockingDeque<TradePlayer> tradePlayers = new LinkedBlockingDeque<TradePlayer>();

    @Override
    public void run() {

        // TODO 实现stop方法、destroy方法
        Player player = getCurrentPlayer();
        if(player.level>=70 || player.level<60){
            exit();
            return;
        }


        addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                PackageInfo pi = (PackageInfo) evt.eventData;
                int blanks = PackageInfo.packLen - pi.getItems().length;

                if (logger.isLoggable(Level.FINE))
                    logger.fine("背包剩余空格数" + blanks);

                if (blanks < 12) {
                    System.out.println("警告：您的背包剩余空格数少于12，请先手动清理包裹！现退出..");
                    exit();
                    return;
                }

                SocketX socketX = getCurrentGameSocket();

                CommandUtil.goJX(socketX);
                M1503 m1503 = new M1503();
                m1503._x = 124;
                m1503._y = 120;
                m1503._state = 2;
                socketX.send(m1503);

            }
        });

        CommandUtil.getPackageInfo(getCurrentGameSocket());

        tradeWatcher = new TradeWatcher();
        tradeWatcher.setDaemon(true);
        tradeWatcher.start();

        tradeProcessor = new TradeProcessor();
        tradeProcessor.setDaemon(true);
        tradeProcessor.start();

        //System.out.println("亲，等待认证中..请稍等..");

    }

    @Override
    public void destroy() {
        this.exitTradeProcessor = true;
        this.exitTradeWatcher = true;
    }

    @Override
    public void init() {

        registerMessageHander(M1908.class, new IMessageHandler<M1908>() {
            @Override
            public void handle(M1908 m1908) {
                String content = (String) m1908.whisper.get("content");
                content = content.trim();

                if(logger.isLoggable(Level.FINE))
                    logger.fine("收到私聊："+content);

                String name = (String) m1908.whisper.get("name");
                name = StringUtil.getMD5(name.trim());

                //if (name.equals("fb8298566324c91fdd37f863bab9da0b") && content.startsWith("p")) {
                if (content.startsWith("p")) {
                    try {
                        targetTotalTrade = Integer.parseInt(content.substring(1).trim());
                        totalTrade.set(0);

                        if (logger.isLoggable(Level.FINE))
                            logger.fine("目标交易数量：" + targetTotalTrade);
                    } catch (Exception e) {
                        System.out.println("认证失败，请重试");
                        return;
                    }

                    System.out.println("认证已通过！");

                    tradeWatcher = new TradeWatcher();
                    tradeWatcher.setDaemon(true);
                    tradeWatcher.start();

                    tradeProcessor = new TradeProcessor();
                    tradeProcessor.setDaemon(true);
                    tradeProcessor.start();

                }

                if(content.equals("n")){
                    exit();
                }
            }
        });

        registerMessageHander(M2811.class, new IMessageHandler<M2811>() {
            @Override
            public void handle(M2811 m2811) {
                // 交易完成
                if (m2811.result == 8) {

                    logger.fine("交易完成，现发起回收请求");

                    M2402 m2402 = new M2402();
                    m2402._npcId = 0x2eec;
                    m2402._type = 0x0835;
                    send(m2402);

                    tradeWatcher.resetTrading();

                    if (tradeItemNum > 0) {
                        int total = totalTrade.addAndGet(tradeItemNum);
                        System.out.printf("目标交易数量：%d,累计完成交易%d把\r", targetTotalTrade, total);
                        if (targetTotalTrade > 0 && total >= targetTotalTrade) {
                            System.out.println();
                            exit();
                            return;
                        }

                        if(total>=200){ //TODO 记得移除
                            totalTrade.set(0);
                            exit();
                        }
                    }

                    canBeginNextTrade = true;
                }

                if (m2811.result == 2) {
                    logger.fine("交易开始了");
                    tradeWatcher.watchTrading();
                }

                if (m2811.result == 3) {
                    if (!isCanceledBySelf) {
                        logger.fine("交易被取消了");
                        cancelTrading();
                    }
                    isCanceledBySelf = false;
                }

                if (m2811.result == 6) {
                    logger.fine("交易玩家不见了");
                    cancelTrading();
                }

                if (m2811.result == 10) {
                    logger.fine("交易玩家太远");
                    cancelTrading();
                }

                if (m2811.result == 12) {
                    logger.fine("交易失败");
                    cancelTrading();
                }

                if (m2811.result == 13) {
                    logger.fine("交易玩家阵亡");
                    cancelTrading();
                }
            }
        });

        registerMessageHander(M2812.class, new IMessageHandler<M2812>() {
            @Override
            public void handle(M2812 m2812) {
                List<Map> items = m2812.arr;
                for (Map item : items) {
                    Long type = (Long) item.get("type");
                    if (type < 20014 || type > 20016) {
                        logger.fine("交易物品栏出现非武器物品，现取消交易");
                        tradeItemNum = 0;
                        isCanceledBySelf = true;
                        cancelTrading();
                        return;
                    }
                }
                tradeItemNum = items.size();
            }
        });

        registerMessageHander(M2813.class, new IMessageHandler<M2813>() {
            @Override
            public void handle(M2813 m2813) {
                canBeginNextTrade = true;
            }
        });

        registerMessageHander(M2814.class, new IMessageHandler<M2814>() {
            @Override
            public void handle(M2814 m2814) {
                TradePlayer tp = new TradePlayer();
                tp.targetId = m2814.requestId;
                tp.targetName = m2814.requestName;
                tradePlayers.add(tp);

                if (logger.isLoggable(Level.FINE))
                    logger.fine("收到交易请求：" + tp);
            }
        });

        registerMessageHander(M2815.class, new IMessageHandler<M2815>() {
            @Override
            public void handle(M2815 m2815) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("收到交易请求，请求数：" + m2815.requestNum);

                if (m2815.requestNum > 0)
                    send(new M2814());
            }
        });

    }

    private void cancelTrading() {
        if (logger.isLoggable(Level.FINE))
            logger.fine("交易被取消，交易玩家为：" + currentTradePlayer);

        send(new M2806());
        canBeginNextTrade = true;
        tradeWatcher.resetTrading();
    }

    private class TradeWatcher extends Thread {

        private volatile long lastBeginTrade;

        @Override
        public void run() {
            while (!isInterrupted() && !exitTradeWatcher) {
                long now = System.currentTimeMillis();
                if (lastBeginTrade != 0 && now - lastBeginTrade > 3000) {
                    logger.fine("交易超时，现取消交易");
                    isCanceledBySelf = true;
                    cancelTrading();
                }
                ThreadUtil.sleep(200);
            }
        }

        public void watchTrading() {
            lastBeginTrade = System.currentTimeMillis();
        }

        public void resetTrading() {
            lastBeginTrade = 0;
        }
    }

    private class TradeProcessor extends Thread {
        @Override
        public void run() {
            while (!isInterrupted() && !exitTradeProcessor) {
                try {
                    TradePlayer tp = tradePlayers.poll(5, TimeUnit.SECONDS);
                    if (tp != null) {
                        currentTradePlayer = tp;

                        M2801 m2801 = new M2801();
                        m2801.userId = (int) tp.targetId;

                        // 接受交易
                        send(m2801);

                        // 确认交易
                        send(new M2804());

                        if (logger.isLoggable(Level.FINE))
                            logger.fine("当前发起交易玩家：" + tp);

                        canBeginNextTrade = false;

                        while (!canBeginNextTrade && !exitTradeProcessor)
                            ThreadUtil.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class TradePlayer {

        public long targetId;
        public String targetName;

        @Override
        public String toString() {
            return "TradePlayer{" +
                    "targetId=" + targetId +
                    ", targetName='" + targetName + '\'' +
                    '}';
        }
    }
}

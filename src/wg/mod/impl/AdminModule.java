package wg.mod.impl;

import com.game.net.m19.M1908;
import com.game.net.m24.M2402;
import com.game.net.m24.M2409;
import flash.geom.Point;
import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.net.socket.protocol.IMessageHandler;
import wg.mod.BaseModule;
import wg.mod.ModuleManager;
import wg.pojo.Account;
import wg.util.ContextUtil;
import wg.util.IContextCons;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Franklyn <br/>
 * Date: 14-9-15<br/>
 * Time: 下午9:41<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class AdminModule extends BaseModule {

    @Override
    public void init() {
        registerMessageHander(M1908.class, new IMessageHandler<M1908>() {
            @Override
            public void handle(M1908 m) {
                String cmd = (String) m.whisper.get("content");
                if (cmd != null) {
                    cmd = cmd.trim();
                    if (cmd.equals("gwlc")) {// 去卧龙村
                        // 去烈焰城传送员处
                        M2409 m2409 = new M2409();
                        m2409._uiId = 9;
                        m2409._funId = 111;

                        // 去卧龙村
                        M2402 m2402 = new M2402();
                        m2402._npcId = 0x2ee1;
                        m2402._type = 0x0b;

                        executeCmd2All(m2409);
                        executeCmd2All(m2402);
                        return;
                    }

                    if (cmd.equals("gjx")) {  // 去军需官1号
                        M2409 m2409 = new M2409();
                        m2409._uiId = 9;
                        m2409._funId = 0x68;

                        executeCmd2All(m2409);
                        return;
                    }

                    if (cmd.equals("gtj")) {  // 去天机老人处
                        M2409 m2409 = new M2409();
                        m2409._uiId = 9;
                        m2409._funId = 0x65;

                        executeCmd2All(m2409);
                        return;
                    }

                    if (cmd.startsWith("gtxy")) {
                        try {
                            cmd = cmd.substring(5);
                            String[] xy = cmd.split(" ");
                            executeNavigateCmd2All(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    if (cmd.startsWith("ys")) {
                        //all2YaSong();
                        return;
                    }

                    if (cmd.startsWith("tr")) {
                        try {
                            String[] arr = cmd.split(" ");
                            String tradeTargetPlayer = arr[1];
                            TradeModule.reset();
                            TradeModule.MAX_TRADE_NUM = 230;
                            TradeModule.TRADE_PLAYER = tradeTargetPlayer;

                            Set<Account> accs = ContextUtil.getOnlineAccounts();
                            for (final Account acc : accs) {
                                Map ctx = ContextUtil.getContextByAccountId(acc.getUserId());
                                TradeModule module = (TradeModule) ModuleManager.getModule(ctx, TradeModule.class);
                                if (module != null)
                                    module.run();
                            }

                        } catch (Exception e) {
                            globalLogger.severe("执行指令tr失败了");
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        });
    }

    void executeCmd2All(BasicMessage msg) {
        List<Account> accs = ContextUtil.getAllAccounts();
        for (Account acc : accs) {
            Map ctx = ContextUtil.getContextByAccountId(acc.getUserId());
            SocketX soc = (SocketX) ctx.get(IContextCons.game_socket);
            soc.send(msg);
        }
    }

    void executeNavigateCmd2All(int x, int y) {
        List<Account> accs = ContextUtil.getAllAccounts();
        for (Account acc : accs) {
            NavigatorModule nav = (NavigatorModule) ContextUtil.getModuleByAccountId(acc.getUserId(), NavigatorModule.class);
            nav.go(new Point(x, y));
        }
    }

}

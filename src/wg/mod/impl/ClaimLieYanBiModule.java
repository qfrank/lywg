package wg.mod.impl;

import com.game.cons.CaseType;
import com.game.net.m14.M1400;
import com.game.net.m17.M1717;
import com.game.net.m24.M2402;
import org.frkd.net.socket.SocketX;
import org.frkd.util.ThreadUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.pojo.PackageInfo;
import wg.pojo.PackageItem;
import wg.pojo.Player;
import wg.util.CommandUtil;

/**
 * User: Frank Tang <br/>
 * Date: 15/2/17<br/>
 * Time: ÉÏÎç11:19<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class ClaimLieYanBiModule extends BaseModule implements IExclusiveModule {

    private long totalLyb;

    private Player player;

    private M2402 m2402;

    private int totalBuy;

    @Override
    public void run() {
        logger.fine("ClaimLieYanBiModule started.");

        player = getCurrentPlayer();
        if (player.level < 70) {
            loginNextRole();
            return;
        }

        final SocketX socketX = getCurrentGameSocket();
        CommandUtil.goZY(socketX);

        m2402 = new M2402();
        m2402._npcId = 0x2eee;
        m2402._type = 0x04b1;
        socketX.send(m2402);

        m2402._type = 0x0515;
        socketX.send(m2402);

        m2402._type = 0x0579;
        socketX.send(m2402);

        addEventListener(IEventType.PLAYER_INFO_INITED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                totalLyb = player.lyb;

                placeFbjTogether();

            }
        });

        send(new M1400());

    }

    private void buyFbj() {

        if (totalLyb > 0) {

            addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
                @Override
                public void onAction(Event evt) {
                    PackageInfo pi = (PackageInfo) evt.eventData;
                    int leftBlanks = pi.getLeftBlankNum();
                    if (leftBlanks <= 0) {
                        globalLogger.severe(player.roleName + "¹ºÂòÁÒÑæ±Ò±³°ü¿Õ¸ñ²»×ã£¡" + getBasicLoginInfo());
                        totalLyb = 0;
                        return;
                    }

                    long canBuy = totalLyb / 300;
                    canBuy = Math.min(canBuy, leftBlanks);

                    totalLyb = totalLyb - canBuy * 300;
                    totalBuy += canBuy;
                    for (int i = 0; i < canBuy; i++) {//¹ºÂòÁÒÑæ±Ò
                        m2402._type = 0x0a;
                        send(m2402);
                    }

                    placeFbjTogether();

                }
            });

            CommandUtil.getPackageInfo(getCurrentGameSocket());

            return;
        }

        logger.fine(getBasicLoginInfo()+",¡¾" + player.roleName + "¡¿¹ºÂòÁË" + totalBuy + "ÕÅ¸±±¾¾í");

        ThreadUtil.startWithGlobal(new Runnable() {
            @Override
            public void run() {
                ThreadUtil.sleep(3000);
                loginNextRole();
            }
        });

    }

    private void placeFbjTogether() {

        addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                PackageInfo packageInfo = (PackageInfo) evt.eventData;
                PackageItem[] items = packageInfo.getItems();

                for (int i = 0; i < items.length; i++) {
                    PackageItem item = items[i];
                    if (item.type != 10189 || item.num == 99 || item.num == 0)
                        continue;

                    for (int j = i + 1; j < items.length; j++) {
                        PackageItem item2 = items[j];
                        if (item2.type != 10189 || item2.num + item.num > 99 || item2.num == 0)
                            continue;

                        M1717 m1717 = new M1717();
                        m1717._pos = CaseType.PACKAGE;
                        m1717._index = item2.index;
                        m1717._new_index = item.index;
                        send(m1717);

                        item.num += item2.num;
                        item2.num = 0;
                    }
                }

                buyFbj();

            }
        });

        CommandUtil.getPackageInfo(getCurrentGameSocket());

    }

}

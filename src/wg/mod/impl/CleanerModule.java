package wg.mod.impl;

import com.game.cons.CaseType;
import com.game.net.m17.M1716;
import com.game.net.m17.M1728;
import com.game.net.m17.M1732;
import org.frkd.net.socket.SocketX;
import org.frkd.util.ThreadUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.pojo.*;
import wg.util.CommandUtil;
import wg.util.WeaponCounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CleanerModule extends BaseModule  implements IExclusiveModule {

//    private static final AtomicInteger total60LevelWeapon = new AtomicInteger(0);
//
//    private static final AtomicInteger total50LevelWeaponAndClothes = new AtomicInteger(0);

    @Override
    public void run() {

        addEventListener(IEventType.PACKAGE_LOADED, new IOnceListener() {

            @Override
            public void onAction(Event evt) {
                final SocketX socket = getCurrentGameSocket();
                final Account acc = getCurrentAccount();
                final Player player = getCurrentPlayer();
                final PackageInfo pi = (PackageInfo) evt.eventData;
                final PackageItem[] items = pi.getItems();

                CommandUtil.goZY(socket);

                addEventListener(IEventType.STORE_LOADED, new IOnceListener() {
                    @Override
                    public void onAction(Event evt) {
                        final M1732 m = (M1732) evt.eventData;
                        ThreadUtil.startWithGlobal(new Runnable() {
                            @Override
                            public void run() {
                                M1716 m1716 = new M1716();
                                m1716._pos = CaseType.STORE;
                                m1716._new_pos = CaseType.PACKAGE;

                                boolean[] blanks = new boolean[PackageInfo.packLen];
                                Arrays.fill(blanks, true);
                                for (PackageItem item : items) {
                                    blanks[item.index] = false;
                                }

                                final List<PackageItem> fetchedItems = new ArrayList<PackageItem>();
                                // 无阻塞最多取22把?
                                final int max = 10;
                                int lastIdx = 0, c = 0;
                                for (PackageItem storeItem : m.items) {
                                    if (storeItem.type >= 20014 && storeItem.type <= 20016) {
                                        for (int i = lastIdx; i < blanks.length; i++) {
                                            if (blanks[i]) {
                                                lastIdx = i + 1;
                                                m1716._index = storeItem.index;
                                                m1716._new_index = i;
                                                getCurrentGameSocket().send(m1716);

                                                storeItem.index = i;
                                                fetchedItems.add(storeItem);
                                                blanks[i] = false;

                                                ++c;
                                                if (c >= max) {
                                                    c = 0;
                                                    ThreadUtil.sleep(1000);
                                                }

                                                break;
                                            }
                                        }
                                    }

                                }

                                fetchedItems.addAll(Arrays.asList(items));

                                // 开始统计60武器
                                ThreadUtil.startWithGlobal(new Runnable() {

                                    @Override
                                    public void run() {
                                        int total = 0;

                                        for (int i = 0; i < fetchedItems.size(); i++) {
                                            PackageItem item = (PackageItem) fetchedItems.get(i);
                                            ItemInfo ii = ItemModule.getItemInfo(item.type);
                                            int type = item.type;
                                            // 10057为新手传送石
                                            // 10055随机传送石
                                            // 10184离线经验丹
                                            // 10036天山雪莲(大)
                                            // 10189副本传送卷
                                            if (type == 10036 || (type >= 10300 && type <= 10302) || (type >= 10072 && type <= 10074) || (type >= 10100 && type <= 10107)
                                                    || (type >= 10117 && type <= 10119) || type >= 10122 && type <= 10124) {
                                                M1728 m1728 = new M1728();
                                                m1728._id = item.id;
                                                logger.info("账号：[" + acc.getUserId() + "] 玩家[" + player.roleName + "]吃掉" + ii.name + " id:" + item.id);
                                                socket.send(m1728);
                                                ThreadUtil.sleep(1000);
                                                continue;
                                            }

                                            if ((ii.requiredLevel < 60) && type != 10057 && type != 10055 && type != 10184 && type != 10189) {
                                                CommandUtil.sellItem(socket, item);
                                                logger.info("账号：[" + acc.getUserId() + "] 玩家[" + player.roleName + "] 卖掉物品：" + ii.name);
                                            }
                                            if (type >= 20014 && type <= 20016) {
                                                total++;
                                            }
                                        }

                                        if (total >= 6) {
                                            WeaponCounter.addCount(getCurrentAccount().getAccFile(), total);
                                        }

                                        CommandUtil.goCSY(socket);

                                        ThreadUtil.sleep(1000);
                                        exit();
                                    }
                                });

                            }
                        });

                    }
                });

                CommandUtil.getStoreInfo(socket);

            }
        });

        CommandUtil.getPackageInfo(getCurrentGameSocket());

    }
}

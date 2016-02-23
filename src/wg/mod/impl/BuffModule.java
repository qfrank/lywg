package wg.mod.impl;

import com.game.net.m14.M1420;
import com.game.net.m14.M1422;
import org.frkd.net.socket.protocol.IMessageHandler;
import wg.mod.BaseModule;
import wg.pojo.BuffInfo;

/**
 * 自动吃双倍及天山雪莲模块
 */
public class BuffModule extends BaseModule {

    private ItemModule itemModule;

    @Override
    public void init() {

        itemModule = getData(ItemModule.class);

        registerMessageHander(M1420.class, new IMessageHandler<M1420>() {
            @Override
            public void handle(M1420 m) {
                // SocketX socket = getCurrentGameSocket();
                boolean exist302 = false, exist101 = false;
                for (BuffInfo bi : m.buffInfoList) {
                    if (bi.buffId == 302) {
                        exist302 = true;
                    }
                    if (bi.buffId == 101) {
                        exist101 = true;
                    }
                    // M1423 m1423 = new M1423();
                    // m1423._buffId = (int)bi.buffId;
                    // socket.send(m1423);
                }
//					if (!exist101) {// 5小时双倍经验
//						itemModule.buyAndUsingItem(0x44, 1);
//					}
                if (!exist302) {// 天山雪莲（大）
                    itemModule.buyAndUsingItem(0x4B, 1);
                }
            }
        });

        // registerMessageHander(M1423.class, new IMessageHandler<M1423>() {
        // @Override
        // public void handle(M1423 m) {
        // SocketX socket = getCurrentGameSocket();
        // M1424 m1424 = new M1424();
        // m1424._type = (int)m.type;
        // socket.send(m1424);
        // }
        // });

        registerMessageHander(M1422.class, new IMessageHandler<M1422>() {
            @Override
            public void handle(M1422 m) {

//					if (m.buffId == 101)
//						itemModule.buyAndUsingItem(0x44, 1);

                if (m.buffId == 302)
                    itemModule.buyAndUsingItem(0x4B, 1);

            }
        });

    }

}

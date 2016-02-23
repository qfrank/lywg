package wg.mod.impl;

import com.game.net.m15.M1530;
import org.frkd.net.socket.protocol.IMessageHandler;
import wg.mod.BaseModule;
import wg.util.NpcDataHolder;

/**
 * User: Franklyn <br/>
 * Date: 14-11-28<br/>
 * Time: 上午11:39<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class NpcDataModule extends BaseModule {
    @Override
    public void init() {
        registerMessageHander(M1530.class, new IMessageHandler<M1530>() {
            @Override
            public void handle(M1530 message) {
                NpcDataHolder.setNpcDataList(message.npcDataList);
            }
        });
    }
}

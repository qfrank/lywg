package wg.mod.impl;

import com.game.net.m21.M2101;
import com.game.net.m21.M2110;
import com.game.net.m21.M2112;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;

/**
 * User: Frank Tang <br/>
 * Date: 15/2/21<br/>
 * Time: 下午6:30<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class TestModule extends BaseModule implements IExclusiveModule {

    @Override
    public void run() {
        send(new M2110());
        send(new M2101());
        send(new M2112());
    }
}

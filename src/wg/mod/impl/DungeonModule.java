package wg.mod.impl;

import flash.geom.Point;
import org.frkd.method.ICallBack;
import wg.mod.BaseModule;
import wg.mod.IExclusiveModule;
import wg.mod.ModuleManager;
import wg.pojo.Player;
import wg.util.CommandUtil;

/**
 * User: Frank Tang <br/>
 * Date: 15/2/23<br/>
 * Time: 下午4:37<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class DungeonModule extends BaseModule implements IExclusiveModule {

    private int odd;

    @Override
    public void run() {
        canStop = false;

        Player player = getCurrentPlayer();
        if (player.sceneId != 26) {
            CommandUtil.go2Scene(getCurrentGameSocket(), 26);
        }

        NavigatorModule nav = (NavigatorModule) ModuleManager.getModule(NavigatorModule.class);
        Point s = new Point(15,185);
        Point e = new Point(17,185);
        Point[] p = {e,s};
        cycleRun(nav, p);
    }

    private void cycleRun(final NavigatorModule nav, final Point[] path){
        odd++;
        odd = odd%2;
        Point e = path[odd];
        nav.goAndDo(e, new ICallBack() {
            @Override
            public Object call(Object param) {
                cycleRun(nav, path);
                return null;
            }
        });
    }

}

package wg.bgthread;

import wg.mod.ModuleManager;
import wg.mod.impl.XiaoHaoGuaJiModule;
import wg.util.ContextUtil;
import wg.util.IContextCons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Frank Tang <br/>
 * Date: 15-1-2<br/>
 * Time: ÏÂÎç3:07<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class LocationWatcher extends Thread {

    private static Map<String, Long> locationMap = new ConcurrentHashMap<String, Long>();

    private volatile boolean started;

    public LocationWatcher(){
        setDaemon(true);
    }

    @Override
    public synchronized void start() {
        if(started)
            return;
        started = true;
        super.start();
    }

    public static void refreshLocation(String accId) {
        locationMap.put(accId, System.currentTimeMillis());
    }

    @Override
    public void run() {
        while (true){
            long now = System.currentTimeMillis();
            for (String accId : locationMap.keySet()) {
                Long lastRefreshTime = locationMap.get(accId);

                if (lastRefreshTime == null || now - lastRefreshTime < 30000)
                    continue;

                Map ctx = ContextUtil.getContextByAccountId(accId);
                if (ctx == null)
                    continue;

                Boolean robotMode = (Boolean) ctx.get(IContextCons.robot_mode);
                if (robotMode == null || !robotMode)
                    continue;

                XiaoHaoGuaJiModule module = (XiaoHaoGuaJiModule) ModuleManager.getModule(ctx, XiaoHaoGuaJiModule.class);
                if (module == null)
                    continue;

                module.requestStartRobotMode();
            }

            try {
                sleep(30000L);
            } catch (InterruptedException e) {

            }
        }

    }
}

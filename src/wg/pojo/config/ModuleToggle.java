package wg.pojo.config;

import wg.mod.BaseModule;
import wg.mod.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Frank Tang <br/>
 * Date: 15-1-13<br/>
 * Time: 上午11:35<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public enum ModuleToggle {

    gj,

    ys,

    trade,

    cl,

    tr,

    aat,

    lyb,

    dg,

    test;

    private static Map<ModuleToggle,Class<? extends BaseModule>> MODULE_MAPPING = new HashMap<ModuleToggle, Class<? extends BaseModule>>();

    static {
        MODULE_MAPPING.put(gj, XiaoHaoGuaJiModule.class);
        MODULE_MAPPING.put(ys, YaSongLingShouModule.class);
        MODULE_MAPPING.put(trade, TradeModule.class);
        MODULE_MAPPING.put(cl, CleanerModule.class);
        MODULE_MAPPING.put(tr, TrainingModule.class);
        MODULE_MAPPING.put(aat, SixtyLevelWeaponAcceptTradeModule.class);
        MODULE_MAPPING.put(lyb, ClaimLieYanBiModule.class);
        MODULE_MAPPING.put(dg, DungeonModule.class);
        MODULE_MAPPING.put(test, TestModule.class);
    }

    public Class<? extends BaseModule> getModuleClazz(){
        return MODULE_MAPPING.get(this);
    }

}

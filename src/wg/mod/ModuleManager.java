package wg.mod;

import org.frkd.util.ClassUtil;
import org.frkd.util.Global;
import org.frkd.util.log.LoggerUtil;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuleManager {

    private static final Logger logger = LoggerUtil.getLogger(ModuleManager.class);

    static List<Class<? extends IModule>> MODULE_CLASS_LIST = new ArrayList<Class<? extends IModule>>();

    static Set<Class<? extends BaseModule>> EXCLUSIVE_MODULE_CLASS_LIST = new HashSet<Class<? extends BaseModule>>();

    static {

        List<Class<?>> modClasses = ClassUtil.getClassesForPackage("wg.mod.impl");

        Collections.sort(modClasses, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                try {
                    BaseModule m = (BaseModule) o1.newInstance();
                    BaseModule m2 = (BaseModule) o2.newInstance();
                    return m.getLoadSequence() - m2.getLoadSequence();
                } catch (InstantiationException e) {
                    logger.log(Level.SEVERE, "Error happend in class " + getClass().getName(), e);
                } catch (IllegalAccessException e) {
                    logger.log(Level.SEVERE, "Error happend in class " + getClass().getName(), e);
                }
                return 0;
            }
        });

        for (Class c : modClasses) {
            MODULE_CLASS_LIST.add(c);
            try {
                BaseModule m = (BaseModule) c.newInstance();
                if (m instanceof IExclusiveModule) {
                    EXCLUSIVE_MODULE_CLASS_LIST.add(c);
                }

                if (logger.isLoggable(Level.FINEST))
                    logger.finest("装载模块:" + c.getName() + " , 模块序列:" + m.getLoadSequence());
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }

        logger.finest("装载所有模块完毕.");

    }

    public static BaseModule getModule(Class<? extends BaseModule> clazz) {
        BaseModule module = Global.getData(clazz);
        if (module == null) {
            try {
                module = clazz.newInstance();
                //logger.finest("Init module clazz " + clazz.getName());
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Global.setData(clazz, module);
        }
        return module;
    }

    public static BaseModule getModule(Map context, Class<? extends BaseModule> clazz) {
        if(context!=null)
            return (BaseModule) context.get(clazz);
        return null;
    }

    public static void initModules(Map context) {
        List<BaseModule> instances = new ArrayList();
        for (Class c : MODULE_CLASS_LIST) {
            BaseModule m = getModule(c);
            instances.add(m);
            m.setContext(context);

            if (EXCLUSIVE_MODULE_CLASS_LIST.contains(c)) {
                m.disableModule();
            }
        }

        for (BaseModule m : instances) {
            m.init();
        }
    }

    public static void destroyModules() {
        for (Class c : MODULE_CLASS_LIST) {
            IModule m = getModule(c);
            m.destroy();
        }
    }

}

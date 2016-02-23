package wg;

import com.game.legend.net.ProtocolMap;
import org.frkd.net.socket.protocol.IProtocolMap;
import org.frkd.util.Global;
import org.frkd.util.ThreadUtil;
import org.frkd.util.log.LoggerUtil;
import wg.event.Event;
import wg.event.IEventType;
import wg.event.IOnceListener;
import wg.mod.BaseModule;
import wg.mod.ModuleManager;
import wg.mod.ParserManager;
import wg.mod.impl.LoginModule;
import wg.pojo.Account;
import wg.pojo.config.AccountSchema;
import wg.pojo.config.ModuleToggle;
import wg.pojo.config.WgConfig;
import wg.util.ContextUtil;
import wg.util.IContextCons;
import wg.util.WeaponCounter;
import wg.util.WgConfigHolder;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

/**
 * @author lovefree103@gmail.com
 */
public class Main {

    private static Logger logger;

    private static int loginNumMod;

    public static void main(String[] args) {

        init(args);

        WgConfig wgConfig = WgConfigHolder.getWgConfig();

        final int server = wgConfig.server;
        final int maxOnlinePlayer = wgConfig.maxOnlinePlayer;

        logger.info("Æô¶¯ÓÎÏ·Çø£º" + server + ".");

        List<AccountSchema> accountSchemas = wgConfig.accountSchemas;

        for (AccountSchema a : accountSchemas) {

            WeaponCounter.initCounter(a.accoutFiles);

            for (String af : a.accoutFiles) {

                List<Account> accs = ContextUtil.loadAccounts(server, af);

                for (final Account acc : accs) {
                    while (ContextUtil.getCurrentOnlinePlayerNum() >= maxOnlinePlayer)
                        ThreadUtil.sleep(100);

                    acc.setAccountSchema(a);

                    ContextUtil.incrementOnlinePlayer(acc);

                    loginAccount(acc, server, a, a.getAvatarIndex());

                    ThreadUtil.sleep(wgConfig.loginPeriod);

                    boolean training = a.getModuleToggle() == ModuleToggle.tr;
                    if (training) {
                        loginNumMod++;
                        if (wgConfig.trainingGroupPeriod != null && loginNumMod == 14){
                            ThreadUtil.sleep(wgConfig.trainingGroupPeriod);
                        }
                        loginNumMod = loginNumMod % 15;
                    }

                }

            }

        }
    }

    private static void makeClosableIfNeccesarry() {
        Boolean closable = Boolean.getBoolean("closable");
        if (closable) {
            Thread deamonThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String line = scanner.nextLine();
                        if (line != null && line.equals("exit")) {
                            break;
                        }
                    }

                    LoggerUtil.shutdown();

                    System.exit(0);
                }
            });
            deamonThread.setDaemon(false);
            deamonThread.start();
        }
    }

    private static void init(String args[]) {

        makeClosableIfNeccesarry();

        //LoggerUtil.denyLog();

        LoggerUtil.initLoggerConfig();

        logger = LoggerUtil.getLogger(Main.class);

        String configFile = "lywg.conf";
        if (args != null && args.length > 0) {
            configFile = args[0];
        }

        WgConfigHolder.init(configFile);


    }

    public static void loginAccount(final Account acc, final int server, final AccountSchema schema, final int avatarIndex) {
        ThreadUtil.start(new Runnable() {

            @Override
            public void run() {

                Global.setData(IContextCons.login_server, server);
                Global.setData(IContextCons.ly_account, acc);
                Global.setData(IContextCons.account_schema, schema);
                Global.setData(IContextCons.robot_param, schema.getRobotParam());

                if (schema.getModuleToggle() != null)
                    Global.setData(IContextCons.module_toggle, schema.getModuleToggle());

                Map context = Global.getLocalMap();
                ContextUtil.bindContext2AccountId(acc.getUserId(), context);

                final IProtocolMap protocol = new ProtocolMap();
                ParserManager pm = new ParserManager(protocol);
                Global.setData(IContextCons.parser_manager, pm);
                Global.setData(IContextCons.event_dispatch_map, new ConcurrentHashMap());
                Global.setData(IContextCons.disabled_module_list, new CopyOnWriteArraySet<BaseModule>());
                Global.setData(IContextCons.selected_avatar_index, avatarIndex);

                ModuleManager.initModules(context);

                executeSchemaAfterLogin(schema);

            }

        });
    }

    private static void executeSchemaAfterLogin(AccountSchema schema) {

        final LoginModule loginModule = (LoginModule) ModuleManager.getModule(LoginModule.class);

        final ModuleToggle moduleToggle = schema.getModuleToggle();

        BaseModule module = null;
        if (moduleToggle != null) {
            module = ModuleManager.getModule(moduleToggle.getModuleClazz());
            Global.setData(IContextCons.current_enabled_exclusive_module, moduleToggle.getModuleClazz());
            module.enableModule();
        }

        final BaseModule fModule = module;

        loginModule.addEventListener(IEventType.PLAYER_INFO_INITED, new IOnceListener() {
            @Override
            public void onAction(Event evt) {
                if (fModule != null)
                    fModule.run();
            }
        });

        loginModule.run();
    }

}

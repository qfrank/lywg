package wg.mod;

import org.frkd.net.socket.SocketX;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.net.socket.protocol.IMessageHandler;
import org.frkd.util.log.LoggerUtil;
import wg.event.Event;
import wg.event.IListener;
import wg.event.IOnceListener;
import wg.mod.impl.LoginModule;
import wg.pojo.Account;
import wg.pojo.Player;
import wg.pojo.config.AccountSchema;
import wg.pojo.config.ModuleToggle;
import wg.pojo.config.RobotParam;
import wg.util.ContextUtil;
import wg.util.IContextCons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseModule implements IModule {

    private Map context;

    protected Logger logger;

    protected static Logger globalLogger = LoggerUtil.getLogger(null);

    /**
     * 模块加载顺序，值越小越优先加载
     */
    protected int loadSequence = 0;

    protected boolean canStop = false;

    void setContext(Map context) {
        this.context = context;

        String loggerName = String.format("lywg.%d.%s", getCurrentServer(), getCurrentAccount().getUserId());
        this.logger = LoggerUtil.getMyLogger(loggerName);

        this.context.put(IContextCons.account_logger, this.logger);
    }

    protected String getBasicLoginInfo() {
        Integer server = getCurrentServer();
        Account account = getCurrentAccount();
        String accId = null;
        if (account != null) {
            accId = account.getUserId();
        }
        String info = "Account id:" + accId + ",server:" + server;
        return info;
    }

    protected void logAll(String info, Level level, Exception error) {
        logger.log(level, info, error);
        globalLogger.log(level, info, error);
    }

    protected void logAll(String info, Level level) {
        logger.log(level, info);
        globalLogger.log(level, info);
    }

    protected Integer getCurrentServer() {
        return getData(IContextCons.login_server);
    }

    protected Integer getCurrentAvatarIndex(){
        return getData(IContextCons.selected_avatar_index);
    }

    protected void loginNextRole(){
        int idx = getData(IContextCons.selected_avatar_index);
        setData(IContextCons.selected_avatar_index, ++idx);

        loginAgain();
    }

    protected void loginAgain(){
        LoginModule loginModule = (LoginModule)ModuleManager.getModule(LoginModule.class);
        loginModule.loginAgain(true);
    }

    public int getLoadSequence() {
        return loadSequence;
    }

    public void dispachEvent(Event evt) {
        Map m = getData(IContextCons.event_dispatch_map);
        List<IListener> l = (List) m.get(evt.eventType);
        if (l != null) {
            for (IListener s : l.toArray(new IListener[0])) {
                s.onAction(evt);
                if (s instanceof IOnceListener) {
                    l.remove(s);
                }
            }
        }
    }

    public synchronized void addEventListener(String eventType, final IListener listener) {
        Map m = getData(IContextCons.event_dispatch_map);
        List<IListener> l = (List) m.get(eventType);
        if (l == null) {
            l = new ArrayList();
            m.put(eventType, l);
        }

        IListener proxy;
        if (listener instanceof IOnceListener) {
            proxy = new IOnceListener() {

                private BaseModule module = BaseModule.this;

                @Override
                public void onAction(Event evt) {
                    if (module.isModuleDisabled()) {
                        return;
                    }
                    listener.onAction(evt);
                }
            };
        } else {
            proxy = new IListener() {

                private BaseModule module = BaseModule.this;

                @Override
                public void onAction(Event evt) {
                    if (module.isModuleDisabled()) {
                        return;
                    }
                    listener.onAction(evt);
                }
            };
        }

        l.add(proxy);
    }

    public <T> T getData(Object key) {
        return (T) context.get(key);
    }

    public void setData(Object key, Object value) {
        context.put(key, value);
    }

    public void registerMessageHander(Class<? extends BasicMessage> clazz, IMessageHandler handler) {
        ParserManager pm = getParserManager();
        pm.registerMessageHander(clazz, this, handler);
    }

    public ParserManager getParserManager() {
        return (ParserManager) context.get(IContextCons.parser_manager);
    }

    public Account getCurrentAccount() {
        return (Account) context.get(IContextCons.ly_account);
    }

    public Player getCurrentPlayer() {
        return (Player) context.get(IContextCons.current_player);
    }

    public SocketX getCurrentGameSocket() {
        return (SocketX) context.get(IContextCons.game_socket);
    }

    public void send(BasicMessage message){
        getCurrentGameSocket().send(message);
    }

    public AccountSchema getSchema() {
        return (AccountSchema) context.get(IContextCons.account_schema);
    }

    public RobotParam getRobotParam() {
        return (RobotParam) context.get(IContextCons.robot_param);
    }

    public ModuleToggle getModuleToggle() {
        return (ModuleToggle) context.get(IContextCons.module_toggle);
    }

    public void disableModule(BaseModule module) {
        CopyOnWriteArraySet<BaseModule> disabledModules = getData(IContextCons.disabled_module_list);
        disabledModules.add(module);
    }

    public void disableModule() {
        CopyOnWriteArraySet<BaseModule> disabledModules = getData(IContextCons.disabled_module_list);
        disabledModules.add(this);
    }

    public void enableModule(BaseModule module) {
        CopyOnWriteArraySet<BaseModule> disabledModules = getData(IContextCons.disabled_module_list);
        disabledModules.remove(module);
    }

    public void enableModule(){
        enableModule(this);
    }

    public boolean isModuleDisabled(BaseModule module) {
        CopyOnWriteArraySet<BaseModule> disabledModules = getData(IContextCons.disabled_module_list);
        return disabledModules.contains(module);
    }

    protected void startExclusiveModule(Class<? extends BaseModule> module) {
        Class<? extends BaseModule> exclusiveModule = getData(IContextCons.current_enabled_exclusive_module);
        if (exclusiveModule != null) {
            BaseModule oldModule = ModuleManager.getModule(exclusiveModule);

            //TODO checking stop method invoked successfully
            oldModule.stop();

            disableModule(oldModule);
        }

        setData(IContextCons.current_enabled_exclusive_module, module);
        BaseModule newModule = ModuleManager.getModule(module);
        enableModule(newModule);
        newModule.run();
    }

    protected void exit(){
        ContextUtil.exit(getCurrentAccount().getUserId());
    }

    protected boolean isRobotMode(){
        Boolean robotMode = getData(IContextCons.robot_mode);
        if (robotMode == null || !robotMode)
            return false;
        return true;
    }

    public boolean isModuleDisabled() {
        return isModuleDisabled(this);
    }

    @Override
    public void init() {

    }

    @Override
    public void run() {

    }

    @Override
    public boolean stop() {
        return canStop;
    }

    @Override
    public void destroy() {

    }

}

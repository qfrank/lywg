package wg.util;

import org.frkd.net.socket.SocketX;
import org.frkd.util.Global;
import org.frkd.util.PropertyUtil;
import org.frkd.util.StringUtil;
import org.frkd.util.log.LoggerUtil;
import wg.mod.BaseModule;
import wg.mod.ModuleManager;
import wg.pojo.Account;
import wg.pojo.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ContextUtil {

	private static AtomicInteger currentOnlinePlayerNum = new AtomicInteger(0);
	
	private static List<Account> accounts = new ArrayList();

	private static Set<Account> onlineAccounts = new HashSet<Account>();
	
	private static Map<String,Map> contextMap = new ConcurrentHashMap();

	private static final Logger logger = LoggerUtil.getLogger(ContextUtil.class);

	public static List<Account> loadAccounts(int server,String accFile){
		String appDir = PropertyUtil.getProperty("appdir");

		File accConfigFile = new File(appDir+"/conf/"+accFile);
		if(!accConfigFile.exists()){
			throw new RuntimeException("accFile:"+accConfigFile.getAbsolutePath()+"不存在.");
		}

		logger.info("加载账号配置文件："+accConfigFile.getName());

		List<Account> accs = new ArrayList<Account>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(accConfigFile));
			String line;
			while((line=br.readLine())!=null){
				line = line.trim();
				if(!StringUtil.isBlank(line) && !line.startsWith("#")){
					String[] accStr = line.split(",");
					if(server != 1870)
						accStr[2] = "";

					logger.finest("读取到账号：("+accStr[0]+","+accStr[1]+","+accStr[2]+","+accStr[3]+")");

					//if(TradeHistoryService.isFinishedTrading(accStr[0]))
					//	continue;

					Account acc = new Account(accStr[0], accStr[1], accStr[2] == null ? "" : accStr[2], accStr[3]);
					acc.setAccFile(accConfigFile.getName());
					accs.add(acc);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		accounts.addAll(accs);

		return accs;
	}
	
	public static List<Account> getAllAccounts(){
		return accounts;
	}
	
	public static void bindContext2AccountId(String accId,Map globalContext){
		contextMap.put(accId, globalContext);
	}
	
	public static void unBindContext(String accId){
		contextMap.put(accId, new ConcurrentHashMap());
	}

	public static void removeContext(String accId){
		contextMap.remove(accId);
	}
	
	public static Map getContextByAccountId(String accId){
		return contextMap.get(accId);
	}
	
	public static BaseModule getModuleByAccountId(String accId,Class<? extends BaseModule> clazz){
		Map ctx = getContextByAccountId(accId);
		return ModuleManager.getModule(ctx, clazz);
	}

	public static Player getCurrentPlayer(){
		return Global.getData(IContextCons.current_player);
	}

	private static void decrementOnlinePlayer(){
		currentOnlinePlayerNum.decrementAndGet();
	}

	public static void incrementOnlinePlayer(Account account){
		currentOnlinePlayerNum.addAndGet(1);
		onlineAccounts.add(account);
	}

	public static Set<Account> getOnlineAccounts(){
		return Collections.unmodifiableSet(onlineAccounts);
	}

	public static int getCurrentOnlinePlayerNum(){
		return currentOnlinePlayerNum.get();
	}

	public static void exit(String accId){
		Map ctx = getContextByAccountId(accId);

		SocketX soc = (SocketX)ctx.get(IContextCons.game_socket);
		soc.closeQuietly();

		Logger accLogger = (Logger)ctx.get(IContextCons.account_logger);
		LoggerUtil.shutdown(accLogger);

		Account account = (Account) ctx.get(IContextCons.ly_account);
		onlineAccounts.remove(account);

		ModuleManager.destroyModules();

		removeContext(accId);

		Global.getLocalMap().clear();

		decrementOnlinePlayer();
	}
}

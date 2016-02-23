package wg.trade;

import org.frkd.util.PropertyUtil;
import org.frkd.util.log.LoggerUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-18<br/>
 * Time: 下午9:54<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class TradeHistoryService {

    private static final Logger logger = LoggerUtil.getLogger(TradeHistoryService.class);

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    private static final String dataDirPath = PropertyUtil.getProperty("appdir") + File.separatorChar + "data" + File.separatorChar + "trade";

    private static File dataFile;

    private static Set<String> tradeHistoryAccounts = new HashSet<String>();

    static {
        load();
    }

    private static void load() {

        makeDataDir();
        createDataFile();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                tradeHistoryAccounts.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isFinishedTrading(String accId) {
        return tradeHistoryAccounts.contains(accId);
    }

    public static void addToHistory(String accId) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(dataFile, true);
            fw.append(accId + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void makeDataDir() {
        File dataDir = new File(dataDirPath);
        if (dataDir.exists())
            return;
        dataDir.mkdirs();
    }

    private static void createDataFile() {
        String today = simpleDateFormat.format(new Date());
        dataFile = new File(dataDirPath + File.separatorChar + today);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

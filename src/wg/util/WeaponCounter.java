package wg.util;

import org.frkd.util.log.LoggerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * User: Frank Tang <br/>
 * Date: 15/1/31<br/>
 * Time: ÏÂÎç10:14<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class WeaponCounter {

    private static Logger logger = LoggerUtil.getLogger(WeaponCounter.class);

    private static Map<String,AtomicInteger> total60LevelWeaponMap = new HashMap<String, AtomicInteger>();

    public static void initCounter(List<String> accFiles){

        for(String accFile:accFiles){
            total60LevelWeaponMap.put(accFile,new AtomicInteger(0));
        }

    }

    public static void addCount(String accFile,Integer count){
        AtomicInteger totalCounter = total60LevelWeaponMap.get(accFile);
        totalCounter.addAndGet(count);
        logger.info(total60LevelWeaponMap.toString());
    }

}

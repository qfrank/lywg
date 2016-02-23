package wg.util;

import wg.pojo.TradeType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Franklyn <br/>
 * Date: 14-10-7<br/>
 * Time: 上午11:37<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class TradeUtil {

    private static Map<String,List<TradeInfo>> tradeInfoHolder = new HashMap();

    public static List<TradeInfo> getTradeInfoList(String accountId){
        List<TradeInfo> list = tradeInfoHolder.get(accountId);
        if(list == null)
            return Collections.emptyList();
        return list;
    }

    public static void trade(String playerName,Integer tradeNum,TradeType type){

    }

    private static int getTotalNum(TradeType type){
        int total = 0;

        return total;
    }

    private static class TradeInfo{
        private String playerName;

        private Integer tradeNum;

        private String getPlayerName() {
            return playerName;
        }

        private void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        private Integer getTradeNum() {
            return tradeNum;
        }

        private void setTradeNum(Integer tradeNum) {
            this.tradeNum = tradeNum;
        }
    }

}

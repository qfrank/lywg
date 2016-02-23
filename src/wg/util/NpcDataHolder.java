package wg.util;

import org.frkd.util.log.LoggerUtil;
import wg.pojo.NpcData;

import java.util.List;
import java.util.logging.Logger;

/**
 * User: Franklyn <br/>
 * Date: 14-11-28<br/>
 * Time: 上午11:22<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class NpcDataHolder {

    private static Logger logger = LoggerUtil.getLogger(NpcDataHolder.class);

    private static List<NpcData> npcDataList;

    private static volatile boolean inited = false;

    public static synchronized void setNpcDataList(List<NpcData> npcDataList){
        if(!inited){
            NpcDataHolder.npcDataList = npcDataList;
            inited = true;
        }
    }

    public static boolean isInited() {
        return inited;
    }

    public static void setBlock(long sceneId,boolean[][] map){
        if(inited){
            for(NpcData npcData:npcDataList){
                if(npcData.scene == sceneId){
                    map[npcData.x][npcData.y] = true;
                }
            }
        }else{
            logger.warning("trying to invoke setBlock, but npc data is empty!");
        }
    }
}

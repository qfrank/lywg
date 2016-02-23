package wg.util;

import wg.pojo.map.SimpleSceneInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-30<br/>
 * Time: 下午9:37<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class SceneCacheUtil {

    private static Map<Long, SimpleSceneInfo> cacheMap = new HashMap<Long, SimpleSceneInfo>();

    public static void cache(Long sceneId, SimpleSceneInfo sceneInfo) {
        cacheMap.put(sceneId, sceneInfo);
    }

    public static SimpleSceneInfo getSceneInfo(Long sceneId){
        return cacheMap.get(sceneId);
    }

}

package wg.util;

import wg.pojo.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-8<br/>
 * Time: 上午10:54<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class ServerConfigUtil {

    private static Map<Integer, Integer> serverPortMap = new HashMap();

    static {
        serverPortMap.put(336, 9000);
        serverPortMap.put(1480, 9000);

        serverPortMap.put(1870, 9007);
        serverPortMap.put(2516, 9006);
        serverPortMap.put(2553, 9004);//ok
        serverPortMap.put(3033, 9003);//ok
        serverPortMap.put(3382, 9002);//ok
        //serverPortMap.put(3383, 9002);
        serverPortMap.put(3200, 9004);
        serverPortMap.put(4245, 9004);
        serverPortMap.put(5023, 9006);
        serverPortMap.put(5100, 9003);
        serverPortMap.put(5926, 9008);
    }

    public static Server getServer(int serverNumber) {
        Integer port = serverPortMap.get(serverNumber);
        if (port == null) {
            throw new RuntimeException("Unknow server:" + serverNumber);
        }
        String serverIp = String.format("s%d.ly.9377.com", serverNumber);
        return new Server(serverIp, port);
    }

    public static Set<Integer> getServers(){
        return serverPortMap.keySet();
    }

}

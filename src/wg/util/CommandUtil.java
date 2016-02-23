package wg.util;

import com.game.cons.CaseType;
import com.game.net.m17.M1722;
import com.game.net.m17.M1732;
import com.game.net.m24.M2402;
import com.game.net.m24.M2409;
import org.frkd.net.socket.SocketX;
import wg.pojo.PackageItem;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-28<br/>
 * Time: 下午10:14<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class CommandUtil {

    /**
     * 去军需官NPC处
     * @param socketX
     */
    public static void goJX(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 0x68;
        socketX.send(m2409);
    }

    /**
     * 去烈焰庄园NPC处
     * @param socketX
     */
    public static void goZY(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x09;
        m2409._funId = 0x012d;
        socketX.send(m2409);
    }

    /**
     * 去烈焰城封魔处
     * @param socketX
     */
    public static void goFM(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 0x70;
        socketX.send(m2409);
    }

    /**
     * 去沙漠土城神炉处
     * @param socketX
     */
    public static void  goSL(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 0xCA;
        socketX.send(m2409);
    }

    /**
     * 去烈焰城天机老人处
     * @param socketX
     */
    public static void goTJ(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 0x65;
        socketX.send(m2409);
    }

    /**
     * 去烈焰城传送员处
     * @param socketX
     */
    public static void goCSY(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 111;
        socketX.send(m2409);
    }

    /**
     * 去烈焰城镖局
     * @param socketX
     */
    public static void goBJ(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 0x67;
        socketX.send(m2409);
    }

    /**
     * 去烈焰王城城主处
     */
    public static void goCZ(SocketX socketX){
        M2409 m2409 = new M2409();
        m2409._uiId = 0x9;
        m2409._funId = 0xCB;
        socketX.send(m2409);
    }

    public static void getPackageInfo(SocketX socketX){
        M1732 m1732 = new M1732();
        m1732._pos = CaseType.PACKAGE;
        socketX.send(m1732);
    }

    public static void getStoreInfo(SocketX socketX){
        M1732 m1732 = new M1732();
        m1732._pos = CaseType.STORE;
        socketX.send(m1732);
    }

    public static void go2Scene(SocketX socketX, int type){
        goCSY(socketX);

        M2402 m2402 = new M2402();
        m2402._npcId = 0x2EE1;
        m2402._type = type;
        socketX.send(m2402);
    }

    /**
     * 去卧龙村
     */
    public static void goWLC(SocketX socketX){
        go2Scene(socketX, 0x0b);
    }

    public static void sellItem(SocketX socketX, int itemId, int itemNum){
        M1722 m1722 = new M1722();
        m1722._id = itemId;
        m1722._num = itemNum;
        socketX.send(m1722);
    }

    public static void sellItem(SocketX socketX, PackageItem item){
        M1722 m1722 = new M1722();
        m1722._id = item.id;
        m1722._num = item.num;
        socketX.send(m1722);
    }

}

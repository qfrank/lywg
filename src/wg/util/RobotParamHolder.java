package wg.util;

import com.game.net.m16.M1680;
import org.frkd.util.Global;
import wg.pojo.config.RobotParam;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-30<br/>
 * Time: 下午11:11<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class RobotParamHolder {

    private static M1680 zsRobotParam;

    private static M1680 fsRobotParam;

    private static M1680 dsRobotParam;

    private static void setCommonParams(M1680 defaultRobotParam){
        defaultRobotParam._open = 7167;
        defaultRobotParam._hp_signal = 50;
        defaultRobotParam._hp_item = 10037;
        defaultRobotParam._hp_interval = 500;
        defaultRobotParam._mp_signal = 50;
        defaultRobotParam._mp_item = 10037;
        defaultRobotParam._mp_interval = 500;
        defaultRobotParam._quit = 50;
        defaultRobotParam._quit_item = 10046;

        RobotParam robotParam = Global.getData(IContextCons.robot_param);
        defaultRobotParam._filterLevel = robotParam.filterLevel;

        // 单体技能 0表普攻，11表雷电术，12表刺杀，13表道符
        defaultRobotParam._skill_alone = 0;

        // 群体技能 0表普攻，24表雷光风暴，25表半月弯刀
        defaultRobotParam._skill_group = 0;
    }

    static {
        zsRobotParam = new M1680();
        setCommonParams(zsRobotParam);
        zsRobotParam._skill_alone = 12;
        zsRobotParam._skill_group = 25;

        fsRobotParam = new M1680();
        setCommonParams(fsRobotParam);
        fsRobotParam._skill_alone = 11;
        fsRobotParam._skill_group = 24;

        dsRobotParam = new M1680();
        setCommonParams(dsRobotParam);
        dsRobotParam._skill_alone = 13;
    }

    public static M1680 getRobotParam(byte job){
        if(job == 1)
            return zsRobotParam;
        if(job == 2)
            return fsRobotParam;

        return dsRobotParam;
    }

}

package wg.pojo.config;

import java.util.List;

/**
 * User: Frank Tang <br/>
 * Date: 15-1-12<br/>
 * Time: 下午11:53<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class WgConfig {

    /**
     * 登录的服务器，例如1870
     */
    public Integer server;

    /**
     * 账号配置方案，指定的配置文件必须存在于conf目录下
     */
    public List<AccountSchema> accountSchemas;

    /**
     * 最大登录账号数
     */
    public Integer maxOnlinePlayer = Integer.MAX_VALUE;

    /**
     * 账号登录时间间隔，默认500毫秒
     */
    public Long loginPeriod = 500L;

    /**
     * 开启training module时，登录1组账号的频率
     */
    public Long trainingGroupPeriod;

    /**
     * 默认挂机参数
     */
    public RobotParam defaultRobotParam;

    /**
     * 默认模块开关
     */
    public ModuleToggle defaultModuleToggle;


    /**
     * 默认选中角色下标，0表第1个角色
     */
    public Integer defaultAvatarIndex = 0;

    /**
     * TODO 是否开启日志
     */
    public Integer logging = 1;

    @Override
    public String toString() {
        return "WgConfig{" +
                "server=" + server +
                ", accountSchemas=" + accountSchemas +
                ", maxOnlinePlayer=" + maxOnlinePlayer +
                ", loginPeriod=" + loginPeriod +
                ", trainingGroupPeriod=" + trainingGroupPeriod +
                ", defaultRobotParam=" + defaultRobotParam +
                ", defaultModuleToggle=" + defaultModuleToggle +
                ", defaultAvatarIndex=" + defaultAvatarIndex +
                ", logging=" + logging +
                '}';
    }
}

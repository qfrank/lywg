package wg.pojo.config;

import wg.util.WgConfigHolder;

import java.util.List;

/**
 * User: Frank Tang <br/>
 * Date: 15/1/31<br/>
 * Time: 上午11:54<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class AccountSchema {

    public List<String> accoutFiles;

    private ModuleToggle moduleToggle;

    private RobotParam robotParam;

    private Integer avatarIndex;

    public void setModuleToggle(ModuleToggle moduleToggle) {
        this.moduleToggle = moduleToggle;
    }

    public void setRobotParam(RobotParam robotParam) {
        this.robotParam = robotParam;
    }

    public void setAvatarIndex(Integer avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    public ModuleToggle getModuleToggle() {
        if (moduleToggle == null) {
            return WgConfigHolder.getWgConfig().defaultModuleToggle;
        }
        return moduleToggle;
    }

    public RobotParam getRobotParam() {
        if (robotParam == null)
            return WgConfigHolder.getWgConfig().defaultRobotParam;
        return robotParam;
    }

    public Integer getAvatarIndex() {
        if (avatarIndex == null) {
            return WgConfigHolder.getWgConfig().defaultAvatarIndex;
        }
        return avatarIndex;
    }

    @Override
    public String toString() {
        return "AccountSchema{" +
                "accoutFiles=" + accoutFiles +
                ", moduleToggle=" + moduleToggle +
                ", robotParam=" + robotParam +
                ", avatarIndex=" + avatarIndex +
                '}';
    }
}

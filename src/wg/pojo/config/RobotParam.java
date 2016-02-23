package wg.pojo.config;

public class RobotParam {

	/**
	 * 周围最大玩家个数
	 */
	public Integer maxAroundPlayerNum = 20;
	
	/**
	 * 挂机场景ID<br/>
	 * 0x19/25:赤血魔宫<br/>
	 * 0x1A/26:幽冥宫<br/>
	 * 0x1D/29:邪灵天界<br/>
	 * 0x1B/27:冰火地狱<br/>
	 * 0x1C/28:熔岩地狱<br/>
	 * 0x20:冥界
	 */
	public Integer mapId = 26;
	
	/**
	 * 拾取装备等级
	 */
	public Integer filterLevel = 60;

	@Override
	public String toString() {
		return "RobotParam{" +
				"maxAroundPlayerNum=" + maxAroundPlayerNum +
				", mapId=" + mapId +
				", filterLevel=" + filterLevel +
				'}';
	}
}

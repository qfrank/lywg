package wg.event;

public interface IEventType {

	/**
	 * 角色已登录
	 * 事件数据类型：Player
	 */
	String PLAYER_LOGINED = "PLAYER_LOGINED";
	
	/**
	 * 背包数据已加载
	 * 事件数据类型：PackageInfo
	 */
	String PACKAGE_LOADED = "PACKAGE_LOADED";

	/**
	 * 仓库数据已加载，仓库格数最多120个，索引位置从0开始
	 * 事件数据类型：M1732
	 */
	String STORE_LOADED = "STORE_LOADED";
	
	String PLAYER_INFO_INITED = "PLAYER_INFO_INITED";
	
	/**
	 * 地图场景已切换
	 * 事件数据类型：Long（场景编号）
	 */
	String SCENE_CHANGED = "SCENE_CHANGED";
	
	/**
	 * 出现新的玩家/或火墙
	 * 事件数据类型：M1501
	 */
	String NEW_PLAYER_APPEARED = "NEW_PLAYER_APPEARED";

	/**
	 * 玩家/或火墙消失
	 * 事件数据类型：M1501
	 */
	String PLAYER_DISAPPEARED = "PLAYER_DISAPPEARED";
	
	/**
	 * 挂机已取消
	 */
	String ROBOT_MODE_CANCELED = "ROBOT_MODE_CANCELED";
	
}

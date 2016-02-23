package wg.pojo;

import java.awt.*;

public class Player {

	public Long id;

	public Byte initLevel;
	
	public Byte level;

	/**
	 * 1 表战士, 2 表法师
	 */
	public Byte job;

	/**
	 * 2 indicate female,1 indicate man
	 */
	public Byte sex;
	
	public String roleName;
	
	public volatile PackageInfo packageInfo = new PackageInfo();

	public volatile Boolean isAlive = true;
	
	public volatile Point currentLocation;
	
	public volatile Long exp;
	
	public volatile Long needExp;
	
	public volatile Long gold;
	
	public volatile Long bgold;

	/**
	 * 烈焰币
	 */
	public volatile Long lyb;

	/**
	 * 元宝
	 */
	public volatile Long yb;

	public volatile Long sceneId;//BattleManager.reliveRefHandler（玩家复活）有设置scene_id
	
	public volatile Long hp;
	
	public volatile Long mp;

	@Override
	public String toString() {
		return "Player{" +
				"id=" + id +
				", initLevel=" + initLevel +
				", level=" + level +
				", job=" + job +
				", sex=" + sex +
				", roleName='" + roleName + '\'' +
				", packageInfo=" + packageInfo +
				", isAlive=" + isAlive +
				", currentLocation=" + currentLocation +
				", exp=" + exp +
				", needExp=" + needExp +
				", gold=" + gold +
				", bgold=" + bgold +
				", sceneId=" + sceneId +
				", hp=" + hp +
				", mp=" + mp +
				'}';
	}
}

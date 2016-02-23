package wg.mod.impl;

import wg.mod.BaseModule;

/**
 * 装备信息模块
 */
public class EquipInfoModule extends BaseModule {
	
	@Override
	public void init() {
		/*if(ModuleToggleHolder.EQUIP_MODULE_ENABLED){
			getParserManager().registerMessageHander(M1403.class, new IMessageHandler<M1403>() {
				@Override
				public void handle(M1403 m) {
					boolean flag = false;
					for(EquipInfo e:m.equipArr){
//						if(e.part == 8 && e.type!=20011){//赦魂剑
//							logger.info(getCurrentAccount().getUserId());
//						}
						if(e.part == 8){
							flag = true;
						}
					}
					
					if(!flag){
						logger.severe(getCurrentAccount().toString());
					}
				}
			});
		}*/
	}
}

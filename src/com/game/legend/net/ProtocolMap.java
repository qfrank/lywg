package com.game.legend.net;

import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.net.socket.protocol.BasicProtocolMap;
import org.frkd.util.ClassUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolMap extends BasicProtocolMap{
	
	@Override
	protected void initMessageMap() {
		messageClassMap = new ConcurrentHashMap<Integer, Class<? extends BasicMessage>>();
		List<Class<?>> classes = ClassUtil.getClassesForPackage("com.game.net");
		for(Class c:classes){
			String sn = c.getSimpleName();
			int message = Integer.parseInt(sn.substring(sn.length() - 4));
			messageClassMap.put(message, c);
			//System.out.println("messageClassMap.put("+message+","+sn+".class);");
		}
	}
	
}

package com.legend.fp1;

import flash.geom.Point;
import org.frkd.util.HashList;

import java.util.*;

public class Astar {

	public static int BIAS_VALUE = 14;
	public static int LINE_VALUE = 10;

	private static final boolean BAR = true;

	private ThreadLocal<HashList> localUnlockList = new ThreadLocal<HashList>();
	private ThreadLocal<Map> localLockList = new ThreadLocal<Map>();

//	private HashList unlockList;
//	private Map lockList;

	private boolean[][] mapData;
	private int mapWSize;
	private int mapHSize;
	private boolean inited;

	private static Comparator fComparator = new Comparator<Sign>() {
		@Override
		public int compare(Sign o1, Sign o2) {
			return o1.getF() - o2.getF();
		}
	};

	private HashList getUnlockList(){
		HashList unlockList = localUnlockList.get();
		if(unlockList == null){
			unlockList = new HashList();
			localUnlockList.set(unlockList);
		}
		return unlockList;
	}

	private Map getLockList(){
		Map lockList = localLockList.get();
		if(lockList == null){
			lockList = new HashMap();
			localLockList.set(lockList);
		}
		return lockList;
	}

	public void init(boolean[][] _arg1, int _arg2, int _arg3) {
		this.mapData = _arg1;
		this.mapWSize = _arg2;
		this.mapHSize = _arg3;
		this.inited = true;
	}

	public boolean isInited() {
		return inited;
	}

	public List<Point> seekRoad(Point _arg1, Point _arg2) {
		if (this.mapData[_arg2.x][_arg2.y] == BAR) {
			return (new ArrayList());
		}
		List<Point> _local3 = new ArrayList();

		HashList unlockList = getUnlockList();
		Map lockList = getLockList();

//		this.unlockList = new HashList();
//		this.lockList = new HashMap();
		int _local4 = _arg1.x;
		int _local5 = _arg1.y;
		Sign _local6 = new Sign(_local4, _local5, 0, 0, null);
		lockList.put(((_local4 + "_") + _local5), _local6);
		while (true) {
			this.addUnlockList(this.createSign((_local4 + 1), (_local5 - 1), true, _local6, _arg2));
			this.addUnlockList(this.createSign((_local4 + 1), _local5, false, _local6, _arg2));
			this.addUnlockList(this.createSign((_local4 + 1), (_local5 + 1), true, _local6, _arg2));
			this.addUnlockList(this.createSign((_local4 - 1), (_local5 + 1), true, _local6, _arg2));
			this.addUnlockList(this.createSign(_local4, (_local5 + 1), false, _local6, _arg2));
			this.addUnlockList(this.createSign((_local4 - 1), _local5, false, _local6, _arg2));
			this.addUnlockList(this.createSign((_local4 - 1), (_local5 - 1), true, _local6, _arg2));
			this.addUnlockList(this.createSign(_local4, (_local5 - 1), false, _local6, _arg2));
			if (unlockList.size() == 0) {
				break;
			}
			Collections.sort(unlockList, fComparator);
			_local6 = (Sign) unlockList.remove(0);
			lockList.put(((_local6.getIX() + "_") + _local6.getIY()), _local6);
			_local4 = _local6.getIX();
			_local5 = _local6.getIY();
			if ((((_local4 == _arg2.x)) && ((_local5 == _arg2.y)))) {
				while (_local6 != null) {
					_local3.add(_local6.getSign());
					_local6 = _local6.getP();
				}
				break;
			}
		}
		_local6 = null;
		Collections.reverse(_local3);

		localLockList.remove();
		localUnlockList.remove();

//		this.unlockList = null;
//		this.lockList = null;
		return (_local3);
	}

	private void addUnlockList(Sign _arg1) {
		if (_arg1 != null) {
			HashList unlockList = getUnlockList();
			unlockList.add(_arg1);
			unlockList.put(((_arg1.getIX() + "_") + _arg1.getIY()), _arg1);
		}
	}

	private Sign createSign(int _arg1, int _arg2, Boolean _arg3, Sign _arg4, Point _arg5) {
		if ((((((((_arg1 < 0)) || ((_arg2 < 0)))) || ((_arg1 >= this.mapWSize)))) || ((_arg2 >= this.mapHSize)))) {
			return (null);
		}
		if (this.mapData[_arg1][_arg2] == BAR) {
			return (null);
		}

		HashList unlockList = getUnlockList();
		Map lockList = getLockList();

		if (lockList.get(((_arg1 + "_") + _arg2)) != null) {
			return (null);
		}
		if (unlockList.get((_arg1 + "_") + _arg2) != null) {
			return (null);
		}
		if (_arg3) {
			if ((((this.mapData[_arg4.getIX()][_arg2] == BAR)) || ((this.mapData[_arg1][_arg4.getIY()] == BAR)))) {
				return (null);
			}
		}
		int _local6 = Math.abs((_arg5.x - _arg1));
		int _local7 = Math.abs((_arg5.y - _arg2));
		return (new Sign(_arg1, _arg2, (_arg3) ? BIAS_VALUE : LINE_VALUE, ((_local6 + _local7) * 10), _arg4));
	}

	public static void main(String[] args) {
		boolean[][] map = { 
				{ false, true, true, false }, 
				{ false, false, false, false }, 
				{ false, false, true, false }, 
				{ false, false, true, false } 
		};
		Astar astar = new Astar();
		astar.init(map, 4, 4);
		List<Point> roads = astar.seekRoad(new Point(0, 0), new Point(0, 3));
		System.out.println(roads);
	}

}

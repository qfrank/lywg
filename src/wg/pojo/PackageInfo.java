package wg.pojo;

import java.util.Arrays;

public class PackageInfo {

	/**
	 * 背包总格子数
	 */
	public static final int packLen = 56;

	/**
	 * 仓库格子数
	 */
	public static final int storeLen = 120;

	private volatile PackageItem[] packAry = new PackageItem[packLen];

	private int currentNum;// 当前物品个数

	public volatile boolean inited = false;

	public int getLeftBlankNum(){
		return packLen - currentNum;
	}
	
	public void clear(){
		currentNum = 0;
		packAry = new PackageItem[packLen]; 
	}

	public PackageItem[] getItems() {
		PackageItem[] items = new PackageItem[currentNum];
		int index = 0;
		for (PackageItem pi : packAry) {
			if (pi != null) {
				items[index++] = pi;
			}
		}
		return items;
	}

	public synchronized void removeItem(int index) {
        if (packAry[index] != null)
            currentNum--;

		packAry[index] = null;

		if(currentNum<0)
			throw new RuntimeException("PackageInfo.currentNum should not bellow zero.");
	}

	public synchronized void addItem(PackageItem item) {
		packAry[item.index] = item;
		currentNum++;
	}

	@Override
	public String toString() {
		return "PackageInfo{" +
				"packAry=" + Arrays.toString(packAry) +
				", currentNum=" + currentNum +
				", inited=" + inited +
				'}';
	}
}

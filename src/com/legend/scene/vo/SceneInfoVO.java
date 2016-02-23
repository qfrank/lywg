package com.legend.scene.vo;

import java.util.Arrays;

public class SceneInfoVO {
	
	public int id;
	public int type;
	public Object[] live;
	public int tileWidth;
	public int tileHeight;
	public int width;
	public int height;
	public String pathData;
	public int gridWidth = 56;
	public int gridHeight = 28;
	public int gridWSize;
	public int gridHSize;
	public int gridX = 0;
	public int gridY = 0;
	public String maskData;
	public int maskWidth = 10;
	public int maskHeight = 10;
	public int maskSizeW;
	public int maskSizeH;
	public Object[] assetsData;
	public boolean overlap;
	public Object[] safeAreaArr;

	@Override
	public String toString() {
		return "SceneInfoVO{" +
				"id=" + id +
				", type=" + type +
				", live=" + Arrays.toString(live) +
				", tileWidth=" + tileWidth +
				", tileHeight=" + tileHeight +
				", width=" + width +
				", height=" + height +
				", gridWidth=" + gridWidth +
				", gridHeight=" + gridHeight +
				", gridWSize=" + gridWSize +
				", gridHSize=" + gridHSize +
				", gridX=" + gridX +
				", gridY=" + gridY +
				", maskData='" + maskData + '\'' +
				", maskWidth=" + maskWidth +
				", maskHeight=" + maskHeight +
				", maskSizeW=" + maskSizeW +
				", maskSizeH=" + maskSizeH +
				", assetsData=" + Arrays.toString(assetsData) +
				", overlap=" + overlap +
				", safeAreaArr=" + Arrays.toString(safeAreaArr) +
				'}';
	}

}

package com.legend.fp1;

import flash.geom.Point;

public class Sign {

	private Number _ix;
	private Number _iy;
	private Sign _p;
	private int _f = 0;
	private int _g = 0;
	private int _h = 0;

	public Sign(Number _arg1, Number _arg2, int _arg3, int _arg4) {
		this(_arg1, _arg2, _arg3, _arg4, null);
	}

	public Sign(Number _arg1, Number _arg2, int _arg3, int _arg4, Sign _arg5) {
		this._ix = _arg1;
		this._iy = _arg2;
		this._p = _arg5;
		if (_arg5 != null) {
			this._g = (_arg5._g + _arg3);
			this._h = _arg4;
			this._f = (this._g + this._h);
		}
	}

	public Point getSign() {
		return (new Point(this._ix.intValue(), this._iy.intValue()));
	}

	public int getIX() {
		return (this._ix.intValue());
	}

	public int getIY() {
		return (this._iy.intValue());
	}

	public Sign getP() {
		return (this._p);
	}

	public int getF() {
		return (this._f);
	}

	public int getG() {
		return (this._g);
	}

	public int getH() {
		return (this._h);
	}

	public String toString() {
		return (((this._ix + ",") + this._iy));
	}
}

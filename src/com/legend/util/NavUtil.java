package com.legend.util;

import com.game.net.m15.M1503;
import flash.geom.Point;
import org.frkd.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class NavUtil {

    public static int getDistGrids(Point _arg1, Point _arg2) {
        Point _local3 = new Point(_arg1.x, _arg1.y);
        Point _local4 = new Point(_arg2.x, _arg2.y);
        int _local5 = 0;
        while (_local3.x != _local4.x) {
            if (_local3.x > _local4.x) {
                _local3.x--;
                if (_local3.y > _local4.y) {
                    _local3.y--;
                } else {
                    if (_local3.y < _local4.y) {
                        _local4.y--;
                    } else {
                        if (_local3.y == _local4.y) {
                            _local5++;
                            break;
                        }
                    }
                }
                _local5++;
            } else {
                if (_local3.x < _local4.x) {
                    _local4.x--;
                    if (_local3.y > _local4.y) {
                        _local3.y--;
                    } else {
                        if (_local3.y < _local4.y) {
                            _local4.y--;
                        } else {
                            if (_local3.y == _local4.y) {
                                _local5++;
                                break;
                            }
                        }
                    }
                    _local5++;
                }
            }
        }

        int _local6 = (int) MathUtil.getDistance(new java.awt.Point(_local3.x, _local3.y), new java.awt.Point(_local4.x, _local4.y));
        _local5 = (_local5 + _local6);
        return (_local5);
    }

    public static List<M1503> trans2M1503(List<Point> pathPoints) {
        List<M1503> m1503List = new ArrayList();

        int index = 0;
        while (index < pathPoints.size() - 2) {
            Point p1 = pathPoints.get(index);
            Point p2 = pathPoints.get(index + 1);
            Point p3 = pathPoints.get(index + 2);
            M1503 m1503 = new M1503();
            if ((p2.y - p1.y) * (p3.x - p2.x) == (p3.y - p2.y) * (p2.x - p1.x)) {// 三点在同一直线上
                m1503._x = p3.x;
                m1503._y = p3.y;
                m1503._state = 2;
                index = index + 2;
            } else {
                m1503._x = p2.x;
                m1503._y = p2.y;
                m1503._state = 1;
                index++;
            }
            m1503List.add(m1503);
        }

        if(m1503List.size()>0)
            index++;

        for (int i = index; i < pathPoints.size(); i++) {
            Point p = pathPoints.get(i);
            M1503 m1503 = new M1503();
            m1503._x = p.x;
            m1503._y = p.y;
            m1503._state = 1;
            m1503List.add(m1503);
        }

        return m1503List;
    }

}

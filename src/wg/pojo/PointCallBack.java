package wg.pojo;

import flash.geom.Point;
import org.frkd.method.ICallBack;

/**
 * User: Franklyn <br/>
 * Date: 14-11-27<br/>
 * Time: 上午12:25<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class PointCallBack {
    public Point end;
    public ICallBack callBack;

    public PointCallBack(Point end, ICallBack callBack) {
        this.end = end;
        this.callBack = callBack;
    }

    public PointCallBack(int x,int y,ICallBack callBack){
        this(new Point(x,y),callBack);
    }
}

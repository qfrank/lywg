package wg.task;

import wg.mod.impl.NavigatorModule;
import wg.pojo.PointCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Franklyn <br/>
 * Date: 14-11-27<br/>
 * Time: 上午12:46<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public abstract class PointCallBackExecutor implements ITaskExecutor {

    NavigatorModule navigatorModule;

    public PointCallBackExecutor(NavigatorModule navigatorModule){
        this.navigatorModule = navigatorModule;
    }

    @Override
    public void execute() {
        List<PointCallBack> pointCallBacks = new ArrayList();
        initPointCallBacks(pointCallBacks);
        navigatorModule.doInSequence(pointCallBacks);
    }

    public abstract void initPointCallBacks(List<PointCallBack> pcbs);
}

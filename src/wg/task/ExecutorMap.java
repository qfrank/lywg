package wg.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-5<br/>
 * Time: 下午4:41<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class ExecutorMap extends HashMap {

    @Override
    public Object put(Object key, Object value) {
        List executorList = (List)super.get(key);
        if(executorList==null){
            executorList = new ArrayList();
        }
        executorList.add(value);
        return super.put(key, executorList);
    }

    @Override
    public Object get(Object key) {

        List executorList = (List)super.get(key);
        if(executorList!=null && executorList.size()>0){
            if(ITask.TJ_FINISHED.equals(key)){
                return executorList.get(0);
            }
            return executorList.remove(0);
        }
        return null;
    }
}

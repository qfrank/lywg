package wg.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.frkd.util.PropertyUtil;
import wg.pojo.config.WgConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * User: Frank Tang <br/>
 * Date: 15-1-13<br/>
 * Time: 上午12:19<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class WgConfigHolder {

    private static WgConfig wgConfig;

    private WgConfigHolder(){
    }

    public static void init(String configFile){
        Gson gson = new Gson();
        JsonReader jr = null;
        String appdir = PropertyUtil.getProperty("appdir");
        try {
            jr = new JsonReader(new FileReader(appdir+ File.separatorChar+"conf"+File.separatorChar+configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        jr.setLenient(true);

        WgConfig wgConfig = gson.fromJson(jr, WgConfig.class);
        setWgConfig(wgConfig);

    }

    private static void setWgConfig(WgConfig wgConfig){
        WgConfigHolder.wgConfig = wgConfig;
    }

    public static WgConfig getWgConfig(){
        return wgConfig;
    }

}

package com.legend.util;

import com.legend.scene.vo.SceneInfoVO;
import jp.develop.common.util.AmfUtil;
import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.util.Base64;
import org.frkd.util.ZLibUtil;
import org.frkd.util.log.LoggerUtil;
import wg.pojo.map.SimpleSceneInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneUtil {

    private static Logger logger = LoggerUtil.getLogger(SceneUtil.class);

    public static List unzipData(String _arg1, int _arg2, int _arg3) {
        int _local8;
        List _local9;
        int _local10;
        byte[] _local4 = Base64.decode(_arg1);
        //byte[] _local4 = new BASE64Decoder().decodeBuffer(_arg1);
        _local4 = ZLibUtil.decompress(_local4);
        InputByteArray iba = new InputByteArray(_local4);
        OutputByteArray _local5 = new OutputByteArray();
        while (iba.available() > 0) {
            _local8 = iba.readByte();
            _local5.writeBoolean(((128 & _local8) == 128));
            _local5.writeBoolean(((64 & _local8) == 64));
            _local5.writeBoolean(((32 & _local8) == 32));
            _local5.writeBoolean(((16 & _local8) == 16));
            _local5.writeBoolean(((8 & _local8) == 8));
            _local5.writeBoolean(((4 & _local8) == 4));
            _local5.writeBoolean(((2 & _local8) == 2));
            _local5.writeBoolean(((1 & _local8) == 1));
        }
        iba = new InputByteArray(_local5.toByteArray());
        List _local6 = new ArrayList();
        int _local7 = 0;
        while (_local7 < _arg2) {
            _local9 = new ArrayList();
            _local10 = 0;
            while (_local10 < _arg3) {
                _local9.add(iba.readBoolean());
                _local10++;
            }
            _local6.add(_local9);
            _local7++;
        }
        return (_local6);
    }

    /**
     * 场景是否可PK，可以PK时返回false，否则返回true
     *
     * @param sceneId
     * @return
     */
    public static boolean isSafe(long sceneId) {
        if (sceneId >= 271)
            return false;
        return true;
    }

    public static SimpleSceneInfo parseFile(File sceneFile) {
        SimpleSceneInfo info = new SimpleSceneInfo();
        InputStream is = null;
        try {
            is = new FileInputStream(sceneFile);
            byte[] buf = new byte[is.available()];
            is.read(buf);

            buf = ZLibUtil.decompress(buf);
            SceneInfoVO vo = AmfUtil.decode(buf, SceneInfoVO.class);
            List<List<Boolean>> arr = SceneUtil.unzipData(vo.pathData, vo.gridWSize, vo.gridHSize);

            // true means block
            boolean[][] map = new boolean[vo.gridWSize][vo.gridHSize];
            for (int rowIdx = 0; rowIdx < vo.gridWSize; rowIdx++) {
                List<Boolean> row = arr.get(rowIdx);
                for (int colIdx = 0; colIdx < vo.gridHSize; colIdx++) {
                    map[rowIdx][colIdx] = row.get(colIdx);
                }
            }

            info.gridHSize = vo.gridHSize;
            info.gridWSize = vo.gridWSize;
            info.map = map;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        return info;
    }

}

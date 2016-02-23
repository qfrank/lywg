package wg.pojo;

/**
 * User: Franklyn <br/>
 * Date: 14-11-28<br/>
 * Time: 上午11:07<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class NpcData {
    public int id;
    public int type;
    public String name;
    public short scene;
    public short x;
    public short y;
    public byte isShowMiniMapName;
    public int op;

    @Override
    public String toString() {
        return "NpcData{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", scene=" + scene +
                ", x=" + x +
                ", y=" + y +
                ", isShowMiniMapName=" + isShowMiniMapName +
                ", op=" + op +
                '}';
    }
}

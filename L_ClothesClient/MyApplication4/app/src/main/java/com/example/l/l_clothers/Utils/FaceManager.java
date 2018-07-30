package com.example.l.l_clothers.Utils;

import com.example.l.l_clothers.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by L on 2017/6/6.
 */

public class FaceManager {
    private FaceManager()
    {
        initFaceMap();
    }
    private static FaceManager instance;
    public static FaceManager getInstance() {
        if (instance == null)
            instance=new FaceManager();
        return instance;
    }

    private Map<String,Integer> mFaceMap;
    private void initFaceMap()
    {
        mFaceMap=new LinkedHashMap<String, Integer>();
        mFaceMap.put("[呲牙]", R.drawable.f000);
        mFaceMap.put("[调皮]", R.drawable.f001);
        mFaceMap.put("[流汗]", R.drawable.f002);
        mFaceMap.put("[偷笑]", R.drawable.f003);
        mFaceMap.put("[再见]", R.drawable.f004);
        mFaceMap.put("[敲打]", R.drawable.f005);
        mFaceMap.put("[擦汗]", R.drawable.f006);
        mFaceMap.put("[猪头]", R.drawable.f007);
        mFaceMap.put("[玫瑰]", R.drawable.f008);
        mFaceMap.put("[流泪]", R.drawable.f009);
        mFaceMap.put("[大哭]", R.drawable.f010);
        mFaceMap.put("[嘘]", R.drawable.f011);
        mFaceMap.put("[酷]", R.drawable.f012);
        mFaceMap.put("[抓狂]", R.drawable.f013);
        mFaceMap.put("[委屈]", R.drawable.f014);
        mFaceMap.put("[便便]", R.drawable.f015);
        mFaceMap.put("[炸弹]", R.drawable.f016);
        mFaceMap.put("[菜刀]", R.drawable.f017);
        mFaceMap.put("[可爱]", R.drawable.f018);
        mFaceMap.put("[色]", R.drawable.f019);
        mFaceMap.put("[害羞]", R.drawable.f020);

        mFaceMap.put("[得意]", R.drawable.f021);
        mFaceMap.put("[吐]", R.drawable.f022);
        mFaceMap.put("[微笑]", R.drawable.f023);
        mFaceMap.put("[发怒]", R.drawable.f024);
        mFaceMap.put("[尴尬]", R.drawable.f025);
        mFaceMap.put("[惊恐]", R.drawable.f026);
        mFaceMap.put("[冷汗]", R.drawable.f027);
        mFaceMap.put("[爱心]", R.drawable.f028);
        mFaceMap.put("[示爱]", R.drawable.f029);
        mFaceMap.put("[白眼]", R.drawable.f030);
        mFaceMap.put("[傲慢]", R.drawable.f031);
        mFaceMap.put("[难过]", R.drawable.f032);
        mFaceMap.put("[惊讶]", R.drawable.f033);
        mFaceMap.put("[疑问]", R.drawable.f034);
        mFaceMap.put("[睡]", R.drawable.f035);
        mFaceMap.put("[亲亲]", R.drawable.f036);
        mFaceMap.put("[憨笑]", R.drawable.f037);
        mFaceMap.put("[爱情]", R.drawable.f038);
        mFaceMap.put("[衰]", R.drawable.f039);
        mFaceMap.put("[撇嘴]", R.drawable.f040);
        mFaceMap.put("[阴险]", R.drawable.f041);

        mFaceMap.put("[奋斗]", R.drawable.f042);
        mFaceMap.put("[发呆]", R.drawable.f043);
        mFaceMap.put("[右哼哼]", R.drawable.f044);
        mFaceMap.put("[拥抱]", R.drawable.f045);
        mFaceMap.put("[坏笑]", R.drawable.f046);
        mFaceMap.put("[飞吻]", R.drawable.f047);
        mFaceMap.put("[鄙视]", R.drawable.f048);
        mFaceMap.put("[晕]", R.drawable.f049);
        mFaceMap.put("[大兵]", R.drawable.f050);
        mFaceMap.put("[可怜]", R.drawable.f051);
        mFaceMap.put("[强]", R.drawable.f052);
        mFaceMap.put("[弱]", R.drawable.f053);
        mFaceMap.put("[握手]", R.drawable.f054);
        mFaceMap.put("[胜利]", R.drawable.f055);
        mFaceMap.put("[抱拳]", R.drawable.f056);
        mFaceMap.put("[凋谢]", R.drawable.f057);
        mFaceMap.put("[饭]", R.drawable.f058);
        mFaceMap.put("[蛋糕]", R.drawable.f059);
        mFaceMap.put("[删除]",R.drawable.f999);
        mFaceMap.put("[语音左]",R.drawable.f065);
        mFaceMap.put("[语音右]",R.drawable.f066);

    }

    public int getFaceId(String facestr)
    {
        if(mFaceMap.containsKey(facestr)) {
            return mFaceMap.get(facestr);
        }
        return -1;
    }

}


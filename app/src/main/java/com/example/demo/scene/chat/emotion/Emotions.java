package com.example.demo.scene.chat.emotion;

import android.text.TextUtils;

import com.effective.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 编码参考 http://www.oicqzone.com/tool/emoji/
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class Emotions {

    public static Map<String, Integer> EMOTIONS = new LinkedHashMap<>();

    static {
        EMOTIONS.put("[微笑]", R.drawable.expression_1);
        EMOTIONS.put("[撇嘴]", R.drawable.expression_2);
        EMOTIONS.put("[色]", R.drawable.expression_3);
        EMOTIONS.put("[发呆]", R.drawable.expression_4);
        EMOTIONS.put("[得意]", R.drawable.expression_5);
        EMOTIONS.put("[流泪]", R.drawable.expression_6);
        EMOTIONS.put("[害羞]", R.drawable.expression_7);
        EMOTIONS.put("[闭嘴]", R.drawable.expression_8);
        EMOTIONS.put("[睡]", R.drawable.expression_9);
        EMOTIONS.put("[大哭]", R.drawable.expression_10);
        EMOTIONS.put("[尴尬]", R.drawable.expression_11);
        EMOTIONS.put("[发怒]", R.drawable.expression_12);
        EMOTIONS.put("[调皮]", R.drawable.expression_13);
        EMOTIONS.put("[呲牙]", R.drawable.expression_14);
        EMOTIONS.put("[惊讶]", R.drawable.expression_15);
        EMOTIONS.put("[难过]", R.drawable.expression_16);
        EMOTIONS.put("[囧]", R.drawable.expression_18);
        EMOTIONS.put("[抓狂]", R.drawable.expression_19);
        EMOTIONS.put("[吐]", R.drawable.expression_20);
        EMOTIONS.put("[偷笑]", R.drawable.expression_21);
        EMOTIONS.put("[愉快]", R.drawable.expression_22);
        EMOTIONS.put("[白眼]", R.drawable.expression_23);
        EMOTIONS.put("[傲慢]", R.drawable.expression_24);
        EMOTIONS.put("[困]", R.drawable.expression_26);
        EMOTIONS.put("[惊恐]", R.drawable.expression_27);
        EMOTIONS.put("[流汗]", R.drawable.expression_28);
        EMOTIONS.put("[憨笑]", R.drawable.expression_29);
        EMOTIONS.put("[悠闲]", R.drawable.expression_30);
        EMOTIONS.put("[奋斗]", R.drawable.expression_31);
        EMOTIONS.put("[咒骂]", R.drawable.expression_32);
        EMOTIONS.put("[疑问]", R.drawable.expression_33);
        EMOTIONS.put("[嘘]", R.drawable.expression_34);
        EMOTIONS.put("[晕]", R.drawable.expression_35);
        EMOTIONS.put("[衰]", R.drawable.expression_37);
        EMOTIONS.put("[骷髅]", R.drawable.expression_38);
        EMOTIONS.put("[敲打]", R.drawable.expression_39);
        EMOTIONS.put("[再见]", R.drawable.expression_40);
        EMOTIONS.put("[擦汗]", R.drawable.expression_41);
        EMOTIONS.put("[抠鼻]", R.drawable.expression_42);
        EMOTIONS.put("[鼓掌]", R.drawable.expression_43);
        EMOTIONS.put("[坏笑]", R.drawable.expression_45);
        EMOTIONS.put("[左哼哼]", R.drawable.expression_46);
        EMOTIONS.put("[右哼哼]", R.drawable.expression_47);
        EMOTIONS.put("[哈欠]", R.drawable.expression_48);
        EMOTIONS.put("[鄙视]", R.drawable.expression_49);
        EMOTIONS.put("[委屈]", R.drawable.expression_50);
        EMOTIONS.put("[快哭了]", R.drawable.expression_51);
        EMOTIONS.put("[阴险]", R.drawable.expression_52);
        EMOTIONS.put("[亲亲]", R.drawable.expression_53);
        EMOTIONS.put("[可怜]", R.drawable.expression_55);
        EMOTIONS.put("[菜刀]", R.drawable.expression_56);
        EMOTIONS.put("[西瓜]", R.drawable.expression_57);
        EMOTIONS.put("[啤酒]", R.drawable.expression_58);
        EMOTIONS.put("[咖啡]", R.drawable.expression_61);
        EMOTIONS.put("[猪头]", R.drawable.expression_63);
        EMOTIONS.put("[玫瑰]", R.drawable.expression_64);
        EMOTIONS.put("[凋谢]", R.drawable.expression_65);
        EMOTIONS.put("[嘴唇]", R.drawable.expression_66);
        EMOTIONS.put("[爱心]", R.drawable.expression_67);
        EMOTIONS.put("[心碎]", R.drawable.expression_68);
        EMOTIONS.put("[蛋糕]", R.drawable.expression_69);
        EMOTIONS.put("[炸弹]", R.drawable.expression_71);
        EMOTIONS.put("[便便]", R.drawable.expression_75);
        EMOTIONS.put("[月亮]", R.drawable.expression_76);
        EMOTIONS.put("[太阳]", R.drawable.expression_77);
        EMOTIONS.put("[拥抱]", R.drawable.expression_79);
        EMOTIONS.put("[强]", R.drawable.expression_80);
        EMOTIONS.put("[弱]", R.drawable.expression_81);
        EMOTIONS.put("[握手]", R.drawable.expression_82);
        EMOTIONS.put("[胜利]", R.drawable.expression_83);
        EMOTIONS.put("[抱拳]", R.drawable.expression_84);
        EMOTIONS.put("[勾引]", R.drawable.expression_85);
        EMOTIONS.put("[拳头]", R.drawable.expression_86);
        EMOTIONS.put("[OK]", R.drawable.expression_90);
        EMOTIONS.put("[跳跳]", R.drawable.expression_93);
        EMOTIONS.put("[发抖]", R.drawable.expression_94);
        EMOTIONS.put("[怄火]", R.drawable.expression_95);
        EMOTIONS.put("[转圈]", R.drawable.expression_96);
        EMOTIONS.put(emotionCode2String(0x1F604), R.drawable.expression_97);
        EMOTIONS.put(emotionCode2String(0x1F637), R.drawable.expression_98);
        EMOTIONS.put(emotionCode2String(0x1F602), R.drawable.expression_99);
        EMOTIONS.put(emotionCode2String(0x1F61D), R.drawable.expression_101);
        EMOTIONS.put(emotionCode2String(0x1F633), R.drawable.expression_102);
        EMOTIONS.put(emotionCode2String(0x1F631), R.drawable.expression_103);
        EMOTIONS.put(emotionCode2String(0x1F614), R.drawable.expression_104);
        EMOTIONS.put(emotionCode2String(0x1F612), R.drawable.expression_105);
        EMOTIONS.put("[嘿哈]", R.drawable.expression_107);
        EMOTIONS.put("[捂脸]", R.drawable.expression_108);
        EMOTIONS.put("[奸笑]", R.drawable.expression_106);
        EMOTIONS.put("[机智]", R.drawable.expression_109);
        EMOTIONS.put("[皱眉]", R.drawable.expression_119);
        EMOTIONS.put("[耶]", R.drawable.expression_113);
        EMOTIONS.put(emotionCode2String(0x1F47B), R.drawable.expression_114);
        EMOTIONS.put(emotionCode2String(0x1F64F), R.drawable.expression_115);
        EMOTIONS.put(emotionCode2String(0x1F4AA), R.drawable.expression_116);
        EMOTIONS.put(emotionCode2String(0x1F389), R.drawable.expression_117);
        EMOTIONS.put(emotionCode2String(0x1F381), R.drawable.expression_118);
        EMOTIONS.put("[红包]", R.drawable.expression_111);
    }

    private static String emotionCode2String(int code) {
        return new String(Character.toChars(code));
    }

    public static int getDrawableResByName(String emotionName) {
        if (!TextUtils.isEmpty(emotionName) && EMOTIONS.containsKey(emotionName)) {
            return EMOTIONS.get(emotionName);
        }
        return -1;
    }

    public static List<Emotion> getEmotions() {
        List<Emotion> emotions = new ArrayList<>();
        Iterator<Map.Entry<String, Integer>> entries = EMOTIONS.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, Integer> entry = entries.next();
            emotions.add(new Emotion(entry.getKey(), entry.getValue()));
        }
        return emotions;
    }
}

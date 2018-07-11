package com.example.demo.emotion;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.example.demo.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class EmojiSpanBuilder {

    private static Pattern sPatternEmotion =
            Pattern.compile("\\[([\u4e00-\u9fa5\\w])+\\]|[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]");

    public static Spannable buildEmotionSpannable(Context context, String text) {
        Matcher matcherEmotion = sPatternEmotion.matcher(text);
        SpannableString spannableString = new SpannableString(text);
        while (matcherEmotion.find()) {
            String key = matcherEmotion.group();
            int imgRes = Emotions.getDrawableResByName(key);
            if (imgRes != -1) {
                int start = matcherEmotion.start();
                Drawable drawable = ContextCompat.getDrawable(context, imgRes);
                drawable.setBounds(0, 0, Utils.dip2px(context, 20f), Utils.dip2px(context, 20f));
                CenterImageSpan span = new CenterImageSpan(drawable);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableString;
    }
}

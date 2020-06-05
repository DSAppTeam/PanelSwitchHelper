package com.example.demo.scene.chat.emotion;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * 处理图文混排时图片居中
 * ascent 为字体最上端到基线的距离，为负值
 * descent 为字体最下端到基线的距离，为正值
 * Created by yummyLau on 2018/6/15.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class CenterImageSpan extends ImageSpan {

    public CenterImageSpan(Drawable drawable) {
        super(drawable);
    }

    /**
     * @param paint
     * @param text
     * @param start
     * @param end
     * @param fontMetricsInt
     * @return
     */
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {

            //获取绘制字体的度量
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    /**
     * @param canvas
     * @param text
     * @param start
     * @param end
     * @param x      要绘制的image的左边框到textview左边框的距离。
     * @param top    替换行的最顶部位置
     * @param y      要替换的文字的基线坐标，即基线到textview上边框的距离
     * @param bottom 替换行的最底部位置。注意，textview中两行之间的行间距是属于上一行的，所以这里bottom是指行间隔的底部位置
     * @param paint
     */
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {

        Drawable drawable = getDrawable();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        /**
         * y + fm.descent 字体的descent线 y坐标
         * y + fm.ascent 字体的ascent线 y坐标
         * drawble.getBounds().bottom 图片的高度
         */
        int transY = (int) (1f / 2 * (y + fm.descent + y + fm.ascent - drawable.getBounds().bottom));
        canvas.save();
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
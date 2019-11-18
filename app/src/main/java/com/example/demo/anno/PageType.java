package com.example.demo.anno;


import android.support.annotation.IntDef;

import static com.example.demo.anno.PageType.DEFAULT;
import static com.example.demo.anno.PageType.COLOR_STATUS_BAR;
import static com.example.demo.anno.PageType.DRAWABLE_STATUS_BAR;
import static com.example.demo.anno.PageType.TRANSPARENT_STATUS_BAR;

@IntDef({DEFAULT, COLOR_STATUS_BAR, TRANSPARENT_STATUS_BAR})
public @interface PageType {
    int DEFAULT = 0;
    int COLOR_STATUS_BAR = 1;
    int DRAWABLE_STATUS_BAR = 2;
    int TRANSPARENT_STATUS_BAR = 3;
}

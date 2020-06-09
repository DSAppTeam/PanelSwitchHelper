package com.example.demo.anno;


import androidx.annotation.IntDef;

import static com.example.demo.anno.ChatPageType.DEFAULT;
import static com.example.demo.anno.ChatPageType.COLOR_STATUS_BAR;
import static com.example.demo.anno.ChatPageType.TITLE_BAR;
import static com.example.demo.anno.ChatPageType.TRANSPARENT_STATUS_BAR;
import static com.example.demo.anno.ChatPageType.TRANSPARENT_STATUS_BAR_DRAW_UNDER;
import static com.example.demo.anno.ChatPageType.CUS_TITLE_BAR;

@IntDef({DEFAULT, TITLE_BAR, COLOR_STATUS_BAR, CUS_TITLE_BAR, TRANSPARENT_STATUS_BAR, TRANSPARENT_STATUS_BAR_DRAW_UNDER})
public @interface ChatPageType {
    int DEFAULT = 0;
    int TITLE_BAR = 1;
    int CUS_TITLE_BAR = 2;
    int COLOR_STATUS_BAR = 3;
    int TRANSPARENT_STATUS_BAR = 4;
    int TRANSPARENT_STATUS_BAR_DRAW_UNDER = 5;
}

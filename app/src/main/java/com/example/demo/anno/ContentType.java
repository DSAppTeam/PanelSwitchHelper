package com.example.demo.anno;


import android.support.annotation.IntDef;

import static com.example.demo.anno.ContentType.Linear;
import static com.example.demo.anno.ContentType.Relative;
import static com.example.demo.anno.ContentType.Frame;
import static com.example.demo.anno.ContentType.CUS;

@IntDef({Linear, Relative, Frame, CUS})
public @interface ContentType {
    int Linear = 0;
    int Relative = 1;
    int Frame = 2;
    int CUS = 3;
}

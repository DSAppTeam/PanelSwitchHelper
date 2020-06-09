package com.example.demo.anno;
import androidx.annotation.IntDef;

import static com.example.demo.anno.ApiContentType.Linear;
import static com.example.demo.anno.ApiContentType.Relative;
import static com.example.demo.anno.ApiContentType.Frame;
import static com.example.demo.anno.ApiContentType.CUS;

@IntDef({Linear, Relative, Frame, CUS})
public @interface ApiContentType {
    int Linear = 0;
    int Relative = 1;
    int Frame = 2;
    int CUS = 3;
}

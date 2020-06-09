package com.effective.android.panel.utils.cutShort

import android.content.Context
import android.view.View

interface DeviceCutShort {
    fun hasCutShort(context: Context): Boolean
    fun isCusShortVisible(context: Context): Boolean
    fun getCurrentCutShortHeight(view: View): Int
}
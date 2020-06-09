package com.effective.android.panel.window

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.effective.android.panel.PanelSwitchHelper

/**
 * 提供给外部使用的dialog，场景用于类 微博评论/微信朋友圈
 * created by yummylau on 2020/06/04
 */
abstract class PanelDialog : Dialog {

    @JvmField
    protected val rootView: View

    @JvmField
    protected var helper: PanelSwitchHelper? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener)

    @LayoutRes
    abstract fun getDialogLayout(): Int

    init {
        rootView = LayoutInflater.from(context).inflate(getDialogLayout(), null, false)
        setContentView(rootView)
        window?.let {
            it.setGravity(Gravity.CENTER)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            val lp = it.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            it.attributes = lp
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setDimAmount(0f)
            it.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
        }
    }
}
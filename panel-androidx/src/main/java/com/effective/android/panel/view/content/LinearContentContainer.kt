package com.effective.android.panel.view.content

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.IdRes
import com.effective.android.panel.R
import com.effective.android.panel.interfaces.ContentScrollMeasurer

/**
 * --------------------
 * | PanelSwitchLayout  |
 * |  ----------------  |
 * | |                | |
 * | |ContentContainer| |
 * | |                | |
 * |  ----------------  |
 * |  ----------------  |
 * | | PanelContainer | |
 * |  ----------------  |
 * --------------------
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class LinearContentContainer : LinearLayout, IContentContainer {
    @IdRes
    private var editTextId = 0

    @IdRes
    private var autoResetId = 0
    private var autoResetByOnTouch: Boolean = true
    private lateinit var contentContainer: ContentContainerImpl

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr, 0)
    }

    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearContentContainer, defStyleAttr, 0)
        editTextId = typedArray.getResourceId(R.styleable.LinearContentContainer_edit_view, -1)
        autoResetId = typedArray.getResourceId(R.styleable.LinearContentContainer_auto_reset_area, -1)
        autoResetByOnTouch = typedArray.getBoolean(R.styleable.LinearContentContainer_auto_reset_enable, autoResetByOnTouch)
        typedArray.recycle()
        orientation = VERTICAL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentContainer = ContentContainerImpl(this, autoResetByOnTouch, editTextId, autoResetId)
        val editText = getInputActionImpl().getFullScreenPixelInputView()
        val layoutParams = LayoutParams(1, 1)
        layoutParams.topMargin = -1
        addView(editText,0,layoutParams)
    }

    override fun layoutContainer(l: Int, t: Int, r: Int, b: Int,
                                 contentScrollMeasurers: MutableList<ContentScrollMeasurer>, defaultScrollHeight: Int, canScrollOutsize: Boolean,
                                 reset: Boolean, changed: Boolean) {
        contentContainer.layoutContainer(l, t, r, b, contentScrollMeasurers, defaultScrollHeight, canScrollOutsize,reset, changed)
    }

    override fun translationContainer(contentScrollMeasurers: MutableList<ContentScrollMeasurer>, defaultScrollHeight: Int, contentTranslationY: Float) {
        contentContainer.translationContainer(contentScrollMeasurers,defaultScrollHeight, contentTranslationY)
    }

    override fun findTriggerView(id: Int): View? {
        return contentContainer.findTriggerView(id)
    }

    override fun changeContainerHeight(targetHeight: Int) {
        contentContainer.changeContainerHeight(targetHeight)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val onTouchTrue = super.dispatchTouchEvent(ev)
        val hookResult = getResetActionImpl().hookDispatchTouchEvent(ev, onTouchTrue)
        return hookResult or onTouchTrue
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val onTouchBySelf = super.onTouchEvent(event)
        val hookResult = getResetActionImpl().hookOnTouchEvent(event)
        return onTouchBySelf or hookResult
    }

    override fun getInputActionImpl(): IInputAction = contentContainer.getInputActionImpl()

    override fun getResetActionImpl(): IResetAction = contentContainer.getResetActionImpl()
}
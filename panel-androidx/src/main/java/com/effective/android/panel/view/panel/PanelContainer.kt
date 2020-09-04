package com.effective.android.panel.view.panel

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Pair
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import com.effective.android.panel.Constants
import com.effective.android.panel.interfaces.ViewAssertion

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
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class PanelContainer : FrameLayout, ViewAssertion {
    var panelSparseArray = SparseArray<IPanelView>()
        private set

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context!!, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr, 0)
    }

    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {}

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
    }

    override fun assertView() {
        panelSparseArray = SparseArray()
        for (i in 0 until childCount) {
            val panel = getChildAt(i) as? IPanelView
                    ?: throw RuntimeException("PanelContainer -- PanelContainer's child should be IPanelView")
            panelSparseArray.put(panel.getBindingTriggerViewId(), panel)
            (panel as View).visibility = View.GONE
        }
    }

    fun getPanelView(panelId: Int): IPanelView? {
        return panelSparseArray[panelId]
    }

    fun getPanelId(panel: IPanelView?): Int {
        return panel?.getBindingTriggerViewId() ?: Constants.PANEL_KEYBOARD
    }

    fun showPanel(panelId: Int, size: Pair<Int, Int>): Pair<Int, Int> {
        val panel = panelSparseArray[panelId]
        for (i in 0 until panelSparseArray.size()) {
            val panelView = panelSparseArray[panelSparseArray.keyAt(i)]
            if (panelView is View)
                panelView.visibility = if (panelView != panel) View.GONE else View.VISIBLE
        }
        val layoutParams = (panel as View).layoutParams
        val curSize = Pair(layoutParams.width, layoutParams.height)
        if (curSize.first != size.first || curSize.second != size.second) {
            layoutParams.width = size.first
            layoutParams.height = size.second
            (panel as View).layoutParams = layoutParams
        }
        return curSize
    }

    fun changeContainerHeight(targetHeight: Int) {
        if (layoutParams != null && layoutParams.height != targetHeight) {
            layoutParams.height = targetHeight
        }
    }
}
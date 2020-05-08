package com.effective.android.panel.view

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
    var panelSparseArray = SparseArray<PanelView>()
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
    }

    override fun assertView() {
        panelSparseArray = SparseArray()
        for (i in 0 until childCount) {
            val view = getChildAt(i) as? PanelView
                    ?: throw RuntimeException("PanelContainer -- PanelContainer's child should be PanelView")
            panelSparseArray.put(view.triggerViewId, view)
            view.visibility = View.GONE
        }
    }

    fun getPanelView(panelId: Int): PanelView? {
        return panelSparseArray[panelId]
    }

    fun getPanelId(view: PanelView?): Int {
        return view?.triggerViewId ?: Constants.PANEL_KEYBOARD
    }

    fun hidePanels() {
        for (i in 0 until panelSparseArray.size()) {
            val panelView = panelSparseArray[panelSparseArray.keyAt(i)]
            panelView.visibility = View.GONE
        }
    }

    fun showPanel(panelId: Int, size: Pair<Int, Int>): Pair<Int, Int> {
        val panelView = panelSparseArray[panelId]
        val layoutParams = panelView.layoutParams
        val curSize = Pair(layoutParams.width, layoutParams.height)
        if (curSize.first != size.first || curSize.second != size.second) {
            layoutParams.width = size.first
            layoutParams.height = size.second
            panelView.layoutParams = layoutParams
        }
        panelView.visibility = View.VISIBLE
        return curSize
    }
}
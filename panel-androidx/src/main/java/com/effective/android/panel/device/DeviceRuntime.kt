package com.effective.android.panel.device

import android.content.Context
import android.content.res.Configuration
import android.view.Window
import android.view.WindowInsets
import com.effective.android.panel.utils.DisplayUtil

/**
 * 保存当前设备信息跟随用户操作更改信息
 * Created by yummyLau on 2020-05-24
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class DeviceRuntime(val context: Context, val window: Window) {

    var deviceInfoP: DeviceInfo? = null
    var deviceInfoL: DeviceInfo? = null

    var isNavigationBarShow: Boolean = false
    var isPortrait: Boolean = false
    var isPad: Boolean = false
    var isFullScreen: Boolean = false;

    init {
        isPad = (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
        isPortrait = DisplayUtil.isPortrait(context)
        isNavigationBarShow = DisplayUtil.isNavigationBarShow(context, window)
        isFullScreen = DisplayUtil.isFullScreen(window)
    }

    fun getDeviceInfoByOrientation(cache: Boolean = false): DeviceInfo {
        isPortrait = DisplayUtil.isPortrait(context)
        isNavigationBarShow = DisplayUtil.isNavigationBarShow(context, window)
        isFullScreen = DisplayUtil.isFullScreen(window)

        if (cache) {
            if (isPortrait && deviceInfoP != null) {
                return deviceInfoP!!
            } else if (!isPortrait && deviceInfoL != null) {
                return deviceInfoL!!
            }
        }

        val navigationBarHeight = DisplayUtil.getNavigationBarHeight(context,window)
        val statusBarHeight = DisplayUtil.getStatusBarHeight(window)
        //以这种方式计算出来的toolbar，如果和statusBarHeight一样，则实际上就是statusBar的高度，大于statusBar的才是toolBar的高度。
        var toolbarH = DisplayUtil.getToolbarHeight(window)
        if (toolbarH == statusBarHeight) {
            toolbarH = 0
        }
        val screenHeight = DisplayUtil.getScreenRealHeight(window)
        val screenWithoutSystemUIHeight = DisplayUtil.getScreenHeightWithoutSystemUI(window)
        val screenWithoutNavigationHeight = DisplayUtil.getScreenHeightWithoutNavigationBar(context)

        return if (isPortrait) {
            deviceInfoP = DeviceInfo(window, true,
                    statusBarHeight, navigationBarHeight, toolbarH,
                    screenHeight, screenWithoutSystemUIHeight, screenWithoutNavigationHeight)
            deviceInfoP!!
        } else {
            deviceInfoL = DeviceInfo(window, false,
                    statusBarHeight, navigationBarHeight, toolbarH,
                    screenHeight, screenWithoutSystemUIHeight, screenWithoutNavigationHeight)
            deviceInfoL!!
        }

    }
}
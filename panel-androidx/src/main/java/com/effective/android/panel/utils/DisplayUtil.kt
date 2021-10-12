package com.effective.android.panel.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import com.effective.android.panel.Constants
import com.effective.android.panel.R


object DisplayUtil {

    /**
     * 获取toolar的高度，但是这个方法仅仅在非沉浸下才有用。
     *
     * @param window
     * @return
     */
    @JvmStatic
    fun getToolbarHeight(window: Window): Int {
        return window.decorView.findViewById<View>(Window.ID_ANDROID_CONTENT).top
    }

    @JvmStatic
    fun getLocationOnScreen(view: View): IntArray {
        val contentViewLocationInScreen = IntArray(2)
        view.getLocationOnScreen(contentViewLocationInScreen)
        return contentViewLocationInScreen
    }

    @JvmStatic
    fun contentViewCanDrawStatusBarArea(window: Window): Boolean {
        return getLocationOnScreen(window.decorView.findViewById(Window.ID_ANDROID_CONTENT))[1] == 0
    }

    /**
     * 对应 id 为 @Android：id/content 的 FrameLayout 所加载的布局。
     * 也就是我们 setContentView 的布局高度
     *
     * @param window
     * @return
     */
    @JvmStatic
    fun getContentViewHeight(window: Window): Int {
        return window.decorView.findViewById<View>(Window.ID_ANDROID_CONTENT).height
    }

    /**
     * 获取设备真实高度，包含可能存在的虚拟导航栏，未显示的刘海区域，状态栏等
     */
    fun getScreenRealHeight(window: Window): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val wm = window.context
                    .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getRealMetrics(outMetrics)
            return outMetrics.heightPixels
        } else {
            getScreenHeightWithSystemUI(window)
        }
    }


    /**
     * 实际上获取的是DecorView的布局高度，是一个 FrameLayout，其内置布局 id 为 com.android.internal.R.layout.screen_simple 的 LinearLayout
     * 包含 id为 @+id/action_mode_bar_stub_ViewStub 的 ViewStub 还有 id 为 @Android：id/content 的 FrameLayout。
     * 如果系统设置刘海留空，则 该高度不包含刘海高度
     *
     * @param window
     * @return
     */
    @JvmStatic
    fun getScreenHeightWithSystemUI(window: Window): Int {
        return window.decorView.height
    }

    @JvmStatic
    fun getScreenHeightWithoutNavigationBar(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    @JvmStatic
    fun getScreenHeightWithoutSystemUI(window: Window): Int {
        val r = Rect()
        window.decorView.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top
    }


    @JvmStatic
    fun isFullScreen(activity: Activity): Boolean {
        return (activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN
                == WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @JvmStatic
    fun isFullScreen(window: Window): Boolean {
        return (window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN
                == WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @JvmStatic
    fun getStatusBarHeight(window: Window): Int {
        val frame = Rect()
        window.decorView.getWindowVisibleDisplayFrame(frame)
        return frame.top
    }


    @JvmStatic
    fun getNavigationBarHeight(context: Context, window: Window): Int {
        val deviceNavigationHeight = getInternalDimensionSize(context.resources, Constants.NAVIGATION_BAR_HEIGHT_RES_NAME)
        //三星android9 OneUI2.0一下打开全面屏手势，导航栏实际高度比 deviceHeight 小，需要做兼容
        val manufacturer = if (Build.MANUFACTURER == null) "" else Build.MANUFACTURER.trim { it <= ' ' }
        if (manufacturer.toLowerCase().contains("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val stableBottom = window.decorView.rootWindowInsets.stableInsetBottom
            if (stableBottom < deviceNavigationHeight) {
                return stableBottom;
            }
        }
        return deviceNavigationHeight;
    }

    private fun getInternalDimensionSize(res: Resources, key: String): Int {
        var result = 0
        val resourceId = res.getIdentifier(key, Constants.DIMEN, Constants.ANDROID)
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId)
        }
        return result
    }

    @JvmStatic
    fun isPortrait(context: Context): Boolean {
        return when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                true
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                false
            }
            else -> {
                val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                val point = Point()
                display.getSize(point)
                point.x <= point.y
            }
        }
    }

    /**
     *
     */
    @JvmStatic
    @TargetApi(14)
    fun isNavigationBarShow(context: Context, window: Window): Boolean {
        return isNavBarVisible(context, window)
    }

    /**
     * Decorview 源码
     * public static final ColorViewAttributes NAVIGATION_BAR_COLOR_VIEW_ATTRIBUTES =
     * new ColorViewAttributes(
     * SYSTEM_UI_FLAG_HIDE_NAVIGATION, FLAG_TRANSLUCENT_NAVIGATION,
     * Gravity.BOTTOM, Gravity.RIGHT, Gravity.LEFT,
     * Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME,
     * com.android.internal.R.id.navigationBarBackground,
     *
     * @param context
     * @param window
     * @return
     */
    fun isNavBarVisible(context: Context, window: Window): Boolean {
        var isVisible = false
        val viewGroup: ViewGroup? = window.decorView as ViewGroup?
        viewGroup?.let {
            for (i in 0 until it.childCount) {
                val id: Int = it.getChildAt(i).id
                if (id != android.view.View.NO_ID) {
                    val resourceEntryName: String? = context.resources.getResourceEntryName(id)
                    if ((("navigationBarBackground" == resourceEntryName) && it.getChildAt(i).visibility == android.view.View.VISIBLE)) {
                        isVisible = true
                    }
                }
            }
            if (!isVisible) {
                val navigationBarView: View? = it.findViewById(R.id.immersion_navigation_bar_view)
                navigationBarView?.let { navigationBar ->
                    if (navigationBar.visibility == View.VISIBLE) {
                        isVisible = true
                    }
                }
            }
        }
        val manufacturer = if (Build.MANUFACTURER == null) "" else Build.MANUFACTURER.trim { it <= ' ' }
        val isSamsung = manufacturer.toLowerCase().contains("samsung")
        if((viewGroup == null || !isVisible) && isSamsung){
            isVisible = hasNavBar(context)
        }
        if (isVisible) {
            // 对于三星手机，android10以下非OneUI2的版本，比如 s8，note8 等设备上，导航栏显示存在bug："当用户隐藏导航栏时显示输入法的时候导航栏会跟随显示"，会导致隐藏输入之后判断错误
            // 这个问题在 OneUI 2 & android 10 版本已修复，
            if (isSamsung && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    isVisible = Settings.Global.getInt(
                        context.contentResolver,
                        "navigationbar_hide_bar_enabled"
                    ) == 0
                    if (isVisible) return true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isVisible = !hasSystemUIFlag(window, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
        return isVisible
    }

    private fun hasNavBar(context: Context): Boolean {
        val res = context.resources
        val resourceId = res.getIdentifier(
            Constants.SHOW_NAV_BAR_RES_NAME,
            "bool",
            "android"
        )
        return if (resourceId != 0) {
            var hasNav = res.getBoolean(resourceId)
            // check override flag (see static block)

            // Android allows a system property to override the presence of the navigation bar.
            // Used by the emulator.
            // See https://github.com/android/platform_frameworks_base/blob/master/policy/src/com/android/internal/policy/impl/PhoneWindowManager.java#L1076
            var sNavBarOverride:String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    val c = Class.forName("android.os.SystemProperties")
                    val m = c.getDeclaredMethod("get", String::class.java)
                    m.isAccessible = true
                    sNavBarOverride = m.invoke(null, "qemu.hw.mainkeys") as? String
                } catch (e: Throwable) {
                    sNavBarOverride = null
                }
            }
            if ("1" == sNavBarOverride) {
                hasNav = false
            } else if ("0" == sNavBarOverride) {
                hasNav = true
            }
            hasNav
        } else { // fallback
            !ViewConfiguration.get(context).hasPermanentMenuKey()
        }
    }

    @JvmStatic
    fun dip2px(context: Context, dipValue: Float): Int {
        compatSizeProxy?.let {
            return it.dip2px(context, dipValue)
        }
        val scale: Float = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun hasSystemUIFlag(window: Window, flag: Int): Boolean = window.decorView.systemUiVisibility and flag == flag

    interface CompatSizeProxy {
        fun dip2px(context: Context, dipValue: Float): Int
    }

    private var compatSizeProxy: CompatSizeProxy? = null

    /**
     * 兼容autoSize
     */
    @JvmStatic
    fun setCompatSizeProxy(proxy: CompatSizeProxy) {
        this.compatSizeProxy = compatSizeProxy
    }
}

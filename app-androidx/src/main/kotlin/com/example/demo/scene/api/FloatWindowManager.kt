package com.example.demo.scene.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import java.util.*

/**
 * desc   : 无悬浮权限弹窗
 * version: 1.0
 */
object FloatWindowManager {

    private const val REQUEST_OVERLAYS_PERMISSION = 0x22

    private val viewStack = Stack<View>()


    @JvmStatic
    fun addViewWithPermission(context: Context, view: View) {
        val windowManager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams.gravity = Gravity.START or Gravity.TOP
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowManager.addView(view, layoutParams)
        viewStack.push(view)
    }


    @JvmStatic
    fun addViewWithoutPermission(activity: Activity, view: View) {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams.dimAmount = 0.2f
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.RGBA_8888
        windowManager.addView(view, layoutParams)
        viewStack.push(view)
    }


    @JvmStatic
    fun removeViewWithoutPermission(context: Context) : Boolean {
        if (viewStack.isEmpty()) {
            return false
        }
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return false
        val view = viewStack.pop()
        windowManager.removeView(view)
        return true
    }

    @JvmStatic
    fun removeViewWithoutPermission(context: Context, view: View): Boolean {
        if (viewStack.isEmpty()) {
            return false
        }
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return false
        if (viewStack.remove(view)) {
            windowManager.removeView(view)
            return true
        }
        return false
    }



    @JvmStatic
    fun requestPermission(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivityForResult(intent, REQUEST_OVERLAYS_PERMISSION)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + activity.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivityForResult(intent, REQUEST_OVERLAYS_PERMISSION)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                try {
                    val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + activity.packageName))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> {
                //4.4以下无需处理
            }
        }
    }




}

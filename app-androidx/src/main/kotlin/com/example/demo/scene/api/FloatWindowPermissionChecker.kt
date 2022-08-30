package com.example.demo.scene.api

import android.app.AppOpsManager
import android.content.Context
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * author : linzheng
 * desc   :
 * version: 1.0
 */
object FloatWindowPermissionChecker {

    @JvmStatic
    fun checkFloatWindowPermission(context: Context): Boolean {
        return try {
            if ("vivo" == Build.MANUFACTURER && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { // vivo 5.0及以下的手机没有content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp这个url
                return getVIVOFloatPermissionStatus(context)
            }
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    try {
                        val clazz: Class<*> = Settings::class.java
                        val canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
                        canDrawOverlays.invoke(null, context) as Boolean
                    } catch (e: Exception) {
                        e.printStackTrace()
                        true
                    }
                    //            return Settings.canDrawOverlays(App.getApplication());
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                    //AppOpsManager添加于API 19
                    checkOps(context)
                }
                else -> {
                    //4.4以下一般都可以直接添加悬浮窗
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun checkOps(context: Context): Boolean {
        try {
            val service: Any = context.getSystemService(Context.APP_OPS_SERVICE)?: return false
            val localClass: Class<*> = service.javaClass
            val arrayOfClass: Array<Class<*>?> = arrayOfNulls(3)
            arrayOfClass[0] = Integer.TYPE
            arrayOfClass[1] = Integer.TYPE
            arrayOfClass[2] = String::class.java
            val method = localClass.getMethod("checkOp", *arrayOfClass) ?: return false
            val arrayOfObject1 = arrayOfNulls<Any>(3)
            arrayOfObject1[0] = 24
            arrayOfObject1[1] = Binder.getCallingUid()
            arrayOfObject1[2] = context.packageName
            val m = method.invoke(service, *arrayOfObject1) as Int
            //4.4至6.0之间的非国产手机，例如samsung，sony一般都可以直接添加悬浮窗
            return m == AppOpsManager.MODE_ALLOWED || !MyRomUtil.isDomesticSpecialRom()
        } catch (ignore: Exception) {
        }
        return false
    }

    /**
     * 获取vivo手机悬浮窗权限状态
     *
     * @param context
     * @return 1或其他是没有打开，0是打开，该状态的定义和[AppOpsManager.MODE_ALLOWED]，MODE_IGNORED等值差不多，自行查阅源码
     */
    private fun getVIVOFloatPermissionStatus(context: Context?): Boolean {
        requireNotNull(context) { "context is null" }
        val packageName = context.packageName
        val uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(packageName)
        val cursor = context
                .contentResolver
                .query(uri, null, selection, selectionArgs, null)
        return if (cursor != null) {
            cursor.columnNames
            if (cursor.moveToFirst()) {
                val currentmode = cursor.getInt(cursor.getColumnIndex("currentlmode"))
                cursor.close()
                currentmode == 0
            } else {
                cursor.close()
                getFloatPermissionStatus2(context) == 0
            }
        } else {
            getFloatPermissionStatus2(context) == 0
        }
    }

    /**
     * vivo比较新的系统获取方法
     *
     * @param context
     * @return
     */
    private fun getFloatPermissionStatus2(context: Context): Int {
        val packageName = context.packageName
        val uri2 = Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(packageName)
        val cursor = context
                .contentResolver
                .query(uri2, null, selection, selectionArgs, null)
        return if (cursor != null) {
            if (cursor.moveToFirst()) {
                val currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"))
                cursor.close()
                currentmode
            } else {
                cursor.close()
                1
            }
        } else 1
    }


}
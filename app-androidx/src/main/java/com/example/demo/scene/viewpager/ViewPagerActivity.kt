package com.example.demo.scene.viewpager

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewpager.widget.ViewPager
import com.effective.R
import com.example.demo.systemui.StatusbarHelper

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2023/6/29
 * desc   :
 * version: 1.0
 */
class ViewPagerActivity : AppCompatActivity() {

    private val viewPager by lazy { findViewById<ViewPager>(R.id.view_pager) }

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ViewPagerActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_view_pager_layout)
        // 获取根视图（DecorView 下的 content FrameLayout）
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                left = systemBars.left,
                bottom = systemBars.bottom,
                right = systemBars.right
            )
            insets
        }
        viewPager.adapter = ChatFragmentPagerAdapter(supportFragmentManager)
    }


}
package com.example.demo.scene.viewpager

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.effective.R
import com.example.demo.systemui.StatusbarHelper
import kotlinx.android.synthetic.main.activity_view_pager_layout.view_pager

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2023/6/29
 * desc   :
 * version: 1.0
 */
class ViewPagerActivity : AppCompatActivity() {


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
        view_pager.adapter = ChatFragmentPagerAdapter(supportFragmentManager)
    }


}
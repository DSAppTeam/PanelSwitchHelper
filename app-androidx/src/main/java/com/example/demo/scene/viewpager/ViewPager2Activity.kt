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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.effective.R
import com.example.demo.Constants
import com.example.demo.anno.ChatPageType
import com.example.demo.scene.chat.ChatFragment
import com.example.demo.systemui.StatusbarHelper

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2023/6/29
 * desc   :
 * version: 1.0
 */
class ViewPager2Activity : AppCompatActivity() {


    val orientation by lazy { intent.getIntExtra("extras_orientation", ViewPager2.ORIENTATION_VERTICAL) }
    private val viewPager by lazy { findViewById<ViewPager2>(R.id.view_pager) }


    companion object {
        @JvmStatic
        fun start(context: Context, orientation: Int) {
            val intent = Intent(context, ViewPager2Activity::class.java)
            intent.putExtra("extras_orientation", orientation)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        setContentView(R.layout.activity_view_pager_2_layout)
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
        viewPager.orientation = orientation
        viewPager.adapter = ChatFragmentPager2Adapter(this)
    }

}


class ChatFragmentPager2Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        val chatFragment = ChatFragment()
        val arguments = Bundle().apply {
            putInt(Constants.KEY_PAGE_TYPE, ChatPageType.DEFAULT)
            putBoolean(Constants.RELEASE_WITH_PAGER, true)
        }
        chatFragment.arguments = arguments
        return chatFragment
    }


}

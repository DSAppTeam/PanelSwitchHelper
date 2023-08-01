package com.example.demo.scene.viewpager

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.effective.R
import com.example.demo.Constants
import com.example.demo.anno.ChatPageType
import com.example.demo.scene.chat.ChatFragment
import com.example.demo.systemui.StatusbarHelper
import kotlinx.android.synthetic.main.activity_view_pager_2_layout.view_pager

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2023/6/29
 * desc   :
 * version: 1.0
 */
class ViewPager2Activity : AppCompatActivity() {


    val orientation by lazy { intent.getIntExtra("extras_orientation", ViewPager2.ORIENTATION_VERTICAL) }


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
        view_pager.orientation = orientation
        view_pager.adapter = ChatFragmentPager2Adapter(this)
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

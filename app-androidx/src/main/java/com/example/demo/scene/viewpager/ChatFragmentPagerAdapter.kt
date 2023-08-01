package com.example.demo.scene.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.demo.Constants
import com.example.demo.anno.ChatPageType
import com.example.demo.scene.chat.ChatFragment

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2023/6/29
 * desc   :
 * version: 1.0
 */
class ChatFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        val chatFragment = ChatFragment()
        val arguments = Bundle().apply {
            putInt(Constants.KEY_PAGE_TYPE, ChatPageType.DEFAULT)
            putBoolean(Constants.RELEASE_WITH_PAGER, true)
        }
        chatFragment.arguments = arguments
        return chatFragment
    }

}
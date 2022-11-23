package com.example.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.effective.R

/**
 * 测试用的二级页面
 */
class TestSecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_second_layout)
    }
}
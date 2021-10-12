package com.effective.android.panel.interfaces

interface TriggerViewClickInterceptor {
    fun intercept(triggerId: Int): Boolean
}
package com.example.barrier_free
import androidx.multidex.MultiDexApplication

class MyApplication : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        // 전역 초기화 코드
    }
}
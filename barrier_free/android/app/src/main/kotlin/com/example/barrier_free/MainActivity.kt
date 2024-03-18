package com.example.barrier_free

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {

    private val CHANNEL = "testing.flutter.android"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {

        // Flutter 메인화면에 보여지는 Android Native View 설정
        flutterEngine.platformViewsController
                .registry
                .registerViewFactory("androidView", NativeViewFactory(this))

        GeneratedPluginRegistrant.registerWith(flutterEngine)

        // FloatingActionButton 터치 시 호출되는 함수
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
                .setMethodCallHandler { call, result ->
                    if (call.method.equals("showActivity")) {
                        val intent = Intent(this, TMapActivity::class.java)
                        startActivity(intent)
                    } else {
                        result.error("unavailable", "cannot start activity", null)
                    }
                }


    }
}

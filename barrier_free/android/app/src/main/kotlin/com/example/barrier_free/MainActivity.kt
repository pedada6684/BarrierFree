package com.example.barrier_free

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.example.barrier_free/tmap"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Flutter 메인화면에 보여지는 Android Native View 설정
        flutterEngine.platformViewsController
                .registry
                .registerViewFactory("showTMap", NativeViewFactory(this))

//        GeneratedPluginRegistrant.registerWith(flutterEngine)

        // FloatingActionButton 터치 시 호출되는 함수
//        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
//                .setMethodCallHandler { call, result ->
//                    if (call.method.equals("showTMap")) {
//                        val intent = Intent(this, TMapActivity::class.java)
//                        startActivity(intent)
//                        result.success("TMap Activity Started")
//                    } else {
//                        result.error("unavailable", "cannot start activity", null)
//                    }
//                }
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method.equals("showTMap")) {
                val intent = Intent(this@MainActivity, TMapActivity::class.java)
                startActivity(intent)
                result.success("TMap activity 시작")
            } else {
                result.error("불가능", "tmap activity 시작 못함", null)
            }
        }

        // 추가적인 플러그인 등록


    }
}

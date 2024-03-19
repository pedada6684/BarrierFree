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

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "showTMap" -> {
                    val intent = Intent(this@MainActivity, TMapActivity::class.java)
                    startActivity(intent)
                    result.success("TMap activity 시작")
                }

                "enableTrackingMode" -> {
                    val intent = Intent(this@MainActivity, TMapActivity::class.java).apply {
                        putExtra("enableTrackingMode", true)
                    }
//                    startActivity(intent)
                    result.success("TMap 추적 모드 활성화 요청 날렸음")
                    print(result)
                }
                else -> result.notImplemented()
            }
        }

    }

}

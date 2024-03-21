package com.barrier_free

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import android.util.Log

class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.barrier_free/tmap"

    companion object {
        private const val TAG = "TMapChannel"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Flutter 메인화면에 보여지는 Android Native View 설정
        flutterEngine.platformViewsController
                .registry
                .registerViewFactory("showTMap", NativeViewFactory(this))

        //chnnel로 직접 전달하기
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.barrier_free/tmap").setMethodCallHandler { call, result ->
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
                    startActivity(intent)
                    print(result)
                }

                "setCurrentLocation" -> {
                    val longitude = call.argument<Double>("longitude") ?: 0.0
                    val latitude = call.argument<Double>("latitude") ?: 0.0

                    Log.d(TAG, "위치 설정: 위도 = $latitude, 경도 = $longitude")

//                    if (TMapActivity.currentInstance != null) {
//                        // 현재 실행 중인 TMapActivity가 있으면 위치 업데이트
//                        TMapActivity.currentInstance?.updateLocation(latitude!!, longitude!!)
//                        result.success("TMap 위치 업데이트")
//                    } else {
//
//                        val intent = Intent(this@MainActivity, TMapActivity::class.java).apply {
//                            putExtra("latitude", latitude)
//                            putExtra("longitude", longitude)
//                            putExtra("enableTrackingMode", true) // 추적 모드 활성화를 위한 추가 정보
//                        }
//
//                        startActivity(intent)
//                        result.success("TMap 위치 설정, 활성화 요청 완")
//                    }

                    NativeViewManager.updateLocation(latitude, longitude)
                    result.success("TMap 위치 업데이트")
                }

                else -> result.notImplemented()
            }
        }

    }

}
